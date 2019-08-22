package lu.circl.expandablecardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ExpandableCardView extends LinearLayout {

    private Context context;

    private FrameLayout contentLayout;
    private int cardContentPadding;

    private boolean isExpanded = true;
    private int animationSpeed = 200;


    public ExpandableCardView(Context context) {
        this(context, null);
    }

    public ExpandableCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        setClipToOutline(true);

        TypedArray customAttributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ExpandableCardView, defStyleAttr, 0);

        // general
        int cornerRadius = customAttributes.getDimensionPixelSize(R.styleable.ExpandableCardView_card_corner_radius, 12);

        // header
        String cardTitle = customAttributes.getString(R.styleable.ExpandableCardView_card_title);
        int iconRes = customAttributes.getResourceId(R.styleable.ExpandableCardView_card_icon, 0x0);
        int headerForegroundColor = customAttributes.getColor(R.styleable.ExpandableCardView_card_header_foreground_color, 0xFF000000);
        int headerBackgroundColor = customAttributes.getColor(R.styleable.ExpandableCardView_card_header_background_color, 0xFFFFFFFF);

        // content
        cardContentPadding = customAttributes.getDimensionPixelSize(R.styleable.ExpandableCardView_card_content_padding, 0);
        int cardContentBackgroundColor = customAttributes.getColor(R.styleable.ExpandableCardView_card_content_background_color, 0xFFFFFFFF);

        customAttributes.recycle();

        GradientDrawable cardBackground = new GradientDrawable();
        cardBackground.setCornerRadius(cornerRadius);
        cardBackground.setColor(cardContentBackgroundColor);

        setBackground(cardBackground);
        setElevation(10);

        initHeader(cardTitle, iconRes, headerBackgroundColor, headerForegroundColor);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() == 0) {
            super.addView(child, index, params);  // add header
        } else {
            if (contentLayout == null) {
                contentLayout = new FrameLayout(context);
                contentLayout.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                contentLayout.setPadding(cardContentPadding, cardContentPadding, cardContentPadding, cardContentPadding);

                super.addView(contentLayout, index, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            }

            contentLayout.addView(child);
        }
    }


    private void initHeader(String title, int iconRes, int backgroundColor, int foregroundColor) {
        View header = LayoutInflater.from(context).inflate(R.layout.expandable_card_view_header, this, true);
        LinearLayout ll = header.findViewById(R.id.llRoot);
        ll.setBackgroundColor(backgroundColor);

        TextView titleTextView = header.findViewById(R.id.expandable_card_view_header_title);
        titleTextView.setText(title);
        titleTextView.setTextColor(foregroundColor);

        ImageView iconView = header.findViewById(R.id.expandable_card_view_header_icon);
        if (iconRes == 0x0) {
            iconView.setVisibility(GONE);
        } else {
            iconView.setImageResource(iconRes);
            iconView.setColorFilter(foregroundColor);
        }

        final ImageButton expandToggle = header.findViewById(R.id.expandable_card_view_header_toggle);
        expandToggle.setColorFilter(foregroundColor);
        expandToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExpanded) {
                    collapse(contentLayout);
                    expandToggle.animate().rotation(0).setDuration(animationSpeed);
                } else {
                    expand(contentLayout);
                    expandToggle.animate().rotation(180).setDuration(animationSpeed);
                }

                isExpanded = !isExpanded;
            }
        });
    }

    private void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(animationSpeed);
        v.startAnimation(a);
    }

    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(animationSpeed);
        v.startAnimation(a);
    }

}
