package com.example.drivermodule.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.services.core.PoiItem
import com.example.drivermodule.Fragment.SearchActivity
import com.example.drivermodule.R
import kotlinx.android.synthetic.main.history_item.view.*


class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    var mListDatas: ArrayList<PoiItem>? = null
    var activity: SearchActivity? = null
    var mInflate: LayoutInflater? = null

    constructor(activity: SearchActivity) {
        this.activity = activity
        this.mInflate = activity.activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SearchViewHolder {
        return SearchViewHolder(mInflate?.inflate(R.layout.history_item, p0, false)!!)
    }

    override fun getItemCount(): Int {
        return if (mListDatas == null) 0 else mListDatas?.size!!
    }

    override fun onBindViewHolder(holder: SearchViewHolder, p1: Int) {
        var data = this!!.mListDatas!![p1]
        holder.itemView.coarse_location.text = data.title
        holder.itemView.fine_location.text = data.snippet
        holder.itemView.history_item_layout.setOnClickListener {
            if (listener != null) {
                listener?.itemClick(it,data)
            }
        }
    }

    fun setDatas(mList: ArrayList<PoiItem>) {
        this.mListDatas = mList
        this.notifyDataSetChanged()
    }

    inner class SearchViewHolder : RecyclerView.ViewHolder {
        constructor(itemView: View) : super(itemView) {
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun itemClick(view: View, tip: PoiItem)
    }
}