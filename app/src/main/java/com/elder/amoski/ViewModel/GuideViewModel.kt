package com.elder.amoski.ViewModel

import android.net.Uri
import android.view.View
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.amoski.Activity.GuideActivity
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_guide.*
import org.cs.tec.library.Base.Utils.context


class GuideViewModel : BaseViewModel() {

    lateinit var guideActivity: GuideActivity
    fun inject(guideActivity: GuideActivity) {
        this.guideActivity = guideActivity
        guideActivity.guide_video.post {
            guideActivity.guide_video.setFixedSize(guideActivity.guide_video.width, guideActivity.guide_video.height)
            guideActivity.guide_video.invalidate()
            guideActivity.guide_video.setVideoURI(Uri.parse("android.resource://" + context.packageName + "/raw/guide_movie"))
            guideActivity.guide_video.start()
            guideActivity.guide_video.setOnCompletionListener {
                guideActivity.guide_video.start()
            }
        }
    }


    fun onClick(view: View) {
        ARouter.getInstance().build(RouterUtils.ActivityPath.LOGIN_CODE).navigation(guideActivity, object : NavCallback() {
            override fun onArrival(postcard: Postcard?) {
                finish()
            }
        })
    }
}