package com.elder.zcommonmodule.Error

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.elder.zcommonmodule.R


class ErrorLoadingActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        var t = intent.getStringExtra("error")
        findViewById<TextView>(R.id.error_tv).text = t

//        OkGo.post<String>("http://47.112.109.151:8082/" + "faceswingFile/Fileupload").tag(this).params("file", file).execute(object : StringCallback() {
//            override fun onSuccess(response: Response<String>?) {
//                Log.e("result", response!!.body().toString())
//            }
//
//            override fun onError(response: Response<String>?) {
//                super.onError(response)
//            }
//        })
    }

}