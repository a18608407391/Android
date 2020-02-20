package com.cstec.administrator.chart_module.Activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.event.ConversationRefreshEvent
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.event.MessageRetractEvent
import cn.jpush.im.android.api.event.OfflineMessageEvent
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.Base.ChartBaseActivity
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.MsgViewModel
import com.cstec.administrator.chart_module.databinding.ActivityMsgBinding
import com.zk.library.Base.BaseApplication
import kotlinx.android.synthetic.main.activity_msg.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.uiContext
import org.greenrobot.eventbus.EventBus
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.MSG_RETURN_REFRESH_REQUEST
import com.elder.zcommonmodule.MSG_RETURN_REQUEST
import com.zk.library.Utils.RouterUtils
import com.zk.library.Utils.StatusbarUtils


@Route(path = RouterUtils.Chat_Module.MSG_AC)
class MsgActivity : ChartBaseActivity<ActivityMsgBinding, MsgViewModel>() {


    @Autowired(name = RouterUtils.SocialConfig.SOCIAL_LOCATION)
    @JvmField
    var location: Location? = null


    private var mSearchHead: LinearLayout? = null
    private var mHeader: LinearLayout? = null
    private var mLoadingHeader: RelativeLayout? = null
    private var mLoadingIv: ImageView? = null
    private var mLoadingTv: LinearLayout? = null
    private var mSearch: LinearLayout? = null
    override fun initVariableId(): Int {
        return BR.msg_model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        StatusbarUtils.setRootViewFitsSystemWindows(this, true)
        StatusbarUtils.setTranslucentStatus(this)
        StatusbarUtils.setStatusBarMode(this, true, 0x00000000)
        return R.layout.activity_msg
    }


    override fun initViewModel(): MsgViewModel? {
        return ViewModelProviders.of(this)[MsgViewModel::class.java]
    }


    override fun initData() {
        super.initData()
        initView()
        mViewModel?.inject(this)
    }


    override fun doPressBack() {
        super.doPressBack()
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(this, object : NavCallback() {
            override fun onArrival(postcard: Postcard?) {
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.initReceiver()
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mViewModel?.mReceiver)
    }


    private fun initView() {
        mHeader = layoutInflater.inflate(R.layout.conv_list_head_view, conv_list_view, false) as LinearLayout
        mSearchHead = layoutInflater.inflate(R.layout.conversation_head_view, conv_list_view, false) as LinearLayout
        mLoadingHeader = layoutInflater.inflate(R.layout.jmui_drop_down_list_header, conv_list_view, false) as RelativeLayout
        mLoadingIv = mLoadingHeader!!.findViewById<View>(R.id.jmui_loading_img) as ImageView
        mLoadingTv = mLoadingHeader!!.findViewById<View>(R.id.loading_view) as LinearLayout
        mSearch = mSearchHead!!.findViewById<View>(R.id.search_title) as LinearLayout
//        conv_list_view.addHeaderView(mLoadingHeader)
//        conv_list_view.addHeaderView(mSearchHead)
//        conv_list_view.addHeaderView(mHeader)
    }

    fun setConvListAdapter(adapter: ListAdapter) {
        conv_list_view!!.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MSG_RETURN_REQUEST -> {
                if (resultCode == MSG_RETURN_REQUEST) {
                    setResult(MSG_RETURN_REQUEST)
                    finish()
                }
            }
            MSG_RETURN_REFRESH_REQUEST -> {
                mViewModel?.initNet()
            }
        }
    }

    fun setListener(onClickListener: View.OnClickListener) {
        mSearch!!.setOnClickListener(onClickListener)
    }

    fun setItemListeners(onClickListener: AdapterView.OnItemClickListener) {
        conv_list_view!!.onItemClickListener = onClickListener
    }

    fun setLongClickListener(listener: AdapterView.OnItemLongClickListener) {
        conv_list_view!!.onItemLongClickListener = listener
    }


    fun showHeaderView() {
        mHeader!!.findViewById<View>(R.id.network_disconnected_iv).visibility = View.VISIBLE
        mHeader!!.findViewById<View>(R.id.check_network_hit).visibility = View.VISIBLE
    }

    fun dismissHeaderView() {
        mHeader!!.findViewById<View>(R.id.network_disconnected_iv).visibility = View.GONE
        mHeader!!.findViewById<View>(R.id.check_network_hit).visibility = View.GONE
    }


    fun showLoadingHeader() {
        mLoadingIv!!.visibility = View.VISIBLE
        mLoadingTv!!.visibility = View.VISIBLE
        var drawable = mLoadingIv!!.drawable as AnimationDrawable
        drawable.start()
    }

    fun dismissLoadingHeader() {
        mLoadingIv!!.visibility = View.GONE
        mLoadingTv!!.visibility = View.GONE
    }

    fun setNullConversation(isHaveConv: Boolean) {
        if (isHaveConv) {
            null_conversation!!.visibility = View.GONE
        } else {
            null_conversation!!.visibility = View.VISIBLE
        }
    }


    fun setUnReadMsg(count: Int) {
//        CoroutineScope(uiContext).launch {
//            if (all_unread_number != null) {
//                if (count > 0) {
//                    mAllUnReadMsg!!.visibility = View.VISIBLE
//                    if (count < 100) {
//                        mAllUnReadMsg!!.text = count.toString() + ""
//                    } else {
//                        mAllUnReadMsg!!.text = "99+"
//                    }
//                } else {
//                    mAllUnReadMsg!!.visibility = View.GONE
//                }
//            }
//        }
    }


    fun onEvent(event: MessageEvent) {
        setUnReadMsg(JMessageClient.getAllUnReadMsgCount())
        var msg = event.message
        if (msg.targetType === ConversationType.group) {
            var groupId = (msg.targetInfo as GroupInfo).groupID
            var conv = JMessageClient.getGroupConversation(groupId)
            if (conv != null) {
                if (msg.isAtMe) {
                    BaseApplication.isAtMe.put(groupId, true)
                    mViewModel?.mListAdapter?.putAtConv(conv, msg.id)
                }
                if (msg.isAtAll) {
                    BaseApplication.isAtall.put(groupId, true)
                    mViewModel?.mListAdapter?.putAtAllConv(conv, msg.id)
                }

                mViewModel?.refresh(conv)
            }
        } else {
            var userInfo = msg.targetInfo as UserInfo
            var targetId = userInfo.userName
            var conv = JMessageClient.getSingleConversation(targetId, userInfo.appKey)
            CoroutineScope(uiContext).launch {
                if (conv != null) {
                    if (TextUtils.isEmpty(userInfo.avatar)) {
                        userInfo.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String, avatarBitmap: Bitmap) {
                                if (responseCode == 0) {
                                    mViewModel?.mListAdapter?.notifyDataSetChanged()
                                }
                            }
                        })
                    }
                    if (conv.type != ConversationType.chatroom) {
                        mViewModel?.mListAdapter?.setToTop(conv)
                    }
                }
            }
        }
    }


    fun onEven(event: OfflineMessageEvent) {
        var conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android") && conv.getType() != ConversationType.chatroom) {
            mViewModel?.refresh(conv)
//            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
        }
    }

    fun onEven(event: MessageRetractEvent) {
        var conv = event.getConversation();
        mViewModel?.refresh(conv)
    }

    fun onEven(event: ConversationRefreshEvent) {
        var conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android") && conv.getType() != ConversationType.chatroom) {
            mViewModel?.refresh(conv)
//            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
            //多端在线未读数改变时刷新
            if (event.getReason().equals(ConversationRefreshEvent.Reason.UNREAD_CNT_UPDATED)) {
                mViewModel?.refresh(conv)
//                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
            }
        }
    }

    fun onEventMainThread(event: MessageReceiptStatusChangeEvent) {
        mViewModel?.mListAdapter?.notifyDataSetChanged()
    }


}