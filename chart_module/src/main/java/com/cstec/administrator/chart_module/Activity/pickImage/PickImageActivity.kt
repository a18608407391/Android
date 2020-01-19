package com.cstec.administrator.chart_module.Activity.pickImage

import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.PickImage.PickImageViewModel
import com.cstec.administrator.chart_module.databinding.ActivityChatPickimgBinding
import com.zk.library.Base.BaseActivity
import android.content.Intent
import android.app.Activity
import com.cstec.administrator.chart_module.Utils.Extras
class PickImageActivity:BaseActivity<ActivityChatPickimgBinding,PickImageViewModel>(){


    companion object {
        val FROM_LOCAL = 1

        val FROM_CAMERA = 2

        private val REQUEST_CODE_CROP = 3

        private val REQUEST_CODE_LOCAL = FROM_LOCAL

        private val REQUEST_CODE_CAMERA = FROM_CAMERA

        fun start(activity: Activity, requestCode: Int, from: Int,
                  outPath: String, mutiSelectMode: Boolean, multiSelectLimitSize: Int,
                  isSupportOrig: Boolean, crop: Boolean, outputX: Int, outputY: Int) {
            var intent = Intent(activity, PickImageActivity::class.java)
            intent.putExtra(Extras.EXTRA_FROM, from)
            intent.putExtra(Extras.EXTRA_FILE_PATH, outPath)
            intent.putExtra(Extras.EXTRA_MUTI_SELECT_MODE, mutiSelectMode)
            intent.putExtra(Extras.EXTRA_MUTI_SELECT_SIZE_LIMIT, multiSelectLimitSize)
            intent.putExtra(Extras.EXTRA_SUPPORT_ORIGINAL, isSupportOrig)
            intent.putExtra(Extras.EXTRA_NEED_CROP, crop)
            intent.putExtra(Extras.EXTRA_OUTPUTX, outputX)
            intent.putExtra(Extras.EXTRA_OUTPUTY, outputY)
            activity.startActivityForResult(intent, requestCode)
        }
    }




    override fun initVariableId(): Int {
        return BR.pick_imge_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_chat_pickimg
    }

    override fun initViewModel(): PickImageViewModel? {
        return ViewModelProviders.of(this)[PickImageViewModel::class.java]
    }
}