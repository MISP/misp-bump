package lu.circl.mispbump.auxiliary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;
import java.util.Map;

public class QrCodeGenerator {

    private Activity callingActivity;

    public QrCodeGenerator(Activity callingActivity) {
        this.callingActivity = callingActivity;
    }

    public Bitmap generateQrCode(String content) {
        Point displaySize = new Point();
        callingActivity.getWindowManager().getDefaultDisplay().getSize(displaySize);

        int size = displaySize.x;

        if (displaySize.x > displaySize.y) {
            size = displaySize.y;
        }

        size = (int)(size * 0.8);

        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            Map<EncodeHintType, Integer> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 0);

            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            return createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Bitmap createBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? 0xFF000000 : 0x99FFFFFF;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
