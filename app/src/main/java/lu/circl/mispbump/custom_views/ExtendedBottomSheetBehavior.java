package lu.circl.mispbump.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class ExtendedBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    private boolean swipeable = false;
    private Context context;

    public ExtendedBottomSheetBehavior() {
        super();
    }

    public ExtendedBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (swipeable) {
            return super.onInterceptTouchEvent(parent, child, event);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (swipeable) {
            return super.onTouchEvent(parent, child, event);
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout parent, V child, View target, float velocityX, float velocityY) {
        if (swipeable) {
            return super.onNestedPreFling(parent, child, target, velocityX, velocityY);
        }
        return false;
    }

    public void setSwipeable(boolean swipeable) {
        this.swipeable = swipeable;
    }

    public boolean isSwipeable() {
        return swipeable;
    }
}
