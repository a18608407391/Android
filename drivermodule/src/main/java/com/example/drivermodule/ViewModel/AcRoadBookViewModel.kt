package com.example.drivermodule.ViewModel.RoadBook

import android.databinding.ObservableArrayList
import com.elder.zcommonmodule.Component.TitleComponent
import com.example.drivermodule.Fragment.RoadHomeActivity
import com.example.drivermodule.BR
import com.example.drivermodule.ItemModel.HotRoadItemModle
import com.example.drivermodule.ItemModel.NearRoadItemModle
import com.example.drivermodule.R
import com.zk.library.Base.BaseViewModel
import com.elder.zcommonmodule.Component.ItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.OnItemBind
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import android.widget.TextView
import android.support.design.widget.TabLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.example.drivermodule.Fragment.RoadBookSearchActivity
import com.zk.library.Utils.RouterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.getColor
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class AcRoadBookViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack, SwipeRefreshLayout.OnRefreshListener {

    var curItems = 0
    override fun onRefresh() {
        if (curItems == 0) {
            var model = items[0] as HotRoadItemModle
            model.initDatas(model.page)

        } else if (curItems == 1) {
            var model = items[1] as NearRoadItemModle
            model.initDatas(model.page)
        }
    }

    override fun onComponentClick(view: View) {
//        finish()
        roadHomeActivity.setFragmentResult(REQUEST_LOAD_ROADBOOK, null)
        roadHomeActivity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {

        roadHomeActivity.startForResult(ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_SEARCH_ACTIVITY).navigation() as RoadBookSearchActivity, REQUEST_LOAD_ROADBOOK)

//        RxBus.default?.post(RxBusEven.getInstance(RxBusEven.ENTER_TO_SEARCH))
//        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_SEARCH_ACTIVITY).navigation(roadHomeActivity.activity, REQUEST_LOAD_ROADBOOK)
    }

    lateinit var roadHomeActivity: RoadHomeActivity
    fun inject(roadHomeActivity: RoadHomeActivity) {
        this.roadHomeActivity = roadHomeActivity
        CoroutineScope(uiContext).launch {
            delay(500)
            initTabLayoutChangeUI()
            (items[0] as HotRoadItemModle).initDatas((items[0] as HotRoadItemModle).page)
        }
    }

    private val mTiltes = arrayOf(getString(R.string.hot_title), getString(R.string.nearly))

    var titleComponent = TitleComponent().apply {
        this.title.set(getString(R.string.road_book_title))
        this.rightText.set("")
        this.rightVisibleType.set(false)
        this.rightIcon.set(context.getDrawable(R.drawable.ic_sousuo))
        this.arrowVisible.set(false)
        this.setCallBack(this@AcRoadBookViewModel)
    }

    var items = ObservableArrayList<ItemViewModel<AcRoadBookViewModel>>().apply {
        this.add(HotRoadItemModle(this@AcRoadBookViewModel, 0))
        this.add(NearRoadItemModle(this@AcRoadBookViewModel, 1))
    }

    var pagerSelectCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            curItems = t
            Log.e("result", "curItems" + t)
            if (t == 1) {
                if ((items[1] as NearRoadItemModle).items.size == 0) {
                    (items[1] as NearRoadItemModle).initDatas((items[1] as NearRoadItemModle).page)
                }
            } else {
//                (items[0] as HotRoadItemModle).initDatas()
            }
        }
    })


    var adapter = BindingViewPagerAdapter<ItemViewModel<AcRoadBookViewModel>>()


    var itembingding = OnItemBind<ItemViewModel<AcRoadBookViewModel>> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.hot_itemmodel, R.layout.hot_item_model_layout)
            }
            1 -> {
                itemBinding.set(BR.near_itemmodel, R.layout.near_item_model_layout)
            }
        }
    }

    var pagerTitle = BindingViewPagerAdapter.PageTitles<ItemViewModel<AcRoadBookViewModel>> { position, item ->
        mTiltes[position]
    }

    private fun initTabLayoutChangeUI() {
        for (i in 0..roadHomeActivity.mTabLayout.tabCount) {
            val tab = roadHomeActivity.mTabLayout.getTabAt(i)
            tab?.customView = getTabView(i)
        }

        roadHomeActivity.mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val textView = tab!!.customView?.findViewById(R.id.tab_item_textview) as TextView
                val indicator = tab!!.customView?.findViewById(R.id.view_music_indicator) as View
                textView.paint.isFakeBoldText = false
                textView.setTextColor(getColor(R.color.blackTextColor))
                indicator.visibility = View.INVISIBLE
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val textView = tab!!.customView?.findViewById(R.id.tab_item_textview) as TextView
                val indicator = tab!!.customView?.findViewById(R.id.view_music_indicator) as View
                textView.paint.isFakeBoldText = true
                textView.setTextColor(getColor(R.color.line_color))
                indicator.visibility = View.VISIBLE
            }
        })
    }

    private fun getTabView(position: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.indicate_layout_tab, null)
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
}