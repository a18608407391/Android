package com.cstec.administrator.party_module

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.elder.zcommonmodule.Entity.PhotoEntitiy
import com.elder.zcommonmodule.Entity.SocialPhotoEntity
import me.tatarka.bindingcollectionadapter2.BindingListViewAdapter
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter


class PartyDetailAdapter : BindingRecyclerViewAdapter<Any>() {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        var manager = recyclerView.layoutManager as GridLayoutManager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(p0: Int): Int {
                var items = getAdapterItem(p0)
                if (items is PhotoEntitiy) {
                    if (items.getItemType() == 0) {
                        return 4
                    } else {
                        return 1
                    }
                } else {
                    return 4
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