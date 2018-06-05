package de.overview.wg.its.misp_authentificator.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.glxn.qrgen.android.QRCode;

import java.lang.reflect.Field;

import de.overview.wg.its.misp_authentificator.R;

public class SyncActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        generateMyQR();
    }

    private void generateMyQR() {
//        ImageView qrImageView = findViewById(R.id.sync_my_qr);

        Bitmap myBitmap = QRCode.from("This is my organisation information!")
                .withColor(0xFF000000, 0x00000000)
                .withSize(512,512)
                .bitmap();

//        qrImageView.setImageBitmap(myBitmap);
    }
}
