package lu.circl.mispbump.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import lu.circl.mispbump.R;

public class MaterialPreferenceSwitch extends ConstraintLayout {

    private View rootView;

    private TextView titleView, subTitleView;
    private Switch switchView;

    public MaterialPreferenceSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.material_preference_switch, this);
        rootView = view.findViewById(R.id.rootLayout);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialPreferenceSwitch);
        String title = a.getString(R.styleable.MaterialPreferenceSwitch_title);
        String subTitle = a.getString(R.styleable.MaterialPreferenceSwitch_subtitle);
        a.recycle();

        titleView = view.findViewById(R.id.material_preference_title);
        titleView.setText(title);

        subTitleView = view.findViewById(R.id.material_preference_subtitle);
        subTitleView.setText(subTitle);

        switchView = view.findViewById(R.id.material_preference_switch);

        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchView.isEnabled()) {
                    switchView.setChecked(!switchView.isChecked());
                }
            }
        });
    }

    public void setEnabled(boolean enabled) {
        switchView.setEnabled(enabled);
    }

    public void setChecked(boolean checked) {
        switchView.setChecked(checked);
    }

    public boolean isChecked() {
        return switchView.isChecked();
    }

}
