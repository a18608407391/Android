package com.elder.amoski.ViewModel

import android.content.Intent
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.chart_module.Fragment.MessageFragment
import com.cstec.administrator.social.Fragment.SocialFragment
import com.elder.amoski.Activity.HomeActivity
import com.elder.amoski.R
import com.elder.logrecodemodule.UI.ActivityFragment
import com.elder.logrecodemodule.UI.LogRecodeFragment
import com.elder.zcommonmodule.CALL_BACK_STATUS
import com.elder.zcommonmodule.DriverCancle
import com.elder.zcommonmodule.Entity.DriverDataStatus
import com.elder.zcommonmodule.Even.ActivityResultEven
import com.elder.zcommonmodule.Even.BooleanEven
import com.zk.library.Bus.ServiceEven
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.Utils.getScaleUpAnimation
import com.example.private_module.UI.UserInfoFragment
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_home.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.http.NetworkUtil
import java.util.*


class HomeViewModel : BaseViewModel() {


    var statusHeight = ObservableField(0)


    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.StatusBar -> {
                statusHeight.set(it.value as Int)
            }
        }
    }

    //
//
//    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
//        when (checkedId) {
//            R.id.same_city -> {//同城
//                BaseApplication.getInstance().curFragment = 0
//                changerFragment(0)
//                lastChecked = checkedId
//            }
//            R.id.main_left -> {//发现
//                BaseApplication.getInstance().curFragment = 1
//                Utils.setStatusTextColor(true, homeActivity)
//                changerFragment(1)
//                lastChecked = checkedId
//            }
//            R.id.driver_middle -> {//骑行
//                if (!NetworkUtil.isNetworkAvailable(homeActivity)) {
//                    Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
//                    return
//                }
//                if (PreferenceUtils.getString(homeActivity, USERID) == null) {
//                    Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
//                    homeActivity.main_bottom_bg.check(lastChecked)
//                    return
//                }
//                if (type != DriverCancle) {
////                    val intent = Intent()
////                    var pos = ServiceEven()
////                    pos.type = "HomeDriver"
////                    RxBus.default?.post(pos)
////                    intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
////                    intent.setClass(homeActivity, MapActivity::class.java)
////                    intent.putExtra(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal")
////                    homeActivity.startActivity(intent)
//                } else {
//                    var pos = ServiceEven()
//                    pos.type = "HomeDriver"
//                    RxBus.default?.post(pos)
//                    ARouter.getInstance()
//                            .build(RouterUtils.MapModuleConfig.MAP_ACTIVITY)
//                            .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
//                            .withOptionsCompat(getScaleUpAnimation(homeActivity.rootlayout))
//                            .withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal")
//                            .navigation()
//                }
//                homeActivity.main_bottom_bg.check(lastChecked)
//            }
//            R.id.dynamics -> {//消息
//                BaseApplication.getInstance().curFragment = 3
//                Utils.setStatusTextColor(true, homeActivity)
//                changerFragment(2)
//                lastChecked = checkedId
//            }
//            R.id.main_right -> {//我的
//                BaseApplication.getInstance().curFragment = 4
//                Utils.setStatusTextColor(true, homeActivity)
//                changerFragment(3)
//                lastChecked = checkedId
//            }
//        }
//    }
//
//
//    var lastChecked = 0
//    var bottomVisible = ObservableField<Boolean>(true)
//
//    lateinit var homeActivity: HomeActivity
//    var mFragments = ArrayList<Fragment>()
//    var type = 2
//
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    var bottomBg = ObservableField<Drawable>(context.getDrawable(R.drawable.home_bottom_bg))
//
//    var logSelected = ObservableField<Boolean>()
//
//    var driverSelected = ObservableField<Drawable>()
//
//    var privateSelected = ObservableField<Boolean>()
//
//    var curPosition = 0
//
//
//    var myself: Fragment? = null
//    var social: SocialFragment? = null
//    var activityFragment: ActivityFragment? = null
//    var messageFragment: MessageFragment? = null
//
//    var tans: FragmentTransaction? = null
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun inject(homeActivity: HomeActivity) {
//        this.homeActivity = homeActivity
//        lastChecked = R.id.same_city
//        if (BaseApplication.getInstance().curFragment == 1) {
//            homeActivity.main_bottom_bg.check(R.id.main_left)
//        } else {
//            changerFragment(0)
//        }
//        Utils.setStatusTextColor(false, homeActivity)
//        RxSubscriptions.add(RxBus.default?.toObservable(BooleanEven::class.java)?.subscribe {
//            Log.e("home", "$it")
//            bottomVisible.set(it.flag)
//            if (it.type == 1) {
//                homeActivity.main_bottom_bg.check(R.id.main_right)
//            }
//        })
    }
//
//
//    /**
//     * @param position 0活动
//     *                 1动态
//     *                 2消息
//     *                 3我的
//     *
//     * */
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    fun changerFragment(position: Int) {
//        tans = homeActivity.supportFragmentManager.beginTransaction()
//        if (position == 0) {//活动
//            if (activityFragment == null) {
//                activityFragment = ARouter.getInstance().build(RouterUtils.LogRecodeConfig.ACTIVITY_FRAGMENT).navigation() as ActivityFragment
//                mFragments.add(activityFragment!!)
//                tans?.add(R.id.rootlayout, activityFragment!!)
//                if (activityFragment!!.curOffset > -ConvertUtils.dp2px(122F)) {
//                    Utils.setStatusTextColor(false, homeActivity)
//                } else {
//                    Utils.setStatusTextColor(true, homeActivity)
//                }
//            }
//            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
//            if (type == 2) {
//                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
//            } else {
//                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
//            }
//            logSelected.set(true)
//            privateSelected.set(false)
//
//        } else if (position == 1) {//发现
//            if (social == null) {
//                social = ARouter.getInstance()
//                        .build(RouterUtils.SocialConfig.SOCIAL_MAIN)
//                        .navigation() as SocialFragment
//                mFragments.add(social!!)
//                tans?.add(R.id.rootlayout, social!!)
//            }
//        } else if (position == 2) {//消息
//            if (messageFragment == null) {
//                messageFragment = ARouter.getInstance()
//                        .build(RouterUtils.Chat_Module.MESSAGE_FRAGMENT)
//                        .navigation() as MessageFragment
//                mFragments.add(messageFragment!!)
//                tans?.add(R.id.rootlayout, messageFragment!!)
//            }
//        } else if (position == 3) {
//            if (myself == null) {//我的
//                myself = ARouter.getInstance()
//                        .build(RouterUtils.FragmentPath.MYSELFPAGE)
//                        .navigation() as Fragment
//                mFragments.add(myself!!)
//                tans?.add(R.id.rootlayout, myself!!)
//            }
//            bottomBg.set(context.getDrawable(R.drawable.home_bottom_bg))
//            if (type == 2) {
//                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
//            } else {
//                driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
//            }
//            var fr = myself as UserInfoFragment
//            fr.getUserInfo(true)
//            logSelected.set(false)
//            privateSelected.set(true)
//        }
//        curPosition = position
//        mFragments!!.forEach {
//            tans!!.hide(it)
//        }
//
//        Log.e("result", "CurPosition$position;$messageFragment")
//        if (position == 0) {
//            tans!!.show(activityFragment!!)
//        } else if (position == 1) {//发现
//            tans!!.show(social!!)
//        } else if (position == 2) {//消息
//            tans!!.show(messageFragment!!)
//        } else if (position == 3) {
//            tans!!.show(myself!!)
//        }
//        tans!!.commitAllowingStateLoss()
//    }
//
//    fun onClick(view: View) {
//        when (view.id) {
//            R.id.main_img_src -> {
//                if (!NetworkUtil.isNetworkAvailable(homeActivity)) {
//                    Toast.makeText(context, getString(R.string.network_notAvailable), Toast.LENGTH_SHORT).show()
//                    return
//                }
//                if (type != DriverCancle) {
////                    val intent = Intent()
////                    var pos = ServiceEven()
////                    pos.type = "HomeDriver"
////                    RxBus.default?.post(pos)
////                    intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
////                    intent.setClass(homeActivity, MapActivity::class.java)
////                    intent.putExtra(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal")
////                    homeActivity.startActivity(intent)
//                } else {
////                    var flag = PreferenceUtils.getBoolean(context, SERVICE_AUTO_BOOT_COMPLETED)
////                    if (!flag || !OSUtil.checkIgnoreBattery(homeActivity)) {
////                        var dialog = DialogUtils.createNomalDialog(homeActivity, getString(R.string.checked_not_white), getString(R.string.setting_permisstion), getString(R.string.continue_driving))
////                        dialog.setOnBtnClickL(OnBtnClickL {
////                            dialog.dismiss()
////                            ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USER_SETTING).withInt(RouterUtils.PrivateModuleConfig.SETTING_CATEGORY, 0).navigation()
////                        }, OnBtnClickL {
////                            dialog.dismiss()
////                            context.startService(Intent(context, LowLocationService::class.java).setAction("driver"))
////                            ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withOptionsCompat(getScaleUpAnimation(homeActivity.rootlayout)).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal").navigation()
////                        })
////                        dialog.show()
////                    } else {
//                    var pos = ServiceEven()
//                    pos.type = "HomeDriver"
//                    RxBus.default?.post(pos)
//                    ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT).withOptionsCompat(getScaleUpAnimation(homeActivity.rootlayout)).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "nomal").navigation()
////                    }
//                }
//            }
//
//            R.id.main_left -> {
//                if (curPosition == 0) {
//                    return
//                }
//                changerFragment(0)
//            }
//            R.id.main_right -> {
//                if (curPosition == 1) {
//                    return
//                }
//                changerFragment(1)
//            }
//        }
//    }
//
//    constructor() {
//        var m = RxBus.default?.toObservable(ActivityResultEven::class.java)?.subscribe {
//            when (it.name) {
//                CALL_BACK_STATUS -> {
//                    type = it.data as Int
//                    if (type == 2) {
//                        driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
//                    } else {
//                        driverSelected.set(context.getDrawable(R.drawable.driver_nomal_icon))
//                    }
//                }
//            }
//        }
//
//        var n = RxBus.default?.toObservableSticky(DriverDataStatus::class.java)?.subscribe {
//
//        }
//        var s = RxBus.default?.toObservable(String::class.java)?.subscribe {
//            if (it == "ShareFinish") {
//                homeActivity.main_bottom_bg.check(R.id.same_city)
//                var log = mFragments[0] as LogRecodeFragment
//                log.loadDatas(log.viewModel?.location!!)
//                type = DriverCancle
//                driverSelected.set(context.getDrawable(R.drawable.black_driver_icon))
//                AppManager.get()?.finishOtherActivity(homeActivity.javaClass)
//            } else if (it == "ToUser") {
//                homeActivity.runOnUiThread {
//                    homeActivity.main_bottom_bg.check(R.id.main_right)
//                }
//            } else if (it == "ActiveWebGotoApp") {
//                returnPrivate = true
//            }
//        }
//        RxSubscriptions.add(m)
//        RxSubscriptions.add(s)
//        RxSubscriptions.add(n)
//    }
//
//
//    var returnPrivate = false
//
}