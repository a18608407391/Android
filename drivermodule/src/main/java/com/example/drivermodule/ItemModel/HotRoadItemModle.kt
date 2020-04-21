package com.example.drivermodule.ItemModel

import android.databinding.ObservableArrayList
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.drivermodule.BR
import com.elder.zcommonmodule.Entity.HotData
import com.example.drivermodule.Adapter.StrageAdapter
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBook.AcRoadBookViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.elder.zcommonmodule.Component.ItemViewModel
import com.elder.zcommonmodule.Widget.RoadBook.HotBannerData
import com.example.drivermodule.Fragment.RoadBookFirstActivity
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class HotRoadItemModle : ItemViewModel<AcRoadBookViewModel>, HttpInteface.getRoadBookList {

    override fun getRoadBookSuccess(it: String) {
        acRoadBookViewModel?.roadHomeActivity.dismissProgressDialog()
        acRoadBookViewModel.roadHomeActivity.swipe.isRefreshing = false
        if (it.isNullOrEmpty()) {
            return
        }
        var list = Gson().fromJson<ArrayList<HotData>>(it, object : TypeToken<ArrayList<HotData>>() {}.type)
        if (list.isNullOrEmpty()) {
            return
        }
        if (page == 1) {
            items.clear()
        }
        list.forEach {
            items.add(it)
        }
        if (acRoadBookViewModel.roadHomeActivity.type == 1 && category) {
            category = false
            acRoadBookViewModel.roadHomeActivity.road_book_viewpager.currentItem = 1
        }
    }


    var category = true
    override fun getRoadBookError(ex: Throwable) {
        viewModel?.roadHomeActivity.dismissProgressDialog()
        acRoadBookViewModel.roadHomeActivity.swipe.isRefreshing = false
    }


    fun ItemClickCommand(date: HotData) {
//        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY, date).navigation(acRoadBookViewModel.roadHomeActivity.activity, REQUEST_LOAD_ROADBOOK)
        var bundle = Bundle()
        bundle.putSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY,date)
        acRoadBookViewModel.startFragment(acRoadBookViewModel.roadHomeActivity,RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY,bundle,REQUEST_LOAD_ROADBOOK)
//        acRoadBookViewModel.roadHomeActivity._mActivity!!.startForResult((ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).navigation() as RoadBookFirstActivity).setHotData(date),REQUEST_LOAD_ROADBOOK)

//        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY,date).navigation(acRoadBookViewModel.roadHomeActivity,REQUEST_LOAD_ROADBOOK)

//        Log.e("result","传值"+Gson().toJson(date))
//        var intent = Intent()
//        intent.putExtra("hotdata", date)
//        acRoadBookViewModel.roadHomeActivity.setResult(REQUEST_LOAD_ROADBOOK,intent)
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
        map["orderByType"] = "1"
        HttpRequest.instance.getRoadBookList(map)
    }

    var adapter = StrageAdapter()

    var items = ObservableArrayList<HotData>()

    var itemBinding = ItemBinding.of<HotData> { itemBinding, position, item ->
        itemBinding.set(BR.hot_data, R.layout.hot_recy_item_layout).bindExtra(BR.hot_item_model, this@HotRoadItemModle)
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