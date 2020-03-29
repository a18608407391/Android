package com.cstec.administrator.party_module.ViewModel

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.net.Uri
import android.support.design.widget.TabLayout
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.party_module.Activity.PartyDetailActivty
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailIntroduceItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailPhotoItemModel
import com.cstec.administrator.party_module.PartyDetailEntity
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Inteface.TitleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_party_clock_detail.*
import kotlinx.android.synthetic.main.activity_party_detail.*
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer


class PartyDetailViewModel : BaseViewModel(), TitleClickListener, HttpInteface.PartyDetail, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>, HttpInteface.PartyRestore_inf {
    override fun PartyRestoreSucccess(it: String) {
        partyDetailActivty.dismissProgressDialog()
        if (collection.get() == 0) {
            collection.set(1)
        } else {
            collection.set(0)
        }
    }

    override fun PartyRestoreError(it: Throwable) {
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        var t = p0?.position
        if (t == 0) {
            initData()
        } else if (t == 1) {
            var model = items[1] as PartyDetailPhotoItemModel
            model.initData(true)
        }
    }


    var dis = ObservableField<String>()
    var visible = ObservableField<Boolean>(false)
    var data = ObservableField<PartyDetailEntity>()

    var typeData = ObservableArrayList<String>()

    var members = ObservableArrayList<String>()

    var restoreTime = ObservableField<String>()


    override fun getPartyDetailSuccess(it: String) {
        if (it.isNullOrEmpty()) {
            return
        }

        if (!typeData.isNullOrEmpty()) {
            typeData!!.clear()
            members!!.clear()
        }

        var entity = Gson().fromJson<PartyDetailEntity>(it, PartyDetailEntity::class.java)
        if (!entity.LABEL.isNullOrEmpty()) {
            entity.LABEL!!.split(",").forEach {
                typeData.add(it)
            }
        }
        if (!entity.SIGN_UP.isNullOrEmpty()) {
            entity.SIGN_UP!!.forEach {
                if (it.HEAD_IMG_FILE.isNullOrEmpty()) {
                    members.add("")
                } else {
                    members.add(it.HEAD_IMG_FILE)
                }
            }
        }

        dis.set((entity.SQRTVALUE / 1000).toString())
        collection.set(entity.IS_COLLECTION)
        state.set(entity.ACTIVITY_STATUS)
        restoreTime.set(entity.ACTIVITY_START + "至" + entity.ACTIVITY_STOP)
        if (entity.TICKET_PRICE.isNullOrEmpty() || entity.TICKET_PRICE!!.toDouble() <= 0) {
            entity.TICKET_PRICE = "免费"
        } else {
            entity.TICKET_PRICE = getString(R.string.rmb) + entity.TICKET_PRICE
        }
        data.set(entity)
        if (partyDetailActivty.mPartyDetailViewPager.currentItem == 0) {
            var t = items[0] as PartyDetailIntroduceItemModel
            t.initData()
        } else if (partyDetailActivty.mPartyDetailViewPager.currentItem == 1) {
            var t = items[1] as PartyDetailPhotoItemModel
            t.initData(true)
        }
        partyDetailActivty.detail_refreshLayout.finishRefresh()
    }

    override fun getPartyDetailError(it: Throwable) {

    }

    override fun onTitleArrowClick(entity: Any) {

    }

    lateinit var partyDetailActivty: PartyDetailActivty
    fun inject(partyDetailActivty: PartyDetailActivty) {
        this.partyDetailActivty = partyDetailActivty
        items.add(PartyDetailIntroduceItemModel().ItemViewModel(this@PartyDetailViewModel))
        items.add(PartyDetailPhotoItemModel().setActivity(partyDetailActivty).setPartyId(partyDetailActivty!!.code).ItemViewModel(this@PartyDetailViewModel))
    }

    fun initData() {
        HttpRequest.instance.partyDetail = this
        var map = HashMap<String, String>()
        map["id"] = partyDetailActivty.code!!.toString()
        map["x"] = partyDetailActivty.location!!.longitude.toString()
        map["y"] = partyDetailActivty.location!!.latitude.toString()
        HttpRequest.instance.getPartyDetail(map)
    }

    var tabCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {
        }
    })
    var collection = ObservableField(0)
    var state = ObservableField(1)
    @SuppressLint("MissingPermission")
    fun onClick(view: View) {
        when (view.id) {
            R.id.detail_iv_back -> {
                partyDetailActivty.returnBack()
            }
            R.id.tel_phone -> {
                var intent = Intent(Intent.ACTION_CALL)
                var datas = Uri.parse("tel:" + data.get()?.SERVICE_TEL)
                intent.data = datas
                partyDetailActivty.startActivity(intent)
            }
            R.id.restore_party -> {
                partyDetailActivty.showProgressDialog(getString(R.string.http_loading))
                HttpRequest.instance.partyRestore = this
                var map = HashMap<String, String>()
                map["targetId"] = data.get()!!.CODE.toString()
                map["targetIdType"] = data.get()!!.BIG_TYPE.toString()
                HttpRequest.instance.getPartyRestore(map)
            }
            R.id.right_now -> {
                if (state.get() == 1) {
                    ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC)
                            .withString(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_ID, data.get()!!.ID.toString())
                            .withString(RouterUtils.PartyConfig.PARTY_CODE, data.get()!!.CODE.toString())
                            .withInt(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE, 5).navigation()
                }
            }
            R.id.detail_members_click -> {
                ARouter.getInstance().build(RouterUtils.PartyConfig.ENROLL).withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION, partyDetailActivty.location).withInt(RouterUtils.PartyConfig.PARTY_ID, data.get()!!.CODE).navigation()
            }
        }
    }

    var titlelistener: TitleClickListener = this


    var mTiltes = arrayOf("介绍", "相册")

    var pagerTitle = BindingViewPagerAdapter.PageTitles<BasePartyItemModel> { position, item ->
        mTiltes[position]
    }
    var adapter = BindingViewPagerAdapter<BasePartyItemModel>()

    var items = ObservableArrayList<BasePartyItemModel>().apply {

    }
    var itemBinding = ItemBinding.of<BasePartyItemModel> { itemBinding, position, item ->
        when (position) {
            0 -> {
                itemBinding.set(BR.detail_introduce, R.layout.party_detail_part_introduce)
            }
            1 -> {
                itemBinding.set(BR.detail_photo, R.layout.party_detail_part_photo)
            }
        }
    }
}