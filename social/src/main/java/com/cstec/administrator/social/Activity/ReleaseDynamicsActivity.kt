package com.cstec.administrator.social.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.BR
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.cstec.administrator.social.Entity.SocialUserModel
import com.cstec.administrator.social.R
import com.cstec.administrator.social.ViewModel.ReleaseDynamicsViewModel
import com.cstec.administrator.social.databinding.ActivityReleasedynamicsBinding
import com.elder.zcommonmodule.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.REQUEST_CODE_CROP_IMAGE
import com.elder.zcommonmodule.SELECT_USER_CALLBACK
import com.elder.zcommonmodule.SOCIAL_SELECT_PHOTOS
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.elder.zcommonmodule.Widget.RichEditText.RichEditBuilder
import com.elder.zcommonmodule.Widget.RichEditText.RichEditText
import com.elder.zcommonmodule.Widget.RichEditText.listener.OnEditTextUtilJumpListener
import com.elder.zcommonmodule.Widget.RichEditText.listener.SpanAtUserCallBack
import com.elder.zcommonmodule.Widget.RichEditText.listener.SpanTopicCallBack
import com.elder.zcommonmodule.Widget.RichEditText.listener.SpanUrlCallBack
import com.elder.zcommonmodule.Widget.RichEditText.model.TopicModel
import com.elder.zcommonmodule.Widget.RichEditText.model.UserModel
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseActivity
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import kotlinx.android.synthetic.main.activity_releasedynamics.*
import org.cs.tec.library.Utils.ConvertUtils
import java.io.File

@Route(path = RouterUtils.SocialConfig.SOCIAL_RELEASE)
class ReleaseDynamicsActivity : BaseActivity<ActivityReleasedynamicsBinding, ReleaseDynamicsViewModel>(), TextWatcher {
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //重新计算过滤器
        if (et.text.toString().length <= 140) {
            //整体都小于140
            var count = 0
            et.realUserList.forEach {
                count += it.user_name.length
            }
            setFilter(140 + count)
        }
        LastEt = et
    }

    //发布动态页面
    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID)
    @JvmField
    var type: Int = 0


    //1 详情 2//动态列表 3//我的动态

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY)
    @JvmField
    var entity: DynamicsCategoryEntity.Dynamics? = null

    override fun initVariableId(): Int {
        return BR.rd_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
        return R.layout.activity_releasedynamics
    }


    override fun initViewModel(): ReleaseDynamicsViewModel? {
        return ViewModelProviders.of(this)[ReleaseDynamicsViewModel::class.java]
    }

    var nameList = ArrayList<UserModel>()
    val nameListET = ArrayList<UserModel>()
    val topicModels = ArrayList<TopicModel>()
    private val topicModelsEd = ArrayList<TopicModel>()
    val spanUrlCallBack = object : SpanUrlCallBack {
        override fun phone(view: View, phone: String) {
        }

        override fun url(view: View, url: String) {
        }
    }
    val spanAtUserCallBack = object : SpanAtUserCallBack {
        override fun onClick(view: View, userModel1: UserModel) {
            if (view is TextView) {
                view.highlightColor = Color.TRANSPARENT
            }
        }
    }
    //    IndexOutOfBoundsException
    val spanTopicCallBack = object : SpanTopicCallBack {
        override fun onClick(view: View, topicModel: TopicModel) {
            if (view is TextView) {
                view.highlightColor = Color.TRANSPARENT
            }
        }
    }

    override fun doPressBack() {
        super.doPressBack()
        mViewModel?.toNav()
    }

    var LastEt: RichEditText? = null

    lateinit var behaviors: BottomSheetBehavior<LinearLayout>
    override fun initData() {
        super.initData()
        behaviors = BottomSheetBehavior.from<LinearLayout>(behavior_by_release)
        behaviors.state = BottomSheetBehavior.STATE_HIDDEN
        mViewModel?.inject(this)
        et.addTextChangedListener(this)
        var filter = InputFilter.LengthFilter(140)
        et.filters = arrayOf(filter)
        val richEditBuilder = RichEditBuilder()
        richEditBuilder.setEditText(et)
                .setUserModels(nameListET)
                .setTopicModels(topicModelsEd)
                .setColorAtUser("#3FC5C9")
                .setColorTopic("#F0F0C0")
                .setEditTextAtUtilJumpListener(object : OnEditTextUtilJumpListener {
                    override fun notifyAt() {
                        var et = et.text.toString()
                        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_AITE).navigation(this@ReleaseDynamicsActivity, SELECT_USER_CALLBACK)
                    }

                    override fun notifyTopic() {
//                        JumpUtil.goToTopicList(this@MainActivity, MainActivity.REQUEST_TOPIC_CODE_INPUT)
                    }
                })
                .builder()
    }

    var realPath: String? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SOCIAL_SELECT_PHOTOS -> {
                if (data != null) {
                    var array = data!!.getStringArrayListExtra("PhotoUrls")
                    if (array.size == 9) {
                        mViewModel?.items!!.clear()
                    }
                    array.forEach {
                        mViewModel?.items!!.add(0, it)
                    }
                    if (mViewModel?.items!!.size > 9) {
                        mViewModel?.items!!.remove("")
                    }
                }
            }
            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (mViewModel?.realPath != null) {
                        realPath = DialogUtils.startCrop(this, mViewModel?.realPath!!, ConvertUtils.dp2px(160F), ConvertUtils.dp2px(160F))
                    }
                }
            }
            REQUEST_CODE_CROP_IMAGE -> {
                if (File(realPath).exists()) {
                    if (mViewModel?.items!!.size == 9) {
                        mViewModel?.items!!.removeAt(8)
                        mViewModel?.items!!.add(realPath)
                    } else {
                        mViewModel?.items!!.add(0, realPath)
                    }
                }
            }
            SELECT_USER_CALLBACK -> {
                var list = Gson().fromJson<ArrayList<SocialUserModel>>(data?.getStringExtra("array"), object : TypeToken<ArrayList<SocialUserModel>>() {}.type)
                var count = data?.getIntExtra("Count", 0)
                if (list == null || count == null) {
                    return
                }
//                Log.e("result", "count" + count)
//                var filter = InputFilter.LengthFilter(et.text.toString().length + count!! + 2)
//                et.filters = arrayOf(filter)
                lenthCheck(list!!, count!!)
                list?.forEach {
                    var model = UserModel()
                    model.user_id = it.id!!
                    model.user_name = it.name!!
//                    var flag = false
//                    var arr  = ArrayList<String>()
//                    et.realUserList.forEach {
//                        if (it.user_id == model.user_id) {
//                            flag = true
//                        }else{
//
//                        }
//                    }
//                    if (!flag) {

                    et.resolveAtResultByEnterAt(model)
//                    }
                }
                Log.e("result", "当前Et的长度" + et.text.toString().length)
//                et.resolveInsertText(context, "第三方的说法都是", nameListET, topicModels)
            }
        }
    }

    fun lenthCheck(list: ArrayList<SocialUserModel>, count: Int) {
        var addCount = 0
        if (et.text.toString().length < 140) {
            addCount = 140 + count + list.size
        } else {
            addCount = et.text.toString().length + count + list.size
        }
        setFilter(addCount)
    }

    fun setFilter(size: Int) {
        Log.e("result", "设置的尺寸" + size)
        var filter = InputFilter.LengthFilter(size)
        et.filters = arrayOf(filter)
    }
}