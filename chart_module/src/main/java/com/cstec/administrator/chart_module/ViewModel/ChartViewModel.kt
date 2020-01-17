package com.cstec.administrator.chart_module.ViewModel

import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import cn.jpush.im.android.api.JMessageClient
import com.cstec.administrator.chart_module.Activity.ChatActivity
import com.cstec.administrator.chart_module.Adapter.ViewPagerAdapter
import com.zk.library.Base.BaseViewModel
import com.cstec.administrator.chart_module.Fragment.ContactsFragment
import com.cstec.administrator.chart_module.Fragment.ChatRoomFragment
import com.cstec.administrator.chart_module.Fragment.ConversationListFragment
import com.cstec.administrator.chart_module.R
import kotlinx.android.synthetic.main.activity_char.*
class ChartViewModel : BaseViewModel(), ViewPager.OnPageChangeListener, View.OnClickListener {

     var mContactsFragment: ContactsFragment? = null
     var mChatRoomFragment: ChatRoomFragment? = null
     var mConvListFragment: ConversationListFragment? = null
     override fun onClick(v: View?) {
        when (v!!.getId()) {
            R.id.actionbar_msg_btn -> activity.main_view.setCurrentItem(0, false)
            R.id.actionbar_chatroom_btn -> activity.main_view.setCurrentItem(1, false)
            R.id.actionbar_contact_btn -> activity.main_view.setCurrentItem(2, false)
            R.id.actionbar_me_btn -> activity.main_view.setCurrentItem(3, false)
        }
    }

    override fun onPageScrollStateChanged(p0: Int) {
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(position: Int) {
        activity.main_view.setButtonColor(position)

    }

    lateinit var activity: ChatActivity
    var fragments = ArrayList<Fragment>()

    fun inject(chatActivity: ChatActivity) {
        this.activity = chatActivity
        mConvListFragment= ConversationListFragment()
        mChatRoomFragment = ChatRoomFragment()
        mContactsFragment= ContactsFragment()
        fragments.add(mConvListFragment!!)
        fragments.add(mChatRoomFragment!!)
        fragments.add(mContactsFragment!!)
        var viewPagerAdapter = ViewPagerAdapter(chatActivity.supportFragmentManager,
                fragments)

        chatActivity.main_view.setViewPagerAdapter(viewPagerAdapter)
    }

//    fun sortConvList() {
//        mConvListFragment.sortConvList()
//    }
}