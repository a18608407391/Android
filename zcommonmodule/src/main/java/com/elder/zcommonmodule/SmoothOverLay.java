package com.elder.zcommonmodule;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BasePointOverlay;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.animation.Animation;
import com.autonavi.amap.mapcore.IPoint;
import com.autonavi.amap.mapcore.MapProjection;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SmoothOverLay {
    private AMap mAMap;
    private long duration = 10000L;
    private long mStepDuration = 20L;
    private LinkedList<LatLng> points = new LinkedList();
    private LinkedList<Double> eachDistance = new LinkedList();
    private double totalDistance = 0.0D;
    private double remainDistance = 0.0D;
    private ExecutorService mThreadPools;
    private Object mLock = new Object();
    private BasePointOverlay baseOverlay = null;
    private int index = 0;
    private boolean useDefaultDescriptor = false;
    AtomicBoolean exitFlag = new AtomicBoolean(false);
    private SmoothOverLay.MoveListener moveListener;
    private SmoothOverLay.a STATUS;
    private long pauseMillis;
    private long mAnimationBeginTime;

    public SmoothOverLay(AMap var1, BasePointOverlay var2) {
        this.STATUS = SmoothOverLay.a.a;
        this.mAnimationBeginTime = System.currentTimeMillis();
        if (var1 != null && var2 != null) {
            this.mAMap = var1;
            this.mThreadPools = new ThreadPoolExecutor(1, 2, 5L, TimeUnit.SECONDS, new SynchronousQueue(), new SmoothOverLay.b());
            this.baseOverlay = var2;
        }
    }

    public void setPoints(List<LatLng> var1) {
        Object var2 = this.mLock;
        synchronized (this.mLock) {
            try {
                if (var1 != null && var1.size() >= 2) {
                    this.stopMove();
                    this.points.clear();
                    Iterator var7 = var1.iterator();
                    while (var7.hasNext()) {
                        LatLng var3;
                        if ((var3 = (LatLng) var7.next()) != null) {
                            this.points.add(var3);
                        }
                    }
                    this.eachDistance.clear();
                    this.totalDistance = 0.0D;

                    for (int var8 = 0; var8 < this.points.size() - 1; ++var8) {
                        double var10 = (double) AMapUtils.calculateLineDistance((LatLng) this.points.get(var8), (LatLng) this.points.get(var8 + 1));
                        this.eachDistance.add(var10);
                        this.totalDistance += var10;
                    }
                    this.remainDistance = this.totalDistance;
                    LatLng var9 = (LatLng) this.points.get(0);
                    this.baseOverlay.setPosition(var9);
                    this.reset();
                    return;
                }
            } catch (Throwable var5) {
                var1 = null;
                var5.printStackTrace();
                return;
            }
        }
    }

    private void reset() {
        try {
            if (this.STATUS == SmoothOverLay.a.c || this.STATUS == SmoothOverLay.a.d) {
                this.exitFlag.set(true);
                this.mThreadPools.awaitTermination(this.mStepDuration + 20L, TimeUnit.MILLISECONDS);
                this.baseOverlay.setAnimation((Animation) null);
                this.STATUS = SmoothOverLay.a.a;
            }

        } catch (InterruptedException var1) {
            var1.printStackTrace();
        }
    }

    public void resetIndex() {
        this.index = 0;
    }

    public void setTotalDuration(int var1) {
        this.duration = (long) (var1 * 1000);
    }

    public void startSmoothMove() {
        if (this.STATUS == SmoothOverLay.a.d) {
            this.STATUS = SmoothOverLay.a.c;
            long var1 = System.currentTimeMillis() - this.pauseMillis;
            this.mAnimationBeginTime += var1;
        } else {
            if (this.STATUS == SmoothOverLay.a.a || this.STATUS == SmoothOverLay.a.e) {
                if (this.points.size() <= 0) {
                    return;
                }

                this.index = 0;

                try {
                    this.mThreadPools.execute(new SmoothOverLay.c());
                    return;
                } catch (Throwable var3) {
                    var3.printStackTrace();
                }
            }

        }
    }

    private IPoint getCurPosition(long var1) {
        if (var1 > this.duration) {
            this.exitFlag.set(true);
            IPoint var15 = new IPoint();
            this.index = this.points.size() - 1;
            LatLng var16 = (LatLng) this.points.get(this.index);
            --this.index;
            this.index = Math.max(this.index, 0);
            this.remainDistance = 0.0D;
            if (this.endListener != null) {
                this.endListener.EndPoint(index);
            }
            Log.e("result", var16.longitude + "经度" + var16.latitude + "纬度" + var1 + "调用时间");
            MapProjection.lonlat2Geo(var16.longitude, var16.latitude, var15);
            //拿最后一个店
            if (this.moveListener != null) {
                this.moveListener.move(this.remainDistance);
            }

            return var15;
        } else {
            double var3 = (double) var1 * this.totalDistance / (double) this.duration;
            this.remainDistance = this.totalDistance - var3;
            int var9 = 0;
            double var5 = 1.0D;

            for (int var2 = 0; var2 < this.eachDistance.size(); ++var2) {
                double var7 = (Double) this.eachDistance.get(var2);
                if (var3 <= var7) {
                    if (var7 > 0.0D) {
                        var5 = var3 / var7;
                    }
                    var9 = var2;
                    break;
                }
                var3 -= var7;
            }
            //拿中间的点
            if (var9 != this.index && this.moveListener != null) {
                this.moveListener.move(this.remainDistance);
            }
            this.index = var9;

            LatLng var11 = (LatLng) this.points.get(var9);
            LatLng var17 = (LatLng) this.points.get(var9 + 1);
            IPoint var8 = new IPoint();
            MapProjection.lonlat2Geo(var11.longitude, var11.latitude, var8);
            IPoint var10 = new IPoint();
            MapProjection.lonlat2Geo(var17.longitude, var17.latitude, var10);
            int var14 = var10.x - var8.x;
            int var4 = var10.y - var8.y;
            if (AMapUtils.calculateLineDistance(var11, var17) > 1.0F) {
                float var12 = this.getRotate(var8, var10);
                CameraPosition var13;
                if (this.mAMap != null && (var13 = this.mAMap.getCameraPosition()) != null) {
                    this.baseOverlay.setRotateAngle(360.0F - var12 + var13.bearing);
                }
            }

            return new IPoint((int) ((double) var8.x + (double) var14 * var5), (int) ((double) var8.y + (double) var4 * var5));
        }
    }

    private float getRotate(IPoint var1, IPoint var2) {
        if (var1 != null && var2 != null) {
            double var3 = (double) var2.y;
            double var5 = (double) var1.y;
            double var7 = (double) var1.x;
            return (float) (Math.atan2((double) var2.x - var7, var5 - var3) / 3.141592653589793D * 180.0D);
        } else {
            return 0.0F;
        }
    }

    public void stopMove() {
        if (this.STATUS == SmoothOverLay.a.c) {
            this.STATUS = SmoothOverLay.a.d;
            this.pauseMillis = System.currentTimeMillis();
        }

    }

    public BasePointOverlay getObject() {
        return this.baseOverlay;
    }

    public LatLng getPosition() {
        return this.baseOverlay != null ? this.baseOverlay.getPosition() : null;
    }

    public int getIndex() {
        return this.index;
    }

    public void destroy() {
        try {
            this.reset();
            this.mThreadPools.shutdownNow();
            Object var1 = this.mLock;
            synchronized (this.mLock) {
                this.points.clear();
                this.eachDistance.clear();
            }
        } catch (Throwable var4) {
            var4.printStackTrace();
        }
    }

    public void removeMarker() {
        try {
            if (this.baseOverlay != null) {
                this.baseOverlay.remove();
                this.baseOverlay = null;
            }

            this.points.clear();
            this.eachDistance.clear();
        } catch (Exception var1) {
            var1.printStackTrace();
        }
    }

    public void setPosition(LatLng var1) {
        try {
            if (this.baseOverlay != null) {
                this.baseOverlay.setPosition(var1);
            }

        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    private SmoothOverLay.AllListener allListener;
    private SmoothOverLay.EndListener endListener;
    public void setAllTimeListener(SmoothOverLay.AllListener allListener) {
        this.allListener = allListener;
    }
    public interface AllListener {
        void ScreenMove(int point);
    }

    public interface EndListener {
        void EndPoint(int point);
    }
    public void setEndListener(SmoothOverLay.EndListener listener) {
        {
            this.endListener = listener;
        }
    }





    public void setRotate(float var1) {
        try {
            CameraPosition var2;
            if (this.baseOverlay != null && this.mAMap != null && (var2 = this.mAMap.getCameraPosition()) != null) {
                this.baseOverlay.setRotateAngle(360.0F - var1 + var2.bearing);
            }

        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    public void setVisible(boolean var1) {
        try {
            if (this.baseOverlay != null) {
                this.baseOverlay.setVisible(var1);
            }

        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public void setMoveListener(SmoothOverLay.MoveListener var1) {
        this.moveListener = var1;
    }

    private class c implements Runnable {
        public final void run() {
            try {
                SmoothOverLay.this.mAnimationBeginTime = System.currentTimeMillis();
                SmoothOverLay.this.STATUS = SmoothOverLay.a.b;
                SmoothOverLay.this.exitFlag.set(false);

                for (; !SmoothOverLay.this.exitFlag.get() && SmoothOverLay.this.index <= SmoothOverLay.this.points.size() - 1; Thread.sleep(SmoothOverLay.this.mStepDuration)) {
                    synchronized (SmoothOverLay.this.mLock) {
                        if (SmoothOverLay.this.exitFlag.get()) {
                            return;
                        }

                        if (SmoothOverLay.this.STATUS != SmoothOverLay.a.d) {
                            long var2 = System.currentTimeMillis() - SmoothOverLay.this.mAnimationBeginTime;
                            IPoint var6 = SmoothOverLay.this.getCurPosition(var2);
                            if (allListener != null) {
                                allListener.ScreenMove(index);
                            }
                            SmoothOverLay.this.baseOverlay.setGeoPoint(var6);
                            SmoothOverLay.this.STATUS = SmoothOverLay.a.c;
                        }
                    }
                }

                SmoothOverLay.this.STATUS = SmoothOverLay.a.e;
            } catch (Throwable var5) {
                var5.printStackTrace();
            }
        }
    }

    private class b implements ThreadFactory {
        private b() {
        }

        public final Thread newThread(Runnable var1) {
            return new Thread(var1, "MoveSmoothThread");
        }
    }

    public interface MoveListener {
        void move(double var1);
    }




    private static enum a {
        a,
        b,
        c,
        d,
        e;

        private a() {
        }
    }
}
