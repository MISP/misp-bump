package lu.circl.mispbump.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lu.circl.mispbump.R;

public class MaterialPreferenceText extends ConstraintLayout {

    private View baseView;
    private TextView title, subtitle;

    public MaterialPreferenceText(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        baseView = inflater.inflate(R.layout.material_preference_text, this);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialPreferenceText);

        ImageView icon = baseView.findViewById(R.id.material_preference_src);
        icon.setImageResource(a.getResourceId(R.styleable.MaterialPreferenceText_pref_icon, 0x0));

        title = baseView.findViewById(R.id.material_preference_title);
        title.setText(a.getString(R.styleable.MaterialPreferenceText_text));

        subtitle = baseView.findViewById(R.id.material_preference_subtitle);
        subtitle.setText(a.getString(R.styleable.MaterialPreferenceText_subText));

        a.recycle();
    }

    public void setSubText(String subText) {
        subtitle.setText(subText);
    }
}
