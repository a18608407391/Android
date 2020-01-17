package com.example.private_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Component.TitleComponent
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse
import com.elder.zcommonmodule.USER_TOKEN
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Utils.NameLengthFilter
import com.example.private_module.Activity.RequestIdeaActivity
import com.example.private_module.BR
import com.example.private_module.Entitiy.RequestEntity
import com.example.private_module.Entitiy.RequestUiEntity
import com.example.private_module.R
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.binding.command.ViewAdapter.checkbox.CheckBoxInfo
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_userinfo.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import okhttp3.*
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.util.concurrent.TimeUnit


class RequestViewModel : BaseViewModel(), TitleComponent.titleComponentCallBack {
    var checkInfo = BindingCommand(object : BindingConsumer<CheckBoxInfo> {
        override fun call(t: CheckBoxInfo) {
            if (t.flag) {
                checkList.add(t.position)
            } else {
                checkList.remove(t.position)
            }
        }
    })

    fun loadDatas() {
        var token = PreferenceUtils.getString(org.cs.tec.library.Base.Utils.context, USER_TOKEN)
        requestIdeaActivity.showProgressDialog(getString(R.string.get_user_request_opition))
        Observable.create(ObservableOnSubscribe<Response> {
            var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            var map = HashMap<String, String>()
            var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
            var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/getUserFeedback").build()
            var call = client.newCall(request)
            var response = call.execute()
            it.onNext(response)
        }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
            return@Function it.body()?.string()
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            var entity = Gson().fromJson<RequestEntity>(it, RequestEntity::class.java)
            entity.data?.forEach {
                items.add(RequestUiEntity(ObservableField(false), ObservableField(it.id), ObservableField(it.name!!), ObservableField(it.pId)))
            }
            requestIdeaActivity.dismissProgressDialog()
        }
    }

    var adapter = BindingRecyclerViewAdapter<RequestUiEntity>()
    var items = ObservableArrayList<RequestUiEntity>().apply {
    }
    var itemBinding = ItemBinding.of<RequestUiEntity> { itemBinding, position, item ->
        itemBinding.set(BR.request_item, R.layout.request_item_layout).bindExtra(BR.position, position).bindExtra(BR.requestmodel, this@RequestViewModel)
    }


    var checkList = ArrayList<Int>()

    val viewHolder = BindingRecyclerViewAdapter.ViewHolderFactory { binding -> PictureViewHolder(binding.root) }

    private class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onComponentClick(view: View) {
        finish()
    }

    override fun onComponentFinish(view: View) {

    }


    lateinit var requestIdeaActivity: RequestIdeaActivity
    fun inject(requestIdeaActivity: RequestIdeaActivity) {
        this.requestIdeaActivity = requestIdeaActivity
        requestIdeaActivity.edit_introduce.filters = arrayOf(NameLengthFilter(160))
        requestIdeaActivity.edit_introduce.setSelection(0)
        component.callback = this
        component.title.set(getString(R.string.request_idea))
        component.arrowVisible.set(false)
        component.rightText.set("")
        loadDatas()
    }

    var content = ObservableField<String>("")
    var inputSize = ObservableField<String>("0")
    var contentChange = BindingCommand(object : BindingConsumer<String> {
        override fun call(t: String) {
            content.set(t)
            inputSize.set(t.length.toString())
        }
    })
    var component = TitleComponent()


    fun onClick(view: View) {
        when (view.id) {
            R.id.request_btn -> {
                var t = ""
                checkList.forEachIndexed { index, i ->
                    if (index == checkList.size - 1) {
                        t += items[i].id.get()
                    } else {
                        t += "" + items[i].id.get() + ","
                    }
                }
                if (content.get() == null || content.get()?.isEmpty()!!) {
                    Toast.makeText(context, getString(R.string.request_content_empty), Toast.LENGTH_SHORT).show()
                    return
                }
                if (t.isEmpty()) {
                    Toast.makeText(context, getString(R.string.select_request_opition), Toast.LENGTH_SHORT).show()
                    return
                }
                var token = PreferenceUtils.getString(org.cs.tec.library.Base.Utils.context, USER_TOKEN)
                requestIdeaActivity.showProgressDialog(getString(R.string.upload_request))
                Observable.create(ObservableOnSubscribe<Response> {
                    var client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                    var map = HashMap<String, String>()
                    map.put("suggestionId", t)
                    map.put("remake", content.get()!!)
                    var body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), Gson().toJson(map))
                    var request = Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").addHeader("appToken", token).post(body).url(Base_URL + "AmoskiActivity/userCenterManage/userSubmitSuggest").build()
                    var call = client.newCall(request)
                    var response = call.execute()
                    it.onNext(response)
                }).subscribeOn(Schedulers.io()).map(Function<Response, String> {
                    return@Function it.body()?.string()
                }).observeOn(AndroidSchedulers.mainThread()).subscribe {
                    Log.e("result", it)
                    var result = Gson().fromJson<BaseResponse>(it, BaseResponse::class.java)
                    if (result.code == 0) {
                        finish()
                    }
                    Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    requestIdeaActivity.dismissProgressDialog()
                }
            }
        }
    }
}