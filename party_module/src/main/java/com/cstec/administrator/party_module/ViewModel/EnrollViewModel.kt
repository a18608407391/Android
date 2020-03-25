package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.view.View
import com.cstec.administrator.party_module.Activity.EnrollListActivity
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.io.Serializable


class EnrollViewModel : BaseViewModel(), HttpInteface.PartySign, TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {
    }

    override fun getPartySignError(it: Throwable) {

    }

    override fun getPartySignSuccess(it: String) {
        if (it.isNullOrEmpty()) {
            return
        }
        var entity = Gson().fromJson<EnrollEntity>(it, EnrollEntity::class.java)
        entity.data!!.forEach {
            items.add(it)
        }
    }

    lateinit var activity: EnrollListActivity
    fun inject(enrollListActivity: EnrollListActivity) {
        this.activity = enrollListActivity
        titleComponent.title.set("报名列表")
        titleComponent.arrowVisible.set(false)
        titleComponent.rightText.set("")
        titleComponent.callback = this
        initData()
    }

    var start = 1
    var pageSize = 10
    private fun initData() {
        HttpRequest.instance.partySign = this
        var map = HashMap<String, String>()
        map["id"] = activity.id.toString()
        map["start"] = start.toString()
        map["pageSize"] = pageSize.toString()
        HttpRequest.instance.getPartySign(map)
    }


    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<EnrollEntity.Enroll>()

    var items = ObservableArrayList<EnrollEntity.Enroll>()

    var itemBinding = ItemBinding.of<EnrollEntity.Enroll>(BR.enroll_model, R.layout.enroll_item_layout)


    class EnrollEntity : Serializable {
        var data: ArrayList<Enroll>? = null

        class Enroll : Serializable {
            var NAME: String? = null
            var CREATE_DATA: String? = null
            var ID = 0
            var HEAD_IMG_FILE: String? = null
            var RN = 0
        }
    }


}