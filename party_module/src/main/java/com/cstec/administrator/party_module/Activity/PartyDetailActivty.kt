package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailIntroduceItemModel
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.PartyDetailViewModel
import com.cstec.administrator.party_module.databinding.ActivityPartyDetailBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_party_detail.*


@Route(path = RouterUtils.PartyConfig.PARTY_DETAIL)
class PartyDetailActivty : BaseActivity<ActivityPartyDetailBinding, PartyDetailViewModel>(), AppBarLayout.OnOffsetChangedListener {
    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
//        Log.e("result", "offset" + ConvertUtils.px2dp(Math.abs(p1) * 1F))
//        var model = mViewModel?.items!![0] as PartyDetailIntroduceItemModel
//        if (p1 >= -ConvertUtils.dp2px(382F)) {
//            model.scrollEnable.set(false)
//        }else{
//            model.scrollEnable.set(true)
//        }
    }

    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.PartyConfig.PARTY_ID)
    @JvmField
    var party_id: Int = 0

    override fun initVariableId(): Int {
        return BR.party_detail_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        Utils.setStatusBar(this, false, false)
        return R.layout.activity_party_detail
    }

    override fun initViewModel(): PartyDetailViewModel? {
        return ViewModelProviders.of(this)[PartyDetailViewModel::class.java]
    }

    override fun doPressBack() {
        super.doPressBack()
        finish()
    }

    override fun initData() {
        super.initData()
        mPartyDetailViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mPartyDetailTabLayout))
        mPartyDetailTabLayout.setupWithViewPager(mPartyDetailViewPager)
        party_appbar_layout.addOnOffsetChangedListener(this)
        initTrans(true)
        mViewModel?.inject(this)
    }

    fun initTrans(falg: Boolean) {
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, falg, 0x00000000)
    }

}