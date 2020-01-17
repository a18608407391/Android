package com.example.drivermodule.Adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.elder.zcommonmodule.getImageUrl
import com.example.drivermodule.Activity.ShareDriverActivity
import com.elder.zcommonmodule.Entity.ShareEntity
import com.example.drivermodule.R
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Utils.ConvertUtils
import java.text.SimpleDateFormat
import java.util.*

class ShareAdapter : RecyclerView.Adapter<ShareAdapter.AddCarViewHolder> {

    var mLayoutInflater: LayoutInflater
    var mListDatas: ArrayList<ShareEntity>? = null
    var activity: ShareDriverActivity? = null

    constructor(activity: ShareDriverActivity) {
        this.activity = activity
        this.mLayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setCarDatas(list: ArrayList<ShareEntity>) {
        this.mListDatas = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AddCarViewHolder {
        var v = mLayoutInflater.inflate(R.layout.share_item_layout, p0, false)
        return AddCarViewHolder(v)
    }

    override fun getItemCount(): Int {
        return if (mListDatas != null) mListDatas?.size!! else 0
    }

    override fun onBindViewHolder(holder: AddCarViewHolder, p1: Int) {
        var path: Bitmap? = null
        if (p1 == 0) {
            path = mListDatas!![p1].shareIcon
            holder.share_item_speed.setTextColor(getColor(R.color.blackTextColor))
            holder.share_item_distance.setTextColor(getColor(R.color.blackTextColor))
            holder.share_item_total_time.setTextColor(getColor(R.color.blackTextColor))
            holder.share_item_date.setTextColor(getColor(R.color.nomalTextColor))
            holder.share_item_name.setTextColor(getColor(R.color.nomalTextColor))
            holder.share_user_nomal_distance.setTextColor(getColor(R.color.blackTextColor))
            holder.share_user_nomal_time.setTextColor(getColor(R.color.blackTextColor))
            holder.share_user_nomal_speed.setTextColor(getColor(R.color.blackTextColor))
        } else {
            path = mListDatas!![p1].secondBitmap
            holder.share_item_speed.setTextColor(getColor(R.color.white))
            holder.share_item_distance.setTextColor(getColor(R.color.white))
            holder.share_item_total_time.setTextColor(getColor(R.color.white))
            holder.share_item_date.setTextColor(getColor(R.color.white))
            holder.share_item_name.setTextColor(getColor(R.color.white))
            holder.share_user_nomal_distance.setTextColor(getColor(R.color.white))
            holder.share_user_nomal_time.setTextColor(getColor(R.color.white))
            holder.share_user_nomal_speed.setTextColor(getColor(R.color.white))

            holder.share_user_nomal_distance.setOnClickListener(holder)
            holder.share_user_nomal_speed.setOnClickListener(holder)
            holder.share_item_speed.setOnClickListener(holder)
            holder.share_item_distance.setOnClickListener(holder)
            holder.share_item_total_time.setOnClickListener(holder)
            holder.share_item_date.setOnClickListener(holder)
            holder.share_item_name.setOnClickListener(holder)
            holder.share_user_nomal_time.setOnClickListener(holder)
        }
        var optionsd = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.dp2px(225F), ConvertUtils.dp2px(402F))
        Glide.with(activity!!).asBitmap().load(path).apply(optionsd).into(holder.share_item_img)

        val crop = CircleCrop()
        var options = RequestOptions().transform(crop).error(R.drawable.default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true)
        Glide.with(activity!!).asBitmap().load(getImageUrl(mListDatas!![p1].userInfo.get()?.data?.headImgFile)).apply(options).into(holder.share_item_avatar)
        var simple = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var d = Date(System.currentTimeMillis())
        holder.share_item_date.text = simple.format(d)

        holder.share_item_name.text = mListDatas?.get(p1)?.userInfo?.get()!!.data?.name
        holder.share_item_distance.text = mListDatas?.get(p1)?.Totaldistance!!.get()
        holder.share_item_total_time.text = mListDatas?.get(p1)?.Totaltime!!.get()
        holder.share_item_speed.text = mListDatas?.get(p1)?.averageRate!!.get()

//        holder.setPosition(p1, mListDatas!!)
//        var entity = mListDatas!![p1]
//        if (TextUtils.isEmpty(entity.imgUrl.get())) {
//            holder.emptyLayout.visibility = View.VISIBLE
//            holder.addcar_type.visibility = View.GONE
//        } else {
//            holder.emptyLayout.visibility = View.GONE
//            val options = RequestOptions().placeholder(R.drawable.default_car).error(R.drawable.default_car).diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(true).override(ConvertUtils.dp2px(240f), ConvertUtils.dp2px(160f))
//            Glide.with(holder.itemView.add_car_bg).asBitmap().load(entity.imgUrl.get()).apply(options).into(holder.itemView.add_car_bg)
//            if (entity.isDefault.get() == 1) {
//                holder.itemView.default_icon.visibility = View.VISIBLE
//            } else {
//                holder.itemView.default_icon.visibility = View.GONE
//            }
//            holder.itemView.name.text = entity.carName.get()
//            holder.itemView.brandName.text = entity.brandName.get()
//            holder.itemView.brandTypeName.text = entity.brandTypeName.get()
//            holder.addcar_type.visibility = View.VISIBLE
//        }
    }


    inner class AddCarViewHolder : RecyclerView.ViewHolder, View.OnClickListener {

        var textColor = Color.WHITE

        override fun onClick(v: View?) {
            var tv = v as TextView
            if (textColor == Color.WHITE) {
                textColor = Color.BLACK
                share_user_nomal_distance.setTextColor(textColor)
                share_user_nomal_speed.setTextColor(textColor)
                share_item_speed.setTextColor(textColor)
                share_item_distance.setTextColor(textColor)
                share_item_total_time.setTextColor(textColor)
                share_item_date.setTextColor(textColor)
                share_item_name.setTextColor(textColor)
                share_user_nomal_time.setTextColor(textColor)
            } else {
                textColor = Color.WHITE
                share_user_nomal_distance.setTextColor(textColor)
                share_user_nomal_speed.setTextColor(textColor)
                share_item_speed.setTextColor(textColor)
                share_item_distance.setTextColor(textColor)
                share_item_total_time.setTextColor(textColor)
                share_item_date.setTextColor(textColor)
                share_item_name.setTextColor(textColor)
                share_user_nomal_time.setTextColor(textColor)
            }

        }

        var share_item_img: ImageView

        var share_item_avatar: ImageView

        var share_item_date: TextView

        var share_item_total_time: TextView

        var share_item_name: TextView

        var share_item_speed: TextView

        var share_item_distance: TextView

        var share_user_nomal_distance: TextView
        var share_user_nomal_speed: TextView
        var share_user_nomal_time: TextView

        var mPosition = 0
        lateinit var mListDatas: ArrayList<ShareEntity>

        constructor(itemView: View) : super(itemView) {
            share_item_img = itemView.findViewById(R.id.share_item_img)
            share_item_name = itemView.findViewById(R.id.share_item_name)
            share_item_avatar = itemView.findViewById(R.id.share_item_avatar)
            share_item_total_time = itemView.findViewById(R.id.share_item_total_time)
            share_item_date = itemView.findViewById(R.id.share_item_date)
            share_item_speed = itemView.findViewById(R.id.share_item_speed)
            share_user_nomal_distance = itemView.findViewById(R.id.share_user_nomal_distance)
            share_user_nomal_time = itemView.findViewById(R.id.share_user_nomal_time)
            share_user_nomal_speed = itemView.findViewById(R.id.share_user_nomal_speed)
            share_item_distance = itemView.findViewById(R.id.share_item_distance)

//            itemView.setOnClickListener {
//                if (ClickLisetner != null) {
//                    ClickLisetner?.onItemClick(it, mPosition)
//                }
//            }
//            itemView.car_edit.setOnClickListener {
//                if(EditClickLisetner!=null){
//                    EditClickLisetner?.editClick(it,mPosition)
//                }
//            }
        }

        fun setPosition(position: Int, mListDatas: ArrayList<ShareEntity>) {
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