package com.elder.amoski.ViewModel

import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.chart_module.Fragment.MessageFragment
import com.cstec.administrator.social.Fragment.SocialFragment
import com.elder.amoski.Activity.HomeActivity
import com.elder.amoski.Fragment.HomeFragment
import com.elder.amoski.R
import com.elder.logrecodemodule.UI.ActivityFragment
import com.elder.logrecodemodule.UI.LogRecodeFragment
import com.elder.zcommonmodule.CALL_BACK_STATUS
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Even.ActivityResultEven
import com.zk.library.Bus.event.RxBusEven
import com.elder.zcommonmodule.Utils.Utils
import com.example.drivermodule.Fragment.MapFragment
import com.example.drivermodule.Fragment.RoadBookSearchActivity
import com.example.drivermodule.Fragment.RoadHomeActivity
import com.example.private_module.UI.UserInfoFragment
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.ServiceEven
import com.zk.library.Utils.RouterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.Utils.ConvertUtils
import java.util.ArrayList


class MainFragmentViewModel : BaseViewModel, RadioGroup.OnCheckedChangeListener, MapFragment.MapLoadedCallBack {
    override fun LoadedSuccess() {
//        bottomVisible.set(false)
    }

    var returnCheckId = 0
    var checkModel = 0
    var bottomVisible = ObservableField<Boolean>(true)
    lateinit var homeActivity: HomeFragment
    var mFragments = ArrayList<Fragment>()
    var type = 2
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    var bottomBg = ObservableField<Drawable>(context.getDrawable(R.drawable.home_bottom_bg))
    var logSelected = ObservableField<Boolean>()
    var driverSelected = ObservableField<Drawable>()
    var privateSelected = ObservableField<Boolean>()
    var curPosition = 0
    var myself: UserInfoFragment? = null
    var social: SocialFragment? = null
    var activityFragment: ActivityFragment? = null
    var messageFragment: MessageFragment? = null
    var mapFr: MapFragment? = null
    var tans: FragmentTransaction? = null
    var lastCheckediD = R.id.same_city
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.same_city -> {
                changerFragment(0)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.same_city
            }
            R.id.main_left -> {
                Utils.setStatusTextColor(true, homeActivity.activity)
                changerFragment(1)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.main_left
            }
            R.id.driver_middle -> {
                Utils.setStatusTextColor(true, homeActivity.activity)
                changerFragment(2)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.driver_middle
                checkModel = 1
            }
            R.id.dynamics -> {
                Utils.setStatusTextColor(true, homeActivity.activity)
                changerFragment(3)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.dynamics
            }
            R.id.main_right -> {
                Utils.setStatusTextColor(true, homeActivity.activity)
                changerFragment(4)
                Log.e("lastCheckediD", "returnCheckId" + returnCheckId)
                lastCheckediD = returnCheckId
                returnCheckId = R.id.main_right
                checkModel = 0
            }
        }
    }


    var mRadioGroup: RadioGroup? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun inject(homeActivity: HomeFragment) {
        this.homeActivity = homeActivity
        mRadioGroup = homeActivity.binding!!.root.findViewById(R.id.main_bottom_bg)
        returnCheckId = R.id.same_city
        initStatus()
        RxSubscriptions.add(RxBus.default?.toObservable(RxBusEven::class.java)?.subscribe {
            when (it.type) {
                RxBusEven.DriverReturnRequest -> {
                    CoroutineScope(uiContext).launch {
                        mRadioGroup!!.check(lastCheckediD)
                        bottomVisible.set(true)
                    }
                }
                RxBusEven.PartyWebViewReturn -> {
                    bottomVisible.set(it.value as Boolean)
                    if (it.secondValue as Int == 1) {
                        mRadioGroup!!.check(lastCheckediD)
                    }
                }
                RxBusEven.ENTER_TO_SEARCH -> {
//                    homeActivity.start(SearchCategoryFragment())
                    homeActivity.start((ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_SEARCH_ACTIVITY).navigation() as RoadBookSearchActivity))
                }
                RxBusEven.ENTER_TO_ROAD_HOME -> {
                    var loc = it.value as Location
                    var type = it.value2 as Int
                    homeActivity.start((ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_ACTIVITY).navigation() as RoadHomeActivity).setLocation(loc).setType(type))
                }
                RxBusEven.ACTIVE_WEB_GO_TO_APP -> {
                    returnPrivate = true
                }
                RxBusEven.NET_WORK_SUCCESS -> {
                    if (mapFr != null && mapFr!!.viewModel?.status!!.startDriver.get() != DriverCancle) {
                        var pos = ServiceEven()
                        pos.type = "HomeDriver"
                        RxBus.default?.post(pos)
                    }
                }
                RxBusEven.NET_WORK_ERROR -> {
                    if (mapFr != null && mapFr!!.viewModel?.status!!.startDriver.get() != DriverCancle) {
                        var pos = ServiceEven()
                        pos.type = "HomeStop"
                        RxBus.default?.post(pos)
                    }
                }
            }
        })
    }

    var returnPrivate = false


    private fun initStatus() {
        var home = homeActivity.activity as HomeActivity
        if (home.resume == "nomal" || home.resume.isNullOrEmpty()) {
            changerFragment(0)
        } else {
            mRadioGroup!!.check(R.id.driver_middle)
            lastCheckediD = R.id.same_city
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun changerFragment(position: Int) {
        if (!homeActivity.isAdded) {
            return
        }
        tans = homeActivity.childFragmentManager.beginTransaction()
        if (position == 0) {
            if (activityFragment == null) {
                var pos = ServiceEven()
                pos.type = "HomeStart"
                RxBus.default?.post(pos)
                activityFragment = ARouter.getInstance().build(RouterUtils.LogRecodeConfig.ACTIVITY_FRAGMENT).navigation() as ActivityFragment
                mFragments.add(activityFragment!!)
                tans?.add(R.id.main_rootlayout, activityFragment!!)
//                mapFr!!.loadMultipleRootFragment(R.id.rootLayout,0,activityFragment!!)
                if (activityFragment!!.curOffset > -ConvertUtils.dp2px(122F)) {
                    Utils.setStatusTextColor(false, homeActivity.activity as HomeActivity)
                } else {
                    Utils.setStatusTextColor(true, homeActivity.activity as HomeActivity)
                }
                var home = homeActivity.activity as HomeActivity
                if (home.resume == "nomal" || home.resume.isNullOrEmpty()) {
                    if (mapFr == null) {
                        mapFr = ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_FR).navigation() as MapFragment?
                        mFragments.add(mapFr!!)
                        mapFr?.setDriverStatus(home.resume)
                        tans!!.add(R.id.main_rootlayout, mapFr!!)
                    }
                }
            }
            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
            if (type == 2) {
                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
            } else {
                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
            }
            logSelected.set(true)
            privateSelected.set(false)
        } else if (position == 1) {
            if (social == null) {
                var bundle = Bundle()
                bundle.putParcelable("location", activityFragment!!.viewModel?.curLocation!!)
                social = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_MAIN).navigation() as SocialFragment
                social!!.arguments = bundle
                mFragments.add(social!!)
//                mapFr!!.loadMultipleRootFragment(R.id.main_rootlayout,1,social!!)
                tans!!.add(R.id.main_rootlayout, social!!)
            }
        } else if (position == 2) {
            if (mapFr == null) {
                mapFr = ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_FR).navigation() as MapFragment?
                mFragments.add(mapFr!!)
                mapFr!!.loaded = this
                var home = homeActivity.activity as HomeActivity
                mapFr?.setDriverStatus(home.resume)
                if (home.resume == "road") {
                    mapFr!!.hotData = homeActivity.hot
                }
                tans!!.add(R.id.main_rootlayout, mapFr!!)
            }
            if (mapFr!!.isAdded&&!mapFr!!.initStatus) {
                  mapFr!!.initMap()
            }

            bottomVisible.set(false)
        } else if (position == 3) {
            if (messageFragment == null) {
                var bundle = Bundle()
                bundle.putParcelable("location", activityFragment!!.viewModel?.curLocation!!)
                messageFragment = ARouter.getInstance().build(RouterUtils.Chat_Module.MESSAGE_FRAGMENT).navigation() as MessageFragment
                messageFragment!!.arguments = bundle
                mFragments.add(messageFragment!!)
                tans?.add(R.id.main_rootlayout, messageFragment!!)
//                mapFr!!.loadMultipleRootFragment(R.id.main_rootlayout,3,messageFragment!!)
            }
        } else if (position == 4) {
            if (myself == null) {
                myself = ARouter.getInstance().build(RouterUtils.FragmentPath.MYSELFPAGE).navigation() as UserInfoFragment
                mFragments.add(myself!!)
                tans?.add(R.id.main_rootlayout, myself!!)
//                homeActivity!!.loadMultipleRootFragment(R.id.main_rootlayout,4,myself!!)
            }
            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
            if (type == 2) {
                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
            } else {
                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
            }
            var fr = myself as UserInfoFragment
            fr.getUserInfo(true)
            logSelected.set(false)
            privateSelected.set(true)
        }

//        mapFr!!.showHideFragment(mFragments[curPosition],mFragments[position])
        curPosition = position

        mFragments!!.forEach {
            tans!!.hide(it)
        }
        if (position == 1) {
            tans!!.show(social!!)
        } else if (position == 2) {
            tans!!.show(mapFr!!)
        } else if (position == 3) {
            tans!!.show(messageFragment!!)
        } else if (position == 4) {
            tans!!.show(myself!!)
        } else {
            tans!!.show(mFragments!![position])
        }
        tans!!.commitAllowingStateLoss()
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.main_left -> {
                if (curPosition == 0) {
                    return
                }
                changerFragment(0)
            }
            R.id.main_right -> {
                if (curPosition == 1) {
                    return
                }
                changerFragment(1)
            }
        }
    }

    constructor() {
        var m = RxBus.default?.toObservable(ActivityResultEven::class.java)?.subscribe {
            when (it.name) {
                CALL_BACK_STATUS -> {
                    type = it.data as Int
                    if (type == 2) {
                        driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
                    } else {
                        driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
                    }
                }
            }
        }

        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ShareFinish") {
                mRadioGroup!!.check(R.id.same_city)
                var log = mFragments[0] as LogRecodeFragment
                log.loadDatas(log.viewModel?.location!!)
                type = DriverCancle
                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
                AppManager.get()?.finishOtherActivity(homeActivity.javaClass)
            } else if (it == "ToUser") {
                (homeActivity.activity as HomeActivity).runOnUiThread {
                    mRadioGroup!!.check(R.id.main_right)
                }
            }
        }
        RxSubscriptions.add(m)
        RxSubscriptions.add(s)
    }
}