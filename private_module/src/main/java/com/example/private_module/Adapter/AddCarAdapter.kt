package com.example.private_module.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.elder.zcommonmodule.INSERT_CARS
import com.example.private_module.Activity.AddCarActivity
import com.example.private_module.Bean.CarsEntity
import com.example.private_module.R
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.item_add_car_layout.view.*
import org.cs.tec.library.Utils.ConvertUtils


class AddCarAdapter : RecyclerView.Adapter<AddCarAdapter.AddCarViewHolder> {
    var mLayoutInflater: LayoutInflater
    var mListDatas: ArrayList<CarsEntity>? = null

    constructor(activity: AddCarActivity) {
        this.mLayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }


    fun setCarDatas(list: ArrayList<CarsEntity>) {
        this.mListDatas = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AddCarViewHolder {
        var v = mLayoutInflater.inflate(R.layout.item_add_car_layout, p0, false)
        return AddCarViewHolder(v)
    }

    override fun getItemCount(): Int {
        return if (mListDatas != null) mListDatas?.size!! else 0
    }

    override fun onBindViewHolder(holder: AddCarViewHolder, p1: Int) {
        holder.setPosition(p1, mListDatas!!)
        var entity = mListDatas!![p1]
        if (TextUtils.isEmpty(entity.imgUrl.get())) {
            holder.emptyLayout.visibility = View.VISIBLE
            holder.addcar_type.visibility = View.GONE
        } else {
            holder.emptyLayout.visibility = View.GONE
            val options = RequestOptions().placeholder(R.drawable.default_car).error(R.drawable.default_car).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.dp2px(240f), ConvertUtils.dp2px(160f))
            Glide.with(holder.itemView.add_car_bg).asBitmap().load(entity.imgUrl.get()).apply(options).into(holder.itemView.add_car_bg)
            if (entity.isDefault.get() == 1) {
                holder.itemView.default_icon.visibility = View.VISIBLE
            } else {
                holder.itemView.default_icon.visibility = View.GONE
            }
            holder.itemView.name.text = entity.carName.get()
            holder.itemView.brandName.text = entity.brandName.get()
            holder.itemView.brandTypeName.text = entity.brandTypeName.get()
            holder.addcar_type.visibility = View.VISIBLE
        }
    }


    inner class AddCarViewHolder : RecyclerView.ViewHolder {
        var emptyLayout: LinearLayout
        var addcar_type: LinearLayout
        var mPosition = 0
        lateinit var mListDatas: ArrayList<CarsEntity>

        constructor(itemView: View) : super(itemView) {
            addcar_type = itemView.findViewById(R.id.add_car_type_layout)
            emptyLayout = itemView.findViewById(R.id.add_car_visible_layout)
            itemView.setOnClickListener {
                if (ClickLisetner != null) {
                    ClickLisetner?.onItemClick(it, mPosition)
                }
            }
            itemView.car_edit.setOnClickListener {
                if(EditClickLisetner!=null){
                    EditClickLisetner?.editClick(it,mPosition)
                }
            }
        }

        fun setPosition(position: Int, mListDatas: ArrayList<CarsEntity>) {
            this.mPosition = position
            this.mListDatas = mListDatas
        }
    }

    var ClickLisetner: AddCarItemClickCallBack? = null
    fun setOnItemClickListener(listener: AddCarItemClickCallBack) {
        this.ClickLisetner = listener
    }

    var EditClickLisetner: editClickCallBack? = null
    fun setEditOnItemClickListener(listener: editClickCallBack) {
        this.EditClickLisetner = listener
    }

    interface AddCarItemClickCallBack {
        fun onItemClick(view: View, position: Int)
    }

    interface editClickCallBack {
        fun editClick(view: View, position: Int)
    }
}