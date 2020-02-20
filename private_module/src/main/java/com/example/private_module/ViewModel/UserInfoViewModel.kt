package com.example.private_module.ViewModel

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.location.AMapLocation
import com.elder.zcommonmodule.Entity.Location
import com.elder.zcommonmodule.Entity.UserInfo
import com.elder.zcommonmodule.Even.ActivityResultEven
import com.elder.zcommonmodule.GET_USERINFO
import com.elder.zcommonmodule.MSG_RETURN_REQUEST
import com.elder.zcommonmodule.PRIVATE_DATA_RETURN
import com.elder.zcommonmodule.getImageUrl
import com.example.private_module.BR
import com.example.private_module.R
import com.example.private_module.UI.UserInfoFragment
import com.zk.library.Base.BaseViewModel
import com.zk.library.Bus.ServiceEven
import com.zk.library.Utils.PreferenceUtils
import com.zk.library.Utils.RouterUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.cs.tec.library.Base.Utils.context
import org.cs.tec.library.Base.Utils.getString
import org.cs.tec.library.Bus.RxBus
import org.cs.tec.library.Bus.RxSubscriptions
import org.cs.tec.library.USERID
import org.cs.tec.library.binding.command.BindingCommand
import org.cs.tec.library.binding.command.BindingConsumer
import java.io.Serializable


class UserInfoViewModel : BaseViewModel() {


    var msgCount = ObservableField(0)
    var fr_avatar = ObservableField<String>("")
    var name = ObservableField<String>("爱摩老司机")
    var dynamicsStr = ObservableField<String>("0")
    var focus = ObservableField<String>("0")
    var like = ObservableField<String>()
    var fans = ObservableField<String>("0")
    var allTotal = ObservableField<String>()
    var allTime = ObservableField<String>()
    fun onClick(view: View) {
        when (view.id) {
            R.id.likes -> {
                if (loc == null) {
                    Toast.makeText(context, getString(R.string.get_http_location), Toast.LENGTH_SHORT).show()
                    return
                }
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_LIKE_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc).navigation(userInfoFragment.activity, PRIVATE_DATA_RETURN)
            }
            R.id.foucs -> {
                if (loc == null) {
                    Toast.makeText(context, getString(R.string.get_http_location), Toast.LENGTH_SHORT).show()
                    return
                }
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_FOCUS_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc).navigation(userInfoFragment.activity, PRIVATE_DATA_RETURN)
            }
            R.id.fans -> {
                if (loc == null) {
                    Toast.makeText(context, getString(R.string.get_http_location), Toast.LENGTH_SHORT).show()
                    return
                }
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_FANS_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc).navigation(userInfoFragment.activity, PRIVATE_DATA_RETURN)
            }
            R.id.dynamics -> {
                if (loc == null) {
                    Toast.makeText(context, getString(R.string.get_http_location), Toast.LENGTH_SHORT).show()
                    return
                }
                ARouter.getInstance().build(RouterUtils.SocialConfig.SOCIAL_CAVALIER_HOME)
                        .withString(RouterUtils.SocialConfig.SOCIAL_MEMBER_ID, PreferenceUtils.getString(context, USERID))
                        .withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc)
                        .withInt(RouterUtils.SocialConfig.SOCIAL_NAVITATION_ID, 0).navigation()
//                ARouter.getInstance().build(RouterUtils.SocialConfig.MY_DYNAMIC_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, Location(location?.latitude!!, location?.longitude!!, System.currentTimeMillis().toString(), 0F, 0.0, 0F, location?.aoiName!!, location!!.poiName)).withInt(RouterUtils.SocialConfig.SOCIAL_TYPE, 1).navigation(userInfoFragment.activity,PRIVATE_DATA_RETURN)
            }

            R.id.user_avater -> {

            }
            R.id.enter_myphoto -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_PHOTO_ALBUM).navigation()
            }
            R.id.enter_renzhen -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MemberAuth).navigation()
            }
            R.id.user_info_layout -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USER_INFO_EDIT).withSerializable(RouterUtils.PrivateModuleConfig.USER_INFO, userInfoFragment.userInfo).navigation(userInfoFragment.activity, GET_USERINFO)
            }
            R.id.setting_icon -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USER_SETTING).withInt(RouterUtils.PrivateModuleConfig.SETTING_CATEGORY, 1).navigation()
            }
            R.id.notify_icon -> {
                ARouter.getInstance().build(RouterUtils.Chat_Module.MSG_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION, loc).navigation(userInfoFragment.activity, MSG_RETURN_REQUEST)
            }
            R.id.vertical_linear1 -> {
                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.LogListActivity).withInt(RouterUtils.LogRecodeConfig.LOG_LIST_ENTITY, 3).navigation()
            }
            R.id.vertical_linear2 -> {
                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.LogListActivity).withInt(RouterUtils.LogRecodeConfig.LOG_LIST_ENTITY, 2).navigation()
            }
            R.id.vertical_linear3 -> {
                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.LogListActivity).withInt(RouterUtils.LogRecodeConfig.LOG_LIST_ENTITY, 1).navigation()
            }
            R.id.vertical_linear4 -> {
                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.LogListActivity).withInt(RouterUtils.LogRecodeConfig.LOG_LIST_ENTITY, 0).navigation()
            }
            R.id.log_list_click -> {
                ARouter.getInstance().build(RouterUtils.LogRecodeConfig.LogListActivity).withInt(RouterUtils.LogRecodeConfig.LOG_LIST_ENTITY, 0).navigation()
            }
//            R.id.my_road_book -> {
//                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MY_ROAD_BOOK_AC).navigation()
//            }
//            R.id.my_active -> {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC).withInt(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE, 0).navigation()
//            }
//            R.id.my_ticket -> {
//                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC).withInt(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE, 1).navigation()
//            }
        }
    }

    lateinit var userInfoFragment: UserInfoFragment
    fun inject(userInfoFragment: UserInfoFragment) {
        this.userInfoFragment = userInfoFragment
        var pos = ServiceEven()
        pos.type = "HomeStart"
        RxBus.default?.post(pos)
        titleArray.forEachIndexed { index, s ->
            var entity = IconEntity()
            entity.title = s
            entity.icon = context.getDrawable(iconArray.get(index))
            items.add(entity)
        }
    }


    var location: AMapLocation? = null
    var loc: Location? = null
    override fun registerRxBus() {
        super.registerRxBus()
        RxSubscriptions.add(RxBus.default?.toObservable(AMapLocation::class.java)?.subscribe {
            this.location = it
            loc = Location(location!!.latitude, location!!.longitude, System.currentTimeMillis().toString(), location!!.speed, location!!.altitude, location!!.bearing, location!!.city, location!!.aoiName)
        })
    }


    var iconArray = arrayOf(R.drawable.my_roadbook_icon, R.drawable.user_active_icon, R.drawable.ele_ticket, R.drawable.cert_icon, R.drawable.restore_icon)

    var titleArray = arrayOf(getString(R.string.my_road_book), getString(R.string.my_active), getString(R.string.my_ticket), getString(R.string.auth_center), getString(R.string.my_restore))

    var adapter = BindingRecyclerViewAdapter<IconEntity>()

    var items = ObservableArrayList<IconEntity>()

    var itemBinding = ItemBinding.of<IconEntity>(BR.icon_model, R.layout.icon_layout).bindExtra(BR.user_model, this@UserInfoViewModel)


    fun IconClick(entity: IconEntity) {
        when (items.indexOf(entity)) {
            0 -> {
                ARouter.getInstance().build(RouterUtils.MapModuleConfig.MY_ROAD_BOOK_AC).navigation()
            }
            1 -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC).withInt(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE, 0).navigation()
            }
            2 -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_AC).withInt(RouterUtils.PrivateModuleConfig.MY_ACTIVE_WEB_TYPE, 1).navigation()
            }
            3 -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MemberAuth).navigation()
            }
            4 -> {
                ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.MY_RESTORE_AC).withSerializable(RouterUtils.SocialConfig.SOCIAL_LOCATION,loc!!).navigation()
            }
        }
    }

    class IconEntity : Serializable {
        var icon: Drawable? = null

        var title: String? = null

    }
}