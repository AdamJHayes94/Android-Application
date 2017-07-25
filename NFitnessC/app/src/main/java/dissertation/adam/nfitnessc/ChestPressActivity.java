package dissertation.adam.nfitnessc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChestPressActivity extends AppCompatActivity {
    private ArrayAdapter<String> mAdapter;
    private ListView mDrawerList;
    private ImageButton mTrackButton;
    private ImageButton mTrackNoBandButton;
    private TextView mLastWeight;
    private ImageView mWeightTitle;
    private String mEmail;
    private boolean mLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_press);

        mDrawerList = (ListView) findViewById(R.id.navList_chest);
        addDrawerItems();

        mLoggedIn = ((MyApplication) this.getApplication()).getLoggedIn();
        mEmail = ((MyApplication) this.getApplication()).getEmailLoggedIn();

        mLastWeight = (TextView) findViewById(R.id.last_weight_chest);
        mWeightTitle = (ImageView) findViewById(R.id.last_weight_chest_title);
        mWeightTitle.setVisibility(View.GONE);

        if(mLoggedIn == true) {

            Cursor res = getAllChestData();
            if(res.getCount() == 0){
            } else {
                mWeightTitle.setVisibility(View.VISIBLE);
                StringBuffer buffer = new StringBuffer();
                res.moveToLast();
                buffer.append("Sets : " + res.getString(2) + "\n");
                buffer.append("Reps : " + res.getString(3) + "\n");
                buffer.append("Weights : " + res.getString(4));

                mLastWeight.setText(buffer.toString());
            }


        }


        mTrackButton = (ImageButton) findViewById(R.id.chest_tracking_button);
        mTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLoggedIn == true){
                    Intent i = new Intent(ChestPressActivity.this, ChestPressTrackActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(ChestPressActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mTrackNoBandButton = (ImageButton) findViewById(R.id.tracking_button_noband_chest);
        mTrackNoBandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLoggedIn == true){
                    Intent n = new Intent(ChestPressActivity.this, ChestTrackNoBandActivity.class);
                    startActivity(n);
                } else {
                    Toast.makeText(ChestPressActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void addDrawerItems() {
        String[] osArray = { "Home", "Treadmill", "Bicep Curl", "History", "Goals"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent c = new Intent(ChestPressActivity.this, MainActivity.class);
                        startActivity(c);
                        break;
                    case 1:
                        Intent t = new Intent(ChestPressActivity.this, TreadmillActivity.class);
                        startActivity(t);
                        break;
                    case 2:
                        Intent l = new Intent(ChestPressActivity.this, BicepCurlActivity.class);
                        startActivity(l);
                        break;
                    case 3:
                        Intent h = new Intent(ChestPressActivity.this, GymHistoryActivity.class);
                        startActivity(h);
                        break;
                    case 4:
                        Intent g = new Intent(ChestPressActivity.this, GoalsActivity.class);
                        startActivity(g);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public Cursor getAllChestData() {
        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DbSchema.ChestTable.NAME + " where " + DbSchema.ChestTable.Cols.EMAIL + " = " + "'" + mEmail + "'",null);
        return res;
    }
}
