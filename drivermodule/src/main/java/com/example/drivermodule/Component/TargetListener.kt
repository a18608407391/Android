package com.example.drivermodule.Component

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.bumptech.glide.request.transition.Transition
import com.example.drivermodule.R
import com.zk.library.binding.command.ViewAdapter.image.SimpleTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.uiContext


class TargetListener : SimpleTarget<Drawable> {


    constructor(img: ImageView, position: LatLng, view: View){

    }
//
//    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//        CoroutineScope(uiContext).launch {
//            img.setImageDrawable(resource)
//        }
//        Log.e("result", "图片加载成功")
//        maker = mapActivity.mAmap.addMarker(MarkerOptions().position(position).zIndex(2f)
//                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromView(view)))
//        maker?.title = it.memberName + ",0M"
//        maker?.snippet = it.teamRoleName
////                maker?.showInfoWindow()
//        markerListNumber.add(it.memberId.toString())
//        markerList.put(it.memberId.toString(), maker!!)
//        this.request?.clear()
//    }
//
//    override fun onLoadFailed(errorDrawable: Drawable?) {
//        super.onLoadFailed(errorDrawable)
//        Log.e("result", "图片加载失败")
//        img.setImageResource(R.drawable.default_avatar)
//        maker = mapActivity.mAmap.addMarker(MarkerOptions().position(position).zIndex(2f)
//                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromView(view)))
//        maker?.title = it.memberName + ",0M"
//        maker?.snippet = it.teamRoleName
////                maker?.showInfoWindow()
//        markerListNumber.add(it.memberId.toString())
//        markerList.put(it.memberId.toString(), maker!!)
//        this.request?.clear()
//    }

}