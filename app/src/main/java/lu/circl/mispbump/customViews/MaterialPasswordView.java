package lu.circl.mispbump.customViews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import lu.circl.mispbump.R;


public class MaterialPasswordView extends ConstraintLayout {

    private TextView titleView, passwordView;


    public MaterialPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.material_password_view, this);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialPasswordView);
        final String title = a.getString(R.styleable.MaterialPasswordView_title);
        final String password = a.getString(R.styleable.MaterialPasswordView_password);
        a.recycle();

        titleView = view.findViewById(R.id.material_password_title);
        titleView.setText(title);

        passwordView = view.findViewById(R.id.material_password);
        passwordView.setTransformationMethod(new PasswordTransformationMethod());
        passwordView.setText(password);

        ImageButton visibleToggle = findViewById(R.id.visibleToggle);

        AnimatedVectorDrawable lookAway = (AnimatedVectorDrawable) context.getDrawable(R.drawable.animated_eye_to_up);
        AnimatedVectorDrawable lookCenter = (AnimatedVectorDrawable) context.getDrawable(R.drawable.animated_eye_to_center);

        visibleToggle.setOnClickListener(v -> {
            if (passwordView.getTransformationMethod() == null) {
                passwordView.setTransformationMethod(new PasswordTransformationMethod());
                visibleToggle.setImageDrawable(lookCenter);
                lookCenter.start();
            } else {
                passwordView.setTransformationMethod(null);
                visibleToggle.setImageDrawable(lookAway);
                lookAway.start();
            }
        });
    }


    public void setTitle(String title) {
        titleView.setText(title);
    }

    public String getTitle() {
        return titleView.getText().toString();
    }

    public void setPassword(String password) {
        passwordView.setText(password);
    }

    public String getPassword() {
        return passwordView.getText().toString();
    }

    public void setPasswordVisible(boolean visible) {
        if (!visible) {
            passwordView.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            passwordView.setTransformationMethod(null);
        }
    }

}
