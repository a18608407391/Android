package com.zk.library.Weidge;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

import java.util.Arrays;

public class CustomViewDragHelper {
    private static final String TAG = "CustomViewDragHelper";
    public static final int INVALID_POINTER = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_SETTLING = 2;
    public static final int EDGE_LEFT = 1;
    public static final int EDGE_RIGHT = 2;
    public static final int EDGE_TOP = 4;
    public static final int EDGE_BOTTOM = 8;
    public static final int EDGE_ALL = 15;
    public static final int DIRECTION_HORIZONTAL = 1;
    public static final int DIRECTION_VERTICAL = 2;
    public static final int DIRECTION_ALL = 3;
    private static final int EDGE_SIZE = 20;
    private static final int BASE_SETTLE_DURATION = 256;
    private static final int MAX_SETTLE_DURATION = 600;
    private int mDragState;
    private int mTouchSlop;
    private int mActivePointerId = -1;
    private float[] mInitialMotionX;
    private float[] mInitialMotionY;
    private float[] mLastMotionX;
    private float[] mLastMotionY;
    private int[] mInitialEdgesTouched;
    private int[] mEdgeDragsInProgress;
    private int[] mEdgeDragsLocked;
    private int mPointersDown;
    private VelocityTracker mVelocityTracker;
    private float mMaxVelocity;
    private float mMinVelocity;
    private int mEdgeSize;
    private int mTrackingEdges;
    private OverScroller mScroller;
    private final Callback mCallback;
    private View mCapturedView;
    private boolean mReleaseInProgress;
    private final ViewGroup mParentView;
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            --t;
            return t * t * t * t * t + 1.0F;
        }
    };
    private final Runnable mSetIdleRunnable = new Runnable() {
        public void run() {
           setDragState(0);
        }
    };

    public static CustomViewDragHelper create(@NonNull ViewGroup forParent, @NonNull Callback cb) {
        return new CustomViewDragHelper(forParent.getContext(), forParent, cb);
    }

    public static CustomViewDragHelper create(@NonNull ViewGroup forParent, float sensitivity, @NonNull Callback cb) {
        CustomViewDragHelper helper = create(forParent, cb);
        helper.mTouchSlop = (int) ((float) helper.mTouchSlop * (1.0F / sensitivity));
        return helper;
    }

    private CustomViewDragHelper(@NonNull Context context, @NonNull ViewGroup forParent, @NonNull Callback cb) {
        if (forParent == null) {
            throw new IllegalArgumentException("Parent view may not be null");
        } else if (cb == null) {
            throw new IllegalArgumentException("Callback may not be null");
        } else {
            this.mParentView = forParent;
            this.mCallback = cb;
            ViewConfiguration vc = ViewConfiguration.get(context);
            float density = context.getResources().getDisplayMetrics().density;
            this.mEdgeSize = (int) (20.0F * density + 0.5F);
            this.mTouchSlop = vc.getScaledTouchSlop();
            this.mMaxVelocity = (float) vc.getScaledMaximumFlingVelocity();
            this.mMinVelocity = (float) vc.getScaledMinimumFlingVelocity();
            this.mScroller = new OverScroller(context, sInterpolator);
        }
    }

    public void setMinVelocity(float minVel) {
        this.mMinVelocity = minVel;
    }

    public float getMinVelocity() {
        return this.mMinVelocity;
    }

    public int getViewDragState() {
        return this.mDragState;
    }

    public void setEdgeTrackingEnabled(int edgeFlags) {
        this.mTrackingEdges = edgeFlags;
    }

    @Px
    public int getEdgeSize() {
        return this.mEdgeSize;
    }

    public void captureChildView(@NonNull View childView, int activePointerId) {
        if (childView.getParent() != this.mParentView) {
            throw new IllegalArgumentException("captureChildView: parameter must be a descendant of the ViewDragHelper's tracked parent view (" + this.mParentView + ")");
        } else {
            this.mCapturedView = childView;
            this.mActivePointerId = activePointerId;
            this.mCallback.onViewCaptured(childView, activePointerId);
            this.setDragState(1);
        }
    }

    @Nullable
    public View getCapturedView() {
        return this.mCapturedView;
    }

    public int getActivePointerId() {
        return this.mActivePointerId;
    }

    @Px
    public int getTouchSlop() {
        return this.mTouchSlop;
    }

    public void cancel() {
        this.mActivePointerId = -1;
        this.clearMotionHistory();
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }

    }

    public void abort() {
        this.cancel();
        if (this.mDragState == 2) {
            int oldX = this.mScroller.getCurrX();
            int oldY = this.mScroller.getCurrY();
            this.mScroller.abortAnimation();
            int newX = this.mScroller.getCurrX();
            int newY = this.mScroller.getCurrY();
            this.mCallback.onViewPositionChanged(this.mCapturedView, newX, newY, newX - oldX, newY - oldY);
        }

        this.setDragState(0);
    }

    public boolean smoothSlideViewTo(@NonNull View child, int finalLeft, int finalTop) {
        this.mCapturedView = child;
        this.mActivePointerId = -1;
        boolean continueSliding = this.forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0);
        if (!continueSliding && this.mDragState == 0 && this.mCapturedView != null) {
            this.mCapturedView = null;
        }

        return continueSliding;
    }

    public boolean settleCapturedViewAt(int finalLeft, int finalTop) {
        if (!this.mReleaseInProgress) {
            throw new IllegalStateException("Cannot settleCapturedViewAt outside of a call to Callback#onViewReleased");
        } else {
            return this.forceSettleCapturedViewAt(finalLeft, finalTop, (int) this.mVelocityTracker.getXVelocity(this.mActivePointerId), (int) this.mVelocityTracker.getYVelocity(this.mActivePointerId));
        }
    }

    private boolean forceSettleCapturedViewAt(int finalLeft, int finalTop, int xvel, int yvel) {
        int startLeft = this.mCapturedView.getLeft();
        int startTop = this.mCapturedView.getTop();
        int dx = finalLeft - startLeft;
        int dy = finalTop - startTop;
        if (dx == 0 && dy == 0) {
            this.mScroller.abortAnimation();
            this.setDragState(0);
            return false;
        } else {
            int duration = this.computeSettleDuration(this.mCapturedView, dx, dy, xvel, yvel);
            this.mScroller.startScroll(startLeft, startTop, dx, dy, duration);
            this.setDragState(2);
            return true;
        }
    }

    private int computeSettleDuration(View child, int dx, int dy, int xvel, int yvel) {
        xvel = this.clampMag(xvel, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        yvel = this.clampMag(yvel, (int) this.mMinVelocity, (int) this.mMaxVelocity);
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        int absXVel = Math.abs(xvel);
        int absYVel = Math.abs(yvel);
        int addedVel = absXVel + absYVel;
        int addedDistance = absDx + absDy;
        float xweight = xvel != 0 ? (float) absXVel / (float) addedVel : (float) absDx / (float) addedDistance;
        float yweight = yvel != 0 ? (float) absYVel / (float) addedVel : (float) absDy / (float) addedDistance;
        int xduration = this.computeAxisDuration(dx, xvel, this.mCallback.getViewHorizontalDragRange(child));
        int yduration = this.computeAxisDuration(dy, yvel, this.mCallback.getViewVerticalDragRange(child));
        return (int) ((float) xduration * xweight + (float) yduration * yweight);
    }

    private int computeAxisDuration(int delta, int velocity, int motionRange) {
        if (delta == 0) {
            return 0;
        } else {
            int width = this.mParentView.getWidth();
            int halfWidth = width / 2;
            float distanceRatio = Math.min(1.0F, (float) Math.abs(delta) / (float) width);
            float distance = (float) halfWidth + (float) halfWidth * this.distanceInfluenceForSnapDuration(distanceRatio);
            velocity = Math.abs(velocity);
            int duration;
            if (velocity > 0) {
                duration = 4 * Math.round(1000.0F * Math.abs(distance / (float) velocity));
            } else {
                float range = (float) Math.abs(delta) / (float) motionRange;
                duration = (int) ((range + 1.0F) * 256.0F);
            }

            return Math.min(duration, 600);
        }
    }

    private int clampMag(int value, int absMin, int absMax) {
        int absValue = Math.abs(value);
        if (absValue < absMin) {
            return 0;
        } else if (absValue > absMax) {
            return value > 0 ? absMax : -absMax;
        } else {
            return value;
        }
    }

    private float clampMag(float value, float absMin, float absMax) {
        float absValue = Math.abs(value);
        if (absValue < absMin) {
            return 0.0F;
        } else if (absValue > absMax) {
            return value > 0.0F ? absMax : -absMax;
        } else {
            return value;
        }
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5F;
        f *= 0.47123894F;
        return (float) Math.sin((double) f);
    }

    public void flingCapturedView(int minLeft, int minTop, int maxLeft, int maxTop) {
        if (!this.mReleaseInProgress) {
            throw new IllegalStateException("Cannot flingCapturedView outside of a call to Callback#onViewReleased");
        } else {
            this.mScroller.fling(this.mCapturedView.getLeft(), this.mCapturedView.getTop(), (int) this.mVelocityTracker.getXVelocity(this.mActivePointerId), (int) this.mVelocityTracker.getYVelocity(this.mActivePointerId), minLeft, maxLeft, minTop, maxTop);
            this.setDragState(2);
        }
    }

    public boolean continueSettling(boolean deferCallbacks) {
        if (this.mDragState == 2) {
            boolean keepGoing = this.mScroller.computeScrollOffset();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            int dx = x - this.mCapturedView.getLeft();
            int dy = y - this.mCapturedView.getTop();
            if (dx != 0) {
                ViewCompat.offsetLeftAndRight(this.mCapturedView, dx);
            }

            if (dy != 0) {
                ViewCompat.offsetTopAndBottom(this.mCapturedView, dy);
            }

            if (dx != 0 || dy != 0) {
                this.mCallback.onViewPositionChanged(this.mCapturedView, x, y, dx, dy);
            }

            if (keepGoing && x == this.mScroller.getFinalX() && y == this.mScroller.getFinalY()) {
                this.mScroller.abortAnimation();
                keepGoing = false;
            }

            if (!keepGoing) {
                if (deferCallbacks) {
                    this.mParentView.post(this.mSetIdleRunnable);
                } else {
                    this.setDragState(0);
                }
            }
        }

        return this.mDragState == 2;
    }

    private void dispatchViewReleased(float xvel, float yvel) {
        this.mReleaseInProgress = true;
        this.mCallback.onViewReleased(this.mCapturedView, xvel, yvel);
        this.mReleaseInProgress = false;
        if (this.mDragState == 1) {
            this.setDragState(0);
        }

    }

    private void clearMotionHistory() {
        if (this.mInitialMotionX != null) {
            Arrays.fill(this.mInitialMotionX, 0.0F);
            Arrays.fill(this.mInitialMotionY, 0.0F);
            Arrays.fill(this.mLastMotionX, 0.0F);
            Arrays.fill(this.mLastMotionY, 0.0F);
            Arrays.fill(this.mInitialEdgesTouched, 0);
            Arrays.fill(this.mEdgeDragsInProgress, 0);
            Arrays.fill(this.mEdgeDragsLocked, 0);
            this.mPointersDown = 0;
        }
    }

    private void clearMotionHistory(int pointerId) {
        if (this.mInitialMotionX != null && this.isPointerDown(pointerId)) {
            this.mInitialMotionX[pointerId] = 0.0F;
            this.mInitialMotionY[pointerId] = 0.0F;
            this.mLastMotionX[pointerId] = 0.0F;
            this.mLastMotionY[pointerId] = 0.0F;
            this.mInitialEdgesTouched[pointerId] = 0;
            this.mEdgeDragsInProgress[pointerId] = 0;
            this.mEdgeDragsLocked[pointerId] = 0;
            this.mPointersDown &= ~(1 << pointerId);
        }
    }

    private void ensureMotionHistorySizeForId(int pointerId) {
        if (this.mInitialMotionX == null || this.mInitialMotionX.length <= pointerId) {
            float[] imx = new float[pointerId + 1];
            float[] imy = new float[pointerId + 1];
            float[] lmx = new float[pointerId + 1];
            float[] lmy = new float[pointerId + 1];
            int[] iit = new int[pointerId + 1];
            int[] edip = new int[pointerId + 1];
            int[] edl = new int[pointerId + 1];
            if (this.mInitialMotionX != null) {
                System.arraycopy(this.mInitialMotionX, 0, imx, 0, this.mInitialMotionX.length);
                System.arraycopy(this.mInitialMotionY, 0, imy, 0, this.mInitialMotionY.length);
                System.arraycopy(this.mLastMotionX, 0, lmx, 0, this.mLastMotionX.length);
                System.arraycopy(this.mLastMotionY, 0, lmy, 0, this.mLastMotionY.length);
                System.arraycopy(this.mInitialEdgesTouched, 0, iit, 0, this.mInitialEdgesTouched.length);
                System.arraycopy(this.mEdgeDragsInProgress, 0, edip, 0, this.mEdgeDragsInProgress.length);
                System.arraycopy(this.mEdgeDragsLocked, 0, edl, 0, this.mEdgeDragsLocked.length);
            }

            this.mInitialMotionX = imx;
            this.mInitialMotionY = imy;
            this.mLastMotionX = lmx;
            this.mLastMotionY = lmy;
            this.mInitialEdgesTouched = iit;
            this.mEdgeDragsInProgress = edip;
            this.mEdgeDragsLocked = edl;
        }

    }

    private void saveInitialMotion(float x, float y, int pointerId) {
        this.ensureMotionHistorySizeForId(pointerId);
        this.mInitialMotionX[pointerId] = this.mLastMotionX[pointerId] = x;
        this.mInitialMotionY[pointerId] = this.mLastMotionY[pointerId] = y;
        this.mInitialEdgesTouched[pointerId] = this.getEdgesTouched((int) x, (int) y);
        this.mPointersDown |= 1 << pointerId;
    }

    private void saveLastMotion(MotionEvent ev) {
        int pointerCount = ev.getPointerCount();

        for (int i = 0; i < pointerCount; ++i) {
            int pointerId = ev.getPointerId(i);
            if (this.isValidPointerForActionMove(pointerId)) {
                float x = ev.getX(i);
                float y = ev.getY(i);
                this.mLastMotionX[pointerId] = x;
                this.mLastMotionY[pointerId] = y;
            }
        }

    }

    public boolean isPointerDown(int pointerId) {
        return (this.mPointersDown & 1 << pointerId) != 0;
    }

    void setDragState(int state) {
        this.mParentView.removeCallbacks(this.mSetIdleRunnable);
        if (this.mDragState != state) {
            this.mDragState = state;
            this.mCallback.onViewDragStateChanged(state);
            if (this.mDragState == 0) {
                this.mCapturedView = null;
            }
        }

    }

    boolean tryCaptureViewForDrag(View toCapture, int pointerId) {
        if (toCapture == this.mCapturedView && this.mActivePointerId == pointerId) {
            return true;
        } else if (toCapture != null && this.mCallback.tryCaptureView(toCapture, pointerId)) {
            this.mActivePointerId = pointerId;
            this.captureChildView(toCapture, pointerId);
            return true;
        } else {
            return false;
        }
    }

    protected boolean canScroll(@NonNull View v, boolean checkV, int dx, int dy, int x, int y) {
        if (v instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) v;
            int scrollX = v.getScrollX();
            int scrollY = v.getScrollY();
            int count = group.getChildCount();

            for (int i = count - 1; i >= 0; --i) {
                View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() && y + scrollY >= child.getTop() && y + scrollY < child.getBottom() && this.canScroll(child, true, dx, dy, x + scrollX - child.getLeft(), y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return checkV && (v.canScrollHorizontally(-dx) || v.canScrollVertically(-dy));
    }

    public boolean shouldInterceptTouchEvent(@NonNull MotionEvent ev) {
        int action = ev.getActionMasked();
        int actionIndex = ev.getActionIndex();
        if (action == 0) {
            this.cancel();
        }

        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }

        this.mVelocityTracker.addMovement(ev);
        int pointerId;
        float x;
        View toCapture;
        switch (action) {
            case 0:
                 x = ev.getX();
                x = ev.getY();
                pointerId = ev.getPointerId(0);
                this.saveInitialMotion(x, x, pointerId);
                toCapture = this.findTopChildUnder((int) x, (int) x);
                if (toCapture == this.mCapturedView && this.mDragState == 2) {
                    this.tryCaptureViewForDrag(toCapture, pointerId);
                }

                int edgesTouched = this.mInitialEdgesTouched[pointerId];
                if ((edgesTouched & this.mTrackingEdges) != 0) {
                    this.mCallback.onEdgeTouched(edgesTouched & this.mTrackingEdges, pointerId);
                }
                break;
            case 1:
            case 3:
                this.cancel();
                break;
            case 2:
                if (this.mInitialMotionX != null && this.mInitialMotionY != null) {
                    pointerId = ev.getPointerCount();

                    for (int i = 0; i < pointerId; ++i) {
                        pointerId = ev.getPointerId(i);
                        if (this.isValidPointerForActionMove(pointerId)) {
                             x = ev.getX(i);
                            float y = ev.getY(i);
                            float dx = x - this.mInitialMotionX[pointerId];
                            float dy = y - this.mInitialMotionY[pointerId];
                             toCapture = this.findTopChildUnder((int) x, (int) y);
                            boolean pastSlop = toCapture != null && this.checkTouchSlop(toCapture, dx, dy);
                            if (pastSlop) {
                                int oldLeft = toCapture.getLeft();
                                int targetLeft = oldLeft + (int) dx;
                                int newLeft = this.mCallback.clampViewPositionHorizontal(toCapture, targetLeft, (int) dx);
                                int oldTop = toCapture.getTop();
                                int targetTop = oldTop + (int) dy;
                                int newTop = this.mCallback.clampViewPositionVertical(toCapture, targetTop, (int) dy);
                                int hDragRange = this.mCallback.getViewHorizontalDragRange(toCapture);
                                int vDragRange = this.mCallback.getViewVerticalDragRange(toCapture);
                                if ((hDragRange == 0 || hDragRange > 0 && newLeft == oldLeft) && (vDragRange == 0 || vDragRange > 0 && newTop == oldTop)) {
                                    break;
                                }
                            }

                            this.reportNewEdgeDrags(dx, dy, pointerId);
                            if (this.mDragState == 1 || pastSlop && this.tryCaptureViewForDrag(toCapture, pointerId)) {
                                break;
                            }
                        }
                    }

                    this.saveLastMotion(ev);
                }
            case 4:
            default:
                break;
            case 5:
                pointerId = ev.getPointerId(actionIndex);
                x = ev.getX(actionIndex);
                float y = ev.getY(actionIndex);
                this.saveInitialMotion(x, y, pointerId);
                if (this.mDragState == 0) {
                     edgesTouched = this.mInitialEdgesTouched[pointerId];
                    if ((edgesTouched & this.mTrackingEdges) != 0) {
                        this.mCallback.onEdgeTouched(edgesTouched & this.mTrackingEdges, pointerId);
                    }
                } else if (this.mDragState == 2) {
                    toCapture = this.findTopChildUnder((int) x, (int) y);
                    if (toCapture == this.mCapturedView) {
                        this.tryCaptureViewForDrag(toCapture, pointerId);
                    }
                }
                break;
            case 6:
                pointerId = ev.getPointerId(actionIndex);
                this.clearMotionHistory(pointerId);
        }

        return this.mDragState == 1;
    }

    public void processTouchEvent(@NonNull MotionEvent ev) {
        int action = ev.getActionMasked();
        int actionIndex = ev.getActionIndex();
        if (action == 0) {
            this.cancel();
        }

        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }

        this.mVelocityTracker.addMovement(ev);
        int pointerId;
        int i;
        int id;
        float dx;
        float dy;
        float x;
        float y;
        View toCapture;
        switch (action) {
            case 0:
                 x = ev.getX();
                x = ev.getY();
                pointerId = ev.getPointerId(0);
                toCapture = this.findTopChildUnder((int) x, (int) x);
                this.saveInitialMotion(x, x, pointerId);
                this.tryCaptureViewForDrag(toCapture, pointerId);
                id = this.mInitialEdgesTouched[pointerId];
                if ((id & this.mTrackingEdges) != 0) {
                    this.mCallback.onEdgeTouched(id & this.mTrackingEdges, pointerId);
                }
                break;
            case 1:
                if (this.mDragState == 1) {
                    this.releaseViewForPointerUp();
                }

                this.cancel();
                break;
            case 2:
                if (this.mDragState == 1) {
                    if (this.isValidPointerForActionMove(this.mActivePointerId)) {
                        pointerId = ev.findPointerIndex(this.mActivePointerId);
                        x = ev.getX(pointerId);
                        y = ev.getY(pointerId);
                        i = (int) (x - this.mLastMotionX[this.mActivePointerId]);
                        id = (int) (y - this.mLastMotionY[this.mActivePointerId]);
                        this.dragTo(this.mCapturedView.getLeft() + i, this.mCapturedView.getTop() + id, i, id);
                        this.saveLastMotion(ev);
                    }
                } else {
                    pointerId = ev.getPointerCount();

                    for (i = 0; i < pointerId; ++i) {
                        pointerId = ev.getPointerId(i);
                        if (this.isValidPointerForActionMove(pointerId)) {
                             x = ev.getX(i);
                             y = ev.getY(i);
                            dx = x - this.mInitialMotionX[pointerId];
                            dy = y - this.mInitialMotionY[pointerId];
                            this.reportNewEdgeDrags(dx, dy, pointerId);
                            if (this.mDragState == 1) {
                                break;
                            }

                             toCapture = this.findTopChildUnder((int) x, (int) y);
                            if (this.checkTouchSlop(toCapture, dx, dy) && this.tryCaptureViewForDrag(toCapture, pointerId)) {
                                break;
                            }
                        }
                    }

                    this.saveLastMotion(ev);
                }
                break;
            case 3:
                if (this.mDragState == 1) {
                    this.dispatchViewReleased(0.0F, 0.0F);
                }

                this.cancel();
            case 4:
            default:
                break;
            case 5:
                pointerId = ev.getPointerId(actionIndex);
                x = ev.getX(actionIndex);
                y = ev.getY(actionIndex);
                this.saveInitialMotion(x, y, pointerId);
                if (this.mDragState == 0) {
                    toCapture = this.findTopChildUnder((int) x, (int) y);
                    this.tryCaptureViewForDrag(toCapture, pointerId);
                    id = this.mInitialEdgesTouched[pointerId];
                    if ((id & this.mTrackingEdges) != 0) {
                        this.mCallback.onEdgeTouched(id & this.mTrackingEdges, pointerId);
                    }
                } else if (this.isCapturedViewUnder((int) x, (int) y)) {
                    this.tryCaptureViewForDrag(this.mCapturedView, pointerId);
                }
                break;
            case 6:
                pointerId = ev.getPointerId(actionIndex);
                if (this.mDragState == 1 && pointerId == this.mActivePointerId) {
                    i = -1;
                    pointerId = ev.getPointerCount();

                    for (i = 0; i < pointerId; ++i) {
                        id = ev.getPointerId(i);
                        if (id != this.mActivePointerId) {
                            dx = ev.getX(i);
                            dy = ev.getY(i);
                            if (this.findTopChildUnder((int) dx, (int) dy) == this.mCapturedView && this.tryCaptureViewForDrag(this.mCapturedView, id)) {
                                i = this.mActivePointerId;
                                break;
                            }
                        }
                    }

                    if (i == -1) {
                        this.releaseViewForPointerUp();
                    }
                }

                this.clearMotionHistory(pointerId);
        }

    }

    private void reportNewEdgeDrags(float dx, float dy, int pointerId) {
        int dragsStarted = 0;
        if (this.checkNewEdgeDrag(dx, dy, pointerId, 1)) {
            dragsStarted |= 1;
        }

        if (this.checkNewEdgeDrag(dy, dx, pointerId, 4)) {
            dragsStarted |= 4;
        }

        if (this.checkNewEdgeDrag(dx, dy, pointerId, 2)) {
            dragsStarted |= 2;
        }

        if (this.checkNewEdgeDrag(dy, dx, pointerId, 8)) {
            dragsStarted |= 8;
        }

        if (dragsStarted != 0) {
            this.mEdgeDragsInProgress[pointerId] |= dragsStarted;
            this.mCallback.onEdgeDragStarted(dragsStarted, pointerId);
        }

    }

    private boolean checkNewEdgeDrag(float delta, float odelta, int pointerId, int edge) {
        float absDelta = Math.abs(delta);
        float absODelta = Math.abs(odelta);
        if ((this.mInitialEdgesTouched[pointerId] & edge) == edge && (this.mTrackingEdges & edge) != 0 && (this.mEdgeDragsLocked[pointerId] & edge) != edge && (this.mEdgeDragsInProgress[pointerId] & edge) != edge && (absDelta > (float) this.mTouchSlop || absODelta > (float) this.mTouchSlop)) {
            if (absDelta < absODelta * 0.5F && this.mCallback.onEdgeLock(edge)) {
                this.mEdgeDragsLocked[pointerId] |= edge;
                return false;
            } else {
                return (this.mEdgeDragsInProgress[pointerId] & edge) == 0 && absDelta > (float) this.mTouchSlop;
            }
        } else {
            return false;
        }
    }

    private boolean checkTouchSlop(View child, float dx, float dy) {
        if (child == null) {
            return false;
        } else {
            boolean checkHorizontal = this.mCallback.getViewHorizontalDragRange(child) > 0;
            boolean checkVertical = this.mCallback.getViewVerticalDragRange(child) > 0;
            if (checkHorizontal && checkVertical) {
                return dx * dx + dy * dy > (float) (this.mTouchSlop * this.mTouchSlop);
            } else if (checkHorizontal) {
                return Math.abs(dx) > (float) this.mTouchSlop;
            } else if (checkVertical) {
                return Math.abs(dy) > (float) this.mTouchSlop;
            } else {
                return false;
            }
        }
    }

    public boolean checkTouchSlop(int directions) {
        int count = this.mInitialMotionX.length;

        for (int i = 0; i < count; ++i) {
            if (this.checkTouchSlop(directions, i)) {
                return true;
            }
        }

        return false;
    }

    public boolean checkTouchSlop(int directions, int pointerId) {
        if (!this.isPointerDown(pointerId)) {
            return false;
        } else {
            boolean checkHorizontal = (directions & 1) == 1;
            boolean checkVertical = (directions & 2) == 2;
            float dx = this.mLastMotionX[pointerId] - this.mInitialMotionX[pointerId];
            float dy = this.mLastMotionY[pointerId] - this.mInitialMotionY[pointerId];
            if (checkHorizontal && checkVertical) {
                return dx * dx + dy * dy > (float) (this.mTouchSlop * this.mTouchSlop);
            } else if (checkHorizontal) {
                return Math.abs(dx) > (float) this.mTouchSlop;
            } else if (checkVertical) {
                return Math.abs(dy) > (float) this.mTouchSlop;
            } else {
                return false;
            }
        }
    }

    public boolean isEdgeTouched(int edges) {
        int count = this.mInitialEdgesTouched.length;

        for (int i = 0; i < count; ++i) {
            if (this.isEdgeTouched(edges, i)) {
                return true;
            }
        }

        return false;
    }

    public boolean isEdgeTouched(int edges, int pointerId) {
        return this.isPointerDown(pointerId) && (this.mInitialEdgesTouched[pointerId] & edges) != 0;
    }

    private void releaseViewForPointerUp() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxVelocity);
        float xvel = this.clampMag(this.mVelocityTracker.getXVelocity(this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity);
        float yvel = this.clampMag(this.mVelocityTracker.getYVelocity(this.mActivePointerId), this.mMinVelocity, this.mMaxVelocity);
        this.dispatchViewReleased(xvel, yvel);
    }

    private void dragTo(int left, int top, int dx, int dy) {
        int clampedX = left;
        int clampedY = top;
        int oldLeft = this.mCapturedView.getLeft();
        int oldTop = this.mCapturedView.getTop();
        if (dx != 0) {
            clampedX = this.mCallback.clampViewPositionHorizontal(this.mCapturedView, left, dx);
            ViewCompat.offsetLeftAndRight(this.mCapturedView, clampedX - oldLeft);
        }

        if (dy != 0) {
            clampedY = this.mCallback.clampViewPositionVertical(this.mCapturedView, top, dy);
            ViewCompat.offsetTopAndBottom(this.mCapturedView, clampedY - oldTop);
        }

        if (dx != 0 || dy != 0) {
            int clampedDx = clampedX - oldLeft;
            int clampedDy = clampedY - oldTop;
            this.mCallback.onViewPositionChanged(this.mCapturedView, clampedX, clampedY, clampedDx, clampedDy);
        }

    }

    public boolean isCapturedViewUnder(int x, int y) {
        return this.isViewUnder(this.mCapturedView, x, y);
    }

    public boolean isViewUnder(@Nullable View view, int x, int y) {
        if (view == null) {
            return false;
        } else {
            return x >= view.getLeft() && x < view.getRight() && y >= view.getTop() && y < view.getBottom();
        }
    }

    @Nullable
    public View findTopChildUnder(int x, int y) {
        int childCount = this.mParentView.getChildCount();

        for (int i = childCount - 1; i >= 0; --i) {
            View child = this.mParentView.getChildAt(this.mCallback.getOrderedChildIndex(i));
            if (x >= child.getLeft() && x < child.getRight() && y >= child.getTop() && y < child.getBottom()) {
                return child;
            }
        }

        return null;
    }

    private int getEdgesTouched(int x, int y) {
        int result = 0;
        if (x < this.mParentView.getLeft() + this.mEdgeSize) {
            result |= 1;
        }

        if (y < this.mParentView.getTop() + this.mEdgeSize) {
            result |= 4;
        }

        if (x > this.mParentView.getRight() - this.mEdgeSize) {
            result |= 2;
        }

        if (y > this.mParentView.getBottom() - this.mEdgeSize) {
            result |= 8;
        }

        return result;
    }

    private boolean isValidPointerForActionMove(int pointerId) {
        if (!this.isPointerDown(pointerId)) {
            Log.e("ViewDragHelper", "Ignoring pointerId=" + pointerId + " because ACTION_DOWN was not received " + "for this pointer before ACTION_MOVE. It likely happened because " + " ViewDragHelper did not receive all the events in the event stream.");
            return false;
        } else {
            return true;
        }
    }

    public abstract static class Callback {
        public Callback() {
        }

        public void onViewDragStateChanged(int state) {
        }

        public void onViewPositionChanged(@NonNull View changedView, int left, int top, @Px int dx, @Px int dy) {
        }

        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
        }

        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
        }

        public void onEdgeTouched(int edgeFlags, int pointerId) {
        }

        public boolean onEdgeLock(int edgeFlags) {
            return false;
        }

        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        }

        public int getOrderedChildIndex(int index) {
            return index;
        }

        public int getViewHorizontalDragRange(@NonNull View child) {
            return 0;
        }

        public int getViewVerticalDragRange(@NonNull View child) {
            return 0;
        }

        public abstract boolean tryCaptureView(@NonNull View var1, int var2);

        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return 0;
        }

        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return 0;
        }
    }
}

