package com.elder.logrecodemodule.ViewModel

import android.databinding.ObservableArrayList
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.listener.SimpleClickListener
import com.elder.logrecodemodule.Activity.SearchActivity
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.R
import com.elder.zcommonmodule.Entity.CountryMemberEntity
import com.elder.zcommonmodule.Entity.SearchEntity
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class SameCityViewModel : BaseViewModel(), HttpInteface.SearchMember, TextView.OnEditorActionListener, com.elder.zcommonmodule.Inteface.SimpleClickListener {
    override fun onSimpleClick(entity: Any) {
        var entity = entity as CountryMemberEntity
        ARouter.getInstance()
                .build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, activity.location)
                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, entity.id)
                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1)
                .navigation()

    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            initData()
        }
        return true
    }

    override fun SearchSuccess(it: String) {
        var entity = Gson().fromJson<SearchEntity>(it, SearchEntity::class.java)
        if (!items.isEmpty()) {
            items.clear()
        }
        entity.data?.forEach {
            items.add(it)
        }
    }

    override fun SearchError(it: Throwable) {

    }


    lateinit var activity: SearchActivity
    fun inject(searchActivity: SearchActivity) {
        this.activity = searchActivity
        HttpRequest.instance.queryMember = this
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.cancle -> {
                finish()
            }
        }
    }

    var pageSize = 1
    var length = 10

    private fun initData() {

        var map = HashMap<String, String>()
        map["yAxis"] = activity.location!!.longitude.toString()
        map["xAxis"] = activity.location!!.latitude.toString()
        map["pageSize"] = pageSize.toString()
        map["length"] = length.toString()
        map["name"] = str
        HttpRequest.instance.SearchMember(map)
    }


    var str = ""

    var onEditTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            str = t
        }
    })


    var listener: com.elder.zcommonmodule.Inteface.SimpleClickListener = this

    var adapter = BindingRecyclerViewAdapter<CountryMemberEntity>()

    var items = ObservableArrayList<CountryMemberEntity>()

    var itemBinding = ItemBinding.of<CountryMemberEntity>(BR.search_child_model, R.layout.search_child_layout).bindExtra(BR.listener, listener)


}