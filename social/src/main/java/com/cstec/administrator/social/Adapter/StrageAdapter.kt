package com.cstec.administrator.social.Adapter

import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.Entity.HotData
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

class StrageAdapter : BindingRecyclerViewAdapter<HotData>() {


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemView != null) {
            if (holder.itemView.findViewById<ImageView>(R.id.road_strage_img) != null) {
                var img = holder.itemView.findViewById<ImageView>(R.id.road_strage_img)
                Glide.with(img.context).clear(img)
            }
        }
    }
}