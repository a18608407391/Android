package com.example.drivermodule.ItemModel

import android.databinding.ObservableArrayList
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.drivermodule.BR
import com.example.drivermodule.Entity.RoadBook.HotBannerData
import com.elder.zcommonmodule.Entity.HotData
import com.example.drivermodule.Adapter.StrageAdapter
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBook.AcRoadBookViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import com.zk.library.Base.ItemViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_roadbook.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.DecimalFormat


class NearRoadItemModle : ItemViewModel<AcRoadBookViewModel>, HttpInteface.getRoadBookList {
    override fun getRoadBookSuccess(it: String) {
        acRoadBookViewModel.roadHomeActivity.swipe.isRefreshing = false
        acRoadBookViewModel?.roadHomeActivity.dismissProgressDialog()
        var list = Gson().fromJson<ArrayList<HotData>>(it, object : TypeToken<ArrayList<HotData>>() {}.type)
        if(list.isNullOrEmpty()){
            return
        }
        if (page == 1) {
            items.clear()
        }

        Log.e("result", list.size.toString() + "当前集合长度")
        list.forEach {
            var distance = AMapUtils.calculateLineDistance(LatLng(acRoadBookViewModel.roadHomeActivity.location!!.latitude, acRoadBookViewModel.roadHomeActivity!!.location!!.longitude), LatLng(it.lat, it.lng))
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
        viewModel?.roadHomeActivity.dismissProgressDialog()
        acRoadBookViewModel.roadHomeActivity.swipe.isRefreshing = false
    }

    fun ItemClickCommand(data: HotData) {
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY, data).navigation(acRoadBookViewModel.roadHomeActivity, REQUEST_LOAD_ROADBOOK)

//        var intent = Intent()
//        intent.putExtra("hotdata", data)
//        acRoadBookViewModel.roadHomeActivity.setResult(REQUEST_LOAD_ROADBOOK, intent)
//        acRoadBookViewModel.roadHomeActivity.finish()
    }

     var acRoadBookViewModel: AcRoadBookViewModel

    constructor(acRoadBookViewModel: AcRoadBookViewModel, i: Int) {
        this.acRoadBookViewModel = acRoadBookViewModel
    }

    var page = 1
    override fun initDatas(i: Int) {
        super.initDatas(i)
        acRoadBookViewModel?.roadHomeActivity.showProgressDialog(getString(R.string.http_loading_roadbook))
        HttpRequest.instance.RoadBookGuideList = this
        var map = HashMap<String, String>()
        map["limit"] = "10"
        map["page"] = page.toString()
        map["lat"] = acRoadBookViewModel.roadHomeActivity.location!!.latitude.toString()
        map["lng"] = acRoadBookViewModel.roadHomeActivity.location!!.longitude.toString()
        map["orderByType"] = "2"
        HttpRequest.instance.getRoadBookList(map)
    }


    var adapter = StrageAdapter()

    var items = ObservableArrayList<HotData>()

    var itemBinding = ItemBinding.of<HotData> { itemBinding, position, item ->
        itemBinding.set(BR.hot_data, R.layout.near_recy_item_layout).bindExtra(BR.near_item_model, this@NearRoadItemModle)
    }

    var listDatas = ObservableArrayList<HotBannerData>()

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
}