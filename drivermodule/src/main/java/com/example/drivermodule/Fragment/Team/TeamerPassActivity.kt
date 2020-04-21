package com.example.drivermodule.Fragment.Team

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.TeamerPassViewModel
import com.example.drivermodule.databinding.ActivityTeamerPassBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.TeamModule.TEAMER_PASS)
class TeamerPassActivity : BaseFragment<ActivityTeamerPassBinding, TeamerPassViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_teamer_pass
    }

    @Autowired(name = RouterUtils.TeamModule.TEAM_INFO)
    @JvmField
    var info: TeamPersonInfo? = null

    override fun initVariableId(): Int {
        return BR.teamer_pass_model
    }
    fun setValue(info: TeamPersonInfo): TeamerPassActivity {
        this.info = info
        return this@TeamerPassActivity
    }
//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_teamer_pass
//    }
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }
//    override fun initViewModel(): TeamerPassViewModel? {
//        return ViewModelProviders.of(this)[TeamerPassViewModel::class.java]
//    }

    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }
}