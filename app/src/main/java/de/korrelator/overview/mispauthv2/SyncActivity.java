package de.korrelator.overview.mispauthv2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

public class SyncActivity extends AppCompatActivity {

    private Point displaySize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);

        Intent intent = getIntent();

        String qrData = intent.getStringExtra("qr_data");
        PopulateMyQR(qrData);
    }

    private void PopulateMyQR(String qrData){
        ImageView qrView = findViewById(R.id.image_view_sync_qr);

        Bitmap bitmap =
                QRCode.from(qrData)
                .withSize(displaySize.x, displaySize.x)
                .withColor(0xFF000000, 0x00000000)
                .bitmap();

        qrView.setImageBitmap(bitmap);
    }

    private void returnQrData(String qrData){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("qr_data", qrData);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
