package com.cstec.administrator.party_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.cstec.administrator.party_module.ViewModel.SearchPartyViewModel
import com.cstec.administrator.party_module.databinding.ActivitySearchPartyBinding
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.Utils
import com.zk.library.Base.BaseFragment
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_search_party.*
import org.cs.tec.library.Base.Utils.getStatusBarHeight
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Utils.ConvertUtils

@Route(path = RouterUtils.PartyConfig.SEARCH_PARTY)
class SearchPartyActivity : BaseFragment<ActivitySearchPartyBinding, SearchPartyViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_search_party
    }

    @Autowired(name = RouterUtils.PartyConfig.PARTY_LOCATION)
    @JvmField
    var location: Location? = null
    @Autowired(name = RouterUtils.PartyConfig.PARTY_CITY)
    @JvmField
    var party_city: String? = null

    override fun initVariableId(): Int {
        return BR.search_party_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_search_party
//    }
//
//    override fun initViewModel(): SearchPartyViewModel? {
//        return ViewModelProviders.of(this)[SearchPartyViewModel::class.java]
//    }


    override fun initData() {
        super.initData()
        Log.e("status", getStatusBarHeight().toString())
        RxBus.default!!.post(RxBusEven.getInstance(RxBusEven.StatusBar, getStatusBarHeight()))
        Utils.setStatusTextColor(true, activity)
        party_city = arguments!!.getString(RouterUtils.PartyConfig.PARTY_CITY)
        location = arguments!!.getSerializable(RouterUtils.PartyConfig.PARTY_LOCATION) as Location?
        search_et.setOnEditorActionListener(viewModel)
        viewModel?.inject(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RxBus.default!!.post(RxBusEven.getInstance(RxBusEven.StatusBar, ConvertUtils.dp2px(0F)))
        hideSoftInput()
    }
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }
}