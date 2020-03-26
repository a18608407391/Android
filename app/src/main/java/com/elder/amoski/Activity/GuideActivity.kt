package com.elder.amoski.Activity

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.amoski.BR
import com.elder.amoski.R
import com.elder.amoski.ViewModel.GuideViewModel
import com.elder.amoski.databinding.ActivityGuideBinding
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL
import com.elder.zcommonmodule.Utils.DialogUtils
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_guide.*
import org.cs.tec.library.APP_CREATE
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Utils.ConvertUtils


@Route(path = RouterUtils.ActivityPath.GUIDE)
class GuideActivity : BaseActivity<ActivityGuideBinding, GuideViewModel>() {
    override fun initVariableId(): Int {
        return BR.guide_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.fullScreen(this)
        return R.layout.activity_guide
    }


    override fun initViewModel(): GuideViewModel? {
        return ViewModelProviders.of(this)[GuideViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        var lastStr = "《用户隐私协议》"

        var str = "特别提示：湖南鹏辰星旅文化传播有限公司（以下简称“鹏辰星旅”）在此特别提醒用户（用户）在注册成为用户之前，请认真阅读本《用户隐私协议》（以下简称“协议”），确保用户充分理解本协议中各条款。请用户审慎阅读并选择接受或不接受本协议。除非用户接受本协议所有条款，否则用户无权注册、登录或使用本协议所涉服务。用户的注册、登录、使用等行为将视为对本协议的接受，并同意接受本协议各项条款的约束。\n\n "

        var builder = SpannableStringBuilder(str + lastStr)
        var foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#3FC5C9"))
        builder.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                ARouter.getInstance().build(RouterUtils.LoginModuleKey.WEB_VIEW).withInt(RouterUtils.LoginModuleKey.TYPE_CLASS, 2).navigation()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, (str + lastStr).length - lastStr.length, (str + lastStr).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        builder.setSpan(foregroundColorSpan, (str + lastStr).length - lastStr.length, (str + lastStr).length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        var dialog = DialogUtils.createNomalTwoBuilderDialog(this, builder, getString(R.string.cancle), org.cs.tec.library.Base.Utils.getString(com.elder.blogin.R.string.confirm)).contentTextSize(14F)
        dialog.setOnBtnClickL(OnBtnClickL {
            AppManager.get()?.AppExit()
            dialog.dismiss()
        }, OnBtnClickL {
            PreferenceUtils.putBoolean(context, APP_CREATE, true)
            guide_video.start()
            dialog.dismiss()
        })
        dialog.show()
        mViewModel?.inject(this)
    }
}