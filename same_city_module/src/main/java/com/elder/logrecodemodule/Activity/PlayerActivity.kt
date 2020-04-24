package com.elder.logrecodemodule.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.maps.AMap
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.ViewModel.PlayerViewModel
import com.elder.logrecodemodule.databinding.ActivityPlayerBinding
import com.elder.zcommonmodule.Entity.UIdeviceInfo
import com.elder.zcommonmodule.Service.Screen.ScreenUtil
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.Transaction.ISupportFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_player.*
import org.cs.tec.library.Bus.RxSubscriptions


@Route(path = RouterUtils.LogRecodeConfig.PLAYER)
class PlayerActivity : BaseFragment<ActivityPlayerBinding, PlayerViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_player
    }

    override fun initVariableId(): Int {
        return BR.player
    }


    var imgs: UIdeviceInfo? = null

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, false, 0x00000000)
//        return R.layout.activity_player
//    }

//    override fun initViewModel(): PlayerViewModel? {
//        return ViewModelProviders.of(this).get(PlayerViewModel::class.java)
//    }

    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }

    override fun initMap(savedInstanceState: Bundle?) {
        super.initMap(savedInstanceState)
        player_mapview.onCreate(savedInstanceState)
        player_mapview.map.reloadMap()
        player_mapview.map.isTrafficEnabled = false
        player_mapview.map.showMapText(false)
        player_mapview.map.setLoadOfflineData(false)
        player_mapview.map.uiSettings.isZoomControlsEnabled = false
        player_mapview.map.setMapCustomEnable(true)
        player_mapview.map.mapType = AMap.MAP_TYPE_SATELLITE
    }
//    override fun setMap(savedInstanceState: Bundle?) {
//        super.setMap(savedInstanceState)

//    }

//    @SuppressLint("MissingSuperCall")
//    override fun onSaveInstanceState(outState: Bundle?) {
//        player_mapview.onSaveInstanceState(outState)
//    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        player_mapview.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        player_mapview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (viewModel?.connect != null) {
            this.activity!!.unbindService(viewModel?.connect)
        }
        if(player_mapview!=null){
            player_mapview.onDestroy()
        }
        if (viewModel?.d != null) {
            RxSubscriptions.remove(viewModel?.d)
        }
    }

    override fun onResume() {
        super.onResume()
        player_mapview.onResume()
    }

//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == viewModel?.REQUEST_SCREEN) {
            Log.e("result","请求成功1")
            if (resultCode == Activity.RESULT_OK) {
                Log.e("result","请求成功")
                ScreenUtil.setUpData(resultCode, data)
                viewModel?.start()

            } else {
                Log.e("result", "请求失败")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun setValue(imgs: UIdeviceInfo): ISupportFragment {
        this.imgs = imgs

        return this@PlayerActivity
    }
}