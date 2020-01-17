package com.elder.zcommonmodule.Inteface

import android.widget.ImageView


interface OnItemPictureClickListener{

    fun onItemPictureClick(itemPostion: Int, i: Int, url: String, urlList: List<String>, imageView: ImageView)
}