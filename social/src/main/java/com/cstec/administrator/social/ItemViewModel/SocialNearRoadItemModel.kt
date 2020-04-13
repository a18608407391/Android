package com.cstec.administrator.social.ItemViewModel

import android.databinding.ObservableArrayList
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.cstec.administrator.social.Adapter.StrageAdapter
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.SocialViewModel
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.REQUEST_DISCOVER_LOAD_ROADBOOK
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.ItemViewModel
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.DecimalFormat
import me.tatarka.bindingcollectionadapter2.BR

/***
 * 附近路书 Item-ViewModel
 * */
class SocialNearRoadItemModel : ItemViewModel<SocialViewModel>, HttpInteface.getRoadBookList {

    var socialViewModel: SocialViewModel
    var page = 1
    var adapter = StrageAdapter()//adapter
    var items = ObservableArrayList<HotData>()//item
    var itemBinding = ItemBinding.of<HotData> { itemBinding, position, item ->
        itemBinding.set(BR.hot_data, R.layout.social_near_recy_item_layout)
                .bindExtra(BR.social_near_item_model, this@SocialNearRoadItemModel)
    }
    var curLoad = 0
    var loadMore = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            if (t > curLoad) {
                page++
                initDatas(page)
                curLoad = t
            }
        }
    })

    constructor(socialViewModel: SocialViewModel) {
        this.socialViewModel = socialViewModel
    }

    override fun initDatas(t: Int) {
        super.initDatas(t)
        socialViewModel.socialFragment.showProgressDialog(getString(R.string.http_loading_roadbook))
        HttpRequest.instance.RoadBookGuideList = this
        Log.e("roadbook", "page:$page")
        var map = HashMap<String, String>()
        map["limit"] = "10"
        map["page"] = page.toString()
        map["lat"] = socialViewModel.location!!.latitude.toString()
        map["lng"] = socialViewModel.location!!.longitude.toString()
        map["orderByType"] = "2"
        HttpRequest.instance.getRoadBookList(map)
    }

    override fun getRoadBookSuccess(it: String) {
        Log.e(this.javaClass.name,"$it")
        socialViewModel.socialFragment.dismissProgressDialog()
        socialViewModel.refreshLayout.finishRefresh(true)
        var list = Gson().fromJson<ArrayList<HotData>>(it, object : TypeToken<ArrayList<HotData>>() {}.type)
        Log.e(this.javaClass.name,"$list")
        if (list.isNullOrEmpty()) {
            return
        }
        if (page == 1) {
            items.clear()
        }
        Log.e("result", list.size.toString() + "当前集合长度")
        list.forEach {
            var distance = AMapUtils.calculateLineDistance(LatLng(socialViewModel.location!!.latitude, socialViewModel!!.location!!.longitude), LatLng(it.lat, it.lng))
            it.distance = distance
            if (it.distance < 1000) {
                it.distanceTv = DecimalFormat("0.0").format(it.distance) + "M"
            } else {
                it.distanceTv = DecimalFormat("0.0").format(it.distance / 1000) + "KM"
            }
            items.add(it)
        }
        items.sortBy {
            it.distance
        }
    }

    override fun getRoadBookError(ex: Throwable) {
        socialViewModel.refreshLayout.finishRefresh(true)
        socialViewModel.socialFragment.dismissProgressDialog()
    }

    fun ItemClickCommand(data: HotData) {
        ARouter.getInstance()
                .build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY)
                .withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY, data)
                .withString("type", "discover2RoadBook")
                .navigation(socialViewModel.socialFragment.activity, REQUEST_DISCOVER_LOAD_ROADBOOK)
    }

}