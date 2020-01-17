package com.example.drivermodule.Activity

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Utils.getScaleUpAnimation
import com.example.drivermodule.R
import com.zk.library.Utils.RouterUtils
import hugo.weaving.DebugLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_count_time.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.getDimension
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Utils.ConvertUtils
import java.util.concurrent.TimeUnit


@Route(path = RouterUtils.MapModuleConfig.COUNT_DOWN_ACTIVITY)
class CountDownActivity : Activity() {

    var second = 4
    var dis: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_time)
        dis = Observable.interval(0, 1, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {
            second--
            if (second > 0) {
                start_time.text = (second).toString()
                startAnimation()
            } else if (second == 0) {
                start_time.text = "Go"
                startAnimation()
            } else if (second == -1) {
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).withOptionsCompat(getScaleUpAnimation(count_layout)).navigation()
                finish()
            }
        }
//        count.start()
    }


    var count = object : CountDownTimer(3000, 1000) {
        override fun onFinish() {
            CoroutineScope(uiContext).launch {
                start_time.text = "Go"
                startAnimation()
                delay(1000)
            }
        }

        // 设置路线时隐藏5个按钮


        override fun onTick(millisUntilFinished: Long) {
            var i = (millisUntilFinished / 1000).toInt()
            start_time.text = (i + 1).toString()
            startAnimation()
        }
    }

    fun startAnimation() {
        var alphaAnimation = AlphaAnimation(0F, 1F)
        alphaAnimation.duration = 1000
        var scaleAnimation = ScaleAnimation(2F, 0.5F, 2F, 0.5F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.duration = 1000
        start_time.startAnimation(alphaAnimation)
        start_time.startAnimation(scaleAnimation)
    }

    override fun onDestroy() {
        super.onDestroy()
        dis?.dispose()
        dis = null
    }
}