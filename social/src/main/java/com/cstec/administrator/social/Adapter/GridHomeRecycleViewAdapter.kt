package com.cstec.administrator.social.Adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.ItemViewModel.CavalierDynamicItem
import com.cstec.administrator.social.ItemViewModel.DriverHomeViewModel
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.cstec.administrator.social.ItemViewModel.SocialItemModel
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.NineGridSimpleLayout


class GridHomeRecycleViewAdapter : RecyclerView.Adapter<GridHomeRecycleViewAdapter.GridRecycleViewHolder> {
    var context: Context
    var data: ArrayList<DynamicsCategoryEntity.Dynamics>
    var model: CavalierDynamicItem

    constructor(context: Context, items: ArrayList<DynamicsCategoryEntity.Dynamics>, model: CavalierDynamicItem) {
        this.context = context
        this.data = items
        this.model = model
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GridRecycleViewHolder {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var root = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.spcial_home_child_item_photo, null, false)
        return GridRecycleViewHolder(root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: GridRecycleViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            //局部刷新
            Log.e("result", "长度" + payloads.size)
            var it = model.items[position]
            if (payloads[0].toString() == "LikeClick") {
                var tv = holder.binding.root.findViewById<TextView>(R.id.like_count)
                if (it.isLike == 0) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(tv.context.resources.getDrawable(R.drawable.like_default), null, null, null)
                } else {
                    tv.setCompoundDrawablesWithIntrinsicBounds(tv.context.resources.getDrawable(R.drawable.like_icon), null, null, null)
                }
                tv.setText(it.fabulousCount)
            } else if (payloads[0].toString() == "storeClick") {
                var tv = holder.binding.root.findViewById<TextView>(R.id.star_count)
                if (it.hasCollection == 0) {
                    tv.setCompoundDrawablesWithIntrinsicBounds(tv.context.resources.getDrawable(R.drawable.start_default), null, null, null)
                } else {
                    tv.setCompoundDrawablesWithIntrinsicBounds(tv.context.resources.getDrawable(R.drawable.star_icon), null, null, null)
                }
                tv.setText(it.collectionCount)
            } else if (payloads[0].toString() == "focusClick") {

//                var tv = holder.binding.root.findViewById<TextView>(R.id.focus_visible)
//                if (it.followed == 0) {
//                    tv.visibility = View.VISIBLE
//                } else {
//                    tv.visibility = View.GONE
//                }
            }
            data = model.items
        }
    }

    override fun onBindViewHolder(holder: GridRecycleViewHolder, position: Int) {
        holder.binding.setVariable(BR.photo_item_model, data[position])
        holder.binding.setVariable(BR.model, model.listener)
        holder.binding.executePendingBindings()
    }

    fun initDatas(items: ObservableArrayList<DynamicsCategoryEntity.Dynamics>) {
        Log.e("result","当前数据条数" + items.size)
        this.data = items
        notifyDataSetChanged()
    }

    class GridRecycleViewHolder : RecyclerView.ViewHolder {
        var binding: ViewDataBinding

        constructor(binding: ViewDataBinding) : super(binding.root) {
            this.binding = binding
        }
    }

    override fun onViewRecycled(holder: GridRecycleViewHolder) {
        super.onViewRecycled(holder)
        var view = holder.itemView
        var grid = view.findViewById<NineGridSimpleLayout>(R.id.grid)
        if (!grid.mUrlList.isEmpty()) {
            grid.mUrlList.forEachIndexed { index, s ->
                var view = grid.getChildAt(index)
                if (view != null) {
                    view.visibility =View.GONE
                    Glide.with(grid.mContexts!!).clear(view)
                }
            }
        }
    }
}