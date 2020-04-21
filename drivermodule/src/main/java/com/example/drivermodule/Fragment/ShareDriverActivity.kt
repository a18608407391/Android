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
import com.elder.zcommonmodule.Entity.ShareEntity
import com.elder.zcommonmodule.PICK_IMAGE_ACTIVITY_REQUEST_CODE
import com.elder.zcommonmodule.REQUEST_CODE_CROP_IMAGE
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.Utils.getRealPathFromUri
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.ShareDriverViewModel
import com.example.drivermodule.databinding.ActivityShareDriverBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Utils.ConvertUtils
import java.io.File


@Route(path = RouterUtils.MapModuleConfig.SHARE_ACTIVITY)
class ShareDriverActivity : BaseFragment<ActivityShareDriverBinding, ShareDriverViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_share_driver
    }

    @Autowired(name = RouterUtils.MapModuleConfig.SHARE_TYPE)
    @JvmField
    var type: String? = null


    var entity: ShareEntity? = null

    override fun initVariableId(): Int {
        return BR.share_viewModel
    }


    fun setType(type: String): ShareDriverActivity {
        this.type = type
        return this@ShareDriverActivity
    }

    fun setEntity(entity: ShareEntity): ShareDriverActivity {
        this.entity = entity
        return this@ShareDriverActivity
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, false, 0x00000000)
//        return R.layout.activity_share_driver
//    }

//    override fun doPressBack() {
//        super.doPressBack()
//        if (type == null) {
//            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation()
//            RxBus.default?.post("ShareFinish")
//        }
//        finish()
//    }

//    override fun initViewModel(): ShareDriverViewModel? {
//        return ViewModelProviders.of(this)[ShareDriverViewModel::class.java]
//    }

    override fun initData() {
        super.initData()
        Utils.setStatusTextColor(false, activity)
        entity = arguments!!.getSerializable(RouterUtils.MapModuleConfig.SHARE_ENTITY) as ShareEntity?
        type = arguments!!.getString(RouterUtils.MapModuleConfig.SHARE_TYPE)
        viewModel?.inject(this)
    }

    var realPath: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            //照片裁剪回调
            if (realPath != null) {
                if (File(realPath).exists()) {
                    var date = viewModel?.adapter?.mListDatas?.get(1)
                    date?.secondBitmap = realPath
                    viewModel?.adapter?.mListDatas?.set(1, date!!)
                    viewModel?.adapter?.setCarDatas(viewModel?.adapter?.mListDatas!!)
                }
            }
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (viewModel?.cameraUri != null) {
                    realPath = DialogUtils.startCropFragment(this!!, viewModel?.cameraUri!!, ConvertUtils.dp2px(225F), ConvertUtils.dp2px(402F))
                }
            }
        } else if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                var u = getRealPathFromUri(activity!!, data?.data!!)
                var path: Uri
                if (Build.VERSION.SDK_INT >= 24) {
                    path = data?.data!!
                } else {
                    u = "file://$u"
                    path = Uri.parse(u)
                }
                realPath = DialogUtils.startCropFragment(this!!, path, ConvertUtils.dp2px(225F), ConvertUtils.dp2px(402F))
            }
        }
    }
}