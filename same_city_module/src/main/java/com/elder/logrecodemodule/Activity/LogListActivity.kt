package com.elder.logrecodemodule.Activity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.ViewModel.LogListViewModel
import com.elder.logrecodemodule.databinding.ActivityLoglistBinding
import com.elder.zcommonmodule.Utils.Utils
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_loglist.*


@Route(path = RouterUtils.LogRecodeConfig.LogListActivity)
class LogListActivity : BaseFragment<ActivityLoglistBinding, LogListViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_loglist
    }


    @Autowired(name = RouterUtils.LogRecodeConfig.LOG_LIST_ENTITY)
    @JvmField
    var type: Int = 0


    override fun initVariableId(): Int {
        return BR.log_recode_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setTranslucentStatus(this)
//        return R.layout.activity_loglist
//    }

//    override fun initViewModel(): LogListViewModel? {
//        return ViewModelProviders.of(this)[LogListViewModel::class.java]
//    }

    override fun initData() {
        super.initData()
        type = arguments!!.getInt("type")
        viewModel?.inject(this)
    }

//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        Utils.setStatusTextColor(true, activity!!)
    }


    fun autoScroll(i: Int) {
        var mestric = resources.displayMetrics
        var width = mestric.widthPixels
        var widthChild = log_hori.getChildAt(i).width
        var nbChildInScreen = width / widthChild
        var pos = log_hori.getChildAt(i).left
        hori_scroll.smoothScrollTo((pos - ((nbChildInScreen * widthChild) / 2 + widthChild / 2)), 0)
    }
}