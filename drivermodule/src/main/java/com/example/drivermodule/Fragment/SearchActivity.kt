package com.example.drivermodule.Fragment

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Utils.Utils
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.SearchViewModel
import com.example.drivermodule.databinding.ActivitySearchLocationBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.MapModuleConfig.SEARCH_ACTIVITY)
class SearchActivity : BaseFragment<ActivitySearchLocationBinding, SearchViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_search_location
    }

    @Autowired(name = RouterUtils.MapModuleConfig.SEARCH_MODEL)
    @JvmField
    var model: Int = 0
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }
//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
//        return R.layout.activity_search_location
//    }

    override fun initVariableId(): Int {
        return BR.Search_ViewModel
    }

//    override fun initViewModel(): SearchViewModel? {
//        return ViewModelProviders.of(this)[SearchViewModel::class.java]
//    }


    fun setModel(model:Int): SearchActivity {
        this.model = model
        return this
    }

    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Utils.setStatusTextColor(true,activity)
    }
}