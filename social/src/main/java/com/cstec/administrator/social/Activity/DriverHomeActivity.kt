package com.cstec.administrator.social.Activity

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.ItemViewModel.CavalierDynamicItem
import com.cstec.administrator.social.ItemViewModel.DriverHomeViewModel
import com.cstec.administrator.social.ItemViewModel.SocialItemModel
import com.cstec.administrator.social.R
import com.cstec.administrator.social.databinding.ActivityDriverhomeBinding
import com.elder.zcommonmodule.*
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.Utils
import com.elder.zcommonmodule.Utils.getRealPathFromUri
import com.elder.zcommonmodule.Widget.PoketSwipeRefreshLayout
import com.google.gson.Gson
import com.zk.library.Base.AppManager
import com.zk.library.Base.BaseActivity
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_driverhome.*
import kotlinx.android.synthetic.main.activity_my_dynamics.*
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Utils.ConvertUtils
import java.io.File
import java.util.concurrent.TimeUnit


@Route(path = RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
class DriverHomeActivity : BaseFragment<ActivityDriverhomeBinding, DriverHomeViewModel>(), AppBarLayout.OnOffsetChangedListener {
    override fun initContentView(): Int {
        return R.layout.activity_driverhome
    }

    var curOffset = 0
    override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {
        curOffset = p1
//        Log.e("result","offset"+ConvertUtils.px2dp(800F))
        if (p1 >= -ConvertUtils.dp2px(278F)) {
            Utils.setStatusTextColor(false, this.activity)
            viewModel?.visible!!.set(false)
            mRefresh!!.isEnabled = p1 >= 0
        } else {
            Utils.setStatusTextColor(true, this.activity)
            mRefresh!!.isEnabled = false
            viewModel?.visible!!.set(true)
        }
    }

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null

    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_MEMBER_ID)
    @JvmField
    var id: String? = null


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID)
    @JvmField
    var navigationType: Int = 0

    @Autowired(name = RouterUtils.Chat_Module.Chat_TARGET_ID)
    @JvmField
    var targetId: String? = null


    @Autowired(name = RouterUtils.Chat_Module.Chat_CONV_TITLE)
    @JvmField
    var title: String? = null

    var onCreate = false
    override fun initVariableId(): Int {
        return BR.driver_home_model
    }

//    override fun initContentView(savedInstanceState: Bundle?): Int {
//        Utils.setStatusBar(this, false, false)
//        return R.layout.activity_driverhome
//    }

//    override fun initViewModel(): DriverHomeViewModel? {
//        return ViewModelProviders.of(this)[DriverHomeViewModel::class.java]
//    }

//    override fun doPressBack() {
//        super.doPressBack()
//        doback()
//    }

//    fun doback() {
//        if (navigationType == 1) {
//            if (!mViewModel?.destroyList!!.contains("SearchActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.SEARCH_MEMBER).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            }
//        } else if (navigationType == 3) {
//            if (!mViewModel?.destroyList!!.contains("DynamicsDetailActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_DETAIL).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            }
//        } else if (navigationType == 0) {
//            if (mViewModel?.destroyList!!.contains("HomeActivity")) {
//                Log.e("result", "当前界面只有一个了")
//                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            } else {
//                RxBus.default?.post(mViewModel?.entity!!)
//                finish()
//            }
//        } else if (navigationType == 2) {
//            if (!mViewModel?.destroyList!!.contains("MyDynamicsActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.SocialConfig.MY_DYNAMIC_AC).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            }
//        } else if (navigationType == 4) {
//            if (!mViewModel?.destroyList!!.contains("GetLikeActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_GET_LIKE).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            }
//        } else if (navigationType == 5) {
//            if (!mViewModel?.destroyList!!.contains("MyFansActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_FANS_AC).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            }
//        } else if (navigationType == 6) {
//            if (!mViewModel?.destroyList!!.contains("MyFocusActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_FOCUS_AC).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            }
//        } else if (navigationType == 7) {
//            if (!mViewModel?.destroyList!!.contains("FocusListActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_FOCUS_LIST).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            }
//        } else if (navigationType == 8) {
//            if (!mViewModel?.destroyList!!.contains("AtmeActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.Atme_AC).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            }
//        } else if (navigationType == 9) {
//            if (!mViewModel?.destroyList!!.contains("CommandActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.COMMAND_AC).navigation(this, object : NavCallback() {
//                    override fun onArrival(postcard: Postcard?) {
//                        finish()
//                    }
//                })
//            }
//        } else if (navigationType == 10) {
//            if (!mViewModel?.destroyList!!.contains("ChatRoomActivity")) {
//                finish()
//            } else {
//                if (targetId == null) {
//                    //代表的当前是别人
//                    ARouter.getInstance().build(RouterUtils.Chat_Module.Chat_AC)
//                            .withString(RouterUtils.Chat_Module.Chat_CONV_TITLE, mViewModel?.entity?.Member?.name)
//                            .withString(RouterUtils.Chat_Module.Chat_TARGET_ID, mViewModel?.entity?.Member?.tel)
//                            .withString(RouterUtils.Chat_Module.Chat_App_Key, "35e2033d379dabfde25d9321")
//                            .withString(RouterUtils.Chat_Module.Chat_DRAFT, "")
//                            .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location)
//                            .navigation(this, object : NavCallback() {
//                                override fun onArrival(postcard: Postcard?) {
//                                    finish()
//                                }
//                            })
//                } else {
//                    //代表是自己的头像界面
//                    ARouter.getInstance().build(RouterUtils.Chat_Module.Chat_AC)
//                            .withString(RouterUtils.Chat_Module.Chat_CONV_TITLE, title)
//                            .withString(RouterUtils.Chat_Module.Chat_TARGET_ID, targetId)
//                            .withString(RouterUtils.Chat_Module.Chat_App_Key, "35e2033d379dabfde25d9321")
//                            .withString(RouterUtils.Chat_Module.Chat_DRAFT, "")
//                            .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, location)
//                            .navigation(this, object : NavCallback() {
//                                override fun onArrival(postcard: Postcard?) {
//                                    finish()
//                                }
//                            })
//                }
//            }
//        } else if (navigationType == 11) {
//            if (!mViewModel?.destroyList!!.contains("EnrollListActivity")) {
//                finish()
//            } else {
//                ARouter.getInstance().build(RouterUtils.PartyConfig.ENROLL).withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION, location).withInt(RouterUtils.PartyConfig.PARTY_ID, Integer.valueOf(targetId)).navigation()
//            }
//        }
//    }

    var mViewPager: ViewPager? = null
    var mTabLayout: TabLayout? = null
    var mRefresh: PoketSwipeRefreshLayout? = null
    override fun initData() {
        super.initData()
        id = arguments?.getString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID)
        location = arguments?.getSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION) as Location?
        mViewPager = binding!!.root.findViewById(R.id.nViewPager)
        mTabLayout = binding!!.root.findViewById(R.id.mTab)
        mRefresh = binding!!.root.findViewById(R.id.driver_homes_swipe)
        mViewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTabLayout))
        mTabLayout!!.setupWithViewPager(mViewPager)
        mTabLayout!!.addOnTabSelectedListener(viewModel!!)
        mRefresh!!.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        mRefresh!!.setOnRefreshListener(viewModel!!)
        sappbar_layout.addOnOffsetChangedListener(this)
        mViewPager!!.currentItem = 0
//        initTrans(false)
        viewModel?.inject(this)
    }

//    fun initTrans(falg: Boolean) {
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, falg, 0x00000000)
//    }

    var realPath: String? = null

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        Log.e("result", "onFragmentResultHome")
        if (requestCode == PRIVATE_DATA_RETURN) {
            viewModel?.initData()
        } else if (requestCode == RELEASE_RESULT) {
            if (data != null) {
                var flag = data!!.getBoolean("ResultReleaseDynamicsSuccess")
                if (flag) {
                    if (isAdded) {
                        viewModel?.initData()
                    }
                }
            }
        } else if (requestCode == DETAIL_RESULT) {
            if (data != null) {
                var enttiy = data.getSerializable(RouterUtils.SocialConfig.SOCIAL_DETAIL_ENTITY) as DynamicsCategoryEntity.Dynamics
                if (enttiy != null) {
                    var its = viewModel?.items!![0] as CavalierDynamicItem
                    its.items.forEachIndexed { index, dynamics ->
                        if (dynamics.id == enttiy.id) {
                            its.items[index] = enttiy
                        } else if (dynamics.memberId == enttiy.memberId && dynamics.followed != enttiy.followed) {
                            dynamics.followed = enttiy.followed
                            its.items.set(index, dynamics)
                        }
                    }
                    its.adapter.initDatas(its.items)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            //照片裁剪回调
            if (realPath != null) {
                upLoadFile()
            }
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (viewModel?.cameraUri != null) {
                    realPath = DialogUtils.startCrop(this.activity!!, viewModel?.cameraUri!!, BaseApplication.getInstance().getWidthPixels, ConvertUtils.dp2px(220F))
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
                realPath = DialogUtils.startCrop(this.activity!!, path, ConvertUtils.dp2px(240F), ConvertUtils.dp2px(160F))
            }
        }
    }

    private fun upLoadFile() {
        var file = File(realPath)
        if (!file.exists()) {
            return
        }
        var token = PreferenceUtils.getString(context, USER_TOKEN)
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build()
            var body = RequestBody.create(MediaType.parse("image/jpg"), file)
            var part = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("files", file.name, body).build()
            var request = Request.Builder().addHeader("appToken", token).url(Base_URL + "AmoskiActivity/SameCity/saveBackgroundImages").post(part).build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            var res = Gson().fromJson(it, BaseResponse::class.java)
            if (res.code == 0) {
                viewModel?.bg!!.set(res.data.toString())
                Toast.makeText(context, "图片上传成功！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Utils.setStatusTextColor(true, this.activity)
    }
}