package com.homesoft.iso.heif

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.ParcelFileDescriptor
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.homesoft.iso.Heif
import com.homesoft.iso.Heif.Grid
import com.homesoft.iso.Heif.Image
import java.util.Collections


class MainActivity : AppCompatActivity(), ImageDecoder.OnBitmapRendered {
    private val handlerThread = HandlerThread("HeifWorker")
    private lateinit var imageView: ImageView
    private var parcelFileDescriptor: ParcelFileDescriptor? = null

    private val openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            val handler = Handler(handlerThread.looper)
            handler.post {
                contentResolver.openFileDescriptor(it, "r")?.let {pfd ->
                    val reader = FDStreamReader(pfd.fileDescriptor, 512)
                    val heif = Heif.getParser().parse(reader)
                    val primaryItem = heif.primaryItemId
                    if (primaryItem is Grid) {
                        val gridDecoder = GridDecoder(primaryItem, this, handler)
                        gridDecoder.decode(reader)
                        parcelFileDescriptor = pfd
                    } else if (primaryItem is Image) {
                        val imageDecoder = ImageDecoder(primaryItem)
                        imageDecoder.decode(Collections.singletonList(primaryItem), reader, this, handler)
                        parcelFileDescriptor = pfd
                    }
                }
            }
        }
    }

    init {
        handlerThread.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        imageView = findViewById(R.id.imageView)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.open) {
            openDocument.launch(arrayOf("image/heic","image/avif"))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @WorkerThread
    override fun onBitmapRendered(bitmap: Bitmap?, ptsUs: Long) {
        runOnUiThread {
            imageView.setImageBitmap(bitmap)
        }
    }
}

