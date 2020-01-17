package com.cstec.administrator.chart_module.ViewModel

import android.app.Dialog
import com.cstec.administrator.chart_module.Fragment.ConversationListFragment
import com.zk.library.Base.BaseViewModel
import cn.jpush.im.android.api.model.Conversation
import com.cstec.administrator.chart_module.Adapter.ConversationListAdapter
import com.cstec.administrator.chart_module.View.ConversationListView




class ConversationViewModel : BaseViewModel() {


    private val mConvListView: ConversationListView? = null
    private val mWidth: Int = 0
    private val mListAdapter: ConversationListAdapter? = null
    private val mDatas = ArrayList<Conversation>()
    private val mDialog: Dialog? = null


    lateinit var conversation: ConversationListFragment
    fun inject(conversationListFragment: ConversationListFragment) {
        this.conversation = conversationListFragment
    }














}