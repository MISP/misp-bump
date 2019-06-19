package lu.circl.mispbump.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ConcurrentModificationException;

/**
 * Can disable touch input on bottom sheet.
 *
 * @param <V>
 */
public class ExtendedBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    private boolean swipeable = false;

    public ExtendedBottomSheetBehavior() {
        super();
    }

    public ExtendedBottomSheetBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
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
