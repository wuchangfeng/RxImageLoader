package com.itscoder.allenwu.library

import android.content.Context
import android.util.Log
import android.widget.ImageView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate


class RxImgLoader(builder: Builder){
    private var mContext: Context
    private lateinit var mUrl: String
    private  var mRequestCreator: RequestCreator

    init {
        this.mContext = builder.context;
        mRequestCreator = RequestCreator(mContext);
    }

    companion object  {
        var mSingletoon: RxImgLoader? = null

        fun with(context: Context): RxImgLoader?{
            if (mSingletoon == null) {
                synchronized(RxImgLoader::class.java) {
                    if (mSingletoon == null) {
                        mSingletoon = Builder(context).build()
                    }
                }
            }
            return mSingletoon
        }
    }

    fun load(url: String): RxImgLoader?{
        mUrl = url
        return mSingletoon
    }

    fun into(imageview: ImageView): RxImgLoader?{
        Observable.concat(
                mRequestCreator.getImageFromMemory(mUrl),
                mRequestCreator.getImageFromDisk(mUrl),
                mRequestCreator.getImageFromNetwork(mUrl))
                .filter(object : Predicate<Image>{
                    @Throws(Exception::class)
                    override fun test(image: Image): Boolean {
                        Log.d("TAG", "filter")
                        if(image.bitmap == null){
                            return false
                        }
                        return true
                    }
                })
                .firstElement().toObservable()
                .subscribe(object : Observer<Image> {

                    override fun onSubscribe(d: Disposable) {
                    }
                    override fun onNext(image: Image) {
                        Log.d("TAG", image.url)
                        imageview.setImageBitmap(image.bitmap)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                        Log.d("TAG", "onComplete")
                    }

                })

        return mSingletoon
    }

    class Builder(context: Context){
        var context: Context
        init {
            this.context = context
        }

        fun build(): RxImgLoader {
            return RxImgLoader(this)
        }
    }
}
