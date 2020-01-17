package com.example.drivermodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.elder.zcommonmodule.Utils.DialogUtils
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.NavigationViewModel
import com.example.drivermodule.databinding.ActivityNavigationBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseApplication
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_navigation.*

@Route(path = RouterUtils.MapModuleConfig.NAVIGATION)
class NavigationActivity : BaseActivity<ActivityNavigationBinding, NavigationViewModel>() {
    @Autowired(name = RouterUtils.MapModuleConfig.NAVIGATION_DATA)
    @JvmField
    var list: ArrayList<LatLng>? = null

    @Autowired(name = RouterUtils.MapModuleConfig.NAVIGATION_TYPE)
    @JvmField
    var type: Int = 0

    @Autowired(name = RouterUtils.MapModuleConfig.NAVIGATION_DISTANCE)
    @JvmField
    var distance: Float = 0F


    @Autowired(name = RouterUtils.MapModuleConfig.NAVIGATION_TIME)
    @JvmField
    var duration: Long = 0L

    var onStart:Boolean = false
    override fun initVariableId(): Int {
        return BR.navigation_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        StatusbarUtils.fullScreen(this)
        return R.layout.activity_navigation
    }

    override fun initViewModel(): NavigationViewModel? {
        return ViewModelProviders.of(this)[NavigationViewModel::class.java]
    }

    override fun setMap(savedInstanceState: Bundle?) {
        super.setMap(savedInstanceState)
        anavi_view.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        anavi_view.onSaveInstanceState(outState)
    }

    override fun initViewObservable() {
        super.initViewObservable()
        mViewModel?.inject(this)
    }

    override fun onResume() {
        super.onResume()
        anavi_view.onResume()
        BaseApplication.getInstance().curActivity = 3
        onStart = true
    }




    fun createTeamerNotify() {
        DialogUtils.createNomalDialog(this@NavigationActivity, getString(R.string.checkNavigation), getString(R.string.cover_navigation),getString(R.string.continue_navigation))
    }


    override fun onPause() {
        super.onPause()
        anavi_view.onPause()
        onStart = false

        //
        //        停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
        //        mAMapNavi.stopNavi();
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("result", "导航页面死掉了")
        anavi_view.onDestroy()
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        mViewModel?.mAMapNavi?.stopNavi()
        mViewModel?.mAMapNavi?.destroy()
    }

    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).navigation()
        if (type != 1) {
            finish()
        }
    }
}