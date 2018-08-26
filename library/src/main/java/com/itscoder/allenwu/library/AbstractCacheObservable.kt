package com.itscoder.allenwu.library

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.ObservableOnSubscribe



abstract class AbstractCacheObservable{

    fun requestImage(url: String): Observable<Image> {
        return Observable
                .create(ObservableOnSubscribe<Image> { e ->
                e.onNext(getImage(url))
                e.onComplete()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 保存图片
     *
     * @param image
     */
    abstract fun putImage(image: Image)


    /**
     * 具体获取 image方法
     *
     * @param url
     * @return image
     */
    abstract fun getImage(url: String): Image
}