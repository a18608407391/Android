package com.example.drivermodule.ViewModel

import android.content.Intent
import android.databinding.ObservableField
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeQuery
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.amap.api.services.route.*
import com.elder.zcommonmodule.EDIT_FINAL_POINT
import com.elder.zcommonmodule.RESULT_POINT
import com.elder.zcommonmodule.RESULT_STR
import com.example.drivermodule.Activity.SearchActivity
import com.example.drivermodule.Adapter.SearchAdapter
import com.example.drivermodule.BR
import com.example.drivermodule.Entity.HistoryEntity
import com.example.drivermodule.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_search_location.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class SearchViewModel : BaseViewModel(), Inputtips.InputtipsListener, SearchAdapter.OnItemClickListener, PoiSearch.OnPoiSearchListener {
    override fun onPoiItemSearched(tip: PoiItem?, p1: Int) {


        Log.e("result", "onPoiItemSearched" + tip?.snippet)


//        if (tip?.latLonPoint != null && tip?.latLonPoint?.longitude != null && tip?.latLonPoint?.latitude != null) {
////            Log.e("result",Gson().toJson(tip))
//            var intent = Intent()
//            intent.putExtra("tip", tip)
//            if (searchActivity.model == 0) {
//                searchActivity.setResult(RESULT_POINT, intent)
//            } else if (searchActivity.model == 1) {
//                searchActivity.setResult(RESULT_STR, intent)
//            } else if (searchActivity.model == 3) {
//                searchActivity.setResult(EDIT_FINAL_POINT, intent)
//            }
//            searchActivity.finish()
//        }
    }

    override fun onPoiSearched(p0: PoiResult?, p1: Int) {
        Log.e("result","onPoiSearched" + "p1")
        mList.clear()
        if (p0?.pois.isNullOrEmpty()) {
            var inputquery = InputtipsQuery(curInput, "")
            var tip = Inputtips(searchActivity, inputquery)
            tip.setInputtipsListener(this@SearchViewModel)
            tip.requestInputtipsAsyn()
        } else {
            p0?.pois?.forEach {
                mList.add(it)
            }
            adapter.setDatas(mList)
        }
    }

    override fun itemClick(view: View, tip: PoiItem) {
        if (tip != null) {
            if (tip.latLonPoint != null && tip.latLonPoint.longitude != null && tip.latLonPoint.latitude != null) {
                var intent = Intent()
                intent.putExtra("tip", tip)
                if (searchActivity.model == 0) {
                    searchActivity.setResult(RESULT_POINT, intent)
                } else if (searchActivity.model == 1) {
                    searchActivity.setResult(RESULT_STR, intent)
                } else if (searchActivity.model == 3) {
                    searchActivity.setResult(EDIT_FINAL_POINT, intent)
                }
                searchActivity.finish()
            }
        }
    }

    var mList: ArrayList<PoiItem> = ArrayList()
    override fun onGetInputtips(p0: MutableList<Tip>?, p1: Int) {
        p0?.forEach {
            Log.e("result", "input搜索名字" + it.name + "地址" + it.address + "街道" + it.district)
//            mList.add(it)
            var point = it.point
            var item = PoiItem("123", point, it.name, it.district + it.address)
            mList.add(item)

        }
        adapter.setDatas(mList)
//        adapter.setDatas(mList)
    }

    var historyEmpty = ObservableField<Boolean>(true)
    lateinit var adapter: SearchAdapter
    lateinit var searchActivity: SearchActivity
    fun inject(searchActivity: SearchActivity) {
        this.searchActivity = searchActivity
        adapter = SearchAdapter(searchActivity)
        adapter.setOnItemClickListener(this)
        var layout = LinearLayoutManager(searchActivity)
        searchActivity.recycle.layoutManager = layout
        searchActivity.recycle.adapter = adapter
        if (searchActivity?.model == 3) {

        }
    }


    var curInput = ""
    var textChange = BindingCommand(object : BindingConsumer<String> {

        override fun call(t: String) {
            Log.e("result", t)
            this@SearchViewModel.curInput = t
            var inputQuery = PoiSearch.Query(t, "")
            inputQuery.pageSize = 10
            var tips = PoiSearch(searchActivity, inputQuery)
            tips.setOnPoiSearchListener(this@SearchViewModel)
            tips.searchPOIAsyn()

        }
    })


    fun onClick(view: View) {
        searchActivity.finish()
    }

}