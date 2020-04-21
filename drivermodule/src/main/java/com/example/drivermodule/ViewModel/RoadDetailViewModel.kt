package com.example.drivermodule.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.road.Road
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.ROAD_DETAIL_RETURN_VALUE
import com.example.drivermodule.Activity.RoadDetailActivity
import com.example.drivermodule.BR
import com.example.drivermodule.Entity.RoadDetailEntity
import com.example.drivermodule.R
import com.example.drivermodule.Utils.ThreadUtil
import com.zk.library.Base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.Utils.ConvertUtils
import java.text.DecimalFormat
import java.util.ArrayList


class RoadDetailViewModel : BaseViewModel(), GeocodeSearch.OnGeocodeSearchListener, TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        roadDetailActivity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {
        Log.e("result", "onRegeocodeSearched执行了")
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
    }

    lateinit var roadDetailActivity: RoadDetailActivity
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun inject(roadDetailActivity: RoadDetailActivity) {
        this.roadDetailActivity = roadDetailActivity
        var geocoderSearch = GeocodeSearch(roadDetailActivity.activity)
        component.title.set(getString(R.string.road_detail))
        component.arrowVisible.set(false)
        component.rightText.set("")
        component.setCallBack(this)

        var fomat = ""
        if (roadDetailActivity.time < 60) {
            fomat = "秒钟"
            time.set(roadDetailActivity.time.toString() + fomat)
        } else if (roadDetailActivity.time in 61..3599) {
            fomat = "分钟"
            time.set((roadDetailActivity.time / 60).toInt().toString() + fomat)
        } else {
            fomat = "小时"
            time.set(DecimalFormat("0.0").format(roadDetailActivity.time / 3600) + fomat)
        }
        if (roadDetailActivity.distance < 1000) {
            distance.set(roadDetailActivity.distance.toString() + "米")
        } else {
            distance.set(DecimalFormat("0.0").format(roadDetailActivity.distance / 1000) + "千米")
        }


        geocoderSearch.setOnGeocodeSearchListener(this)
        roadDetailActivity.showProgressDialog(getString(R.string.loading_disrct))
        Observable.just("").subscribeOn(Schedulers.io()).map(Function<String, ArrayList<RoadDetailEntity>> {
            var list = ArrayList<RoadDetailEntity>()
            roadDetailActivity.data!!.forEachIndexed { index, latLonPoint ->
                var query = RegeocodeQuery(latLonPoint, 100F,
                        GeocodeSearch.AMAP)// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                var result = geocoderSearch.getFromLocation(query)// 设置同步逆地理编码请求
                var drawable: Drawable
                if (index == 0) {
                    drawable = context.getDrawable(R.drawable.myself_all)
                } else if (index == (roadDetailActivity.data?.size!! - 1)) {
                    drawable = context.getDrawable(R.drawable.select_point)
                } else {
                    drawable = when (index) {
                        1 -> {
                            context.getDrawable(R.drawable.lable_one)
                        }
                        2 -> {
                            context.getDrawable(R.drawable.lable_two)
                        }
                        3 -> {
                            context.getDrawable(R.drawable.lable_three)
                        }
                        4 -> {
                            context.getDrawable(R.drawable.lable_four)
                        }
                        5 -> {
                            context.getDrawable(R.drawable.lable_five)
                        }
                        6 -> {
                            context.getDrawable(R.drawable.lable_six)
                        }
                        7 -> {
                            context.getDrawable(R.drawable.lable_seven)
                        }
                        8 -> {
                            context.getDrawable(R.drawable.lable_eight)
                        }
                        9 -> {
                            context.getDrawable(R.drawable.lable_nine)
                        }
                        10 -> {
                            context.getDrawable(R.drawable.lable_ten)
                        }
                        11 -> {
                            context.getDrawable(R.drawable.lable_eleven)
                        }
                        12 -> {
                            context.getDrawable(R.drawable.lable_twelve)
                        }
                        13 -> {
                            context.getDrawable(R.drawable.lable_thirteen)
                        }
                        14 -> {
                            context.getDrawable(R.drawable.lable_fourteen)
                        }
                        15 -> {
                            context.getDrawable(R.drawable.lable_fiveteen)
                        }
                        16 -> {
                            context.getDrawable(R.drawable.lable_sixteen)
                        }
                        else -> {
                            context.getDrawable(R.drawable.lable_nine)
                        }
                    }
                }
                var entity = RoadDetailEntity(ObservableField(drawable), ObservableField(if (result.aois.size == 0) result.formatAddress else result.aois[0].aoiName), ObservableField(result.formatAddress))
                entity.setIndex(index)
                list.add(entity)
            }
            return@Function list
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            it.forEach {
                items.add(it)
            }
            items.sortBy {
                it.getIndex()
            }
            roadDetailActivity.dismissProgressDialog()
        }
    }

    var items = ObservableArrayList<RoadDetailEntity>()

    var adapter = BindingRecyclerViewAdapter<RoadDetailEntity>()

    var itemBinding = ItemBinding.of<RoadDetailEntity>(BR.detail, R.layout.detail_item)

    var viewHolder = BindingRecyclerViewAdapter.ViewHolderFactory { binding -> HomeViewHolder(binding.root) }

    private class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    var component = TitleComponent()


    var time = ObservableField<String>()

    var distance = ObservableField<String>()

    fun onClick(view: View) {
//        RxBus.default?.post("startNavigation")
//        finish()

        var bundle = Bundle()
        bundle.putInt("navigation", 1)
        roadDetailActivity.setFragmentResult(ROAD_DETAIL_RETURN_VALUE, bundle)
        roadDetailActivity._mActivity!!.onBackPressedSupport()
    }



}