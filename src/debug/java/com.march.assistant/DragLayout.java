package com.march.assistant;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.march.common.x.SizeX;


/**
 * CreateAt : 2018/6/15
 * Describe : 拖动的 view
 *
 * @author chendong
 */
public class DragLayout extends FrameLayout {

    private int  lastX;
    private int  lastY;
    private int  lastDownX;
    private int  lastDownY;
    private long downTime;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private View.OnClickListener     mClickListener;
    private View.OnLongClickListener mOnLongClickListener;

    public DragLayout(Context context) {
        super(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 绝对位置
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                if (mOnLongClickListener != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Math.abs(lastDownX - lastX) <= 10 && Math.abs(lastDownY - lastY) <= 10) {
                                mOnLongClickListener.onLongClick(DragLayout.this);
                            }
                        }
                    }, 400);
                }
                lastX = rawX;
                lastY = rawY;
                lastDownX = lastX;
                lastDownY = lastY;
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算偏移量
                int offSetX = rawX - lastX;
                int offSetY = rawY - lastY;
                // 在当前的left、top、right、bottom 基础上加上偏移量
                int left = getLeft() + offSetX;
                int top = getTop() + offSetY;
                int right = getRight() + offSetX;
                int bottom = getBottom() + offSetY;
                if (left > 10 && top > 10 && right < SizeX.WIDTH - 10 && bottom < SizeX.HEIGHT - 10) {
                    // LogX.e(left + " " + top + " " + right + " " + bottom);
                    layout(left, top, right, bottom);
                }
                // 重新设置坐标
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(lastDownX - lastX) <= 10 && Math.abs(lastDownY - lastY) <= 10) {
                    if (mClickListener != null && System.currentTimeMillis() - downTime < 400) {
                        mClickListener.onClick(this);
                    }
                }
                mHandler.removeCallbacksAndMessages(null);
                break;
        }
        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        this.mOnLongClickListener = l;
    }
}
