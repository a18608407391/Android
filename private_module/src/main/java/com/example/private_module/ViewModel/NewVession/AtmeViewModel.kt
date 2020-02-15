package com.example.private_module.ViewModel.NewVession

import android.databinding.ObservableArrayList
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.AtmeData
import com.elder.zcommonmodule.Entity.FansEntity
import com.elder.zcommonmodule.Inteface.SimpleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.example.private_module.Activity.NewVession.AtmeActivity
import com.example.private_module.BR
import com.example.private_module.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_myfans.*
import kotlinx.android.synthetic.main.atme_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class AtmeViewModel : BaseViewModel(), SwipeRefreshLayout.OnRefreshListener, HttpInteface.queryAtmeList, SimpleClickListener, TitleComponent.titleComponentCallBack {
    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {
    }

    override fun onSimpleClick(entity: Any) {

    }

    override fun AtmeListSuccess(it: String) {
        Log.e("result", it)
        if (it.isNullOrEmpty()) {
            return
        } else {
            var itemse = Gson().fromJson<ArrayList<AtmeData>>(it, object : TypeToken<ArrayList<AtmeData>>() {}.type)
            itemse.forEach {
                items.add(it)
            }
        }
    }

    override fun AtmeListError(ex: Throwable) {
        Toast.makeText(context, "网络错误，请重试！", Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        pageSize = 1
        lenth = 20
        initDatas()
        CoroutineScope(uiContext).launch {
            delay(10000)
            atmeActivity.at_swipe.isRefreshing = false
        }
    }

    var listener: SimpleClickListener = this
    var titleComponent = TitleComponent()

    var adapter = BindingRecyclerViewAdapter<AtmeData>()

    var items = ObservableArrayList<AtmeData>()

    var itemBinding = ItemBinding.of<AtmeData>(BR.atme_item_entity, R.layout.atme_items_layout).bindExtra(BR.listener, listener)


    var pageSize = 1

    var lenth = 20

    var scrollerBinding = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
            Log.e("result", "加载更多" + t)
            if (t > 1) {
                pageSize++
                initDatas()
            }
        }
    })

    private fun initDatas() {
        HttpRequest.instance.getatmelistResult = this
        var mao = HashMap<String, String>()
        HttpRequest.instance.queryAtMeListRequest(mao)
    }


    lateinit var atmeActivity: AtmeActivity
    fun inject(atmeActivity: AtmeActivity) {
        this.atmeActivity = atmeActivity
        initDatas()
        titleComponent.title.set("@我的")
        titleComponent.rightIcon.set(context.getDrawable(R.drawable.read_all))
        titleComponent.rightText.set("")
        titleComponent.callback = this
    }
}