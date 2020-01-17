package com.cstec.administrator.social.Adapter

import android.support.v7.widget.RecyclerView
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import android.support.v7.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.NineGridSimpleLayout


class NineGridePicAdapter : BindingRecyclerViewAdapter<DynamicsCategoryEntity.Dynamics>() {


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        this.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        var view = holder.itemView
        var grid = view.findViewById<NineGridSimpleLayout>(R.id.grid)
        if (!grid.mUrlList.isEmpty()) {
            grid.mUrlList.forEachIndexed { index, s ->
                var view = grid.getChildAt(index)
                if (view != null) {
                    Glide.with(grid.mContexts!!).clear(view)
                }
            }
        }
    }
}