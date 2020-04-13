package com.cstec.administrator.chart_module.Fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.event.*
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cstec.administrator.chart_module.R
import com.cstec.administrator.chart_module.ViewModel.MessageViewModel
import com.cstec.administrator.chart_module.databinding.FragmentMessageBinding
import com.zk.library.Utils.RouterUtils
import com.cstec.administrator.chart_module.BR
import com.cstec.administrator.chart_module.Base.CharBaseFragment
import com.cstec.administrator.chart_module.Base.ChartBaseActivity
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.MSG_RETURN_REFRESH_REQUEST
import com.elder.zcommonmodule.MSG_RETURN_REQUEST
import com.google.gson.Gson
import com.zk.library.Base.BaseApplication
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_msg.*
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.cs.tec.library.Base.Utils.uiContext
import org.cs.tec.library.USERID
import org.greenrobot.eventbus.EventBus


/**
 * 消息Fragment
 * */
@Route(path = RouterUtils.Chat_Module.MESSAGE_FRAGMENT)
class MessageFragment : CharBaseFragment<FragmentMessageBinding, MessageViewModel>() {

    private var mSearchHead: LinearLayout? = null
    private var mHeader: LinearLayout? = null
    private var mLoadingHeader: RelativeLayout? = null
    private var mLoadingIv: ImageView? = null
    private var mLoadingTv: LinearLayout? = null
    private var mSearch: LinearLayout? = null

    override fun initContentView(): Int {
        return R.layout.fragment_message
    }

    override fun initVariableId(): Int {
        return BR.messageViewModel
    }

    override fun initData() {
        super.initData()
        initView()
        viewModel!!.inject(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.e("message", "$hidden")
        if (!hidden) {
            viewModel!!.initNet()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("message", "onResume")
        viewModel?.initReceiver()
        viewModel!!.initConvListAdapter()

    }

    override fun onDestroy() {
        Log.e(this.javaClass.name, "onDestroy")
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(viewModel?.mReceiver)
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

    fun showHeaderView() {
        mHeader!!.findViewById<View>(R.id.network_disconnected_iv).visibility = View.VISIBLE
        mHeader!!.findViewById<View>(R.id.check_network_hit).visibility = View.VISIBLE
    }

    fun dismissHeaderView() {
        mHeader!!.findViewById<View>(R.id.network_disconnected_iv).visibility = View.GONE
        mHeader!!.findViewById<View>(R.id.check_network_hit).visibility = View.GONE
    }


    fun setListener(onClickListener: View.OnClickListener) {
        mSearch!!.setOnClickListener(onClickListener)
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

    fun setItemListeners(onClickListener: AdapterView.OnItemClickListener) {
        messageListConv!!.onItemClickListener = onClickListener
    }

    fun setLongClickListener(listener: AdapterView.OnItemLongClickListener) {
        messageListConv!!.onItemLongClickListener = listener
    }

    fun setNullConversation(isHaveConv: Boolean) {
        if (isHaveConv) {
            messgaeTvNullConversation!!.visibility = View.GONE
        } else {
            messgaeTvNullConversation!!.visibility = View.VISIBLE
        }
    }


    fun setConvListAdapter(adapter: ListAdapter) {
        messageListConv!!.adapter = adapter
    }

    fun onEvent(event: MessageEvent) {
        Log.e(this.javaClass.name, "MessageEvent:${Gson().toJson(event)}")
        setUnReadMsg(JMessageClient.getAllUnReadMsgCount())
        var msg = event.message
        if (msg.targetType === ConversationType.group) {
            var groupId = (msg.targetInfo as GroupInfo).groupID
            var conv = JMessageClient.getGroupConversation(groupId)
            if (conv != null) {
                if (msg.isAtMe) {
                    BaseApplication.isAtMe.put(groupId, true)
                    viewModel?.mListAdapter?.putAtConv(conv, msg.id)
                }
                if (msg.isAtAll) {
                    BaseApplication.isAtall.put(groupId, true)
                    viewModel?.mListAdapter?.putAtAllConv(conv, msg.id)
                }
                viewModel?.refresh(conv)
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
                                    viewModel?.mListAdapter?.notifyDataSetChanged()
                                }
                            }
                        })
                    }
                    if (conv.type != ConversationType.chatroom) {
                        viewModel?.mListAdapter?.setToTop(conv)
                    }
                }
            }
        }
    }

    fun onEven(event: OfflineMessageEvent) {
        Log.e(this.javaClass.name, "OfflineMessageEvent:${Gson().toJson(event)}")
        var conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android") && conv.getType() != ConversationType.chatroom) {
            viewModel?.refresh(conv)
//            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
        }
    }

    fun onEven(event: MessageRetractEvent) {
        Log.e(this.javaClass.name, "MessageRetractEvent:${Gson().toJson(event)}")
        var conv = event.getConversation();
        viewModel?.refresh(conv)
    }

    fun onEven(event: ConversationRefreshEvent) {
        Log.e(this.javaClass.name, "ConversationRefreshEvent:${Gson().toJson(event)}")
        var conv = event.getConversation();
        if (!conv.getTargetId().equals("feedback_Android") && conv.getType() != ConversationType.chatroom) {
            viewModel?.refresh(conv)
//            mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
            //多端在线未读数改变时刷新
            if (event.getReason().equals(ConversationRefreshEvent.Reason.UNREAD_CNT_UPDATED)) {
                viewModel?.refresh(conv)
//                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST, conv));
            }
        }
    }

    fun onEventMainThread(event: MessageReceiptStatusChangeEvent) {
        Log.e(this.javaClass.name, "onEventMainThread:${Gson().toJson(event)}")
        viewModel?.mListAdapter?.notifyDataSetChanged()
    }

}