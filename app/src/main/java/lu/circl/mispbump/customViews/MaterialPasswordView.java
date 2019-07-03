package lu.circl.mispbump.customViews;

import android.content.Context;
import android.content.res.TypedArray;
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
    private OnCopyClickListener onCopyClickListener;


    public MaterialPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.material_password_view, this);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MaterialPasswordView);
        final String title = a.getString(R.styleable.MaterialPasswordView_title);
        final String password = a.getString(R.styleable.MaterialPasswordView_password);
        a.recycle();

        ImageButton copyButton = view.findViewById(R.id.copy);
        copyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onCopyClickListener.onClick(title, getPassword());
            }
        });

        titleView = view.findViewById(R.id.material_password_title);
        titleView.setText(title);

        passwordView = view.findViewById(R.id.material_password);
        passwordView.setTransformationMethod(new PasswordTransformationMethod());
        passwordView.setText(password);

        ImageButton visibleToggle = findViewById(R.id.visibleToggle);
        visibleToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordView.getTransformationMethod() == null) {
                    passwordView.setTransformationMethod(new PasswordTransformationMethod());
                } else {
                    passwordView.setTransformationMethod(null);
                }
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


    public void addOnCopyClickedListener(OnCopyClickListener listener) {
        onCopyClickListener = listener;
    }


    public interface OnCopyClickListener {
        void onClick(String title, String password);
    }
}
