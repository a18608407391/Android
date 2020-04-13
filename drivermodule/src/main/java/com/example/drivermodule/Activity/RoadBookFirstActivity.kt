package com.example.drivermodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearSnapHelper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.maps.AMap
import com.amap.api.maps.model.Marker
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.drivermodule.BR
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.getRoadImgUrl
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBook.RoadBookFirstViewModel
import com.example.drivermodule.databinding.ActivityRoadbookFirstBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_roadbook_first.*
import kotlinx.android.synthetic.main.fragment_road_book.*
import org.cs.tec.library.Utils.ConvertUtils


@Route(path = RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY)
class RoadBookFirstActivity : BaseActivity<ActivityRoadbookFirstBinding, RoadBookFirstViewModel>(), AMap.InfoWindowAdapter, AMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?): Boolean {
        mViewModel?.markerChange(p0)
        return false
    }

    override fun getInfoContents(maker: Marker?): View {
        var view = layoutInflater?.inflate(R.layout.roadbook_popuwindow_custom1, null, false)
        var list = maker!!.snippet.split(";")
        var img = view!!.findViewById<ImageView>(R.id.left_img)
        var top = view!!.findViewById<TextView>(R.id.top_tv)
        var bottom = view!!.findViewById<TextView>(R.id.bottom_tv)
        var corner = RoundedCorners(ConvertUtils.dp2px(5F))
        var opition = RequestOptions().transform(corner).error(R.drawable.artboard).override(ConvertUtils.dp2px(32F), ConvertUtils.dp2px(32F))
        Glide.with(img).asDrawable().apply(opition).load(getRoadImgUrl(list[0])).into(img)
        top.text = list[1]
        if (list[2].isNullOrEmpty() || list[2] == "null") {
            bottom.text = ""
        } else {
            bottom.text = list[2]
        }
        return view!!
    }

    override fun getInfoWindow(maker: Marker?): View {
        var view = layoutInflater?.inflate(R.layout.roadbook_popuwindow_custom1, null, false)
        var list = maker!!.snippet.split(";")
        var img = view!!.findViewById<ImageView>(R.id.left_img)
        var top = view!!.findViewById<TextView>(R.id.top_tv)
        var bottom = view!!.findViewById<TextView>(R.id.bottom_tv)
        var corner = RoundedCorners(ConvertUtils.dp2px(5F))
        var opition = RequestOptions().transform(corner).error(R.drawable.artboard).override(ConvertUtils.dp2px(32F), ConvertUtils.dp2px(32F))
        Glide.with(img).asDrawable().apply(opition).load(getRoadImgUrl(list[0])).into(img)
        top.text = list[1]
        if (list[2].isNullOrEmpty() || list[2] == "null") {
            bottom.text = ""
        } else {
            bottom.text = list[2]
        }
        return view!!
    }


    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY)
    @JvmField
    var data: HotData? = null

    @Autowired(name = "type")
    @JvmField
    var type: String? = null

    override fun initVariableId(): Int {
        return BR.roadbook_first_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_roadbook_first
    }

    override fun initViewModel(): RoadBookFirstViewModel? {
        return ViewModelProviders.of(this)[RoadBookFirstViewModel::class.java]
    }

    override fun initMap(savedInstanceState: Bundle?) {
        super.initMap(savedInstanceState)
        first_map.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        first_map.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        first_map.onPause()
    }

    override fun onResume() {
        super.onResume()
        first_map.onResume()
    }


    lateinit var mAmap: AMap
    var behavior_by_routes: BottomSheetBehavior<LinearLayout>? = null
    override fun initData() {
        super.initData()
        mAmap = first_map.map
        mAmap.setInfoWindowAdapter(this)
        mAmap.setOnMarkerClickListener(this)
        behavior_by_routes = BottomSheetBehavior.from<LinearLayout>(behavior_by_rout)
        behavior_by_routes!!.state = BottomSheetBehavior.STATE_COLLAPSED
        LinearSnapHelper().attachToRecyclerView(first_bottom_pager)
        first_roadbook_recycle.isNestedScrollingEnabled = true
        mViewModel?.inject(this)
    }


    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

}