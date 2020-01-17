package com.example.private_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.ViewModel.PictureWaterMarkViewModel
import com.example.private_module.databinding.ActivityPictureWatermackBinding
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_picture_watermack.*
import org.cs.tec.library.Base.Utils.context


@Route(path = RouterUtils.PrivateModuleConfig.PICTURE_WATERMARK)
class PictureWaterMackActivity : BaseActivity<ActivityPictureWatermackBinding, PictureWaterMarkViewModel>() {
    override fun initVariableId(): Int {
        return BR.mark_ViewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_picture_watermack
    }
    override fun doPressBack() {
        super.doPressBack()
        finish()
    }
    override fun initViewModel(): PictureWaterMarkViewModel? {
        return ViewModelProviders.of(this)[PictureWaterMarkViewModel::class.java]
    }

    override fun initData() {
        super.initData()
        move_img.setOnClickListener {

        }
        mViewModel?.inject(this)
    }
}