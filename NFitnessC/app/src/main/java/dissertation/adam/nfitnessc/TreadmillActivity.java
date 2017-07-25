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

public class TreadmillActivity extends AppCompatActivity {
    private ArrayAdapter<String> mAdapter;
    private ListView mDrawerList;
    private ImageButton mTreadmillTrackButton;
    private ImageButton mTreadNoBand;
    private ImageView mRunTitle;
    private TextView mLastRun;
    private boolean mLoggedIn;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treadmill);
        mLoggedIn = ((MyApplication) this.getApplication()).getLoggedIn();
        mEmail = ((MyApplication) this.getApplication()).getEmailLoggedIn();

        mDrawerList = (ListView) findViewById(R.id.navList_tread);
        addDrawerItems();

        mRunTitle = (ImageView) findViewById(R.id.last_time_text);
        mRunTitle.setVisibility(View.GONE);

        mLastRun = (TextView) findViewById(R.id.last_weight_tread);

        if(mLoggedIn == true) {

            Cursor res = getAllTreadData();
            if(res.getCount() == 0){


            } else {
                mRunTitle.setVisibility(View.VISIBLE);
                StringBuffer buffer = new StringBuffer();
                res.moveToLast();
                buffer.append("Date : " + res.getString(1) + "\n");
                buffer.append("Time Taken : " + res.getString(2) + "\n");
                buffer.append("Distance : " + res.getString(3) + "km\n");
                buffer.append("Average Speed : " + res.getString(4) + "km/h\n");
                buffer.append("Calories : " + res.getString(5) + "\n");
                buffer.append("Average Heart Rate : " + res.getString(6));

                mLastRun.setText(buffer.toString());
            }
        }

        mTreadmillTrackButton = (ImageButton) findViewById(R.id.treadmill_tracking_button);
        mTreadmillTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mLoggedIn == true){
                    Intent i = new Intent(TreadmillActivity.this, TreadmillTrackActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(TreadmillActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mTreadNoBand = (ImageButton) findViewById(R.id.treadmill_tracking_button_noband);
        mTreadNoBand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLoggedIn == true){
                    Intent t = new Intent(TreadmillActivity.this, TreadTrackNoBandActivity.class);
                    startActivity(t);
                } else {
                    Toast.makeText(TreadmillActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void addDrawerItems() {
        String[] osArray = { "Home", "Chest Press Machine", "Bicep Curl", "History", "Goals" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent c = new Intent(TreadmillActivity.this, MainActivity.class);
                        startActivity(c);
                        break;
                    case 1:
                        Intent t = new Intent(TreadmillActivity.this, ChestPressActivity.class);
                        startActivity(t);
                        break;
                    case 2:
                        Intent l = new Intent(TreadmillActivity.this, BicepCurlActivity.class);
                        startActivity(l);
                        break;
                    case 3:
                        Intent h = new Intent(TreadmillActivity.this, GymHistoryActivity.class);
                        startActivity(h);
                        break;
                    case 4:
                        Intent g = new Intent(TreadmillActivity.this, GoalsActivity.class);
                        startActivity(g);
                        break;
                    default:
                        break;
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.RootViewTread));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
    public Cursor getAllTreadData() {
        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DbSchema.TreadmillTable.NAME + " where " + DbSchema.TreadmillTable.Cols.EMAIL + " = " + "'" + mEmail + "'",null);
        return res;
    }
}
