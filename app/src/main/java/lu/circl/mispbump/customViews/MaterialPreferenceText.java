package lu.circl.mispbump.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import lu.circl.mispbump.R;

public class MaterialPreferenceText extends ConstraintLayout {

    private View rootView;
    private ImageView icon;
    private TextView title, subtitle;


    public MaterialPreferenceText(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.material_preference_text, this);

        rootView = view.findViewById(R.id.rootLayout);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialPreferenceText);

        icon = view.findViewById(R.id.material_preference_src);
        int imageRes = a.getResourceId(R.styleable.MaterialPreferenceText_pref_icon, 0x0);
        if (imageRes != 0x0){
            icon.setImageResource(imageRes);
        } else {
            icon.setVisibility(GONE);
        }

        title = view.findViewById(R.id.material_preference_title);
        title.setText(a.getString(R.styleable.MaterialPreferenceText_title));

        subtitle = view.findViewById(R.id.material_preference_subtitle);
        subtitle.setText(a.getString(R.styleable.MaterialPreferenceText_subtitle));

        a.recycle();
    }


    public void setTitle(String title) {
        this.title.setText(title);
    }

    public String getTitle() {
        return this.title.getText().toString();
    }


    public void setSubtitle(String subtitle) {
        this.subtitle.setText(subtitle);
    }

    public String getSubtitle() {
        return this.subtitle.getText().toString();
    }


    public void setDrawable(int resourceId) {
        this.icon.setImageResource(resourceId);
    }

    public void setDrawable(Drawable drawable) {
        this.icon.setImageDrawable(drawable);
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        rootView.setOnClickListener(l);
    }
}
