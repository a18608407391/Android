package com.elder.logrecodemodule.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.elder.logrecodemodule.Activity.CavalierRankingActivity
import com.elder.logrecodemodule.BR
import com.elder.logrecodemodule.Inteface.RankingClickListener
import com.elder.logrecodemodule.R
import com.elder.zcommonmodule.Entity.CityPartyEntity
import com.elder.zcommonmodule.Entity.CountryMemberEntity
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.RankingData
import com.elder.zcommonmodule.Service.HttpInteface
import com.elder.zcommonmodule.Service.HttpRequest
import com.google.gson.Gson
import com.zk.library.Base.BaseViewModel
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import kotlinx.android.synthetic.main.activity_cavalier_ranking.*
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.USERID


class CavalierRankingViewModel : BaseViewModel(), RankingClickListener, HttpInteface.getRankResult, RadioGroup.OnCheckedChangeListener {
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.cur_week -> {
//                initData(1, "500")
            }
            R.id.last_week -> {
//                initData(8, "")
            }
        }
    }


    var name = ObservableField<String>()

    var memberImage = ObservableField<String>()

    var distanceSum = ObservableField<String>()

    var title  =ObservableField<String>()
    override fun ResultRankingSuccess(it: String) {
        if (it.length < 10) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            return
        }
        items.clear()
        var ranking = Gson().fromJson<RankingData>(it, RankingData::class.java)
        name.set(ranking.member?.name)
        memberImage.set(ranking.member?.memberImage)
        distanceSum.set(ranking.member?.distanceSum)
        ranking.MemberRanking!!.forEachIndexed { index, countryMemberEntity ->
            if (index > 2) {
                items.add(countryMemberEntity)
            } else {
                if (index == 0) {
                    var d = java.lang.Double.valueOf(countryMemberEntity.distanceSum)
                    numberOneDis.set((d.toInt() / 1000).toString() + "KM")
                    numberOneName.set(countryMemberEntity.name)
                    numberOneUrl.set(countryMemberEntity.memberImage)
                    numberoneid = countryMemberEntity.id
                } else if (index == 1) {
                    var d = java.lang.Double.valueOf(countryMemberEntity.distanceSum)
                    numberTwoDis.set((d.toInt() / 1000).toString() + "KM")
                    numberTwoName.set(countryMemberEntity.name)
                    numberTwoUrl.set(countryMemberEntity.memberImage)
                    numbertwoid = countryMemberEntity.id
                } else if (index == 2) {
                    var d = java.lang.Double.valueOf(countryMemberEntity.distanceSum)
                    numberThreeDis.set((d.toInt() / 1000).toString() + "KM")
                    numberThreeName.set(countryMemberEntity.name)
                    numberThreeUrl.set(countryMemberEntity.memberImage)
                    numberthree = countryMemberEntity.id
                }
            }
        }
    }

    override fun ResultRankingError(it: Throwable) {
    }


    override fun RankingItemClick(position: Int, item: CountryMemberEntity) {
        var loc = Location(cavalier.location!!.latitude, cavalier.location!!.longitude, System.currentTimeMillis().toString(), cavalier.location!!.speed, cavalier.location!!.latitude, cavalier.location!!.bearing, cavalier.location!!.aoiName, cavalier.location!!.poiName)
        ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, item.id)
                .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc)
                .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).navigation()
    }

    lateinit var cavalier: CavalierRankingActivity
    fun inject(cavalierRankingActivity: CavalierRankingActivity) {
        this.cavalier = cavalierRankingActivity
        cavalierRankingActivity.rg_ranking.setOnCheckedChangeListener(this)
        HttpRequest.instance.RankingResut = this
        if (cavalier.side == "local") {
            title.set("本地"+getString(R.string.week_ranking))
            initData(1, "200")
        } else if (cavalier.side == "whole") {
            title.set("全国"+getString(R.string.week_ranking))
            initData(1, "")
        }
    }


    var numberOneUrl = ObservableField<String>()
    var numberOneName = ObservableField<String>()
    var numberOneDis = ObservableField<String>()
    var numberoneid: String? = null

    var numberTwoUrl = ObservableField<String>()
    var numberTwoName = ObservableField<String>()
    var numberTwoDis = ObservableField<String>()
    var numbertwoid: String? = null

    var numberThreeUrl = ObservableField<String>()
    var numberThreeName = ObservableField<String>()
    var numberThreeDis = ObservableField<String>()
    var numberthree: String? = null

    fun onClick(view: View) {
        when (view.id) {
            R.id.ranking_numberone -> {
                var loc = Location(cavalier.location!!.latitude, cavalier.location!!.longitude, System.currentTimeMillis().toString(), cavalier.location!!.speed, cavalier.location!!.latitude, cavalier.location!!.bearing, cavalier.location!!.aoiName, cavalier.location!!.poiName)
                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, numberoneid)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc)
                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).navigation()
            }
            R.id.ranking_numbertwo -> {
                var loc = Location(cavalier.location!!.latitude, cavalier.location!!.longitude, System.currentTimeMillis().toString(), cavalier.location!!.speed, cavalier.location!!.latitude, cavalier.location!!.bearing, cavalier.location!!.aoiName, cavalier.location!!.poiName)
                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, numbertwoid)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc)
                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).navigation()
            }
            R.id.ranking_numberthree -> {
                var loc = Location(cavalier.location!!.latitude, cavalier.location!!.longitude, System.currentTimeMillis().toString(), cavalier.location!!.speed, cavalier.location!!.latitude, cavalier.location!!.bearing, cavalier.location!!.aoiName, cavalier.location!!.poiName)
                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, numberthree)
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc)
                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).navigation()
            }
            R.id.ranking_arrow -> {
                ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(cavalier, object : NavCallback() {
                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            }
            R.id.myself_click->{
                var loc = Location(cavalier.location!!.latitude, cavalier.location!!.longitude, System.currentTimeMillis().toString(), cavalier.location!!.speed, cavalier.location!!.latitude, cavalier.location!!.bearing, cavalier.location!!.aoiName, cavalier.location!!.poiName)
                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, PreferenceUtils.getString(context, USERID))
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc)
                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 1).navigation()
            }
        }
    }

    fun initData(s: Int, distance: String) {
        var map = HashMap<String, String>()
        map["yAxis"] = cavalier.location!!.longitude.toString()
        map["xAxis"] = cavalier.location!!.latitude.toString()
        map["cycle"] = s.toString()
        if (!distance.isEmpty()) {
            map["distance"] = distance
        }
        Log.e("result", "请求参数" + Gson().toJson(map))
        HttpRequest.instance.QueryRanking(map)
    }

    var adapter = BindingRecyclerViewAdapter<CountryMemberEntity>()
    var items = ObservableArrayList<CountryMemberEntity>()
    var listener: RankingClickListener = this
    var cityPartyitemBinding = ItemBinding.of<CountryMemberEntity> { itemBinding, position, item ->
        itemBinding.set(BR.city_party_model, R.layout.city_party_recy_item).bindExtra(BR.position, position + 3).bindExtra(BR.listener, listener)
    }
}