package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import com.cstec.administrator.party_module.Activity.OrganizationListActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.io.Serializable


class OrganizationListViewModel : BaseViewModel(), HttpInteface.PartyOrganization {
    override fun getPartyOrganizationSuccess(it: String) {

    }

    override fun getPartyOrganizationError(it: Throwable) {
    }

    lateinit var activity:OrganizationListActivity
    fun inject(organizationListActivity: OrganizationListActivity) {
        this.activity = organizationListActivity
        initData()
    }

    private fun initData() {
        var map = HashMap<String, String>()
        map["id"] = activity.id!!
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

    class Organization : Serializable {
        var itemType = 0
        var title = ""
    }

}