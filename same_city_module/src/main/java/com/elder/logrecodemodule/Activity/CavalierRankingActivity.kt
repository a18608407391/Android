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

    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
            override fun onArrival(postcard: Postcard?) {
                finish()
            }
        })
    }
}