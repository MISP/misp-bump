package lu.circl.mispbump.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

import lu.circl.mispbump.R;
import lu.circl.mispbump.models.UploadInformation;

public class UploadInformationActivity extends AppCompatActivity {

    public static String EXTRA_UPLOAD_INFO_KEY = "uploadInformation";

    private UploadInformation uploadInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_information);

        parseExtra();
        init();
    }

    private void parseExtra() {
        String uploadInfo = getIntent().getStringExtra(EXTRA_UPLOAD_INFO_KEY);
        this.uploadInformation = new Gson().fromJson(uploadInfo, UploadInformation.class);
    }

    private void init() {
        TextView name = findViewById(R.id.orgName);
        name.setText(uploadInformation.getRemote().organisation.name);
    }

}
