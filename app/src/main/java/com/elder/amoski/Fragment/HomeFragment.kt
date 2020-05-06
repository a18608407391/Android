package com.elder.amoski.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.elder.amoski.Activity.HomeActivity
import com.elder.amoski.BR
import com.elder.amoski.R
import com.elder.amoski.ViewModel.MainFragmentViewModel
import com.elder.amoski.databinding.FragmentMainBinding
import com.elder.zcommonmodule.Entity.HotData
import com.elder.zcommonmodule.Entity.UserInfo
import com.elder.zcommonmodule.GET_USERINFO
import com.elder.zcommonmodule.PRIVATE_DATA_RETURN
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.elder.zcommonmodule.REQUEST_WEB_GO_TO_APP
import com.example.drivermodule.Controller.RoadBookItemModel
import com.example.private_module.UI.UserInfoFragment
import com.zk.library.Base.BaseFragment
import kotlinx.android.synthetic.main.fragment_main.*


class HomeFragment : BaseFragment<FragmentMainBinding, MainFragmentViewModel>() {


    companion object {
        fun newInstance(): HomeFragment {
            var args = Bundle()
            var fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun initContentView(): Int {
        return R.layout.fragment_main
    }

    override fun initVariableId(): Int {
        return BR.main_fr_viewmodel
    }

    override fun initData() {
        super.initData()
        main_bottom_bg.setOnCheckedChangeListener(viewModel)
        viewModel?.inject(this)
    }


    var hot: HotData? = null
    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        when (viewModel?.curPosition) {
            0 -> {
                if (resultCode == REQUEST_WEB_GO_TO_APP) {
                    Log.e("result", "REQUEST_WEB_GO_TO_APP_HOME")
                }
            }
            1 -> {
                if (requestCode == REQUEST_LOAD_ROADBOOK) {
                    if (data != null) {
                        loadRoadBook(data!!)
                        viewModel?.lastCheckediD = R.id.main_left
                    }
                } else {
                    viewModel?.social!!.onFragmentResult(requestCode, resultCode, data)
                }
            }
            2 -> {
                viewModel?.mapFr?.onFragmentResult(requestCode, resultCode, data)
            }
            3 -> {

            }
            4 -> {
                if (requestCode == REQUEST_LOAD_ROADBOOK) {
                    if (data != null) {
                        loadRoadBook(data!!)
                    }
                    viewModel?.lastCheckediD = R.id.main_right
                } else if (requestCode == PRIVATE_DATA_RETURN) {
                    var fr = viewModel?.myself as UserInfoFragment
                    fr.getUserInfo(false)
                } else if (requestCode == GET_USERINFO)
                    if (data != null) {
                        var info = data?.getSerializable("userInfo") as UserInfo
                        var fr = viewModel?.myself as UserInfoFragment
                        fr.callback(info)
                    }
            }
        }
    }

    fun loadRoadBook(data: Bundle) {
        //social模块点击跳转路书
        hot = data!!.getSerializable("hotdata") as HotData
        var home = activity as HomeActivity
        home.resume = "road"
        if (viewModel?.mapFr != null) {
            if (viewModel?.mapFr!!.viewModel?.currentPosition == 2) {
                (viewModel?.mapFr!!.viewModel?.items!![2] as RoadBookItemModel).doLoadDatas(hot!!)
            } else {
                viewModel?.mapFr!!.viewModel?.selectTab(2)
            }
        }
        viewModel?.mRadioGroup?.check(R.id.driver_middle)
    }
}