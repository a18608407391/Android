package com.elder.logrecodemodule.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.elder.logrecodemodule.Entity.ActivityPartyEntity
import com.google.gson.Gson
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

class ActivityPartyAdapter : BindingRecyclerViewAdapter<Any>() {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        var manager = recyclerView.layoutManager as GridLayoutManager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(p0: Int): Int {
                var items = getAdapterItem(p0)
                if (items is String) {
                    return 2
                } else if (items is ActivityPartyEntity.MBRecommend) {
                    return 1
                } else {
                    return 2
                }
            }
        }
    }
}