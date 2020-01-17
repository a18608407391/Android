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
import com.elder.zcommonmodule.DataBases.ContextExtensionsKt;
import com.elder.zcommonmodule.DataBases.DataBaseUtilsKt;
import com.elder.zcommonmodule.Entity.DriverDataStatus;
import com.elder.zcommonmodule.Entity.HttpResponseEntitiy.BaseResponse;
import com.elder.zcommonmodule.Utils.Dialog.NormalDialog;
import com.elder.zcommonmodule.Utils.Dialog.OnBtnClickL;
import com.elder.zcommonmodule.Utils.DialogUtils;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.zk.library.Base.AppManager;
import com.zk.library.Base.BaseApplication;
import com.zk.library.Utils.PreferenceUtils;
import com.zk.library.Utils.RouterUtils;

import org.cs.tec.library.Base.Utils.UtilsKt;
import org.cs.tec.library.Bus.RxBus;
import org.cs.tec.library.Utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

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
import static com.elder.zcommonmodule.ConfigKt.USER_TOKEN;
import static com.elder.zcommonmodule.ConfigKt.checkDriverStatus;
import static org.cs.tec.library.ConstantKt.USERID;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "result";
    private static final int RETURN_MSG_TYPE_LOGIN = 1; //登录
    private static final int RETURN_MSG_TYPE_SHARE = 2; //分享
    private Context mContext;

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
            Log.e("result",baseReq.getType() + "的v方式能否科技大厦南方科技");

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
        Log.e(TAG, "onResp:------>");
        Log.e(TAG, "error_code:---->" + baseResp.errCode);
        Log.e("result", "onResp:" + baseResp.errCode + baseResp.errStr + baseResp.transaction + baseResp.getType());
        final int type = baseResp.getType(); //类型：分享还是登录
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝授权

                Toast.makeText(this, "微信授权取消!!!", Toast.LENGTH_SHORT).show();
                finish();
//                ToastUtils.showToast(mContext, "拒绝授权微信登录");
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                Log.e("result","用户取消");
                String message = "";
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    message = "取消了微信登录";
                    Toast.makeText(this, "已取消微信登录!!!", Toast.LENGTH_SHORT).show();
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    message = "取消了微信分享";
                    RxBus.Companion.getDefault().post("ShareSuccess");
                }
                finish();
//                ToastUtils.showToast(mContext, message);
                break;
            case BaseResp.ErrCode.ERR_OK:
                //用户同意
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    //用户换取access_token的code，仅在ErrCode为0时有效
                    final String code = ((SendAuth.Resp) baseResp).code;
                    Log.e("result", "登录" + code);
                    final Dialog dialog = showProgress(getString(R.string.wx_loading_login));
                    Observable.create(new ObservableOnSubscribe<Response>() {
                        @Override
                        public void subscribe(ObservableEmitter<Response> emitter) throws Exception {
                            OkHttpClient client = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build();
                            FormBody.Builder d = new FormBody.Builder().add("code", code).add("type", "app");
                            Request request = new Request.Builder().addHeader("content-type", "application/json; charset=UTF-8").url(Base_URL + "AmoskiActivity/memberUser/getUserAuthInfo").post(d.build()).build();
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
                            final BaseResponse res = new Gson().fromJson(s, BaseResponse.class);
                            Log.e("result", "登录返回数据"+s);
                            if (res.getCode() == 0) {
                                PreferenceUtils.putString(WXEntryActivity.this, USERID, res.getMsg());
                                dialog.dismiss();
                                if (type == 0) {
                                    ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(WXEntryActivity.this, new NavCallback() {
                                        @Override
                                        public void onArrival(Postcard postcard) {
                                            PreferenceUtils.putString(WXEntryActivity.this, USER_TOKEN, res.getData().toString());
                                            Stack<Activity> activities = AppManager.Companion.getActivityStack();
                                            activities.get(0).finish();
                                            activities.get(1).finish();
                                        }
                                    });
                                } else if (type == 1) {
                                    if (checkDriverStatus()) {
                                        ArrayList<DriverDataStatus> statuses = DataBaseUtilsKt.queryDriverStatus(res.getMsg());
                                        if (statuses.size() != 0 && statuses.get(0).getStartDriver().get() != 2) {
                                            final NormalDialog dialog = DialogUtils.Companion.createNomalDialog(WXEntryActivity.this, UtilsKt.getString(R.string.checked_exception_out), UtilsKt.getString(R.string.setting_permisstion), UtilsKt.getString(R.string.continue_driving));
                                            dialog.setOnBtnClickL(new OnBtnClickL() {
                                                @Override
                                                public void onBtnClick() {
                                                    ARouter.getInstance().build(RouterUtils.PrivateModuleConfig.USER_SETTING).withInt(RouterUtils.PrivateModuleConfig.SETTING_CATEGORY, 0).navigation();
                                                }
                                            }, new OnBtnClickL() {
                                                @Override
                                                public void onBtnClick() {
                                                    dialog.dismiss();
                                                    ARouter.getInstance().build(RouterUtils.MapModuleConfig.MAP_ACTIVITY).withString(RouterUtils.MapModuleConfig.RESUME_MAP_ACTIVITY, "continue").navigation(WXEntryActivity.this, new NavCallback() {
                                                        @Override
                                                        public void onArrival(Postcard postcard) {
                                                            PreferenceUtils.putString(WXEntryActivity.this, USER_TOKEN, res.getData().toString());
                                                            Stack<Activity> activities = AppManager.Companion.getActivityStack();
                                                            activities.get(0).finish();
                                                            activities.get(1).finish();
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(WXEntryActivity.this, new NavCallback() {
                                                @Override
                                                public void onArrival(Postcard postcard) {
                                                    PreferenceUtils.putString(WXEntryActivity.this, USER_TOKEN, res.getData().toString());
                                                    Stack<Activity> activities = AppManager.Companion.getActivityStack();
                                                    activities.get(0).finish();
                                                    activities.get(1).finish();
                                                }
                                            });
                                        }
                                    } else {
                                        ARouter.getInstance().build(RouterUtils.ActivityPath.HOME).navigation(WXEntryActivity.this, new NavCallback() {
                                            @Override
                                            public void onArrival(Postcard postcard) {
                                                PreferenceUtils.putString(WXEntryActivity.this, USER_TOKEN, res.getData().toString());
                                                Stack<Activity> activities = AppManager.Companion.getActivityStack();
                                                activities.get(0).finish();
                                                activities.get(1).finish();
                                            }
                                        });
                                    }
                                }
                            } else {
                                finish();
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("result", "登录2");
                            finish();
                        }

                        @Override
                        public void onComplete() {
                            Log.e("result", "登录3");
                            dialog.dismiss();
                        }
                    });
//                    Log.i(TAG, "code:------>" + code);
//                    NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/access_token?" +
//                                    "appid=%s&secret=%s&code=%s&grant_type=authorization_code", "wx941dc5dccc68dee3",
//                            "cecfaae035206e45a332f271a788650e", code), NetworkUtil.GET_TOKEN);
                    //这里拿到了这个code，去做2次网络请求获取access_token和用户个人信息
//                    WXLoginUtils().getWXLoginResult(code, this);

                } else if (type == RETURN_MSG_TYPE_SHARE) {
//                    ToastUtils.showToast(mContext, "微信分享成功");
                    RxBus.Companion.getDefault().post("ShareSuccess");
                    finish();
                }
                break;
        }
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


//    NavCallback callback = new NavCallback() {
//        @Override
//        public void onArrival(Postcard postcard) {

//        }
//    };
}