package com.elder.zcommonmodule

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.elder.zcommonmodule.Inteface.OnItemPictureClickListener
import com.elder.zcommonmodule.Widget.NineGridLayout
import com.elder.zcommonmodule.Widget.RatioImageView
import com.zk.library.R


class NineGridSimpleLayout : NineGridLayout {
    override fun displayImage(position: Int, imageView: RatioImageView?, url: String?) {
        if (mContexts != null) {
            var options = RequestOptions().placeholder(R.drawable.ic_album_default).error(R.drawable.ic_album_default).timeout(10000).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(400, 400)
            Glide.with(mContexts!!).asBitmap().apply(options).load(url).into(imageView!!)
        }
    }

    override fun onClickImage(position: Int, url: String?, urlList: MutableList<String>?, imageView: ImageView?) {
        if (listener != null) {
            listener?.onItemPictureClick(itemPosition, position, url!!, urlList!!, imageView!!)
        }
    }

    var mContexts: Context? = null
    var itemPosition: Int = 0
    var listener: OnItemPictureClickListener? = null

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContexts = context
    }


    fun setItemPositions(itemPosition: Int) {
        this.itemPosition = itemPosition
    }

    fun setListeners(listener: OnItemPictureClickListener) {
        this.listener = listener
    }
}