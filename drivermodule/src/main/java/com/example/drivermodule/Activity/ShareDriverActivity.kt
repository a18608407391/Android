package com.example.drivermodule.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.PICK_IMAGE_ACTIVITY_REQUEST_CODE
import com.elder.zcommonmodule.REQUEST_CODE_CROP_IMAGE
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.getRealPathFromUri
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.ShareDriverViewModel
import com.example.drivermodule.databinding.ActivityShareDriverBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Utils.ConvertUtils
import java.io.File


@Route(path = RouterUtils.MapModuleConfig.SHARE_ACTIVITY)
class ShareDriverActivity : BaseActivity<ActivityShareDriverBinding, ShareDriverViewModel>() {

    @Autowired(name = RouterUtils.MapModuleConfig.SHARE_TYPE)
    @JvmField
    var type: String? = null


    override fun initVariableId(): Int {
        return BR.share_viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, false, 0x00000000)
        return R.layout.activity_share_driver
    }

    override fun doPressBack() {
        super.doPressBack()
        if (type == null) {
            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
            RxBus.default?.post("ShareFinish")
        }
        finish()
    }

    override fun initViewModel(): ShareDriverViewModel? {
        return ViewModelProviders.of(this)[ShareDriverViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        mViewModel?.inject(this)
    }

    var realPath: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            //照片裁剪回调
            if (realPath != null) {
                if(File(realPath).exists()){
                    var date = mViewModel?.adapter?.mListDatas?.get(1)
                    var bitmap = BitmapFactory.decodeFile(realPath)
                    date?.secondBitmap = bitmap
                    mViewModel?.adapter?.mListDatas?.set(1, date!!)
                    mViewModel?.adapter?.setCarDatas(mViewModel?.adapter?.mListDatas!!)
                }
            }
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (mViewModel?.cameraUri != null) {
                    realPath = DialogUtils.startCrop(this, mViewModel?.cameraUri!!, ConvertUtils.dp2px(225F), ConvertUtils.dp2px(402F))
                }
            }
        } else if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                var u = getRealPathFromUri(this, data?.data!!)
                var path: Uri
                if (Build.VERSION.SDK_INT >= 24) {
                    path = data?.data!!
                } else {
                    u = "file://$u"
                    path = Uri.parse(u)
                }
                realPath = DialogUtils.startCrop(this, path, ConvertUtils.dp2px(225F), ConvertUtils.dp2px(402F))
            }
        }
    }
}