package com.homesoft.iso.heif

import android.system.Os
import android.system.OsConstants
import com.homesoft.iso.RandomStreamReader
import java.io.FileDescriptor
import java.io.IOException
import java.nio.ByteBuffer

class FDStreamReader(val fileDescriptor: FileDescriptor, blockSize:Int): RandomStreamReader(blockSize) {

    override fun close() {
        Os.close(fileDescriptor)
    }

    override fun read(byteBuffer: ByteBuffer?, position: Long): Int {
        return when (val bytes = Os.pread(fileDescriptor, byteBuffer, position)) {
            OsConstants.EXIT_FAILURE -> throw IOException("Read Failed")
            0 -> -1
            else -> bytes
        }
    }

    override fun size(): Long {
        val structStat = Os.fstat(fileDescriptor)
        return structStat.st_size
    }
}