package lu.circl.mispbump.activities;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ImageViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import lu.circl.mispbump.R;
import lu.circl.mispbump.models.UploadInformation;

public class UploadInformationActivity extends AppCompatActivity {

    public static String EXTRA_UPLOAD_INFO_KEY = "uploadInformation";

    private View rootLayout;
    private ImageView syncStatusIcon;

    private UploadInformation uploadInformation;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_information);


        parseExtra();
        init();
        tintSystemBars();
        populateContent();
    }

    private void parseExtra() {
        String uploadInfo = getIntent().getStringExtra(EXTRA_UPLOAD_INFO_KEY);
        this.uploadInformation = new Gson().fromJson(uploadInfo, UploadInformation.class);
    }

    private void init() {
        rootLayout = findViewById(R.id.rootLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setDisplayHomeAsUpEnabled(true);

        fab = findViewById(R.id.fab);

        syncStatusIcon = findViewById(R.id.syncStatus);
    }

    private void populateContent() {
        switch (uploadInformation.getCurrentSyncStatus()) {
            case COMPLETE:
                ImageViewCompat.setImageTintList(syncStatusIcon, ColorStateList.valueOf(getColor(R.color.status_green)));
                syncStatusIcon.setImageResource(R.drawable.ic_check_outline);
                fab.hide();
                break;
            case FAILURE:
                ImageViewCompat.setImageTintList(syncStatusIcon, ColorStateList.valueOf(getColor(R.color.status_red)));
                syncStatusIcon.setImageResource(R.drawable.ic_error_outline);
                break;
            case PENDING:
                ImageViewCompat.setImageTintList(syncStatusIcon, ColorStateList.valueOf(getColor(R.color.status_amber)));
                syncStatusIcon.setImageResource(R.drawable.ic_info_outline);
                break;
        }

        TextView name = findViewById(R.id.orgName);
        name.setText(uploadInformation.getRemote().organisation.name);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        fab.show();
    }

    @Override
    public void postponeEnterTransition() {
        super.postponeEnterTransition();
        fab.show();
    }

    @Override
    public void startPostponedEnterTransition() {
        super.startPostponedEnterTransition();
        fab.show();
    }

    private void tintSystemBars() {
        // Initial colors of each system bar.
        final int statusBarColor = getColor(R.color.white);
        final int toolbarColor = getColor(R.color.white);

        // Desired final colors of each bar.
        final int statusBarToColor = getColor(R.color.colorPrimary);
        final int toolbarToColor = getColor(R.color.colorPrimary);

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Use animation position to blend colors.
                float position = animation.getAnimatedFraction();

                // Apply blended color to the status bar.
                int blended = blendColors(statusBarColor, statusBarToColor, position);
                getWindow().setStatusBarColor(blended);

                blended = blendColors(toolbarColor, toolbarToColor, position);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(blended));
            }
        });

        anim.setDuration(500).start();
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
