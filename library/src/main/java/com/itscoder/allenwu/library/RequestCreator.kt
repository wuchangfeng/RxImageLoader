package com.itscoder.allenwu.library

import android.content.Context
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate


class RequestCreator(context: Context){
    var memoryCacheObservable: MemCacheObservable? = null
    private var diskCacheObservable: DiskCacheObservable? = null
    private var networkCacheObservable: NetworkCacheObservable? = null

    init {
        memoryCacheObservable = MemCacheObservable()
        diskCacheObservable = DiskCacheObservable(context)
        networkCacheObservable = NetworkCacheObservable()
    }

    fun getImageFromMemory(url: String): Observable<Image> {
        return memoryCacheObservable!!.requestImage(url)
    }

    fun getImageFromDisk(url: String): Observable<Image> {
        return diskCacheObservable!!.requestImage(url)
                .filter(object : Predicate<Image>{
                    @Throws(Exception::class)
                    override fun test(image: Image): Boolean {
                        return image.bitmap!= null
                    }
                })
                .doOnNext(object : Consumer<Image>{
                    @Throws(Exception::class)
                    override fun accept(image: Image) {
                        memoryCacheObservable!!.putImage(image)
                    }
                })
    }

    fun getImageFromNetwork(url: String): Observable<Image> {
        return networkCacheObservable!!.requestImage(url)
                .filter(object : Predicate<Image>{
                    @Throws(Exception::class)
                    override fun test(image: Image): Boolean {
                        return image.bitmap != null
                    }
                })
                .doOnNext(object : Consumer<Image>{
                    @Throws(Exception::class)
                    override fun accept(image: Image) {
                        diskCacheObservable!!.putImage(image)
                        memoryCacheObservable!!.putImage(image)
                    }
                })
    }
}