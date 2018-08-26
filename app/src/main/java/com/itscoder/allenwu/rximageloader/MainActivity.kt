package com.itscoder.allenwu.rximageloader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.itscoder.allenwu.library.RxImgLoader

class MainActivity : AppCompatActivity() {
    var mUrl : String = "https://cn.bing.com/s/hpb/NorthMale_EN-US8782628354_1920x1080.jpg"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView(){
        var mImageView = findViewById<ImageView>(R.id.iv_photo)
        findViewById<Button>(R.id.btn_load).setOnClickListener(View.OnClickListener {
            RxImgLoader.with(this)!!.load(mUrl)!!.into(mImageView)
        })
    }
}