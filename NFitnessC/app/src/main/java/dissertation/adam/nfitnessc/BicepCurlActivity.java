package dissertation.adam.nfitnessc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import org.w3c.dom.Text;

public class BicepCurlActivity extends AppCompatActivity {
    private ArrayAdapter<String> mAdapter;
    private ListView mDrawerList;
    private ImageButton mTrackButton;
    private ImageButton mTrackNoBandButton;
    private TextView mLastWeight;
    private ImageView mWeightTitle;
    private boolean mLoggedIn;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bicep_curl);

        mDrawerList = (ListView) findViewById(R.id.navList_bicep);
        addDrawerItems();

        mLoggedIn = ((MyApplication) this.getApplication()).getLoggedIn();
        mEmail = ((MyApplication) this.getApplication()).getEmailLoggedIn();

        mLastWeight = (TextView) findViewById(R.id.last_weight_bicep);
        mWeightTitle = (ImageView) findViewById(R.id.last_weight_bicep_title);
        mWeightTitle.setVisibility(View.GONE);

        if(mLoggedIn == true) {

            Cursor res = getAllBicepData();
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

        mTrackButton = (ImageButton) findViewById(R.id.tracking_button);
        mTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLoggedIn == true){
                    Intent l = new Intent(BicepCurlActivity.this, BicepCurlTrackActivity.class);
                    startActivity(l);
                } else {
                    Toast.makeText(BicepCurlActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mTrackNoBandButton = (ImageButton) findViewById(R.id.tracking_button_noband);
        mTrackNoBandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLoggedIn == true){
                    Intent n = new Intent(BicepCurlActivity.this, BicepTrackNoBandActivity.class);
                    startActivity(n);
                } else {
                    Toast.makeText(BicepCurlActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void addDrawerItems() {
        String[] osArray = { "Home", "Chest Press Machine", "Treadmill", "History", "Goals" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent c = new Intent(BicepCurlActivity.this, MainActivity.class);
                        startActivity(c);
                        break;
                    case 1:
                        Intent t = new Intent(BicepCurlActivity.this, ChestPressActivity.class);
                        startActivity(t);
                        break;
                    case 2:
                        Intent l = new Intent(BicepCurlActivity.this, TreadmillActivity.class);
                        startActivity(l);
                        break;
                    case 3:
                        Intent h = new Intent(BicepCurlActivity.this, GymHistoryActivity.class);
                        startActivity(h);
                        break;
                    case 4:
                        Intent g = new Intent(BicepCurlActivity.this, GoalsActivity.class);
                        startActivity(g);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public Cursor getAllBicepData() {
        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DbSchema.BicepTable.NAME + " where " + DbSchema.BicepTable.Cols.EMAIL + " = " + "'" + mEmail + "'",null);
        return res;
    }
}
