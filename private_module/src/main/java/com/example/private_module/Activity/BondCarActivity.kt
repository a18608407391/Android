package com.example.private_module.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
import com.elder.zcommonmodule.GET_NICKNAME
import com.elder.zcommonmodule.PICK_IMAGE_ACTIVITY_REQUEST_CODE
import com.elder.zcommonmodule.REQUEST_CODE_CROP_IMAGE
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.getRealPathFromUri
import com.example.private_module.BR
import com.example.private_module.Bean.CarsEntity
import com.example.private_module.R
import com.example.private_module.ViewModel.BondCarViewModel
import com.example.private_module.databinding.ActivityBondcarsBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import org.cs.tec.library.Utils.ConvertUtils

@Route(path = RouterUtils.PrivateModuleConfig.BOND_CARS)
class BondCarActivity : BaseFragment<ActivityBondcarsBinding, BondCarViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_bondcars
    }


    @Autowired(name = RouterUtils.PrivateModuleConfig.Edit_CARS)
    @JvmField
    var entity: CarsEntity? = null

    var realPath: String? = null

    override fun initVariableId(): Int {
        return BR.bond_car_Model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
//        return R.layout.activity_bondcars
//    }
//
//    override fun initViewModel(): BondCarViewModel? {
//        return ViewModelProviders.of(this)[BondCarViewModel::class.java]
//    }
lateinit var car_name :EditText
    override fun initData() {
        super.initData()
        car_name  = binding!!.root!!.findViewById(R.id.car_name)
        viewModel?.inject(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (viewModel?.cameraUri != null) {
                    realPath = DialogUtils.startCropFragment(this, viewModel?.cameraUri!!, ConvertUtils.dp2px(240F), ConvertUtils.dp2px(160F))
                }
            }
        } else if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                var u = getRealPathFromUri(this.activity!!, data?.data!!)
                var path: Uri
                if (Build.VERSION.SDK_INT >= 24) {
                    path = data?.data!!
                } else {
                    u = "file://$u"
                    path = Uri.parse(u)
                }
                realPath = DialogUtils.startCropFragment(this, path, ConvertUtils.dp2px(240F), ConvertUtils.dp2px(160F))
            }
        }else if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            //照片裁剪回调
            if (realPath != null) {
                viewModel?.avatar!!.set(realPath)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)

    }
}