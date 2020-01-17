package com.example.private_module.Activity.Picture

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.Bean.PhotoEntitiy
import com.example.private_module.R
import com.example.private_module.ViewModel.Picture.ShareEditPictureViewModel
import com.example.private_module.databinding.ActivityShareEditPicBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import org.cs.tec.library.Bus.RxSubscriptions


@Route(path = RouterUtils.PrivateModuleConfig.SHARE_ADD_MARKER_ACTIVITY)
class ShareEditPictureActivity : BaseActivity<ActivityShareEditPicBinding, ShareEditPictureViewModel>() {
    @Autowired(name = RouterUtils.PrivateModuleConfig.ADD_MARKER_URL)
    @JvmField
    var url: String? = null

    override fun initVariableId(): Int {
        return BR.share_edit_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, false, 0x00000000)
        return R.layout.activity_share_edit_pic
    }


    override fun initViewModel(): ShareEditPictureViewModel? {
        return ViewModelProviders.of(this)[ShareEditPictureViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}