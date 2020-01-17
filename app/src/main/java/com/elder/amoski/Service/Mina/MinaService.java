package com.elder.amoski.Service.Mina;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.elder.zcommonmodule.R;
import com.iflytek.speech.UtilityConfig;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class MinaService extends Service {

    private ConnectionHandlerThread thread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            createNotifyCation();
        }
        Log.e("result","CREATE_MINA");
        if (intent != null && intent.getAction() != null) {
            String opition = intent.getAction();
            if (opition != null && opition.equalsIgnoreCase("on")) {

                thread = new ConnectionHandlerThread("mina", getApplicationContext());
                System.out.println("service create:");
                thread.start();
            } else if (opition != null && opition.equals("off")) {
                if (thread != null) {
                    thread.disConnection();
                    thread.interrupt();
                    thread = null;
                }
            }
        }
        return Service.START_STICKY;
    }


    @RequiresApi(Build.VERSION_CODES.O)
    public void createNotifyCation() {
        NotificationManager manager = (NotificationManager) getSystemService(IntentService.NOTIFICATION_SERVICE);
        NotificationChannel Channel = new NotificationChannel(UtilityConfig.CHANNEL_ID, "主服务", NotificationManager.IMPORTANCE_HIGH);
        Channel.enableLights(true);
        Channel.setLightColor(Color.RED);//设置提示灯颜色
        Channel.setShowBadge(true);//显示logo
        Channel.setDescription("ytzn"); //设置描述
        Channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见 VISIBILITY_PUBLIC=可见
        manager.createNotificationChannel(Channel);
        Notification notification = new Notification.Builder(this)
                .setChannelId(UtilityConfig.CHANNEL_ID)
                .setContentTitle("主服务")//标题
                .setContentText("运行中...")//内容
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)//小图标一定需要设置,否则会报错(如果不设置它启动服务前台化不会报错,但是你会发现这个通知不会启动),如果是普通通知,不设置必然报错
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build();
        startForeground(1, notification);//服务前台化只能使用startForeground()方法,不能使用
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnection();
    }

    /**
     * 负责调用ConnectionManager
     */
    class ConnectionHandlerThread extends HandlerThread {
        private Context context;
        boolean isConnection;
        ConnectionManager mManager;

        public ConnectionHandlerThread(String name, Context context) {
            super(name);
            this.context = context;
            //192.168.5.178
            //122.114.91.150
            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setIp("122.114.91.150").setPort(9000)
                    .setReadBufferSize(10240).setReadBufferSize(10000).builder();
            System.out.println(config.getReadBufferSize());
            mManager = new ConnectionManager(config);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            while (true) {
                isConnection = mManager.connect(); //
                if (isConnection) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void disConnection() {
            mManager.disConnection();
        }
    }
}