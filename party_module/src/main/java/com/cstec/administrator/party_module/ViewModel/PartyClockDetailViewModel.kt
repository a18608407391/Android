package com.cstec.administrator.party_module.ViewModel

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.cstec.administrator.party_module.Activity.PartyClockDetailActivity
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailIntroduceItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailPhotoItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailRankingItemModel
import com.cstec.administrator.party_module.PartyDetailEntity
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import me.tatarka.bindingcollectionadapter2.BindingViewPagerAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Base.Utils.ioContext
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.Utils.ConvertUtils
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.text.SimpleDateFormat


class PartyClockDetailViewModel : BaseViewModel(), HttpInteface.PartyDetail, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab>, HttpInteface.PartyRestore_inf {
    override fun PartyRestoreSucccess(it: String) {
        party.dismissProgressDialog()
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
        } else if (t == 2) {
            var model = items[2] as PartyDetailRankingItemModel
            model.initData()
        }
    }


    var day = ObservableField<String>()

    var dis = ObservableField<String>()
    override fun getPartyDetailSuccess(it: String) {
        if (it.isNullOrEmpty()) {
            return
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
        dis.set((entity.DISTANCE).toString())
        collection.set(entity.IS_COLLECTION)
        state.set(entity.ACTIVITY_STATUS)
        restoreTime.set(entity.ACTIVITY_START + "至" + entity.ACTIVITY_STOP)
        if (entity.TICKET_PRICE.isNullOrEmpty() || entity.TICKET_PRICE!!.toDouble() <= 0) {
            entity.TICKET_PRICE = "免费"
        } else {
            entity.TICKET_PRICE = getString(R.string.rmb) + entity.TICKET_PRICE
        }
        data.set(entity)

        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var start = sdf.parse(entity.ACTIVITY_START).time
        var stop = sdf.parse(entity.ACTIVITY_STOP).time
//        if(start){
//
//        }

        if (start < System.currentTimeMillis()) {
            //活动还没开始
            day.set(entity.DAY)
        } else if (stop < System.currentTimeMillis()) {
            day.set("0")
        } else {
            var m = (stop - System.currentTimeMillis()) / 1000 / 3600
            day.set(m.toString())
        }

        if (party.mPartyDetailClockViewPager.currentItem == 0) {
            var t = items[0] as PartyDetailIntroduceItemModel
            t.initDataClock()
        } else if (party.mPartyDetailClockViewPager.currentItem == 1) {
            var t = items[1] as PartyDetailPhotoItemModel
            t.initData(true)
        } else if (party.mPartyDetailClockViewPager.currentItem == 2) {
            var model = items[2] as PartyDetailRankingItemModel
            model.initData()
        }

        party.clock_refreshLayout.finishRefresh()
    }

    override fun getPartyDetailError(it: Throwable) {

    }


    lateinit var party: PartyClockDetailActivity
    fun inject(partyClockDetailActivity: PartyClockDetailActivity) {
        this.party = partyClockDetailActivity
        items.add(PartyDetailIntroduceItemModel().ItemViewModel(this@PartyClockDetailViewModel))
        items.add(PartyDetailPhotoItemModel().setActivity(partyClockDetailActivity.activity!!).setPartyId(party.code).ItemViewModel(this@PartyClockDetailViewModel))
        items.add(PartyDetailRankingItemModel().setCode(party.code).ItemViewModel(this@PartyClockDetailViewModel))
    }
    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.ACTIVE_WEB_GO_TO_APP -> {
                party._mActivity!!.onBackPressedSupport()
            }
        }
    }
    var collection = ObservableField(0)

    var visible = ObservableField<Boolean>(false)
    var data = ObservableField<PartyDetailEntity>()

    var typeData = ObservableArrayList<String>()

    var members = ObservableArrayList<String>()

    var restoreTime = ObservableField<String>()

    var mTiltes = arrayOf("介绍", "相册", "排名")
    var state = ObservableField(1)
    var pagerTitle = BindingViewPagerAdapter.PageTitles<BasePartyItemModel> { position, item ->
        mTiltes[position]
    }

    var tabCommand = BindingCommand(object : BindingConsumer<Int> {
        override fun call(t: Int) {

            if (t == 0) {
                var model = items[0] as PartyDetailIntroduceItemModel
                model.initData()
            } else if (t == 1) {
                var model = items[1] as PartyDetailPhotoItemModel
                model.initData(true)
            } else if (t == 2) {
                var model = items[2] as PartyDetailRankingItemModel
                model.initData()
            }
        }
    })

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
            2 -> {
                itemBinding.set(BR.detail_ranking, R.layout.party_detail_part_ranking)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun onClick(view: View) {
        when (view.id) {
            R.id.clock_iv_back -> {
                party._mActivity!!.onBackPressedSupport()
//                party.returnBack()
            }
            R.id.tel_phone -> {
                var intent = Intent(Intent.ACTION_CALL)
                var datas = Uri.parse("tel:" + data.get()?.SERVICE_TEL)
                intent.data = datas
                party.startActivity(intent)
            }
            R.id.restore_party -> {
                party.showProgressDialog(getString(R.string.http_loading))
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
            R.id.members_click -> {
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION, party.location)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, data.get()!!.CODE)
                startFragment(party, RouterUtils.PartyConfig.ENROLL, bundle)
//                ARouter.getInstance().build(RouterUtils.PartyConfig.ENROLL).withSerializable(RouterUtils.PartyConfig.PARTY_LOCATION, party.location).withInt(RouterUtils.PartyConfig.PARTY_ID, data.get()!!.CODE).navigation()
            }
            R.id.sponsor_click -> {
                var bundle = Bundle()
                bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, data.get()!!.ID)
                startFragment(party , RouterUtils.PartyConfig.ORGANIZATION, bundle)
//                ARouter.getInstance().build(RouterUtils.PartyConfig.ORGANIZATION).withInt(RouterUtils.PartyConfig.PARTY_ID, data.get()!!.ID).navigation()
            }
            R.id.ivClockDetailTrans -> {

                //分享
                shareToWxSmallProgram(data.get()!!.CODE, data.get()!!.TITLE, data.get()!!.FILE_NAME_URL)
            }
        }
    }

    private fun shareToWxSmallProgram(id: Int, title: String?, url: String?) {
        // 小程序消息desc
        CoroutineScope(ioContext).async {
            var str = url!!.replace("/home/uploadFile/images", "")
            var url = Base_URL + "AmoskiActivity/userCenterManage/getImg?appToken=activeImg&imgUrl=" + str
            var file = Glide.with(context)
                    .load(url)
                    .downloadOnly(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
            var files = file.get()
            if (files != null) {
                Log.e(this.javaClass.name, "${files.path}")
                var bitmap = BitmapFactory.decodeFile(files.path)
                var newBitmap = ConvertUtils.compressByQuality(bitmap, 32000, true)
                var wxMiniProgramObject = WXMiniProgramObject()
                wxMiniProgramObject.webpageUrl = "www.amoski.net" // 兼容低版本的网页链接
                wxMiniProgramObject.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;// 正式版:0，测试版:1，体验版:2
                wxMiniProgramObject.userName = "gh_3fdce548de72";     // 小程序原始id
                wxMiniProgramObject.path = "/pages/userCenter-activityview/userCenter-activityview?aId=$id"

                var wxMediaMessage = WXMediaMessage(wxMiniProgramObject)
                wxMediaMessage.title = title;                   // 小程序消息title
                wxMediaMessage.description = "";
                wxMediaMessage.thumbData = newBitmap                // 小程序消息封面图片，小于128k
                var req = SendMessageToWX.Req()
                req.transaction = ""
                req.message = wxMediaMessage
                req.scene = SendMessageToWX.Req.WXSceneSession  // 目前只支持会话
                BaseApplication.getInstance().mWxApi.sendReq(req)
            }
        }
    }

    fun initData() {
        HttpRequest.instance.partyDetail = this
        var map = HashMap<String, String>()
        map["id"] = party.code!!.toString()
        map["x"] = party.location!!.longitude.toString()
        map["y"] = party.location!!.latitude.toString()
        HttpRequest.instance.getPartyDetail(map)
    }

}