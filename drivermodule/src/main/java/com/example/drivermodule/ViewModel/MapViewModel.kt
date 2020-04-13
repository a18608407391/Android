package com.example.drivermodule.ViewModel

import android.databinding.ObservableField
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.Marker
import com.elder.zcommonmodule.Component.DriverComponent
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.example.drivermodule.Activity.MapActivity
import com.elder.zcommonmodule.Entity.HotData
import com.example.drivermodule.R
import com.example.drivermodule.Ui.DriverFragment
import com.example.drivermodule.Ui.MapPointFragment
import com.example.drivermodule.Ui.RoadBookFragment
import com.example.drivermodule.Ui.TeamFragment
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_road_book.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID


class MapViewModel : BaseViewModel(), AMap.OnMarkerClickListener, AMap.OnMarkerDragListener, AMap.OnCameraChangeListener, TitleComponent.titleComponentCallBack, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>, DriverComponent.onFiveClickListener {

    override fun FiveBtnClick(view: View) {
        mapActivity.getDrverFragment().viewModel?.FiveBtnClick(view)
        if (currentPosition == 0) {
        } else if (currentPosition == 2) {
//            mapActivity.getMapPointFragment().viewModel?.FiveBtnClick(view)
        } else if (currentPosition == 3) {
//            mapActivity.getRoadBookFragment().viewModel?.FiveBtnClick(view)
        }
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        if (p0!!.position == currentPosition) {
            return
        }
        if (p0!!.position == 2 && currentPosition == 3) {
            return
        }
        when (p0?.position) {
            0 -> {
                if (currentPosition == 1) {
                    mapActivity.getTeamFragment().viewModel?.backToDriver()

                } else if (currentPosition == 3) {
                    mapActivity.getRoadBookFragment().viewModel?.backToDriver()
                    changerFragment(0)
                }
            }
            1 -> {
                if (currentPosition == 0) {
                    //跳转到组队
                    mapActivity.getDrverFragment().viewModel?.GoTeam()
                } else if (currentPosition == 3) {
                    mapActivity.getRoadBookFragment().viewModel?.backToDriver()
                    mapActivity.getDrverFragment().viewModel?.GoTeam()
                }
            }
            2 -> {
                if (currentPosition == 0 || currentPosition == 1) {
                    //跳转到路书
                    if (currentPosition == 1) {
                        mapActivity.getTeamFragment().viewModel?.backToRoad()
                    }
                    if (mapActivity.getRoadBookFragment().viewModel?.netWorkData == null) {
                        var date = PreferenceUtils.getString(mapActivity, PreferenceUtils.getString(context, USERID) + "hot")
                        if (date == null && mapActivity.hotData == null) {
                            if (mapActivity.getDrverFragment().curPosition != null) {
                                ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY).withInt(RouterUtils.MapModuleConfig.ROAD_CURRENT_TYPE, 1).withSerializable(RouterUtils.MapModuleConfig.ROAD_CURRENT_POINT, mapActivity.getDrverFragment().curPosition).navigation(mapActivity, REQUEST_LOAD_ROADBOOK)
                            }
                        } else {
                            if (date == null) {
                                mapActivity.getRoadBookFragment().viewModel?.data = mapActivity.hotData
                            } else {
                                mapActivity.getRoadBookFragment().viewModel?.data = Gson().fromJson<HotData>(date, HotData::class.java)
                            }
                            changerFragment(3)
                            mapActivity.getRoadBookFragment().viewModel?.doLoadDatas(mapActivity.getRoadBookFragment().viewModel?.data!!)

                        }
                    } else {
                        changerFragment(3)
                        if (mapActivity.getRoadBookFragment().road_tab.selectedTabPosition != 0) {
                            mapActivity.getRoadBookFragment().viewModel!!.selectTab(0)
                        } else {
                            mapActivity
                                    .getDrverFragment()?.viewModel?.recycleComponent?.initDatas(mapActivity.getRoadBookFragment().viewModel?.netWorkData!!, mapActivity.getRoadBookFragment().viewModel?.data, 0)
                        }
                    }
                }
            }
        }
    }


    var cur = 0L

    override fun onComponentClick(view: View) {


        if (currentPosition == 0) {
            mapActivity.getDrverFragment().viewModel?.onComponentClick(view)
        } else if (currentPosition == 1) {
            mapActivity.getTeamFragment().viewModel?.onComponentClick(view)
        } else if (currentPosition == 2) {
            mapActivity.getMapPointFragment().viewModel?.onComponentClick(view)
        } else if (currentPosition == 3) {
            mapActivity.getRoadBookFragment().viewModel?.onComponentClick(view)
        }
    }

    override fun onComponentFinish(view: View) {
        if (currentPosition == 0) {
            mapActivity.getDrverFragment().viewModel?.onComponentFinish(view)
        } else if (currentPosition == 1) {
            mapActivity.getTeamFragment().viewModel?.onComponentFinish(view)
        } else if (currentPosition == 2) {
            mapActivity.getMapPointFragment().viewModel?.onComponentFinish(view)
        } else if (currentPosition == 3) {
            mapActivity.getRoadBookFragment().viewModel?.onComponentFinish(view)
        }
    }


    override fun onCameraChangeFinish(p0: CameraPosition?) {
        RxBus.default?.post("onCameraChangeFinish")
    }

    override fun onCameraChange(p0: CameraPosition?) {
    }

    override fun onMarkerDragEnd(p0: Marker?) {
    }

    override fun onMarkerDragStart(p0: Marker?) {
    }

    override fun onMarkerDrag(p0: Marker?) {
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        when (currentPosition) {
            3 -> {
                mapActivity.getRoadBookFragment().viewModel?.markerChange(p0)
            }
        }
        return false
    }


    var component = DriverComponent()
    var showBottomSheet = ObservableField<Boolean>(false)
    var mFragments = ArrayList<Fragment>()
    lateinit var mapActivity: MapActivity
    var a: Disposable? = null
    fun inject(mapActivity: MapActivity) {
        this.mapActivity = mapActivity
        var fr = ARouter.getInstance().build(RouterUtils.MapModuleConfig.DRIVER_FR).navigation() as DriverFragment
        var team = ARouter.getInstance().build(RouterUtils.MapModuleConfig.TEAM_FR).navigation() as TeamFragment
        var point = ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_POINT_FR).navigation() as MapPointFragment
        var ro = ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROUTE_BOOK_FR).navigation() as RoadBookFragment
        mFragments.add(fr)
        mFragments.add(team)
        mFragments.add(point)
        mFragments.add(ro)
        mFragments.forEach {
        }
        var tans = mapActivity.supportFragmentManager.beginTransaction()
        tans.add(R.id.main_rootlay, fr)
        tans.add(R.id.main_rootlay, team)
        tans.add(R.id.main_rootlay, point)
        tans.add(R.id.main_rootlay, ro)
        tans.commitAllowingStateLoss()
        a = RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            fr.loacation(it)
        }
        RxSubscriptions.add(a)
        component.setCallBack(this)
        component.setOnFiveClickListener(this)
        initTab()
        changerFragment(0)
    }

    lateinit var tab: TabLayout
    private fun initTab() {
        tab = mapActivity.findViewById<TabLayout>(R.id.topTab)
        tab.addTab(tab.newTab().setText(getString(R.string.driver)))
        tab.addTab(tab.newTab().setText(getString(R.string.team)))
        tab.addTab(tab.newTab().setText(getString(R.string.road_book_nomal_title)))
        tab.addOnTabSelectedListener(this)
    }

    fun selectTab(position: Int) {
        var tabs = tab.getTabAt(position)
        tabs?.select()
        Log.e("result", "selectTab已经执行")
    }

    var currentPosition = 0

    fun changerFragment(position: Int) {
        currentPosition = position
        if (position == 3) {
            component.Drivering.set(false)
            component.rightIcon.set(context.getDrawable(R.drawable.three_point))
        } else {
            component.Drivering.set(true)
            component.rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
        }
        if (position == 2) {
            component.titleVisible.set(true)
            tab.visibility = View.GONE
            component.rightVisibleType.set(true)
            component.title.set(getString(R.string.location_select))
            component.rightText.set(getString(R.string.road_detail))
            component.type.set(0)
        } else {
            component.titleVisible.set(false)
            component.rightVisibleType.set(false)
            component.title.set("")
            tab.visibility = View.VISIBLE
            component.rightText.set("")
            component.type.set(1)
        }
        var bt = mapActivity.supportFragmentManager.beginTransaction()
        mFragments!!.forEach {
            bt.hide(it)
        }
        bt.show(mFragments!![position])
        bt.commitAllowingStateLoss()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.share_btn -> {
                var fr = mapActivity.getDrverFragment()
                showBottomSheet.set(false)
                RxBus.default?.postSticky(fr?.viewModel?.share!!)
                fr.behaviors.isHideable = true
                fr.behaviors.state = BottomSheetBehavior.STATE_EXPANDED
                a?.dispose()
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.SHARE_ACTIVITY).navigation(mapActivity, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            }
        }
    }
}