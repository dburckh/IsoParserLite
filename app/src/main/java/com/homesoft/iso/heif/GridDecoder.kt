package com.homesoft.iso.heif

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.util.Log
import com.homesoft.iso.Heif.Grid
import com.homesoft.iso.RandomStreamReader

class GridDecoder(grid: Grid,
                  private val listener:ImageDecoder.OnBitmapRendered,
                  private val handler: Handler):ImageDecoder.OnBitmapRendered, AutoCloseable {
    private val imageList = grid.imageList
    private val gridBitmap:Bitmap = run {
        val spatialExtents = grid.imageSpatialExtents
        Bitmap.createBitmap(spatialExtents.width,
            spatialExtents.height, Bitmap.Config.ARGB_8888)
    }
    private val canvas = Canvas(gridBitmap)
    private val rect = Rect()

    fun decode(streamReader: RandomStreamReader) {
        if (imageList.isEmpty()) {
            return
        }
        val imageDecoder = ImageDecoder(imageList[0])
        imageDecoder.decode(imageList, streamReader, this, handler)
    }

    override fun onBitmapRendered(bitmap: Bitmap?, ptsUs: Long) {
        Log.d(TAG, "Got Bitmap ptsUs=$ptsUs")
        if (bitmap == null) {
            listener.onBitmapRendered(null, ptsUs)
            return
        }
        rect.right = rect.right + bitmap.width
        rect.bottom = rect.top + bitmap.height
        canvas.drawBitmap(bitmap, null, rect, null)
        rect.left = rect.right
        if (rect.right > gridBitmap.width) {
            rect.set(0, rect.bottom, 0, rect.bottom)
        }
        if (rect.top >= gridBitmap.height) {
            listener.onBitmapRendered(gridBitmap, ptsUs)
        }
    }

    override fun close() {
//        imageDecoder.close()
    }

    companion object {
        const val TAG="GridDecoder"
    }
}