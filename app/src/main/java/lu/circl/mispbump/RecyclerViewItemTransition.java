package lu.circl.mispbump;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;


public class RecyclerViewItemTransition extends Transition {

    private static final String PROPNAME_ELEVATION = "customtransition:change_elevation:toolbar";

    private void captureTransitionValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ELEVATION, transitionValues.view.getElevation());
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureTransitionValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureTransitionValues(transitionValues);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (null == startValues || null == endValues) {
            return null;
        }

        final View view = endValues.view;

        int startElevation = 0;
        int endElevation = 6;

        ValueAnimator anim = ValueAnimator.ofFloat(startElevation, endElevation);

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (float) animation.getAnimatedValue();
                view.setElevation(t);
            }
        });

        return anim;
    }
}
