package com.example.private_module.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
import com.elder.zcommonmodule.GET_NICKNAME
import com.elder.zcommonmodule.PICK_IMAGE_ACTIVITY_REQUEST_CODE
import com.elder.zcommonmodule.REQUEST_CODE_CROP_IMAGE
import com.example.private_module.BR
import com.elder.zcommonmodule.Entity.UserInfo
import com.example.private_module.R
import com.example.private_module.ViewModel.AcUserInfoViewModel
import com.example.private_module.databinding.ActivityUserinfoBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import android.os.Build
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.getRealPathFromUri


@Route(path = RouterUtils.PrivateModuleConfig.USER_INFO_EDIT)
class UserInfoActivity : BaseActivity<ActivityUserinfoBinding, AcUserInfoViewModel>() {
    @Autowired(name = RouterUtils.PrivateModuleConfig.USER_INFO)
    @JvmField
    var userInfo: UserInfo? = null


    var realPath: String? = null
    override fun initVariableId(): Int {
        return BR.AcUserInfo_Model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_userinfo
    }

    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
        finish()
    }

    override fun initViewModel(): AcUserInfoViewModel? {
        return ViewModelProviders.of(this)[AcUserInfoViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GET_NICKNAME) {
            var nickname = data?.getStringExtra("nickname")
            if(!nickname.isNullOrEmpty()){
                mViewModel?.nickname?.set(nickname)
            }
        }
        if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE) {
            //选择照片
            if (data != null) {
                Log.e("result", mViewModel!!.iconUri.toString() + "当前3")
                var u = getRealPathFromUri(this, data?.data!!)
                var path: Uri
                if (Build.VERSION.SDK_INT >= 24) {
                    path = data?.data!!
                } else {
                    u = "file://$u"
                    path = Uri.parse(u)
                }
                realPath = DialogUtils.startCrop(this, path, 300, 300)
            }
        } else if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            //照片裁剪回调

            if (realPath != null || !realPath?.isEmpty()!!) {
                Log.e("result", mViewModel!!.iconUri.toString() + "当前1")
                mViewModel?.avatars!!.set(realPath)
            }
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (mViewModel?.iconUri != null) {
                    Log.e("result", mViewModel!!.iconUri.toString() + "当前2")
                    realPath = DialogUtils.startCrop(this, mViewModel!!.iconUri!!, 300, 300)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}