package com.elder.blogin.Activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.blogin.BR
import com.elder.blogin.R
import com.elder.blogin.ViewModel.WebViewModel
import com.elder.blogin.databinding.ActivityWebBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_web.*
import java.util.ArrayList


@Route(path = RouterUtils.LoginModuleKey.WEB_VIEW)
class WebActivity : BaseActivity<ActivityWebBinding, WebViewModel>() {


    @Autowired(name = RouterUtils.LoginModuleKey.TYPE_CLASS)
    @JvmField
    var type: Int = 0
    override fun initVariableId(): Int {
        return BR.web_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_web
    }

    override fun initViewModel(): WebViewModel? {
        return ViewModelProviders.of(this)[WebViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        window.decorView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val outView = ArrayList<View>()
            window.decorView.findViewsWithText(outView, "QQ浏览器", View.FIND_VIEWS_WITH_TEXT)
            val size = outView.size
            if (outView != null && outView.size > 0) {
                outView[0].visibility = View.GONE
            }
        }
        mViewModel?.inject(this)
    }

    override fun doPressBack() {
        super.doPressBack()
        if(type==0){
            ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation()
        }else{
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        web.clearCache(true)
        web.clearHistory()
        web.clearFormData()
        web.clearMatches()
        web.clearSslPreferences()
    }
}