package com.cstec.administrator.social.Activity

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.social.BR
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.FocusListViewModel
import com.cstec.administrator.social.databinding.ActivityFocusListBinding
import com.elder.zcommonmodule.Entity.Location
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_driverhome.*
import kotlinx.android.synthetic.main.activity_focus_list.*


@Route(path = RouterUtils.SocialConfig.SOCIAL_FOCUS_LIST)
class FocusListActivity : BaseActivity<ActivityFocusListBinding, FocusListViewModel>() {


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY)
    @JvmField
    var detail: DynamicsCategoryEntity.Dynamics? = null

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null


    override fun initVariableId(): Int {
        return BR.focus_list_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_focus_list
    }

    override fun initViewModel(): FocusListViewModel? {
        return ViewModelProviders.of(this)[FocusListViewModel::class.java]
    }


    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
    override fun initData() {
        super.initData()
        focuslist_swipe.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        focuslist_swipe.setOnRefreshListener(mViewModel!!)
        mViewModel?.inject(this)
    }

}