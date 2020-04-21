package com.cstec.administrator.party_module.ViewModel

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ViewDataBinding
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.cstec.administrator.party_module.Activity.PartyDetailActivty
import com.cstec.administrator.party_module.BR
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.BasePartyItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailIntroduceItemModel
import com.cstec.administrator.party_module.ItemModel.ActiveDetail.PartyDetailPhotoItemModel
import com.cstec.administrator.party_module.PartyDetailEntity
import com.cstec.administrator.party_module.R
import com.elder.zcommonmodule.Base_URL
import com.elder.zcommonmodule.Inteface.TitleClickListener
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseFragment
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
import org.cs.tec.library.Utils.ConvertUtils
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
        if (entity.DISTANCE == null) {
            dis.set("0")
        } else {
            dis.set((entity.DISTANCE).toString())
        }

        Log.e(this.javaClass.name, "${entity.DISTANCE};$dis")
        collection.set(entity.IS_COLLECTION)
        state.set(entity.ACTIVITY_STATUS)
        restoreTime.set(entity.ACTIVITY_START + "至" + entity.ACTIVITY_STOP)
        if (entity.TICKET_PRICE.isNullOrEmpty() || entity.TICKET_PRICE!!.toDouble() <= 0) {
            entity.TICKET_PRICE = "免费"
        } else {
            entity.TICKET_PRICE = getString(R.string.rmb) + entity.TICKET_PRICE
        }
        data.set(entity)
        Log.e("party", "${Gson().toJson(data)}")
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
        items.add(PartyDetailPhotoItemModel().setActivity(partyDetailActivty.activity!!).setPartyId(partyDetailActivty!!.code).ItemViewModel(this@PartyDetailViewModel))
    }

    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.ACTIVE_WEB_GO_TO_APP -> {
                partyDetailActivty._mActivity!!.onBackPressedSupport()
            }
        }
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
                partyDetailActivty._mActivity!!.onBackPressedSupport()
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
                var bundle = Bundle()
                bundle.putSerializable(RouterUtils.PartyConfig.PARTY_LOCATION, partyDetailActivty.location)
                bundle.putInt(RouterUtils.PartyConfig.PARTY_ID, data.get()!!.CODE)
                startFragment(partyDetailActivty, RouterUtils.PartyConfig.ENROLL, bundle)
            }
            R.id.ivPartyDetailTrans -> {
                //分享
                shareToWxSmallProgram(data.get()!!.CODE, data.get()!!.TITLE, data.get()!!.FILE_NAME_URL)
            }
            R.id.avatar_click -> {
                if (!data.get()!!.NAME.isNullOrEmpty()) {
                    var bundle = Bundle()
                    bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, partyDetailActivty.location)
                    bundle.putSerializable(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, data.get()?.RIDING_OFFICER_MEMBER_ID)
                    var model = ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME).navigation() as BaseFragment<ViewDataBinding, BaseViewModel>
                    model.arguments = bundle
                    partyDetailActivty.start(model)
                }
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

}