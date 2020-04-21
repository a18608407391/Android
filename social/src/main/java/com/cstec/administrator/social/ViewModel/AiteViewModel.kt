package com.cstec.administrator.social.ViewModel

import android.content.Intent
import android.databinding.ObservableArrayList
import android.os.Bundle
import android.util.Log
import android.view.View
import com.cstec.administrator.social.Activity.AiteActivity
import com.cstec.administrator.social.Entity.FocusMemberListEntity
import com.cstec.administrator.social.Entity.SocialUserModel
import com.cstec.administrator.social.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.SELECT_USER_CALLBACK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class AiteViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack, HttpInteface.SocialDynamicsFocuserList {
    override fun ResultFocuserSuccess(it: String) {
        Log.e("result", it + "数据")

        var en = Gson().fromJson<FocusMemberListEntity>(it, FocusMemberListEntity::class.java)
        en.data?.forEach {
            var model = SocialUserModel()
            model.name = it.memberName
            model.id = it.fansMemberId
            model.img = it.memberImage
            items.add(model)
        }

    }

    override fun ResultFocuserError(ex: Throwable) {
        Log.e("result", "ResultFocuserError")
    }

    override fun onComponentClick(view: View) {
//        finish()
        aiteActivity._mActivity!!.onBackPressedSupport()
    }

    override fun onComponentFinish(view: View) {
        var strList = ArrayList<SocialUserModel>()
        var co = 0
        items.forEach {
            if (it.check) {
                co += it.name!!.length
                strList.add(it)
            }
        }
        configItems.forEach {
            if (it.check) {
                co += it.name!!.length
                strList.add(it)
            }
        }

        var bundle = Bundle()
        bundle.putString("array", Gson().toJson(strList))
        bundle.putInt("Count", co + strList.size)
        aiteActivity.setFragmentResult(SELECT_USER_CALLBACK, bundle)
        aiteActivity._mActivity!!.onBackPressedSupport()
//        aiteActivity.setResult(SELECT_USER_CALLBACK, intent)
//        finish()
    }


    var component = TitleComponent()

    lateinit var aiteActivity: AiteActivity
    fun inject(aiteActivity: AiteActivity) {
        this.aiteActivity = aiteActivity
        component.arrowVisible.set(false)
        component.title.set(getString(R.string.warm_watch))
        component.rightText.set(getString(R.string.finish))
        component.rightVisibleType.set(true)
        component.callback = this
        var map = HashMap<String, String>()
        HttpRequest.instance.DynamicFocusListResult = this
        map["pageSize"] = "1"
        map["length"] = "50"
        map["memberName"] = ""
        HttpRequest.instance.getDynamicsFocusList(map)
    }

    fun ItemClick(model: SocialUserModel) {


    }

    fun ImgItemClick(model: SocialUserModel) {
        var index = items.indexOf(model)
        model.check = !model.check
        items[index] = model
    }

    var adapter = BindingRecyclerViewAdapter<SocialUserModel>()

    var items = ObservableArrayList<SocialUserModel>().apply {

    }

    var replaceItems = ObservableArrayList<SocialUserModel>()

    var itemBinding = ItemBinding.of<SocialUserModel>(BR.aite_user_model, R.layout.social_user_select_layout).bindExtra(BR.aite_item_model, this@AiteViewModel)

    var configItems = ObservableArrayList<SocialUserModel>()

    var onEditTextChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            if (!t.isNullOrEmpty()) {
                replaceItems.clear()
                if (configItems.isEmpty()) {
                    items.forEach {
                        if (it.name!!.contains(t) || it.name == t) {
                            replaceItems.add(it)
                        }
                    }
                    replaceList(items, replaceItems)
                } else {
                    configItems.forEach {
                        if (t.startsWith(it.name!!) || it.name == t) {
                            replaceItems.add(it)
                        }
                    }
                    replaceList(items, replaceItems)
                }
            } else {
                if (!configItems.isEmpty()) {
                    replaceItems.clear()
                    items.clear()
                    configItems.forEach {
                        items.add(it)
                    }
                    configItems.clear()
                }
            }
        }
    })


    fun replaceList(ex: ObservableArrayList<SocialUserModel>, ex2: ObservableArrayList<SocialUserModel>) {
        if (configItems.isEmpty()) {
            items.forEach {
                configItems.add(it)
            }
        }
        var config = ArrayList<SocialUserModel>()
        ex.forEach {
            config.add(it)
        }
        ex.clear()
        ex2.forEach {
            ex.add(it)
        }
        ex2.clear()
        config.forEach {
            ex2.add(it)
        }
    }
}