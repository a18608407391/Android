package com.cstec.administrator.social.Entity

import android.widget.ImageView
import java.io.Serializable


class GridClickEntity :Serializable {

    var itemPosition = 0

    var childPosition = 0

    var img: ImageView? = null

    var url: String? = null

    var urlList: List<String>? = null


}