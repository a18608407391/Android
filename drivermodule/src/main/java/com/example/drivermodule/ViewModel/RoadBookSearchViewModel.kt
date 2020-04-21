package com.example.drivermodule.ViewModel

import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.drivermodule.Fragment.RoadBookSearchActivity
import com.example.drivermodule.BR
import com.elder.zcommonmodule.Entity.HotData
import com.example.drivermodule.Fragment.RoadBookFirstActivity
import com.example.drivermodule.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class RoadBookSearchViewModel : BaseViewModel(), TextView.OnEditorActionListener, HttpInteface.SearchRoadBook {
    override fun SearchRoadBookSuccess(it: String) {
        roadBookSearchActivity.dismissProgressDialog()
        items.clear()
        var list = Gson().fromJson<ArrayList<HotData>>(it, object : TypeToken<ArrayList<HotData>>() {}.type)
        if (!list.isNullOrEmpty()) {
            list.forEach {
                items.add(it)
            }
        } else {
            Toast.makeText(context, getString(R.string.cannot_find), Toast.LENGTH_SHORT).show()
        }
    }

    override fun SearchRoadBookError(ex: Throwable) {
        Log.e("result", "搜索错误" + ex.message)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == IME_ACTION_SEARCH) {
            var text = v?.text.toString()
            if (text.isNullOrEmpty()) {
                return false
            }
            roadBookSearchActivity.showProgressDialog(getString(R.string.http_loading))
            var map = HashMap<String, String>()
            map["searchVal"] = text
            map["page"] = "1"
            map["limit"] = "30"
            HttpRequest.instance.SearchRoadBookDetail(map)
        }
        return true
    }

    var historyEmpty = ObservableField<Boolean>(true)

    var textChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {

        }
    })


    var immOpen = true

    var time = 0L
    fun onClick(view: View) {
        if (System.currentTimeMillis() - time > 1000) {
            var imm = roadBookSearchActivity.activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            if (roadBookSearchActivity.activity!!.currentFocus != null) {
                var binder = roadBookSearchActivity.activity!!.currentFocus.windowToken
                if (imm!!.isActive && binder != null) {
                    Log.e("result", "isActive" + imm!!.isActive)
                    (roadBookSearchActivity.activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
            roadBookSearchActivity.setFragmentResult(REQUEST_LOAD_ROADBOOK, null)
            roadBookSearchActivity._mActivity!!.onBackPressedSupport()
        }
        time = System.currentTimeMillis()
    }

    fun ItemClick(data: HotData) {
        if (data == null) {
            return
        }
//        ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).withSerializable(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ENTITY, data).navigation(roadBookSearchActivity.activity, REQUEST_LOAD_ROADBOOK)
        roadBookSearchActivity!!.startForResult((ARouter.getInstance().build(RouterUtils.MapModuleConfig.ROAD_BOOK_FIRST_ACTIVITY).navigation() as RoadBookFirstActivity).setHotData(data), REQUEST_LOAD_ROADBOOK)

//        var intent = Intent()
//        intent.putExtra("hotdata", data)
//        roadBookSearchActivity.setResult(REQUEST_LOAD_ROADBOOK,intent)
//        finish()


    }

    lateinit var roadBookSearchActivity: RoadBookSearchActivity
    fun inject(roadBookSearchActivity: RoadBookSearchActivity) {
        HttpRequest.instance.SearchRoadBook = this
        this.roadBookSearchActivity = roadBookSearchActivity
    }


    var adpter = BindingRecyclerViewAdapter<HotData>()

    var items = ObservableArrayList<HotData>()

    var itemBinding = ItemBinding.of<HotData>(BR.roadbook_itemmodel, R.layout.roadbook_search_item_layout).bindExtra(BR.search_model, this@RoadBookSearchViewModel)

}