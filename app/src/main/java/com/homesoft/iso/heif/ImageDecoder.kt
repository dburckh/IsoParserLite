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
import com.homesoft.iso.parser.ItemInfoEntry
import java.lang.IllegalArgumentException
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
        val mimeType = when (image.type) {
            ItemInfoEntry.ITEM_TYPE_hvc1 -> MediaFormat.MIMETYPE_VIDEO_HEVC
            ItemInfoEntry.ITEM_TYPE_av01 -> MediaFormat.MIMETYPE_VIDEO_AV1
            else -> throw IllegalArgumentException("Unknown type")
        }
        val spatialExtents = image.imageSpatialExtents
        val codecConfig = image.codecSpecificData
        val itemLocation = image.itemLocation

        if (spatialExtents == null || codecConfig == null ||
            itemLocation == null || itemLocation.extentCount == 0) {
            throw IllegalArgumentException("Missing Required Data")
        }
        width = spatialExtents.width
        height = spatialExtents.height
        imageReader = ImageReader.newInstance(width, height, ImageFormat.PRIVATE, IMAGE_READER_SIZE)
        videoDecoder = VideoDecoder(mimeType, width, height, codecConfig)
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