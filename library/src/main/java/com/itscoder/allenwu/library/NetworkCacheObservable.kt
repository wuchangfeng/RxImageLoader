package com.itscoder.allenwu.library

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.URL

class NetworkCacheObservable : AbstractCacheObservable() {
    override fun putImage(image: Image) {
    }

    override fun getImage(url: String): Image {
        Log.d("TAG", "getImage from network")
        val bitmap = downloadImage(url)
        return Image(url, bitmap)
    }

    private fun downloadImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        var inputStream: InputStream? = null
        try {
            val con = URL(url).openConnection()
            inputStream = con.getInputStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        }
        return bitmap
    }
}