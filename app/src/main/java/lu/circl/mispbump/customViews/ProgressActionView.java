package lu.circl.mispbump.customViews;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import lu.circl.mispbump.R;


public class ProgressActionView extends LinearLayout {

    private Context context;

    private ImageView icon;
    private ProgressBar progressBar;
    private TextView title, feedback;

    private Drawable pendingIcon, doneIcon, errorIcon;

    public ProgressActionView(Context context) {
        this(context, null);
    }
    public ProgressActionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ProgressActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    public ProgressActionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initViews(attrs);
    }


    private void initViews(AttributeSet attrs) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_upload_action, this, true);

        icon = v.findViewById(R.id.progressIcon);
        progressBar = v.findViewById(R.id.progressBar);
        title = v.findViewById(R.id.title);
        feedback = v.findViewById(R.id.error);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressActionView);
        title.setText(a.getString(R.styleable.ProgressActionView_action_title));
        pendingIcon = context.getDrawable(a.getResourceId(R.styleable.ProgressActionView_action_pending_icon, 0));
        doneIcon = context.getDrawable(a.getResourceId(R.styleable.ProgressActionView_action_done_icon, 0));
        errorIcon = context.getDrawable(a.getResourceId(R.styleable.ProgressActionView_action_error_icon, 0));
        a.recycle();

        pendingIcon.setTint(context.getColor(R.color.status_amber));
        doneIcon.setTint(context.getColor(R.color.status_green));
        errorIcon.setTint(context.getColor(R.color.status_red));

        pending();
        icon.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.status_amber)));
    }


    public void pending() {
        progressBar.setVisibility(GONE);
        switchIcon(pendingIcon, R.color.status_amber);
        icon.setVisibility(VISIBLE);
    }

    public void start() {
        progressBar.setVisibility(VISIBLE);

        icon.setVisibility(GONE);
        feedback.setVisibility(GONE);
    }

    public void done() {
        done("");
    }

    public void done(String message) {
        progressBar.setVisibility(GONE);

        switchIcon(doneIcon, R.color.status_green);
        icon.setVisibility(VISIBLE);

        if (message.isEmpty()) {
            feedback.setVisibility(GONE);
        } else {
            feedback.setTextColor(context.getColor(R.color.status_amber));
            feedback.setText(message);
            feedback.setVisibility(VISIBLE);
        }
    }

    public void error(String error) {
        progressBar.setVisibility(GONE);

        switchIcon(errorIcon, R.color.status_red);
        icon.setVisibility(VISIBLE);

        feedback.setTextColor(context.getColor(R.color.status_red));
        feedback.setText(error);
        feedback.setVisibility(VISIBLE);
    }

    public void info(String info) {
        progressBar.setVisibility(GONE);

        switchIcon(errorIcon, R.color.status_amber);
        icon.setVisibility(VISIBLE);

        this.feedback.setTextColor(context.getColor(R.color.status_amber));
        this.feedback.setText(info);
        this.feedback.setVisibility(VISIBLE);
    }


    private void switchIcon(Drawable d, int color) {
        icon.setImageDrawable(d);
        icon.setImageTintList(ColorStateList.valueOf(context.getColor(color)));
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setErrorIconDrawable(Drawable d) {
        errorIcon = d;
    }

    public void setPendingIconDrawable(Drawable d) {
        pendingIcon = d;
    }

    public void setDoneIconDrawable(Drawable d) {
        doneIcon = d;
    }
}
