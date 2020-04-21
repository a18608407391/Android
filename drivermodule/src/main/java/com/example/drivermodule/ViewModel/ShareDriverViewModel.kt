package com.example.drivermodule.ViewModel

import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Utils.DialogUtils
import com.elder.zcommonmodule.Widget.CoverFlowLayoutManger
import com.example.drivermodule.Activity.ShareDriverActivity
import com.example.drivermodule.Adapter.ShareAdapter
import com.elder.zcommonmodule.Entity.ShareEntity
import com.example.drivermodule.R
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.zk.library.Base.BaseViewModel
import kotlinx.android.synthetic.main.activity_share_driver.*
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import com.zk.library.Base.BaseApplication
import com.zk.library.Bus.event.RxBusEven
import com.zk.library.Utils.RouterUtils
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Utils.ConvertUtils
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShareDriverViewModel : BaseViewModel(), ShareAdapter.AddCarItemClickCallBack, ShareAdapter.editClickCallBack, CoverFlowLayoutManger.OnSelected, DialogUtils.Companion.IconUriCallBack {
    override fun getIcon(iconUri: Uri) {
        this.cameraUri = iconUri
    }

    override fun onItemSelected(position: Int) {
    }

    override fun editClick(view: View, position: Int) {

    }

    override fun onItemClick(view: View, position: Int) {

    }

    var cameraUri: Uri? = null
    var bitmapcrop = ObservableField<Bitmap>()

    //头像
    var avatar = ObservableField<String>()

    //昵称
    var nickname = ObservableField<String>()
    // 生成时间
    var date = ObservableField<String>()
    //距离
    var distance = ObservableField<String>()
    //配速
    var peisu = ObservableField<String>()
    //用时
    var time = ObservableField<String>()

    lateinit var shareDriverActivity: ShareDriverActivity
    fun inject(shareDriverActivity: ShareDriverActivity) {
        this.shareDriverActivity = shareDriverActivity
        initRecycleView()
        var list = ArrayList<ShareEntity>()
        for (i in 0..2) {
            if (i == 0) {
                list.add(shareDriverActivity.entity!!)
            } else if (i == 1) {
                shareDriverActivity.entity!!.secondBitmap = "default"
                list.add(shareDriverActivity.entity!!)
            }
        }
        adapter?.setCarDatas(list)
    }

    override fun doRxEven(it: RxBusEven?) {
        super.doRxEven(it)
        when (it!!.type) {
            RxBusEven.SHARE_SUCCESS -> {
                shareDriverActivity._mActivity!!.onBackPressedSupport()
            }
            RxBusEven.SHARE_CANCLE -> {

            }
        }
    }

    var adapter: ShareAdapter? = null
    private fun initRecycleView() {
        shareDriverActivity.share_recycle.setGreyItem(true)
        shareDriverActivity.share_recycle.setAlphaItem(true)
        shareDriverActivity.share_recycle.setOnItemSelectedListener(this)
        adapter = ShareAdapter(shareDriverActivity)
        shareDriverActivity.share_recycle.adapter = adapter
        adapter!!.setOnItemClickListener(this)
        adapter!!.setEditOnItemClickListener(this)
//        initDatas()
    }

    private fun buildTransaction(type: String): String {
        return if (type == null) System.currentTimeMillis().toString() else type + System.currentTimeMillis()
    }

    fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray? {
        var output = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output)
        if (needRecycle) {
            bmp.recycle()
        }
        var result = output.toByteArray()
        output.close()
        return result
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.share_back -> {
                shareDriverActivity._mActivity!!.onBackPressedSupport()
            }
            R.id.share_frend -> {
                if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                    Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                } else {
                    var i = shareDriverActivity.share_recycle.selectedPos
                    var hold = shareDriverActivity.share_recycle.findViewHolderForAdapterPosition(i)
                    var layout = hold?.itemView
                    var bitmap = ConvertUtils.view2Bitmap(layout)
                    var newBitmap = Bitmap.createBitmap(bitmap, 0, 0, ConvertUtils.dp2px(28F), ConvertUtils.dp2px(50F))
                    var imgobj = WXImageObject(bitmap)
                    var msg = WXMediaMessage()
                    msg.mediaObject = imgobj
                    msg.title = getString(R.string.share_title)
                    msg.description = getString(R.string.share_description) + distance.get()
                    msg.thumbData = bmpToByteArray(newBitmap!!, true)
                    var req = SendMessageToWX.Req()
                    req.transaction = buildTransaction("img")
                    req.message = msg
                    req.scene = SendMessageToWX.Req.WXSceneSession
                    BaseApplication.getInstance().mWxApi.sendReq(req)
                }
            }
            R.id.share_frendQ -> {
                if (!BaseApplication.getInstance().mWxApi.isWXAppInstalled) {
                    Toast.makeText(context, "您手机尚未安装微信，请安装后再登录", Toast.LENGTH_SHORT).show()
                } else {
                    var i = shareDriverActivity.share_recycle.selectedPos
                    var hold = shareDriverActivity.share_recycle.findViewHolderForAdapterPosition(i)
                    var layout = hold?.itemView
                    var bitmap = ConvertUtils.view2Bitmap(layout)
                    var newBitmap = Bitmap.createBitmap(bitmap, 0, 0, ConvertUtils.dp2px(28F), ConvertUtils.dp2px(50F))
                    var imgobj = WXImageObject(bitmap)
                    var msg = WXMediaMessage()
                    msg.mediaObject = imgobj
                    msg.title = getString(R.string.share_title)
                    msg.description = getString(R.string.share_description) + distance.get()
                    msg.thumbData = bmpToByteArray(newBitmap!!, true)
                    var req = SendMessageToWX.Req()
                    req.transaction = buildTransaction("img")
                    req.message = msg
                    req.scene = SendMessageToWX.Req.WXSceneTimeline
                    BaseApplication.getInstance().mWxApi.sendReq(req)
                }
            }

            R.id.restore_phone -> {
                var i = shareDriverActivity.share_recycle.selectedPos
                var hold = shareDriverActivity.share_recycle.findViewHolderForAdapterPosition(i)
                var layout = hold?.itemView
                var bitmap = ConvertUtils.view2Bitmap(layout)
                var newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap?.width!!, bitmap.height!!)
                val sdf = SimpleDateFormat("yyyyMMddHHmmss")
                var path = Environment.getExternalStorageDirectory().path + "/test_" + sdf.format(Date()) + ".png"
                var fos = FileOutputStream(path)
                var b = newBitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
                if (b!!) {
                    Toast.makeText(context, getString(R.string.stored), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, getString(R.string.stored_fail), Toast.LENGTH_SHORT).show()
                }
            }

            R.id.share_left_tv -> {
                var date = adapter?.mListDatas?.get(1)
                var bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.share_second_bg)
                date?.secondBitmap = "default"
                adapter?.mListDatas?.set(1, date!!)
                adapter?.setCarDatas(adapter?.mListDatas!!)
            }

            R.id.share_right_tv -> {
                DialogUtils.showFragmentAnim(shareDriverActivity!!, 0)
                DialogUtils.lisentner = this@ShareDriverViewModel
            }
        }
    }
}