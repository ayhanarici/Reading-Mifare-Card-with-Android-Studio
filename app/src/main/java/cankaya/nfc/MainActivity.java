package cankaya.nfc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    TextView cardID;
    ConstraintLayout bgLayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nfcAdapter=NfcAdapter.getDefaultAdapter(this);
        cardID = (TextView)findViewById(R.id.cardId);
        bgLayer = (ConstraintLayout)findViewById(R.id.backgroundCard);
        if(nfcAdapter!=null&& nfcAdapter.isEnabled()){
            //Toast.makeText(this, "NFC Kullanılabilir Durumda...", Toast.LENGTH_SHORT).show();
        }else{
            cardID.setText("NFC Ayarlarınızı Kontrol Edip Uygulamayı Yeniden Başlatınız...");
            //Toast.makeText(this, "NFC Kullanılamaz...", Toast.LENGTH_SHORT).show();
            //finish();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(nfcAdapter.EXTRA_TAG);
        byte[] id = tag.getId();
        ByteBuffer wrapped = ByteBuffer.wrap(id);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        int signedInt = wrapped.getInt();
        long number = signedInt & 0xffffffffl;
        cardID.setText(Integer.toHexString(signedInt));
        if (cardID.getText().equals("ad7bec4c"))//Örnek geçerli kart ID kontrolü
        {
            bgLayer.setBackgroundColor(Color.GREEN);
        }else
        {
            bgLayer.setBackgroundColor(Color.RED);
        }


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                cardID.setText("Kart Bekleniyor...");
                bgLayer.setBackgroundColor(Color.rgb(135,135,135));
            }
        }, 2000); // Millisecond 1000 = 1 sec

        super.onNewIntent(intent);
    }
    @Override
    protected void onResume() {
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilter=new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilter,null);
        super.onResume();

    }
    @Override
    protected void onPause() {
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

}