package com.elder.logrecodemodule.Activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.R
import com.elder.logrecodemodule.ViewModel.LogShareViewModel
import com.elder.logrecodemodule.databinding.ActivityLogShareBinding
import com.elder.zcommonmodule.Entity.UIdeviceInfo
import com.elder.zcommonmodule.Utils.Utils
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Base.Transaction.ISupportFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_log_share.*


@Route(path = RouterUtils.LogRecodeConfig.SHARE)
class LogShareActivity : BaseFragment<ActivityLogShareBinding, LogShareViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_log_share
    }

    @Autowired(name = RouterUtils.LogRecodeConfig.SHARE_ENTITY)
    @JvmField
    var imgs: UIdeviceInfo? = null


    override fun initVariableId(): Int {
        return BR.log_share_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_log_share
//    }
//
//    override fun initViewModel(): LogShareViewModel? {
//        return ViewModelProviders.of(this)[LogShareViewModel::class.java]
//    }


    lateinit var behaviors: BottomSheetBehavior<LinearLayout>
    override fun initData() {
        super.initData()
        Utils.setStatusTextColor(true, activity!!)
        behaviors = BottomSheetBehavior.from<LinearLayout>(behavior)
        viewModel?.inject(this)
        behaviors.state = BottomSheetBehavior.STATE_EXPANDED
        behaviors.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, p1: Int) {
                if (p1 == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel?.bottomVisible!!.set(false)
                } else {
                    viewModel?.bottomVisible!!.set(true)
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Utils.setStatusTextColor(false, activity!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (basesult != null) {
            basesult.cancelAllAnims()
            basesult.reset()
        }
    }

    fun setValue(item: UIdeviceInfo): ISupportFragment {
        this.imgs = item
        return this@LogShareActivity
    }

//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }
}