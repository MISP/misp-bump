package de.korrelator.overview.mispauth;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

import java.util.UUID;

public class GenerateQrActivity extends AppCompatActivity {

    private Point displaySize;
    private ImageView qrImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setHomeButtonEnabled(true);

        setContentView(R.layout.activity_generate_qr);
        qrImageView = findViewById(R.id.image_view_qr);


        Display display = getWindowManager().getDefaultDisplay();
        displaySize = new Point();
        display.getSize(displaySize);

        DisplayQR("https://www.google.de \n OrgName \n " + UUID.randomUUID());
    }

    public void DisplayQR(String message){

        Bitmap bitmap = QRCode.from(message)
                .withSize(displaySize.x, displaySize.y)
                .withColor(0xFF000000, 0x00000000)
                .bitmap();

        qrImageView.setImageBitmap(bitmap);
    }
}
