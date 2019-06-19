package lu.circl.mispbump.customViews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.widget.ImageViewCompat;

import lu.circl.mispbump.R;

public class UploadAction extends LinearLayoutCompat {

    private Context context;

    public enum UploadState {
        PENDING,
        LOADING,
        DONE,
        ERROR
    }

    private TextView errorView;
    private UploadState currentUploadState;
    private ImageView stateView;
    private ProgressBar progressBar;


    public UploadAction(Context context) {
        super(context);
        this.context = context;
    }

    public UploadAction(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UploadAction);
        String title = a.getString(R.styleable.UploadAction_description);
        a.recycle();

        LayoutInflater inflater = LayoutInflater.from(context);
        View baseView = inflater.inflate(R.layout.view_upload_action, this);

        errorView = findViewById(R.id.error);

        TextView titleView = baseView.findViewById(R.id.title);
        titleView.setText(title);

        stateView = findViewById(R.id.stateView);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Displays an error message for the upload action.
     * @param error a string to show or null to hide
     */
    public void setError(String error) {
        if (error == null) {
            errorView.setVisibility(GONE);
        }

        errorView.setText(error);
        errorView.setVisibility(VISIBLE);
    }

    public void setCurrentUploadState(UploadState state) {
        currentUploadState = state;
        progressBar.setVisibility(GONE);

        switch (state) {
            case PENDING:
                stateView.setVisibility(VISIBLE);
                stateView.setImageResource(R.drawable.ic_info_outline);
                ImageViewCompat.setImageTintList(stateView, ColorStateList.valueOf(context.getColor(R.color.status_amber)));
                break;

            case LOADING:
                stateView.setVisibility(GONE);
                progressBar.setVisibility(VISIBLE);
                break;

            case DONE:
                stateView.setVisibility(VISIBLE);
                stateView.setImageResource(R.drawable.ic_check);
                ImageViewCompat.setImageTintList(stateView, ColorStateList.valueOf(context.getColor(R.color.status_green)));
                break;

            case ERROR:
                stateView.setVisibility(VISIBLE);
                stateView.setImageResource(R.drawable.ic_error_outline);
                ImageViewCompat.setImageTintList(stateView, ColorStateList.valueOf(context.getColor(R.color.status_red)));
                break;
        }
    }

    public UploadState getCurrentUploadState() {
        return currentUploadState;
    }

}
