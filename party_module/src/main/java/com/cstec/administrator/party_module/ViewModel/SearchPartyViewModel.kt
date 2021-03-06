package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.Activity.SearchPartyActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_search_party.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Bus.RxBus
import java.io.Serializable


class SearchPartyViewModel : BaseViewModel(), HttpInteface.PartySearch, TextView.OnEditorActionListener {
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            initData(activity.search_et.text.toString().trim())
        }
        return true
    }

    override fun getPartySearchSuccess(it: String) {
        items.clear()
        var list = Gson().fromJson<ArrayList<PartySearchEntity>>(it, object : TypeToken<ArrayList<PartySearchEntity>>() {}.type)
        list.forEach {
            items.add(it)
        }
    }

    override fun getPartySearchError(it: Throwable) {

    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.search_cancle -> {
//                finish()
                activity.pop()
            }
        }
    }

    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.ACTIVE_WEB_GO_TO_APP -> {
                activity._mActivity!!.onBackPressedSupport()
            }
        }
    }

    var adapter = BindingRecyclerViewAdapter<PartySearchEntity>()

    var items = ObservableArrayList<PartySearchEntity>()

    fun itemClick(entity: PartySearchEntity) {
        RxBus.default!!.post(RxBusEven.getInstance(RxBusEven.StatusBar,0))
        when (entity.BIG_TYPE) {
            1 -> {
//                ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
//                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                                Location(activity.location!!.latitude, activity.location!!.longitude)).withInt(RouterUtils.PartyConfig.NavigationType, 3).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, activity.party_city).navigation(activity.activity, object : NavCallback() {
//                            override fun onArrival(postcard: Postcard?) {
//                                finish()
//                            }
//                        })

                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        Location(activity.location!!.latitude, activity.location!!.longitude))
                bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, activity.party_city)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE)
                startFragment(activity, RouterUtils.PartyConfig.PARTY_CLOCK_DETAIL, bundle)
            }
            2 -> {
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        Location(activity.location!!.latitude, activity.location!!.longitude))
                bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, activity.party_city)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE)
                startFragment(activity, RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL, bundle)
//                ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_SUBJECT_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
//                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                                Location(activity.location!!.latitude, activity.location!!.longitude)).withInt(RouterUtils.PartyConfig.NavigationType, 3).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, activity.party_city).navigation(activity.activity, object : NavCallback() {
//                            override fun onArrival(postcard: Postcard?) {
//                                finish()
//                            }
//                        })
            }
            3 -> {

                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
                        Location(activity.location!!.latitude, activity.location!!.longitude))
                bundle.putString(RouterUtils.PartyConfig.PARTY_CITY, activity.party_city)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE)
                startFragment(activity, RouterUtils.PartyConfig.PARTY_DETAIL, bundle)

//                ARouter.getInstance().build(RouterUtils.PartyConfig.PARTY_DETAIL).withInt(RouterUtils.PartyConfig.PARTY_ID, entity.ID)
//                        .withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION,
//                                Location(activity.location!!.latitude, activity.location!!.longitude)).withInt(RouterUtils.PartyConfig.NavigationType, 3).withInt(RouterUtils.PartyConfig.PARTY_CODE, entity.CODE).withString(RouterUtils.PartyConfig.PARTY_CITY, activity.party_city).navigation(activity.activity, object : NavCallback() {
//                            override fun onArrival(postcard: Postcard?) {
//                                finish()
//                            }
//                        })
            }
        }
    }

    var itemBinding = ItemBinding.of<PartySearchEntity> { itemBinding, position, item ->
        when (item.BIG_TYPE) {
            1 -> {
                //打卡
                itemBinding.set(BR.search_party, R.layout.search_items_clock_layout).bindExtra(BR.model, this@SearchPartyViewModel)
            }
            2 -> {
                //主题
                itemBinding.set(BR.search_party, R.layout.search_items_subject_layout).bindExtra(BR.model, this@SearchPartyViewModel)
            }
            3 -> {
                //摩旅活动
                itemBinding.set(BR.search_party, R.layout.search_items_active_layout).bindExtra(BR.model, this@SearchPartyViewModel)
            }
        }
    }


    class PartySearchEntity : Serializable {
        var ID = 0
        var CODE = 0
        var BIG_TYPE = 0
        var TITLE: String? = null
        var STATE = 0
        var DAILY_VISITS = 0
        var TOTAL_VISITS = 0
        var TYPE: String? = null
        var CREATE_DATA: String? = null
        var RECOMMEND_ORDER = 0
        var FILE_NAME_URL: String? = null
        var ACTIVITY_START: String? = null
        var ACTIVITY_STOP: String? = null
        var ACTIVITY_END: String? = null
        var TICKET_PRICE: Double = 0.0
        var DACT_X_AXIS: Double = 0.0
        var DACT_Y_AXIS: Double = 0.0
        var memberImages: String? = null
        var SQRTVALUE: Long = 0
        var X_AXIS: Double = 0.0
        var MEMBER_NAME: String? = null
        var MAN_COUNT = 0
        var PATH_POINT: String? = null
        var DISTANCE: String? = null
        var Y_AXIS: Double = 0.0
        var DAY = 0
    }


    fun initData(name: String) {
        if (name.isNullOrEmpty()) {
            Toast.makeText(context, "搜索数据不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        HttpRequest.instance.partySearch = this
        var map = HashMap<String, String>()
        map["searchKey"] = name
        map["x"] = activity.location!!.longitude.toString()
        map["y"] = activity.location!!.latitude.toString()
        map["city"] = activity.party_city!!
        HttpRequest.instance.getPartySearch(map)
    }

    lateinit var activity: SearchPartyActivity
    fun inject(searchPartyActivity: SearchPartyActivity) {
        this.activity = searchPartyActivity
    }
}