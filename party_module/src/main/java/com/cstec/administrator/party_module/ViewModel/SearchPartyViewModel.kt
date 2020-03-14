package com.cstec.administrator.party_module.ViewModel

import android.databinding.ObservableArrayList
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.cstec.administrator.party_module.Activity.SearchPartyActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_search_party.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import java.io.Serializable


class SearchPartyViewModel : BaseViewModel(), HttpInteface.PartySearch, TextView.OnEditorActionListener {
    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            initData(activity.search_et.text.toString().trim())
        }
        return true
    }

    override fun getPartySearchSuccess(it: String) {
        Log.e("result", "搜索数据" + it)
    }

    override fun getPartySearchError(it: Throwable) {
        Log.e("result", "搜索错误" + it)
    }


    fun onClick(view: View) {

    }

    var adapter = BindingRecyclerViewAdapter<PartySearchEntity>()

    var items = ObservableArrayList<PartySearchEntity>()

    var itemBinding = ItemBinding.of<PartySearchEntity> { itemBinding, position, item ->
        itemBinding.set(BR.party_search_model, R.layout.party_search_item)
    }


    class PartySearchEntity : Serializable {


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