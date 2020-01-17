package com.cstec.administrator.social.ViewModel

import android.app.Dialog
import android.databinding.ObservableArrayList
import android.support.design.widget.TabLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.elder.zcommonmodule.Entity.DynamicsCategoryEntity
import com.cstec.administrator.social.Fragment.SocialFragment
import com.cstec.administrator.social.ItemViewModel.SocialItemModel
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.Entity.CanalierHomeEntity
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.elder.zcommonmodule.Utils.DialogUtils
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.ServiceEven
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.RouterUtils.SocialConfig.Companion.SOCIAL_LOCATION
import kotlinx.android.synthetic.main.fragment_social.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import java.text.DecimalFormat


class SocialViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener, HttpInteface.SocialDynamicsList, HttpInteface.SocialDynamicsFocuserList, HttpInteface.SocialDynamicsLikerList, HttpInteface.SocialDynamicsCollection, HttpInteface.SocialDynamicsComment, HttpInteface.SocialDynamicsFocus, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>, HttpInteface.deleteSocialResult {
    override fun deleteSocialSuccess(it: String) {
        var cur = items[socialFragment.social_viewpager.currentItem]
        if (cur.deleteItem != null) {
            cur.items.remove(cur.deleteItem)
            cur.adapter.initDatas(cur.items)
        }
        Toast.makeText(context, getString(R.string.social_delete_success), Toast.LENGTH_SHORT).show()
    }

    override fun deleteSocialError(it: Throwable) {
        Toast.makeText(context, getString(R.string.social_delete_fail), Toast.LENGTH_SHORT).show()
    }

    var curItem = 0
    var progress: Dialog? = null

    override fun onTabReselected(p0: TabLayout.Tab?) {

    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        var position = p0?.position
        curItem = position!!
        var ite = items[position!!]
        if (ite.items.isEmpty()) {
            ite.initDatas(0)
        }
    }

    override fun ResultFocusSuccess(it: String) {
        var cur = items[socialFragment.social_viewpager.currentItem]
        var entity = cur.items[cur.focusClickPosition]
        entity.followed = 1
        cur.items[cur.focusClickPosition] = entity
        cur.adapter.notifyItemRangeChanged(0, cur.adapter.itemCount, "focusClick")
        Toast.makeText(socialFragment.activity, getString(R.string.focused_success), Toast.LENGTH_SHORT).show()
    }

    override fun ResultFocusError(ex: Throwable) {
    }

    override fun ResultCommentSuccess(it: String) {
    }

    override fun ResultCommentError(ex: Throwable) {
    }

    override fun ResultCollectionSuccess(it: String) {
    }

    override fun ResultCollectionError(ex: Throwable) {
    }

    override fun ResultLikerSuccess(it: String) {
    }

    override fun ResultLikerError(ex: Throwable) {
    }

    override fun ResultFocuserSuccess(it: String) {
    }

    override fun ResultFocuserError(ex: Throwable) {
    }


    override fun ResultSDListSuccess(it: String) {
        if (progress != null && progress!!.isShowing!!) {
            progress!!.dismiss()
        }
        if (it.length < 10) {
            return
        }
        if (items[socialFragment.social_viewpager.currentItem].pageSize != 1) {

        } else {
            items[socialFragment.social_viewpager.currentItem].items.clear()
        }
//        if (model.socialFragment.social_viewpager.currentItem == type) {
//            var json = JSONObject(it).getString("data")
//        var list = Gson().fromJson<ArrayList<DynamicsCategoryEntity.Dynamics>>(json, object : TypeToken<ArrayList<DynamicsCategoryEntity.Dynamics>>() {}.type)

        var entity = Gson().fromJson<DynamicsCategoryEntity>(it, DynamicsCategoryEntity::class.java)
//            Log.e("result", "123213213213213213213" + "type" + type)
////        Log.e("result", entity.data!!.size.toString() + "集合尺寸")

        if (entity.data != null && entity.data?.size!! > 0) {
            entity.data?.forEach {
                //                Log.e("result", "执行了额")

                var dis = AMapUtils.calculateLineDistance(LatLng(location!!.latitude, location!!.longitude), LatLng(it.xAxis!!.toDouble(), it.yAxis!!.toDouble()))
                it.distance = DecimalFormat("0.00").format(dis / 1000) + "KM"
                items[socialFragment.social_viewpager.currentItem].items.add(it)
            }
        }
        items[socialFragment.social_viewpager.currentItem].adapter.initDatas(items[socialFragment.social_viewpager.currentItem].items)
        socialFragment.social_swipe.isRefreshing = false
        socialFragment.initSecond()
    }

    override fun ResultSDListError(ex: Throwable) {
        Log.e("result", "ResultSDListError" + socialFragment.social_viewpager.currentItem + "当前" + ex.message)
        if (progress != null && progress?.isShowing!!) {
            progress!!.dismiss()
        }
        socialFragment.social_swipe.isRefreshing = false
    }

    var CurrentClickTime = 0L

    override fun onRefresh() {
        var itemmodel = items[socialFragment.social_viewpager.currentItem]
        itemmodel.curLoad = 0
        itemmodel.page = 20
        itemmodel.pageSize = 1
        itemmodel.initDatas(socialFragment.social_viewpager.currentItem)
        CoroutineScope(uiContext).launch {
            delay(10000)
            socialFragment.social_swipe.isRefreshing = false
        }
    }

    var mTiltes = arrayOf(getString(R.string.focus_str), getString(R.string.hot_str), getString(R.string.nearly))
    var adapter = BindingViewPagerAdapter<SocialItemModel>()
    var items = ObservableArrayList<SocialItemModel>().apply {
        this.add(SocialItemModel(0, this@SocialViewModel))
        this.add(SocialItemModel(1, this@SocialViewModel))
        this.add(SocialItemModel(2, this@SocialViewModel))
    }

    var itemBinding = ItemBinding.of<SocialItemModel> { itemBinding, position, item ->
        itemBinding.set(BR.social_item_model, R.layout.social_item_layout)
    }
    var pagerTitle = BindingViewPagerAdapter.PageTitles<SocialItemModel> { position, item ->
        mTiltes[position]
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.release_photo -> {
                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_RELEASE).withSerializable(SOCIAL_LOCATION, Location(location?.latitude!!, location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, location?.aoiName!!, location!!.poiName)).navigation()
            }
        }
    }

    private fun initTabLayoutChangeUI() {
        for (i in 0..socialFragment.mTabLayout.tabCount) {
            val tab = socialFragment.mTabLayout.getTabAt(i)
            tab?.customView = getTabView(i)
        }
        socialFragment.mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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

    var first = false
    lateinit var socialFragment: SocialFragment
    fun inject(socialFragment: SocialFragment) {
        this.socialFragment = socialFragment
        first = true
        var pos = ServiceEven()
        pos.type = "HomeStart"
        RxBus.default?.post(pos)
        progress = DialogUtils.showProgress(socialFragment.activity!!, getString(R.string.getHttp_location))
        HttpRequest.instance.DynamicListResult = this
        RxSubscriptions.add(RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            invoke(it)
        })
        RxSubscriptions.add(RxBus.default?.toObservable(String::class.java)?.subscribe {
            if (it == "ResultReleaseDynamicsSuccess") {
                if (socialFragment.isAdded) {
                    socialFragment.social_viewpager.currentItem = 0
                    var itemmodel = items[0]
                    itemmodel.page = 20
                    itemmodel.pageSize = 1
                    itemmodel.initDatas(0)
                    CoroutineScope(uiContext).launch {
                        delay(10000)
                        socialFragment.social_swipe.isRefreshing = false
                    }
                }
            }
        })
        RxSubscriptions.add(RxBus.default?.toObservable(DynamicsCategoryEntity.Dynamics::class.java)?.subscribe {
            var m = it
            var its = items[curItem]
            its.items.forEachIndexed { index, dynamics ->
                if (dynamics.id == m.id) {
                    its.items[index] = m
                } else if (dynamics.memberId == m.memberId && dynamics.followed != m.followed) {
                    dynamics.followed = m.followed
                    its.items.set(index, dynamics)
                }
            }
            its.adapter.initDatas(its.items)
        })
        CoroutineScope(uiContext).launch {
            initTabLayoutChangeUI()
        }

        RxSubscriptions.add(RxBus.default?.toObservable(CanalierHomeEntity::class.java)!!.subscribe {
            var m = it
            var its = items[curItem]
            its.items.forEachIndexed { index, dynamics ->
                if (dynamics.memberId == m.Member!!.memberId && dynamics.followed != m.followed) {
                    dynamics.followed = m.followed
                    its.items.set(index, dynamics)
                }
            }
        })
    }

    var location: AMapLocation? = null
    private fun invoke(it: AMapLocation?) {
        if (location == null) {
            if (progress != null && progress!!.isShowing) {
                progress?.dismiss()
            }
            location = it
            var mo = items[0]
            mo.initDatas(0)
        } else {
            location = it
        }
    }
}