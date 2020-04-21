package com.example.drivermodule.Fragment

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBook.AcRoadBookViewModel
import com.example.drivermodule.databinding.ActivityRoadbookBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY)
class RoadHomeActivity : BaseFragment<ActivityRoadbookBinding, AcRoadBookViewModel>() {

    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.MapModuleConfig.ROAD_CURRENT_TYPE)
    @JvmField
    var type: Int = 0

    override fun initVariableId(): Int {
        return BR.roadbook_viewmodel
    }

    override fun initContentView(): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_roadbook
    }
//
//    override fun initViewModel(): AcRoadBookViewModel? {
//        return ViewModelProviders.of(this)[AcRoadBookViewModel::class.java]
//    }

    override fun initData() {
        super.initData()
        location = arguments!!.getSerializable(RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT) as Location?
        type = arguments!!.getInt(RouterUtils.MapModuleConfig.ROAD_CURRENT_TYPE)
        mTabLayout = binding!!.root.findViewById(R.id.mTabLayout)
        road_book_viewpager = binding!!.root.findViewById(R.id.road_book_viewpager)
        swipe = binding!!.root.findViewById(R.id.swipe)
        road_book_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTabLayout))
        mTabLayout.setupWithViewPager(road_book_viewpager)
        swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swipe.setOnRefreshListener(viewModel)
        viewModel?.inject(this)
    }


    lateinit var mTabLayout: TabLayout
    lateinit var road_book_viewpager: ViewPager
    lateinit var swipe: SwipeRefreshLayout
    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)
    }

    fun setLocation(location: Location): RoadHomeActivity {
        this.location = location
        var bundle = Bundle()
        bundle.putSerializable("location", location)
        this.arguments = bundle
        return this@RoadHomeActivity
    }

    fun setType(type: Int): RoadHomeActivity {
        this.type = type
        return this@RoadHomeActivity
    }


    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        Log.e("result", "home" + requestCode)
        if (requestCode == REQUEST_LOAD_ROADBOOK) {
            if (data?.getSerializable("hotdata") != null) {
                setFragmentResult(REQUEST_LOAD_ROADBOOK, data)
                _mActivity!!.onBackPressedSupport()
//                setResult(REQUEST_LOAD_ROADBOOK, data)
//                finish()
            } else {
//                _mActivity!!.onBackPressedSupport()
            }
        }
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)

//    }
//
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }

}