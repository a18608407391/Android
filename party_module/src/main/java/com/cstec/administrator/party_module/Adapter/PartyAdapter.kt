package com.cstec.administrator.party_module.Adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.cstec.administrator.party_module.PartyHomeEntity
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter


class PartyAdapter : BindingRecyclerViewAdapter<Any>() {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        var manager = recyclerView.layoutManager as GridLayoutManager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(p0: Int): Int {
                var items = getAdapterItem(p0)
                if (items is String) {
                    return 2
                } else if (items is PartyHomeEntity.MBRecommend) {
                    return 1
                } else {
                    return 2
                }
            }
        }
    }
}