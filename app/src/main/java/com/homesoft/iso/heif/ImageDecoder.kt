package com.homesoft.iso.heif

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaFormat
import android.os.Handler
import android.view.PixelCopy
import com.homesoft.iso.Heif.Image
import com.homesoft.iso.RandomStreamReader
import com.homesoft.iso.box.CodecSpecificData
import com.homesoft.iso.box.HevcDecoderConfig
import com.homesoft.iso.box.ItemInfoEntry
import java.nio.ByteBuffer
import java.util.Collections

class ImageDecoder(image: Image):AutoCloseable, VideoDecoder.DataSource,
    ImageReader.OnImageAvailableListener {
    private val videoDecoder: VideoDecoder
    private val imageReader:ImageReader
    private val width:Int
    private val height:Int

    private var imageList: List<Image> = Collections.emptyList()
    private var index = 0

    private var listener: OnBitmapRendered? = null
    private var handler: Handler? = null
    private var reader: RandomStreamReader? = null
    private var processing = 0
    init {
        val codecSpecificData = image.codecSpecificData
        val csdList = codecSpecificData.codecSpecificData
        if (csdList.isEmpty()) {
            throw IllegalArgumentException("CodeSpecificData empty")
        }
        val mimeType:String
        val csd0: ByteBuffer
        when (image.type) {
            ItemInfoEntry.ITEM_TYPE_hvc1 -> {
                mimeType = MediaFormat.MIMETYPE_VIDEO_HEVC
                val vps = CodecSpecificData.TypedConfig.findType(HevcDecoderConfig.TYPE_VPS, csdList)
                val vpsSize = vps?.capacity() ?: throw IllegalArgumentException("VPS Required")
                val sps = CodecSpecificData.TypedConfig.findType(HevcDecoderConfig.TYPE_SPS, csdList)
                val spsSize = sps?.capacity() ?: throw IllegalArgumentException("SPS Required")
                val pps = CodecSpecificData.TypedConfig.findType(HevcDecoderConfig.TYPE_PPS, csdList)
                val ppsSize = pps?.capacity() ?: throw IllegalArgumentException("PPS Required")
                csd0 = ByteBuffer.allocateDirect(vpsSize + spsSize + ppsSize + 12)
                csd0.putInt(1)
                csd0.put(vps)
                csd0.putInt(1)
                csd0.put(sps)
                csd0.putInt(1)
                csd0.put(pps)
                csd0.clear()
            }
            ItemInfoEntry.ITEM_TYPE_av01 -> {
                mimeType = MediaFormat.MIMETYPE_VIDEO_AV1
                // Codec tries to access bytes directly, which blows up on RO ByteBuffer
                val csd0ro = csdList[0].byteBuffer
                csd0 = ByteBuffer.allocateDirect(csd0ro.capacity())
                csd0.put(csd0ro)
            }
            else -> throw IllegalArgumentException("Unknown type")
        }
        val spatialExtents = image.imageSpatialExtents
        val itemLocation = image.itemLocation

        if (spatialExtents == null || itemLocation == null || itemLocation.extentCount == 0) {
            throw IllegalArgumentException("Missing Required Data")
        }
        width = spatialExtents.width
        height = spatialExtents.height
        imageReader = ImageReader.newInstance(width, height, ImageFormat.PRIVATE, IMAGE_READER_SIZE)
        videoDecoder = VideoDecoder(mimeType, width, height, csd0)
    }

    fun decode(list:List<Image>, reader: RandomStreamReader,
                      listener: OnBitmapRendered, handler: Handler) {
        this.handler = handler
        this.listener = listener
        this.reader = reader
        imageList = list
        imageReader.setOnImageAvailableListener(this, handler)
        videoDecoder.start(imageReader.surface, this, handler)

    }

    override fun populateImageBuffer(byteBuffer: ByteBuffer,
                                     bufferInfo: MediaCodec.BufferInfo):Int {
        if (index >= imageList.size) {
            return MediaCodec.BUFFER_FLAG_END_OF_STREAM // 4
        }
        if (processing != 0) {
            return MediaCodec.INFO_TRY_AGAIN_LATER // -1
        }
        val image = imageList[index++]
        if (image.itemLocation.extentCount > 0) {
            reader?.let { randomStreamReader ->
                if (image.type == ItemInfoEntry.ITEM_TYPE_hvc1) {
                    bufferInfo.size = image.readExtentAsByteStream(0, randomStreamReader, byteBuffer)
                } else {
                    bufferInfo.size = image.readExtent(0, randomStreamReader, byteBuffer)
                }
                bufferInfo.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
                bufferInfo.presentationTimeUs = index - 1L
            }
            processing++
        }
        return 0
    }

    override fun onImageAvailable(ir: ImageReader?) {
        val myListener = listener
        val myHandler = handler
        if (myListener == null || myHandler == null) {
            imageReader.acquireLatestImage().close()
        } else {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            PixelCopy.request(imageReader.surface, bitmap, { rc->
                val image = imageReader.acquireNextImage()
                val ptsUs = image.timestamp
                image.close()
                processing--
                videoDecoder.maybeQueueInputBuffer()
                myListener.onBitmapRendered(if (rc == PixelCopy.SUCCESS) bitmap else null, ptsUs)
            }, myHandler)
        }
    }
    override fun close() {
        videoDecoder.close()
        imageReader.close()
    }

    companion object {
        const val TAG = "ImageDecoder"
        const val IMAGE_READER_SIZE = 2
    }

    interface OnBitmapRendered {
        fun onBitmapRendered(bitmap: Bitmap?, ptsUs: Long)
    }
}