package com.example.private_module.Adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.private_module.Bean.PhotoEntitiy
import com.example.private_module.R
import com.example.private_module.ViewModel.PhotoAlbumViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import org.cs.tec.library.Base.Utils.context


class PhotoAdapter : BindingRecyclerViewAdapter<PhotoEntitiy> {
    lateinit var model: PhotoAlbumViewModel

    constructor(model: PhotoAlbumViewModel) {
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
          if(holder.itemView!=null){
            var img =   holder.itemView.findViewById<ImageView>(R.id.photo_img)
          }
    }
}