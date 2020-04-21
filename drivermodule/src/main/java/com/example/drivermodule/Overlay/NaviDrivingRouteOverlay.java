package com.example.drivermodule.Overlay;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DriveStep;
import com.example.drivermodule.R;

import org.cs.tec.library.Base.Utils.UtilsKt;
import org.cs.tec.library.Utils.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

import static org.cs.tec.library.Base.Utils.UtilsKt.getString;
import static org.cs.tec.library.Base.Utils.UtilsKt.getWindowWidth;


/**
 * 导航路线图层类。
 */
public class NaviDrivingRouteOverlay extends RouteOverlay {

    private AMapNaviPath drivePath;
    private List<NaviLatLng> throughPointList;
    private List<Marker> throughPointMarkerList = new ArrayList<Marker>();
    private boolean throughPointMarkerVisible = true;
    private PolylineOptions mPolylineOptions;
    private PolylineOptions mPolylineOptionscolor;
    private Context mContext;
    private boolean isColorfulline = true;
    private float mWidth = 18;
    private List<LatLng> mLatLngsOfPath;

    public void setIsColorfulline(boolean iscolorfulline) {
        this.isColorfulline = iscolorfulline;
    }

    /**
     * 根据给定的参数，构造一个导航路线图层类对象。
     *
     * @param amap    地图对象。
     * @param path    导航路线规划方案。
     * @param context 当前的activity对象。
     */
    public NaviDrivingRouteOverlay(Context context, AMap amap, AMapNaviPath path,
                                   NaviLatLng start, NaviLatLng end, List<NaviLatLng> throughPointList) {
        super(context);
        mContext = context;
        mAMap = amap;
        this.drivePath = path;
        startPoint = convertToLatLng(start);
        endPoint = convertToLatLng(end);
        this.throughPointList = throughPointList;
    }

    public float getRouteWidth() {
        return mWidth;
    }

    /**
     * 设置路线宽度
     *
     * @param mWidth 路线宽度，取值范围：大于0
     */
    public void setRouteWidth(float mWidth) {
        this.mWidth = mWidth;
    }

    /**
     * 添加驾车路线添加到地图上显示。
     */
    public void addToMap() {
        initPolylineOptions();
        try {
            if (mAMap == null) {
                return;
            }
            if (mWidth == 0 || drivePath == null) {
                return;
            }
            mLatLngsOfPath = new ArrayList<LatLng>();

            for (int i = 0; i < drivePath.getCoordList().size(); i++) {
                mPolylineOptions.add(convertToLatLng(drivePath.getCoordList().get(i)));
                mLatLngsOfPath.add(convertToLatLng(drivePath.getCoordList().get(i)));
            }


            if (startMarker != null) {
                startMarker.remove();
                startMarker = null;
            }
            if (endMarker != null) {
                endMarker.remove();
                endMarker = null;
            }
            addStartAndEndMarker();
            addThroughPointMarker();
            if (isColorfulline) {
                colorWayUpdate(drivePath.getSteps());
                showcolorPolyline();
            } else {
                showPolyline();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //
    public void zoomSpanAll() {
        if (startPoint != null) {
            if (mAMap == null) {
                return;
            }
            try {
                LatLngBounds bounds = getAllLatLngBounds();
                mAMap.animateCamera(CameraUpdateFactory
                        .newLatLngBounds(bounds, getWindowWidth(), getWindowWidth() - ConvertUtils.Companion.dp2px(100), 50));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected LatLngBounds getAllLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startPoint.latitude, startPoint.longitude));
        b.include(new LatLng(endPoint.latitude, endPoint.longitude));
        List<NaviLatLng> drivePaths = drivePath.getCoordList();
        double biglatitude = 0.0;//最大纬度
        double littlatitude = 180.0;//最小纬度
        double biglon = 0.0; //最大经度
        double littlelon = 180.0; //最小经度
//        point.forEach {
//            if (it.latitude > biglatitude) {
//                biglatitude = it.latitude
//            }
//            if (it.latitude < littlatitude) {
//                littlatitude = it.latitude
//            }
//            if (it.longitude > biglon) {
//                biglon = it.longitude
//            }
//            if (it.longitude < littlelon) {
//                littlelon = it.longitude
//            }
//        }
        for (NaviLatLng it : drivePaths) {
            if (it.getLatitude() > biglatitude) {
                biglatitude = it.getLatitude();
            }
            if (it.getLatitude() < littlatitude) {
                littlatitude = it.getLatitude();
            }
            if (it.getLongitude() > biglon) {
                biglon = it.getLongitude();
            }
            if (it.getLongitude() < littlelon) {
                littlelon = it.getLongitude();
            }
        }

        b.include(new LatLng(biglatitude, biglon));   //东北角
        b.include(new LatLng(littlatitude, littlelon)); //西南角
        return b.build();
    }

    public Marker addRemoveMarker(LatLng end) {
        Marker screenMarker = mAMap.addMarker(new MarkerOptions().title("确定选点").snippet(getString(R.string.way_point))
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.finaly_point)));
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(UtilsKt.getWindowWidth() / 2, UtilsKt.getWindowHight() / 2);
        return screenMarker;
    }

    /**
     * 初始化线段属性
     */
    private void initPolylineOptions() {

        mPolylineOptions = null;

        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getDriveColor()).width(getRouteWidth());
    }

    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }

    private void showcolorPolyline() {
        addPolyLine(mPolylineOptionscolor);
    }

    /**
     * 根据不同的路段拥堵情况展示不同的颜色
     *
     * @param tmcSection
     */
    private void colorWayUpdate(List<AMapNaviStep> tmcSection) {
        if (mAMap == null) {
            return;
        }
        if (tmcSection == null || tmcSection.size() <= 0) {
            return;
        }
        List<AMapNaviStep> steps = drivePath.getSteps();
        AMapNaviStep segmentTrafficStatus;
        mPolylineOptionscolor = null;
        mPolylineOptionscolor = new PolylineOptions();
        mPolylineOptionscolor.width(30);
        List<BitmapDescriptor> colorList = new ArrayList<BitmapDescriptor>();
        mPolylineOptionscolor.add(convertToLatLng(tmcSection.get(0).getLinks().get(0).getCoords().get(0)));
        colorList.add(getDes());
        for (int i = 0; i < tmcSection.size(); i++) {
            segmentTrafficStatus = tmcSection.get(i);
            for (int i1 = 0; i1 < segmentTrafficStatus.getLinks().size(); i1++) {
                BitmapDescriptor color = getTextture(segmentTrafficStatus.getLinks().get(i1).getTrafficStatus());
                List<NaviLatLng> mployline = segmentTrafficStatus.getLinks().get(i1).getCoords();
                for (int j = 1; j < mployline.size(); j++) {
                    mPolylineOptionscolor.add(convertToLatLng(mployline.get(j)));
                    colorList.add(color);
                }
            }
        }
        colorList.add(getDes());
        mPolylineOptionscolor.setCustomTextureList(colorList);
    }

    private int getcolor(int status) {
        if (status == 1) {
            return Color.parseColor("#62B297");
        } else if (status == 2) {
            return Color.parseColor("#EA8D59");
        } else if (status == 3) {
            return Color.parseColor("#E44A44");
        } else if (status == 4) {
            return Color.parseColor("#990033");
        } else {
            return Color.parseColor("#62B297");
        }
    }

    private BitmapDescriptor getTextture(int status) {
        if (status == 1) {
            return BitmapDescriptorFactory.fromResource(R.drawable.custtexture_green);
        } else if (status == 2) {
            return BitmapDescriptorFactory.fromResource(R.drawable.custtexture_slow);
        } else if (status == 3) {
            return BitmapDescriptorFactory.fromResource(R.drawable.custtexture_bad);
        } else if (status == 4) {
            return BitmapDescriptorFactory.fromResource(R.drawable.custtexture_grayred);
        } else {
            return BitmapDescriptorFactory.fromResource(R.drawable.custtexture_no);
        }
    }


    public LatLng convertToLatLng(LatLonPoint point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    public LatLng convertToLatLng(NaviLatLng point) {
        return new LatLng(point.getLatitude(), point.getLongitude());
    }

    /**
     * @param driveStep
     * @param latLng
     */
    private void addDrivingStationMarkers(DriveStep driveStep, LatLng latLng) {
        addStationMarker(new MarkerOptions()
                .position(latLng)
                .title("\u65B9\u5411:" + driveStep.getAction()
                        + "\n\u9053\u8DEF:" + driveStep.getRoad())
                .snippet(driveStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(getDriveBitmapDescriptor()));
    }

    @Override
    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startPoint.latitude, startPoint.longitude));
        b.include(new LatLng(endPoint.latitude, endPoint.longitude));
        if (this.throughPointList != null && this.throughPointList.size() > 0) {
            for (int i = 0; i < this.throughPointList.size(); i++) {
                b.include(new LatLng(
                        this.throughPointList.get(i).getLatitude(),
                        this.throughPointList.get(i).getLongitude()));
            }
        }
        return b.build();
    }

    public void setThroughPointIconVisibility(boolean visible) {
        try {
            throughPointMarkerVisible = visible;
            if (this.throughPointMarkerList != null
                    && this.throughPointMarkerList.size() > 0) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++) {
                    this.throughPointMarkerList.get(i).setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void addThroughPointMarker() {
        if (this.throughPointList != null && this.throughPointList.size() > 0) {
            NaviLatLng latLonPoint = null;
            for (int i = 0; i < this.throughPointList.size(); i++) {
                latLonPoint = this.throughPointList.get(i);
                if (latLonPoint != null) {
                    throughPointMarkerList.add(mAMap
                            .addMarker((new MarkerOptions())
                                    .position(
                                            new LatLng(latLonPoint
                                                    .getLatitude(), latLonPoint
                                                    .getLongitude()))
                                    .visible(throughPointMarkerVisible)
                                    .icon(getThroughPointBitDes(i))
                                    .title("\u9014\u7ECF\u70B9")));
                }
            }
        }
    }

    private BitmapDescriptor getThroughPointBitDes(int i) {
        BitmapDescriptor descriptor;
        switch (i) {
            case 0:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_one);
                break;
            case 1:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_two);
                break;
            case 2:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_three);
                break;
            case 3:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_four);
                break;
            case 4:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_five);
                break;
            case 5:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_six);
                break;
            case 6:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_seven);
                break;
            case 7:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_eight);
                break;
            case 8:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_nine);
                break;
            case 9:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_ten);
                break;
            case 14:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_fiveteen);
                break;
            case 10:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_eleven);
                break;
            case 11:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_twelve);
                break;
            case 12:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_thirteen);
                break;
            case 13:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_fourteen);
                break;
            case 15:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.lable_sixteen);
                break;
            default:
                descriptor = BitmapDescriptorFactory.fromResource(R.drawable.finaly_point);
                break;
        }
        return descriptor;
    }

    /**
     * 获取两点间距离
     *
     * @param start
     * @param end
     * @return
     */
    public static int calculateDistance(LatLng start, LatLng end) {
        double x1 = start.longitude;
        double y1 = start.latitude;
        double x2 = end.longitude;
        double y2 = end.latitude;
        return calculateDistance(x1, y1, x2, y2);
    }

    public static int calculateDistance(double x1, double y1, double x2, double y2) {
        final double NF_pi = 0.01745329251994329; // 弧度 PI/180
        x1 *= NF_pi;
        y1 *= NF_pi;
        x2 *= NF_pi;
        y2 *= NF_pi;
        double sinx1 = Math.sin(x1);
        double siny1 = Math.sin(y1);
        double cosx1 = Math.cos(x1);
        double cosy1 = Math.cos(y1);
        double sinx2 = Math.sin(x2);
        double siny2 = Math.sin(y2);
        double cosx2 = Math.cos(x2);
        double cosy2 = Math.cos(y2);
        double[] v1 = new double[3];
        v1[0] = cosy1 * cosx1 - cosy2 * cosx2;
        v1[1] = cosy1 * sinx1 - cosy2 * sinx2;
        v1[2] = siny1 - siny2;
        double dist = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);

        return (int) (Math.asin(dist / 2) * 12742001.5798544);
    }


    //获取指定两点之间固定距离点
    public static LatLng getPointForDis(LatLng sPt, LatLng ePt, double dis) {
        double lSegLength = calculateDistance(sPt, ePt);
        double preResult = dis / lSegLength;
        return new LatLng((ePt.latitude - sPt.latitude) * preResult + sPt.latitude, (ePt.longitude - sPt.longitude) * preResult + sPt.longitude);
    }

    /**
     * 去掉DriveLineOverlay上的线段和标记。
     */
    @Override
    public void removeFromMap() {
        try {
            super.removeFromMap();
            if (this.throughPointMarkerList != null
                    && this.throughPointMarkerList.size() > 0) {
                for (int i = 0; i < this.throughPointMarkerList.size(); i++) {
                    this.throughPointMarkerList.get(i).remove();
                }
                this.throughPointMarkerList.clear();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}