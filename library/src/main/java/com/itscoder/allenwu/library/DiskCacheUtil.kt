package com.itscoder.allenwu.library

import android.content.pm.PackageManager
import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object DiskCacheUtil {

    fun getDiskCacheDir(context: Context, uniqueName: String): File {
        val cachePath: String
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getCacheDir().getPath()
        } else {
            cachePath = context.getCacheDir().getPath()
        }
        Log.d("DiskCacheUtil", cachePath)
        return File(cachePath + File.separator + uniqueName)
    }


    fun getAppVersionCode(context: Context): Int {
        try {
            val info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
            return info.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 1
    }


    fun getMd5String(key: String): String {
        var cacheKey: String
        try {
            val messageDigest = MessageDigest.getInstance("MD5")
            messageDigest.update(key.toByteArray())
            cacheKey = bytesToHexString(messageDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            cacheKey = key.hashCode().toString()
        }

        return cacheKey
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices) {
            val hex = Integer.toHexString(0xFF and bytes[i].toInt())
            if (hex.length == 1) {
                sb.append('0')
            }
            sb.append(hex)
        }
        return sb.toString()
    }
}