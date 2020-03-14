package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import com.cstec.administrator.party_module.Activity.EnrollListActivity
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.zk.library.Base.BaseViewModel
import me.tatarka.bindingcollectionadapter2.BR
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.io.Serializable


class EnrollViewModel : BaseViewModel(), HttpInteface.PartySign {
    override fun getPartySignError(it: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPartySignSuccess(it: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    lateinit var activity: EnrollListActivity
    fun inject(enrollListActivity: EnrollListActivity) {
        this.activity = enrollListActivity
        initData()
    }
    var start = 1
    var pageSize = 10
    private fun initData() {
        HttpRequest.instance.partySign = this
        var map = HashMap<String, String>()
        map["id"] = activity.id!!
        map["start"] = start.toString()
        map["pageSize"] = pageSize.toString()

    }


    var adapter = BindingRecyclerViewAdapter<EnrollEntity>()

    var items = ObservableArrayList<EnrollEntity>()

    var itemBinding = ItemBinding.of<EnrollEntity>(BR.enroll_model, R.layout.enroll_item_layout)


    class EnrollEntity : Serializable {


    }


}