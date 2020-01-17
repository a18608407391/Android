package com.elder.blogin.Activity

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.blogin.BR
import com.elder.blogin.R
import com.elder.blogin.ViewModel.LoginViewModel
import com.elder.blogin.databinding.ActivityLoginBinding
import com.elder.zcommonmodule.*
import com.zk.library.Bus.ServiceEven
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus

@Route(path = RouterUtils.ActivityPath.LOGIN_CODE)
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    var duration = 0

    var max = 3000

    override fun initVariableId(): Int {
        return BR.LoginModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.fullScreen(this)
        return R.layout.activity_login
    }

    override fun initViewModel(): LoginViewModel? {
        return ViewModelProviders.of(this)[LoginViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        var flag = PreferenceUtils.getBoolean(context, "AppOnCreate", false)
        if (!flag) {
            PreferenceUtils.putBoolean(context, "AppOnCreate", true)
        }
        if (PreferenceUtils.getString(context, USER_TOKEN) != null) {
            if (!PreferenceUtils.getBoolean(context, RE_LOGIN, false)) {
                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
                finish()
            } else {
                Log.e("result","执行了finishOtherActivity")
//                context.startService(Intent(context, LowLocationService::class.java).setAction(SERVICE_CANCLE_MINA))
                var pos = ServiceEven()
                pos.type = "MinaServiceCancle"
                RxBus.default?.post(pos)

                AppManager.get()?.finishOtherActivity(LoginActivity::class.java)
            }
        }
        mViewModel!!.inject(this)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun doPressBack() {
        super.doPressBack()
        System.exit(0)
    }
}