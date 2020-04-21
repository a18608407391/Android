package com.example.drivermodule.Fragment.Team

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.TeamManagerViewModel
import com.example.drivermodule.databinding.ActivityTeamManagerBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.TeamModule.MANAGER)
class TeamManagerActivity : BaseFragment<ActivityTeamManagerBinding, TeamManagerViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_team_manager
    }


    @Autowired(name = RouterUtils.TeamModule.TEAM_INFO)
    @JvmField
    var info: TeamPersonInfo? = null

    override fun initVariableId(): Int {
        return BR.team_manager
    }

    fun setValue(info: TeamPersonInfo): TeamManagerActivity {
        this.info = info
        return this@TeamManagerActivity
    }
//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_team_manager
//    }
//
//    override fun initViewModel(): TeamManagerViewModel? {
//        return ViewModelProviders.of(this)[TeamManagerViewModel::class.java]
//    }

//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }

    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }
}