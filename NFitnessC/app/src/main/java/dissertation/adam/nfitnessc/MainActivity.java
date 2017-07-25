package dissertation.adam.nfitnessc;


import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageButton mLoginMainButton;
    private ImageButton mRegisterButton;
    private ImageButton mLogoutMainButton;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter!=null && nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC Ready", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Enable NFC", Toast.LENGTH_SHORT).show();
        }

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }

        mDrawerList = (ListView) findViewById(R.id.navList);
        addDrawerItems();

        mLoginMainButton = (ImageButton) findViewById(R.id.login_button_main);
        mLoginMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        mLogoutMainButton = (ImageButton) findViewById(R.id.logout_button_main);
        mLogoutMainButton.setVisibility(View.GONE);
        mLogoutMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) MainActivity.this.getApplication()).setEmailLoggedIn(null);
                ((MyApplication) MainActivity.this.getApplication()).setLoggedIn(false);
                mLogoutMainButton.setVisibility(View.GONE);
                mLoginMainButton.setVisibility(View.VISIBLE);
            }
        });

        mRegisterButton = (ImageButton) findViewById(R.id.register_button_main);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private void addDrawerItems() {
        String[] osArray = { "Chest Press Machine", "Treadmill", "Bicep Curl", "History", "Goals"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent c = new Intent(MainActivity.this, ChestPressActivity.class);
                        startActivity(c);
                        break;
                    case 1:
                        Intent t = new Intent(MainActivity.this, TreadmillActivity.class);
                        startActivity(t);
                        break;
                    case 2:
                        Intent l = new Intent(MainActivity.this, BicepCurlActivity.class);
                        startActivity(l);
                        break;
                    case 3:
                        Intent h = new Intent(MainActivity.this, GymHistoryActivity.class);
                        startActivity(h);
                        break;
                    case 4:
                        Intent g = new Intent(MainActivity.this, GoalsActivity.class);
                        startActivity(g);
                        break;
                    default:
                        break;
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        NfcAdapter.getDefaultAdapter(this).enableForegroundDispatch(this, intent,
                null, null);
        if (((MyApplication) this.getApplication()).getEmailLoggedIn()!=null){
            mLoginMainButton.setVisibility(View.GONE);
            mLogoutMainButton.setVisibility(View.VISIBLE);
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        if (NfcAdapter.getDefaultAdapter(this) != null)
            NfcAdapter.getDefaultAdapter(this).disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
        if
                (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }
    void processIntent(Intent intent) {

        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String mNfcText = new String(msg.getRecords()[0].getPayload());

        String newText = mNfcText.substring(3,13);


        if (newText.equals("ChestPress")){
            Intent c = new Intent(MainActivity.this, ChestPressActivity.class);
            startActivity(c);
        } else if (newText.equals("Treadmills")){
            Intent t = new Intent(MainActivity.this, TreadmillActivity.class);
            startActivity(t);
        } else if (newText.equals("BicepCurls")){
            Intent b = new Intent(MainActivity.this, BicepCurlActivity.class);
            startActivity(b);
        } else {
            Toast.makeText(this, "Can't read", Toast.LENGTH_SHORT).show();
        }

    }
}
