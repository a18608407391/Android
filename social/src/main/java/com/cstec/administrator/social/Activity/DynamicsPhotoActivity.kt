package com.cstec.administrator.social.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.DynamicsPhotoViewModel
import com.cstec.administrator.social.databinding.ActivityDynamicsPhotoBinding
import com.elder.zcommonmodule.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.REQUEST_CODE_CROP_IMAGE
import com.elder.zcommonmodule.SOCIAL_SELECT_PHOTOS
import com.elder.zcommonmodule.Utils.DialogUtils
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import org.cs.tec.library.Utils.ConvertUtils


@Route(path = RouterUtils.SocialConfig.SOCIAL_PHOTO)
class DynamicsPhotoActivity : BaseFragment<ActivityDynamicsPhotoBinding, DynamicsPhotoViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_dynamics_photo
    }

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_MAX_COUNT)
    @JvmField
    var count: Int = 9


    override fun initVariableId(): Int {
        return BR.dynamics_photo
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
//        return R.layout.activity_dynamics_photo
//    }
//
//    override fun initViewModel(): DynamicsPhotoViewModel? {
//        return ViewModelProviders.of(this)[DynamicsPhotoViewModel::class.java]
//    }

//
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }

    override fun initData() {
        super.initData()
        count = arguments!!.getInt(RouterUtils.SocialConfig.SOCIAL_MAX_COUNT)
        viewModel?.inject(this)
    }

    var realPath: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (viewModel?.realPath != null) {
                        realPath = DialogUtils.startCropFragment(this, viewModel?.realPath!!, ConvertUtils.dp2px(160F), ConvertUtils.dp2px(160F))
                    }
                }
//                if (data != null && data?.data != null) {
//                    var u = getRealPathFromUri(this, data?.data!!)
//                    var path: Uri
//                    if (Build.VERSION.SDK_INT >= 24) {
//                        path = data?.data!!
//                    } else {
//                        u = "file://$u"
//                        path = Uri.parse(u)
//                    }
//                    Log.e("result", path.path)
//
//                } else {
//                    if (mViewModel?.items!!.size == 9) {
//                        mViewModel?.items!!.remove("")
//                        mViewModel?.items!!.add(mViewModel?.realPath!!.path)
//                    } else {
//                        mViewModel?.items!!.add(0, mViewModel?.realPath!!.path)
//                    }
//                }
            }
            REQUEST_CODE_CROP_IMAGE -> {
                if (realPath != null) {
                    var list = ArrayList<String>()
                    list.add(realPath!!)
                    var bundler = Bundle()
                    bundler.putStringArrayList("PhotoUrls", list)
                    setFragmentResult(SOCIAL_SELECT_PHOTOS, bundler)
                    _mActivity!!.onBackPressedSupport()
//                    intent.putStringArrayListExtra("PhotoUrls", list)
//                    setResult(SOCIAL_SELECT_PHOTOS, intent)
//                    finish()
                }
            }
        }
    }

}