package com.itscoder.allenwu.library

import android.graphics.Bitmap

import android.util.Log
import android.util.LruCache



class MemCacheObservable : AbstractCacheObservable() {

    // 获取到应用的最大内存
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()//kb
    // 设置LruCache的缓存大小
    private val cacheSize = maxMemory / 8

    private val bitmapLruCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            return value.rowBytes * value.height / 1024
        }
    }

    override fun putImage(image: Image) {
        Log.d("TAG", "putImage from Memory" + image.url + image.bitmap)
        bitmapLruCache.put(image.url, image.bitmap)
        Log.d("TAG", "bitmapLruCache" + bitmapLruCache.size())
    }

    override fun getImage(url: String): Image {
        val bitmap = bitmapLruCache.get(url)
        val image = Image(url, bitmap)
        Log.d("TAG", "getImage from Memory" + (image.bitmap== null))
        Log.d("TAG", "bitmapLruCache" + bitmapLruCache.size())
        return image
    }
}