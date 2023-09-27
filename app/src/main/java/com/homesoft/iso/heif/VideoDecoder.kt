package com.homesoft.iso.heif

import android.media.MediaCodec
import android.media.MediaCodec.BufferInfo
import android.media.MediaFormat
import android.os.Handler
import android.util.Log
import android.view.Surface
import java.nio.ByteBuffer
import java.util.BitSet

class VideoDecoder(private val mimeType:String, private val width:Int, private val height:Int,
                   private val csd0 : ByteBuffer):
    MediaCodec.Callback(), AutoCloseable {
    private val mediaCodec = MediaCodec.createDecoderByType(mimeType)
    private val mediaFormat: MediaFormat = MediaFormat.createVideoFormat(mimeType, width, height)
    private val bufferInfo = BufferInfo()
    private val bitSet = BitSet()
    private var dataSource: DataSource? = null

    init {
        mediaFormat.setByteBuffer("csd-0", csd0)
        //mediaFormat.setInteger(MediaFormat.KEY_PRIORITY, 0)
    }

    fun isSupported(mimeType:String, width:Int, height:Int, csd0: ByteBuffer):Boolean {
        return this.mimeType == mimeType &&
                this.width == width && this.height == height &&
                this.csd0 == csd0
    }

    fun start(surface: Surface, dataSource: DataSource, handler:Handler) {
        this.dataSource = dataSource
        mediaCodec.setCallback(this, handler)
        mediaCodec.configure(mediaFormat, surface, null, 0)
        mediaCodec.start()
    }

    override fun close() {
        mediaCodec.flush()
        mediaCodec.release()
    }

    fun maybeQueueInputBuffer() {
        val index = bitSet.nextSetBit(0)
        if (index >= 0) {
            maybeQueueInputBuffer(index)
        }
    }

    private fun maybeQueueInputBuffer(index: Int) {
        dataSource?.let {myDataSource ->
            mediaCodec.getInputBuffer(index)?.let { byteBuffer ->
                when (myDataSource.populateImageBuffer(byteBuffer, bufferInfo)) {
                    0 -> mediaCodec.queueInputBuffer(index, bufferInfo.offset, bufferInfo.size,
                        bufferInfo.presentationTimeUs, bufferInfo.flags)
                    MediaCodec.BUFFER_FLAG_END_OF_STREAM -> {
                        mediaCodec.queueInputBuffer(index, 0,0,0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        dataSource = null
                    }
                    // Subtle: return without clearing bit
                    MediaCodec.INFO_TRY_AGAIN_LATER -> return
                }
                bitSet.clear(index)
            }
        }
    }

    override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
        bitSet.set(index)
        maybeQueueInputBuffer(index)
    }

    override fun onOutputBufferAvailable(
        codec: MediaCodec,
        index: Int,
        info: MediaCodec.BufferInfo
    ) {
        mediaCodec.releaseOutputBuffer(index, true)
    }

    override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
        Log.e(ImageDecoder.TAG, "Decoder Error", e)
    }

    override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
        Log.d(ImageDecoder.TAG, "Output Format: $format")
    }

    private fun byteArrayToHex(byteBuffer:ByteBuffer): String {
        val sb = StringBuilder(byteBuffer.remaining() * 3)
        while (byteBuffer.hasRemaining()) {
            sb.append(String.format("%02x ", byteBuffer.get()))
        }
        return sb.toString()
    }

    interface DataSource {
        /**
         * Populate the next image buffer
         * @return false if the last buffer
         */
        fun populateImageBuffer(byteBuffer:ByteBuffer, bufferInfo: BufferInfo):Int
    }
}