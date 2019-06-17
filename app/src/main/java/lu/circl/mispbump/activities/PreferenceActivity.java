package lu.circl.mispbump.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.PreferenceManager;

public class PreferenceActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        preferenceManager = PreferenceManager.getInstance(PreferenceActivity.this);

        initializeViews();
    }

    private void initializeViews() {
        Button deleteSyncs = findViewById(R.id.deleteSyncs);
        deleteSyncs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.clearUploadInformation();
            }
        });
    }
}
