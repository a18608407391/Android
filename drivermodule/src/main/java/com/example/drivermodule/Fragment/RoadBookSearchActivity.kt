package com.example.drivermodule.Fragment

import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.elder.zcommonmodule.REQUEST_LOAD_ROADBOOK
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBookSearchViewModel
import com.example.drivermodule.databinding.ActivityRoadbookSearchBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_roadbook_search.*


@Route(path = RouterUtils.MapModuleConfig.ROAD_BOOK_SEARCH_ACTIVITY)
class RoadBookSearchActivity : BaseFragment<ActivityRoadbookSearchBinding, RoadBookSearchViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_roadbook_search
    }
    override fun initVariableId(): Int {
        return BR.roadbooksearch_viewmodel
    }

    //    override fun initContentView(savedInstanceState: Bundle?): Int {
//        StatusbarUtils.setRootViewFitsSystemWindows(this, false)
//        StatusbarUtils.setTranslucentStatus(this)
//        StatusbarUtils.setStatusBarMode(this, true, 0x000000)
//        return R.layout.activity_roadbook_search
//    }
    override fun initData() {
        super.initData()
        viewModel?.inject(this)
        etx.setOnEditorActionListener(viewModel)
    }
//    override fun initViewModel(): RoadBookSearchViewModel? {
//        return ViewModelProviders.of(this)[RoadBookSearchViewModel::class.java]
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_LOAD_ROADBOOK) {
//            setResult(REQUEST_LOAD_ROADBOOK, data)
//            finish()
//        }
//    }
//    override fun doPressBack() {
//        super.doPressBack()
//        finish()
//    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        Log.e("result", "search" + requestCode)
        if (requestCode == REQUEST_LOAD_ROADBOOK) {
            if (data?.getSerializable("hotdata") != null) {
                setFragmentResult(REQUEST_LOAD_ROADBOOK, data)
                _mActivity!!.onBackPressedSupport()
//                setResult(REQUEST_LOAD_ROADBOOK, data)
//                finish()
            } else {
//                _mActivity!!.onBackPressedSupport()
            }
        }
    }
}