package com.example.drivermodule.Fragment.Team

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.Entity.SoketBody.TeamPersonInfo
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.TeamDeleteViewModel
import com.example.drivermodule.databinding.ActivityTeamDeleteMemberBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils


@Route(path = RouterUtils.TeamModule.DELETE)
class DeleteActivity : BaseFragment<ActivityTeamDeleteMemberBinding, TeamDeleteViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_team_delete_member
    }

    @Autowired(name = RouterUtils.TeamModule.TEAM_INFO)
    @JvmField
    var info: TeamPersonInfo? = null

    override fun initVariableId(): Int {
        return BR.delete_model
    }
    fun setValue(info: TeamPersonInfo): DeleteActivity {
        this.info = info
        return this@DeleteActivity
    }
//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_team_delete_member
//    }
//
//    override fun initViewModel(): TeamDeleteViewModel? {
//        return ViewModelProviders.of(this)[TeamDeleteViewModel::class.java]
//    }
//
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }

    override fun initData() {
        super.initData()
        viewModel?.inject(this)
    }
}