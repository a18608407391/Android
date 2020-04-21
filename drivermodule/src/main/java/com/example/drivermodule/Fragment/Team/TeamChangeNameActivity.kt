package com.example.drivermodule.Fragment.Team

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.ChangeTeamNameViewModel
import com.example.drivermodule.databinding.ActivityTeamChangenameBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.TeamModule.CHANGE_NAME)
class TeamChangeNameActivity : BaseFragment<ActivityTeamChangenameBinding, ChangeTeamNameViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_team_changename
    }

    @Autowired(name = RouterUtils.TeamModule.TEAM_INFO)
    @JvmField
    var info: TeamPersonInfo? = null


    override fun initVariableId(): Int {
        return BR.change_team_name
    }


    fun setValue(info: TeamPersonInfo): TeamChangeNameActivity {
        this.info = info
        return this@TeamChangeNameActivity
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_team_changename
//    }

    //    override fun initViewModel(): ChangeTeamNameViewModel? {
//        return ViewModelProviders.of(this)[ChangeTeamNameViewModel::class.java]
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