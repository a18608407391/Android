package com.cstec.administrator.social.ItemViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.social.Activity.DriverHomeActivity
import com.cstec.administrator.social.BR
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.Entity.CanalierHomeEntity
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.elder.zcommonmodule.PRIVATE_DATA_RETURN
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import java.util.*


class DriverHomeViewModel : BaseViewModel(), HttpInteface.CanalierResult, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>, DialogUtils.Companion.IconUriCallBack, SwipeRefreshLayout.OnRefreshListener, HttpInteface.SocialDynamicsFocus {

    override fun ResultFocusSuccess(it: String) {
        LoadChildData(activity.mViewPager!!.currentItem)
    }

    override fun ResultFocusError(ex: Throwable) {

    }

    override fun onRefresh() {
        if (activity.mViewPager!!.currentItem == 0) {
            var item = items[0] as CavalierDynamicItem
            item.pageSize = 1
        }
        initData()
        CoroutineScope(uiContext).launch {
            delay(10000)
            activity.mRefresh!!.isRefreshing = false
        }
    }

    var cameraUri: Uri? = null
    override fun getIcon(iconUri: Uri) {
        this.cameraUri = iconUri
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {

    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        if (activity.onCreate) {
            LoadChildData(p0!!.position)
        } else {
            activity.onCreate = true
        }
    }

    override fun ResultCanalierSuccess(it: String) {
        Log.e("result", "骑士首页" + it)
        if (it.length < 10) {
            return
        }

        var entity = Gson().fromJson<CanalierHomeEntity>(it, CanalierHomeEntity::class.java)
        this.entity = entity
        if (activity?.id == PreferenceUtils.getString(context, USERID)) {
            self.set(2)
        }
        bg.set(entity?.backgroundImages)
        LoadChildData(activity.mViewPager!!.currentItem)
        if (entity.followed == 0) {
            follow.set(false)
        } else {
            follow.set(true)
        }
        if (entity.Member?.synopsis != null) {
            des.set(entity.Member?.synopsis)
        } else {
            des.set("-")
        }
        avatar.set(entity.Member?.headImgFile)
        name.set(entity.Member?.name)
        if (activity.location != null) {
            city.set(activity.location!!.aoiName)
        }

        if (entity.getFabulous > 1000) {
            likeCount.set(((entity.getFabulous / 1000 * 1.0F).toString()))
        } else {
            likeCount.set(entity.getFabulous.toString())
        }
        if (entity.FollowCount > 1000) {
            focusCount.set(((entity.FollowCount / 1000 * 1.0F).toString()))
        } else {
            focusCount.set((entity.FollowCount).toString())
        }
        if (entity.FansCount > 1000) {
            fansCount.set(((entity.FansCount / 1000 * 1.0F).toString()))
        } else {
            fansCount.set((entity.FansCount).toString())
        }
        activity.mRefresh!!.isRefreshing = false
    }

    var des = ObservableField<String>()
    var entity: CanalierHomeEntity? = null
    var avatar = ObservableField<String>()
    var name = ObservableField<String>()
    var city = ObservableField<String>()
    var bg = ObservableField<String>()
    var self = ObservableField(0)


    var likeCount = ObservableField<String>()

    var focusCount = ObservableField<String>()

    var fansCount = ObservableField<String>()


    var visible = ObservableField<Boolean>(false)

    private fun LoadChildData(currentItem: Int) {
        if (currentItem == 0) {
            var item1 = items[1] as CavalierPhotoItem
            item1.items.clear()
            var item = items[0] as CavalierDynamicItem
            item.init()
        } else if (currentItem == 1) {
            var item1 = items[0] as CavalierDynamicItem
            item1.items.clear()
            item1.adapter.initDatas(item1.items)
            var item = items[1] as CavalierPhotoItem
            item.init()
        }
    }

    override fun ResultCanalierError(it: Throwable) {
    }


    lateinit var activity: DriverHomeActivity
    fun inject(driverHomeActivity: DriverHomeActivity) {
        this.activity = driverHomeActivity
        HttpRequest.instance.canalierResult = this
        CoroutineScope(uiContext).launch {
            delay(500)
            initTabLayoutChangeUI()
        }
        if (activity.id == null) {
            activity.id = PreferenceUtils.getString(context, USERID)
        }
        RxSubscriptions.add(RxBus.default?.toObservable(DynamicsCategoryEntity.Dynamics::class.java)?.subscribe {
            if (activity.mViewPager!!.currentItem == 0) {
                var mmm = items[0] as CavalierDynamicItem
                mmm.items.forEachIndexed { index, dynamics ->
                    if (dynamics.id == it.id) {
                        mmm.items[index] = it
                    } else if (dynamics.memberId == it.memberId && dynamics.followed != it.followed) {
                        dynamics.followed = it.followed
                        mmm.items.set(index, dynamics)
                    }
                }
                entity?.followed = it.followed
                if (entity!!.followed == 0) {
                    follow.set(false)
                } else {
                    follow.set(true)
                }
                mmm.adapter.initDatas(mmm.items)
            }
        })
        initData()
    }

    fun initData() {
        var map = HashMap<String, String>()
        map.put("id", activity.id!!)
        HttpRequest.instance.CanalierHome(map)
    }

    var mTiltes = arrayOf(getString(R.string.dynamics), getString(R.string.album))
    var adapter = BindingViewPagerAdapter<CavalierItemModel>()
    var items = ObservableArrayList<CavalierItemModel>().apply {
        this.add(CavalierDynamicItem(this@DriverHomeViewModel))
        this.add(CavalierPhotoItem(this@DriverHomeViewModel))
    }

    var itemBinding = ItemBinding.of<CavalierItemModel> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.cavalier_dynamic_model, R.layout.driver_home_item_layout)
            }
            1 -> {
                itemBinding.set(BR.cavalier_photo_model, R.layout.driver_photo_item_layout)
            }
        }
    }

    var pagerTitle = BindingViewPagerAdapter.PageTitles<CavalierItemModel> { position, item ->
        mTiltes[position]
    }

    private fun initTabLayoutChangeUI() {
        for (i in 0..activity.mTabLayout!!.tabCount) {
            val tab = activity.mTabLayout!!.getTabAt(i)
            tab?.customView = getTabView(i)
        }
        activity.mTabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val textView = tab!!.customView?.findViewById(R.id.tab_item_textview) as TextView
                val indicator = tab!!.customView?.findViewById(R.id.view_music_indicator) as View
                textView.paint.isFakeBoldText = false
                indicator.visibility = View.INVISIBLE
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val textView = tab!!.customView?.findViewById(R.id.tab_item_textview) as TextView
                val indicator = tab!!.customView?.findViewById(R.id.view_music_indicator) as View
                textView.paint.isFakeBoldText = true
                indicator.visibility = View.VISIBLE
            }
        })
    }

    private fun getTabView(position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.social_layout_tab, null)
        val textView = view.findViewById(R.id.tab_item_textview) as TextView
        val indicator = view.findViewById(R.id.view_music_indicator) as View
        textView.text = mTiltes[position]
        if (position == 0) {
            textView.paint.isFakeBoldText = true
            indicator.visibility = View.VISIBLE
        } else {
            indicator.visibility = View.INVISIBLE
        }
        return view
    }

    var follow = ObservableField<Boolean>(true)


    var CurrentClickTime = 0L
    fun onClick(view: View) {
        if (System.currentTimeMillis() - CurrentClickTime < 1000) {
            return
        } else {
            CurrentClickTime = System.currentTimeMillis()
        }

        when (view.id) {
            R.id.msg_click -> {
                Log.e(this.javaClass.name, "${Gson().toJson(entity)}")
                if (entity?.Member?.tel.isNullOrEmpty()) {
                    Toast.makeText(context, "未绑定手机号", Toast.LENGTH_SHORT).show()
                    return
                }
                var fr = ARouter.getInstance().build(RouterUtils.Chat_Module.Chat_AC).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
                var bundle = Bundle()
                bundle.putString(RouterUtils.Chat_Module.Chat_CONV_TITLE, entity?.Member?.name)
                bundle.putString(RouterUtils.Chat_Module.Chat_TARGET_ID, entity?.Member?.tel)
                bundle.putString(RouterUtils.Chat_Module.Chat_App_Key, "35e2033d379dabfde25d9321")
                bundle.putString(RouterUtils.Chat_Module.Chat_DRAFT, "")
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                fr.arguments = bundle
                activity.start(fr)
            }
            R.id.home_focus_click -> {
                HttpRequest.instance.DynamicFocusResult = this
                if (follow.get()!!) {
                    entity!!.followed = 0
                    follow.set(false)
                } else {
                    entity!!.followed = 1
                    follow.set(true)
                }

                var map = java.util.HashMap<String, String>()
                map["fansMemberId"] = entity!!.Member?.id.toString()
                HttpRequest.instance.getDynamicsFocus(map)
            }

            R.id.bg_click -> {
                if (activity?.id == PreferenceUtils.getString(context, USERID)) {
                    DialogUtils.showFragmentAnim(activity!!, 0)
                    DialogUtils.lisentner = this@DriverHomeViewModel
                }
            }
            R.id.like_click -> {

//                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_GET_LIKE).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity?.Member!!.id).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location).navigation(activity.activity, PRIVATE_DATA_RETURN)
//                var fr = activity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity?.Member!!.id)
                var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_GET_LIKE).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
                model.arguments = bundle
                activity.start(model)
            }
            R.id.fans_home_click -> {
                if (self.get() == 2) {
//                    var fr = activity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
                    var bundle = Bundle()
                    bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                    bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity?.Member!!.id)
                    var model = ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_FANS_AC).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
                    model.arguments = bundle
                    activity.start(model)
//                    ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_FANS_AC).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity?.Member!!.id).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location).navigation(activity.activity, PRIVATE_DATA_RETURN)
                }
            }
            R.id.focus_home_click -> {
                if (self.get() == 2) {
//                    var fr = activity.parentFragment as BaseFragment<ViewDataBinding, BaseViewModel>
                    var bundle = Bundle()
                    bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                    bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity?.Member!!.id)
                    var model = ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_FOCUS_AC).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
                    model.arguments = bundle
                    activity.startForResult(model, PRIVATE_DATA_RETURN)
//                    ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_FOCUS_AC).withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity?.Member!!.id).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location).navigation(activity.activity, PRIVATE_DATA_RETURN)
                }
            }
            R.id.canalier_back -> {
//                activity.doback()
                activity._mActivity!!.onBackPressedSupport()
            }
        }
    }
}