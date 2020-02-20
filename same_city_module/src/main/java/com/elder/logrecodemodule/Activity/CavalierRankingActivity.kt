package com.elder.logrecodemodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.ViewModel.CavalierRankingViewModel
import com.elder.logrecodemodule.databinding.ActivityCavalierRankingBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils

@Route(path = RouterUtils.LogRecodeConfig.SAME_CITY_RANKING)
class CavalierRankingActivity : BaseActivity<ActivityCavalierRankingBinding, CavalierRankingViewModel>() {
    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null


    @Autowired(name = RouterUtils.LogRecodeConfig.LOCATION_SIDE)
    @JvmField
    var side: String? = null


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID)
    @JvmField
    var navigation = 0


    override fun initVariableId(): Int {
        return BR.cavalier_ranking_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setTranslucentStatus(this)
        return R.layout.activity_cavalier_ranking
    }

    override fun initViewModel(): CavalierRankingViewModel? {
        return ViewModelProviders.of(this)[CavalierRankingViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    fun returnBack() {
        if (navigation == 1) {
            if (!mViewModel?.destroyList!!.contains("SystemNotifyListActivity")) {
                finish()
            } else {
                ARouter.getInstance().build(RouterUtils.Chat_Module.SysNotify_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location).navigation(this, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            }
        } else {
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
                override fun onArrival(postcard: Postcard?) {
                    finish()
                }
            })
        }
    }


    override fun doPressBack() {
        super.doPressBack()
        returnBack()

    }
}