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
import com.elder.zcommonmodule.Service.Screen.ScreenUtil
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_player.*
import org.cs.tec.library.Bus.RxSubscriptions





@Route(path = RouterUtils.LogRecodeConfig.PLAYER)
class PlayerActivity : BaseActivity<ActivityPlayerBinding, PlayerViewModel>() {
    override fun initVariableId(): Int {
        return BR.player
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, false, 0x00000000)
        return R.layout.activity_player
    }

    override fun initViewModel(): PlayerViewModel? {
        return ViewModelProviders.of(this).get(PlayerViewModel::class.java)
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    override fun setMap(savedInstanceState: Bundle?) {
        super.setMap(savedInstanceState)
        player_mapview.onCreate(savedInstanceState)
        player_mapview.map.reloadMap()
        player_mapview.map.isTrafficEnabled = false
        player_mapview.map.showMapText(false)
        player_mapview.map.setLoadOfflineData(false)
        player_mapview.map.uiSettings.isZoomControlsEnabled = false
        player_mapview.map.setMapCustomEnable(true)
        player_mapview.map.mapType = AMap.MAP_TYPE_SATELLITE
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle?) {
        player_mapview.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        player_mapview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mViewModel?.connect!=null){
            this.unbindService(mViewModel?.connect)
        }
        player_mapview.onDestroy()
        if (mViewModel?.d != null) {
            RxSubscriptions.remove(mViewModel?.d)
        }
    }

    override fun onResume() {
        super.onResume()
        player_mapview.onResume()
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mViewModel?.REQUEST_SCREEN) {
            if (resultCode == Activity.RESULT_OK) {
                ScreenUtil.setUpData(resultCode, data)
                mViewModel?.start()

            } else {
                Log.e("result", "请求失败")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}