package com.cstec.administrator.social.Adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.cstec.administrator.social.Entity.PhotoEntitiy
import com.cstec.administrator.social.ItemViewModel.CavalierPhotoItem
import com.cstec.administrator.social.R
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter


class PhotoAdapter : BindingRecyclerViewAdapter<PhotoEntitiy> {
    lateinit var model: CavalierPhotoItem

    constructor(model: CavalierPhotoItem) {
        this.model = model
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        var manager = recyclerView!!.layoutManager as GridLayoutManager

        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(p0: Int): Int {
                var item = getAdapterItem(p0)
                if (item.getItemType() == 0) {
                    return 4
                } else {
                    return 1
                }
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemView != null) {
            var img = holder.itemView.findViewById<ImageView>(R.id.photo_img)
            if (img != null) {
                Glide.with(img.context).clear(img)
            }
        }
    }
}