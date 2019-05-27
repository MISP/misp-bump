package lu.circl.mispbump;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lu.circl.mispbump.security.AESSecurity;

public class AESActivity extends AppCompatActivity {

    private TextView info;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes);

        // populate Toolbar (Actionbar)
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        info = findViewById(R.id.aes_info);
        button = findViewById(R.id.aes_button);

        button.setOnClickListener(onButtonClick);
    }

    private View.OnClickListener onButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AESSecurity sec1 = new AESSecurity();
            AESSecurity sec2 = new AESSecurity();

            String message = "Geheimer Text von A als auch von B ...";

            String pub1 = sec1.getPublicKey().toString();
            info.setText("A PK: " + pub1 + "\n");

            String pub2 = sec2.getPublicKey().toString();
            info.append("B PK: " + pub2 + "\n");

            sec1.setForeignPublicKey(sec2.getPublicKey());
            sec2.setForeignPublicKey(sec1.getPublicKey());
            info.append("\n-- public keys wurden ausgetauscht --\n\n");

            String enc1 = sec1.encrypt(message);
            info.append("A encrypted: " + enc1 + "\n");
            String enc2 = sec2.encrypt(message);
            info.append("B encrypted: " + enc2 + "\n\n");

            info.append("A entschlüsselt B's Nachricht: " + sec1.decrypt(enc2) + "\n");
            info.append("B entschlüsselt A's Nachricht: " + sec2.decrypt(enc1) + "\n");
        }
    };
}
