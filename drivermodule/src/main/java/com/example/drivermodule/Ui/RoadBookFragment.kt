package com.example.drivermodule.Ui

import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.PagerSnapHelper
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.drivermodule.BR
import com.example.drivermodule.R
import com.example.drivermodule.ViewModel.RoadBookViewModel
import com.example.drivermodule.databinding.FragmentRoadBookBinding
import com.zk.library.Base.BaseFragment
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.fragment_road_book.*
import org.cs.tec.library.Bus.RxSubscriptions


@Route(path = RouterUtils.MapModuleConfig.ROUTE_BOOK_FR)
class RoadBookFragment : BaseFragment<FragmentRoadBookBinding, RoadBookViewModel>() {
    override fun initContentView(): Int {
        return R.layout.fragment_road_book
    }

    override fun initVariableId(): Int {
        return BR.road_book_model
    }

    var behavior_by_routes: BottomSheetBehavior<LinearLayout>? = null


    override fun initData() {
        super.initData()
        behavior_by_routes = BottomSheetBehavior.from<LinearLayout>(behavior_by_route)
        behavior_by_routes!!.state = BottomSheetBehavior.STATE_COLLAPSED
        LinearSnapHelper().attachToRecyclerView(bottom_pager)
        roadbook_recycle.isNestedScrollingEnabled = true
        viewModel?.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel?.d?.dispose()
        RxSubscriptions.remove(viewModel?.d)
    }
}