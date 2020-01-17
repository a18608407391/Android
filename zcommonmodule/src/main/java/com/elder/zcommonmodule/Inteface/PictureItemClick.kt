package com.elder.zcommonmodule.Inteface

import com.elder.zcommonmodule.PictureInfo


interface PictureItemClick {
    fun onItemClick(info: PictureInfo, position: Int)
}