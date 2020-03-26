package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.util.Log
import android.view.View
import com.cstec.administrator.party_module.Activity.OrganizationListActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.io.Serializable


class OrganizationListViewModel : BaseViewModel(), HttpInteface.PartyOrganization, TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        activity.returnBack()
    }

    override fun onComponentFinish(view: View) {
    }

    var titleComponent = TitleComponent()
    override fun getPartyOrganizationSuccess(it: String) {
        var data = Gson().fromJson<OrganData>(it, OrganData::class.java)
        //指导单位
        if (data.guidanceUnit != null && !data.guidanceUnit!!.split(",").isNullOrEmpty()) {
            var guid = data.guidanceUnit!!.split(",")
            var organ = Organization()
            organ.itemType = 0
            organ.title = "指导单位"
            items.add(organ)
            guid.forEach {
                var or = Organization()
                or.itemType = 1
                or.title = it
                items.add(or)
            }
        }
        if (!data.sponsorUnit.isNullOrEmpty() && !data.sponsorUnit!!.split(",").isNullOrEmpty()) {
            var guid = data.sponsorUnit!!.split(",")
            var organ = Organization()
            organ.itemType = 0
            organ.title = "主办单位"
            items.add(organ)
            guid.forEach {
                var or = Organization()
                or.itemType = 1
                or.title = it
                items.add(or)
            }
        }
        if (!data.coOrganizer.isNullOrEmpty() && !data.coOrganizer!!.split(",").isNullOrEmpty()) {
            var guid = data.coOrganizer!!.split(",")
            var organ = Organization()
            organ.itemType = 0
            organ.title = "协办单位"
            items.add(organ)
            guid.forEach {
                var or = Organization()
                or.itemType = 1
                or.title = it
                items.add(or)
            }
        }

        if (!data.undertakingUnit.isNullOrEmpty() && !data.undertakingUnit!!.split(",").isNullOrEmpty()) {
            var guid = data.undertakingUnit!!.split(",")
            var organ = Organization()
            organ.itemType = 0
            organ.title = "承办单位"
            items.add(organ)
            guid.forEach {
                var or = Organization()
                or.itemType = 1
                or.title = it
                items.add(or)
            }
        }

        //主办单位
        //协办单位
        //承办单位
        //赞助单位


    }

    override fun getPartyOrganizationError(it: Throwable) {
    }

    lateinit var activity: OrganizationListActivity
    fun inject(organizationListActivity: OrganizationListActivity) {
        this.activity = organizationListActivity
        titleComponent.title.set("组织机构")
        titleComponent.arrowVisible.set(false)
        titleComponent.rightText.set("")
        titleComponent.setCallBack(this)
        initData()
    }

    private fun initData() {
        var map = HashMap<String, String>()
        map["id"] = activity.id.toString()
        HttpRequest.instance.partyOrganization = this
        HttpRequest.instance.getPartyOrganization(map)
    }

    var adapter = BindingRecyclerViewAdapter<Organization>()

    var items = ObservableArrayList<Organization>()

    var itemBinding = ItemBinding.of<Organization> { itemBinding, position, item ->
        when (item.itemType) {
            0 -> {
                itemBinding.set(BR.organization_model, R.layout.organization_item_title)
            }
            1 -> {
                itemBinding.set(BR.organization_model, R.layout.organization_item)
            }
        }
    }


    class OrganData : Serializable {
        var basicsId = 0
        var coOrganizer: String? = null
        var guidanceUnit: String? = null
        var id = 0
        var label: String? = null
        var ridingOfficerMemberId = 0
        var sponsorUnit: String? = null
        var undertakingUnit: String? = null

    }

    class Organization : Serializable {
        var itemType = 0
        var title = ""
    }
}