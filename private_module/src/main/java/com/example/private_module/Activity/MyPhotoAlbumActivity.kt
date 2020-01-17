package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.ENTER_TO_SELECTE
import com.elder.zcommonmodule.SHARE_PICTURE
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.PhotoAlbumViewModel
import com.example.private_module.databinding.ActivityPhotoalbumBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_photoalbum.*


@Route(path = RouterUtils.PrivateModuleConfig.MY_PHOTO_ALBUM)
class MyPhotoAlbumActivity : BaseActivity<ActivityPhotoalbumBinding, PhotoAlbumViewModel>() {
    override fun initVariableId(): Int {
        return BR.photo_album
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_photoalbum
    }

    override fun initViewModel(): PhotoAlbumViewModel? {
        return ViewModelProviders.of(this)[PhotoAlbumViewModel::class.java]
    }

    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        finish()
    }

    override fun initData() {
        super.initData()
        Swiperefresh.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        mViewModel?.inject(this)
        Swiperefresh.setOnRefreshListener(mViewModel)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ENTER_TO_SELECTE -> {
                mViewModel?.getAllPhoto(true)
            }
            SHARE_PICTURE -> {
                mViewModel?.getAllPhoto(true)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}