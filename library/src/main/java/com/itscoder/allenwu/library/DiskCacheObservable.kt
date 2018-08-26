package com.itscoder.allenwu.library

import android.content.Context
import com.jakewharton.disklrucache.DiskLruCache
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.ObservableOnSubscribe
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream


class DiskCacheObservable(private val mContext: Context) : AbstractCacheObservable() {
    private var mDiskLruCache: DiskLruCache? = null
    // 缓存20m
    private val maxSize = (20 * 1024 * 1024).toLong()

    init {
        initDiskLruCache()
    }

    override fun putImage(image: Image) {
        Observable.create(ObservableOnSubscribe<Any> { putDataToDiskCache(image) }).subscribeOn(Schedulers.io()).subscribe()
    }

    /**
     * image加入缓存
     * @param image
     */
    private fun putDataToDiskCache(image: Image) {
        try {
            val key = DiskCacheUtil.getMd5String(image.url)
            val editor = mDiskLruCache!!.edit(key)
            if (editor != null) {
                val outputStream = editor.newOutputStream(0)
                if (saveBitmap(image.bitmap, outputStream)) {
                    editor.commit()
                } else {
                    editor.abort()
                }
            }
            mDiskLruCache!!.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 保存 bitmap
     * @param bitmap
     * @param outputStream
     * @return
     */
    private fun saveBitmap(bitmap: Bitmap?, outputStream: OutputStream): Boolean {
        val b = bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        try {
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return b
    }

    /**
     * 获取 Image 资源从Memory、Disk、Network获取
     */
    override fun getImage(url: String): Image {
        val bitmap = getDataFromDiskCache(url)
        val image = Image(url, bitmap)
        Log.d("TAG", "getImage from Disk" + (image.bitmap == null))
        return image
    }

    /**
     * 从Disk获取
     */
    private fun getDataFromDiskCache(url: String): Bitmap? {
        var fileDescriptor: FileDescriptor? = null
        var fileInputStream: FileInputStream? = null
        var snapshot: DiskLruCache.Snapshot
        try {
            val key = DiskCacheUtil.getMd5String(url)
            Log.d("TAG", "Disk Cache key$key")

            snapshot = mDiskLruCache!!.get(key)
            if (snapshot != null) {
                fileInputStream = snapshot.getInputStream(0) as FileInputStream
                fileDescriptor = fileInputStream.getFD()
            }
            var bitmap: Bitmap? = null
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            }
            return bitmap

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream!!.close()
                } catch (e: IOException) {

                }

            }
        }
        return null
    }

    // 实例化 cache
    private fun initDiskLruCache() {
        try {
            val cacheDir = DiskCacheUtil.getDiskCacheDir(this.mContext, "image_cache")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val versionCode = DiskCacheUtil.getAppVersionCode(mContext)
            mDiskLruCache = DiskLruCache.open(cacheDir, versionCode, 1, maxSize)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}