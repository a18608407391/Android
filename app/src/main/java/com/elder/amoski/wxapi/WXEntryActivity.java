package com.elder.amoski.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.elder.amoski.Activity.HomeActivity;
import com.elder.amoski.AppInstance;
import com.elder.amoski.NetworkUtil;
import com.elder.amoski.R;
import com.elder.amoski.WXLoginBean;
import com.elder.zcommonmodule.DataBases.ContextExtensionsKt;
import com.elder.zcommonmodule.DataBases.DataBaseUtilsKt;
import com.elder.zcommonmodule.Entity.DriverDataStatus;
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse;
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog;
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL;
import com.elder.zcommonmodule.Utils.DialogUtils;
import com.elder.zcommonmodule.Widget.TelePhoneBinder.TelephoneBinder;
import com.elder.zcommonmodule.Widget.TelePhoneBinder.TelephoneBinderDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.zk.library.Base.AppManager;
import com.zk.library.Base.BaseApplication;
import com.zk.library.Bus.event.RxBusEven;
import com.zk.library.Utils.PreferenceUtils;
import com.zk.library.Utils.RouterUtils;

import org.cs.tec.library.Base.Utils.UtilsKt;
import org.cs.tec.library.Bus.RxBus;
import org.cs.tec.library.Utils.ToastUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import cn.jmessage.support.qiniu.android.utils.Json;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.elder.zcommonmodule.ConfigKt.Base_URL;
import static com.elder.zcommonmodule.ConfigKt.OPENID;
import static com.elder.zcommonmodule.ConfigKt.TOKEN_LIMIT;
import static com.elder.zcommonmodule.ConfigKt.UNIONID;
import static com.elder.zcommonmodule.ConfigKt.USER_TOKEN;
import static com.elder.zcommonmodule.ConfigKt.checkDriverStatus;
import static org.cs.tec.library.ConstantKt.USERID;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler, TelephoneBinderDialogFragment.DismissListener {
    private static final String TAG = "result";
    private static final int RETURN_MSG_TYPE_LOGIN = 1; //登录
    private static final int RETURN_MSG_TYPE_SHARE = 2; //分享
    private Context mContext;
    private static final String LOG_TAG = "WXEntryActivity";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NetworkUtil.GET_TOKEN:
                    Bundle bundle = msg.getData();
                    String result = (String) bundle.get("result");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        mContext = this;
        //这句没有写,是不能执行回调的方法的
        BaseApplication.Companion.getInstance().mWxApi.handleIntent(getIntent(), this);
    }

    int type = 0;

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq baseReq) {
        if (baseReq.transaction == "login") {
            type = 0;
        } else if (baseReq.transaction == "inValidate") {
            type = 1;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
//        XBLiveApplication.api.handleIntent(intent, this);
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp baseResp) {
        Log.e(this.getClass().getName(), "onResp:------>");
        Log.e(this.getClass().getName(), "error_code:---->" + baseResp.errCode);
        Log.e(this.getClass().getName(), "onResp:" + baseResp.errCode + baseResp.errStr + baseResp.transaction + baseResp.getType());
        final int type = baseResp.getType(); //类型：分享还是登录
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝授权
                Toast.makeText(this, "微信授权取消!!!", Toast.LENGTH_SHORT).show();
                finish();
//                ToastUtils.showToast(mContext, "拒绝授权微信登录");
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                String message = "";
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    message = "取消了微信登录";
                    Toast.makeText(this, "已取消微信登录!!!", Toast.LENGTH_SHORT).show();
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    message = "取消了微信分享";
                    RxBus.Companion.getDefault().post(RxBusEven.Companion.getInstance(RxBusEven.Companion.getSHARE_CANCLE()));
                }
                finish();
//                ToastUtils.showToast(mContext, message);
                break;
            case BaseResp.ErrCode.ERR_OK:
                //用户同意
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    //用户换取access_token的code，仅在ErrCode为0时有效
                    final String code = ((SendAuth.Resp) baseResp).code;
                    final Dialog dialog = showProgress(getString(R.string.wx_loading_login));
                    Observable.create(new ObservableOnSubscribe<Response>() {
                        @Override
                        public void subscribe(ObservableEmitter<Response> emitter) throws Exception {
                            OkHttpClient client = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build();
                            FormBody.Builder d = new FormBody.Builder().add("code", code).add("type", "app");
                            Request request = new Request.Builder().addHeader("content-type", "application/json; charset=UTF-8")
                                    .url(Base_URL + "AmoskiActivity/memberUser/getUserAuthInfo").post(d.build()).build();
                            Call call = client.newCall(request);
                            emitter.onNext(call.execute());
                        }
                    }).subscribeOn(Schedulers.io()).map(new Function<Response, String>() {
                        @Override
                        public String apply(Response response) throws Exception {
                            return response.body().string();
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String s) {
                            final WXLoginBean wxLoginBean = new Gson().fromJson(s, WXLoginBean.class);
                            if (wxLoginBean.getCode() == 0) {
                                PreferenceUtils.putString(WXEntryActivity.this, USER_TOKEN, wxLoginBean.getData().getAppToken());
                                PreferenceUtils.putString(WXEntryActivity.this, UNIONID, wxLoginBean.getData().getUnionId());
                                PreferenceUtils.putString(WXEntryActivity.this, OPENID, wxLoginBean.getData().getOpenId());
                                PreferenceUtils.putLong(WXEntryActivity.this, TOKEN_LIMIT, System.currentTimeMillis());
                                dialog.dismiss();
                                if (type == 0) {
                                    go2Home();
                                } else if (type == 1) {
                                    if (checkDriverStatus()) {//骑行状态
                                        ArrayList<DriverDataStatus> statuses = DataBaseUtilsKt.queryDriverStatus(wxLoginBean.getMsg());
                                        if (statuses.size() != 0 && statuses.get(0).getStartDriver().get() != 2) {
                                            final NormalDialog dialog = DialogUtils.Companion.createNomalDialog(WXEntryActivity.this, UtilsKt.getString(R.string.checked_exception_out), UtilsKt.getString(R.string.setting_permisstion), UtilsKt.getString(R.string.continue_driving));
                                            dialog.setOnBtnClickL(new OnBtnClickL() {
                                                @Override
                                                public void onBtnClick() {
                                                    ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USER_SETTING)
                                                            .withInt(RouterUtils.PrivateModuleConfig.SETTING_CATEGORY, 0)
                                                            .navigation();
                                                }
                                            }, new OnBtnClickL() {
                                                @Override
                                                public void onBtnClick() {
                                                    dialog.dismiss();
                                                    ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY)
                                                            .withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "continue")
                                                            .navigation(WXEntryActivity.this, new NavCallback() {
                                                                @Override
                                                                public void onArrival(Postcard postcard) {
                                                                    Stack<Activity> activities = AppManager.Companion.getActivityStack();
                                                                    activities.get(0).finish();
                                                                    activities.get(1).finish();
                                                                }
                                                            });
                                                }
                                            });
                                        } else {
                                            go2Home();
                                        }
                                    } else {//没在骑行状态
                                        if (wxLoginBean.getData().getTel().isEmpty()) {//未绑定手机
                                            TelephoneBinder from = TelephoneBinder.Companion.from(WXEntryActivity.this);
                                            TelephoneBinderDialogFragment show = from.show();
                                            show.setFunctionDismiss(WXEntryActivity.this);
                                        } else {//已绑定手机
                                            go2Home();
                                        }
                                    }
                                }
                            } else {
                                finish();
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(LOG_TAG, "登录2");
                            finish();
                        }

                        @Override
                        public void onComplete() {
                            Log.e(LOG_TAG, "登录3");
                            dialog.dismiss();
                        }
                    });
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    RxBus.Companion.getDefault().post(RxBusEven.Companion.getInstance(RxBusEven.Companion.getSHARE_SUCCESS()));
//                    RxBus.Companion.getDefault().post("ShareSuccess");
                    finish();
                }
                break;
        }
    }

    @Override
    public void onDismiss() {//绑定手机成功
        this.go2Home();
    }

    private void go2Home() {
        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME)
                .navigation(WXEntryActivity.this, new NavCallback() {
                    @Override
                    public void onArrival(Postcard postcard) {
                        Stack<Activity> activities = AppManager.Companion.getActivityStack();
                        activities.get(0).finish();
                        activities.get(1).finish();
                    }
                });
    }

    public ProgressDialog showProgress(String text) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(text);
        progressDialog.show();
        return progressDialog;
    }

    @Override
    public void onBack() {
        //返回
        finish();
    }

}