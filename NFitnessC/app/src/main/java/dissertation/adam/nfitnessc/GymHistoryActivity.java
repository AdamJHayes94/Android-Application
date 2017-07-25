package dissertation.adam.nfitnessc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GymHistoryActivity extends AppCompatActivity {
    private ArrayAdapter<String> mAdapter;
    private ListView mDrawerList;
    private ImageButton mChestButton;
    private ImageButton mTreadButton;
    private ImageButton mBicepButton;
    private TextView mQueryView;
    private TextView mTitleView;
    private boolean mLoggedIn = false;
    private String mEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_history);

        mDrawerList = (ListView) findViewById(R.id.navList_history);
        addDrawerItems();

        mLoggedIn = ((MyApplication) this.getApplication()).getLoggedIn();
        mEmail = ((MyApplication) this.getApplication()).getEmailLoggedIn();

        mTitleView = (TextView) findViewById(R.id.history_title);
        mTitleView.setText("");

        mQueryView = (TextView) findViewById(R.id.query_results);
        mChestButton = (ImageButton) findViewById(R.id.chest_button_history);
        mChestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleView.setText("Chest Press Machine History");
                mQueryView.setText("");

                if(mLoggedIn == false){
                    Toast.makeText(GymHistoryActivity.this, "Not Logged In", Toast.LENGTH_SHORT).show();
                    finish();
                }

                Cursor res = getAllChestData();
                if(res.getCount() == 0){
                    Toast.makeText(GymHistoryActivity.this, "No Results", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("Date : " + res.getString(1) + "\n");
                    buffer.append("Sets : " + res.getString(2) + "\n");
                    buffer.append("Reps : " + res.getString(3) + "\n");
                    buffer.append("Weights : " + res.getString(4) + "\n\n");
                }

                mQueryView.setText(buffer.toString());

                mTreadButton.setVisibility(View.VISIBLE);
                mChestButton.setVisibility(View.GONE);
                mBicepButton.setVisibility(View.VISIBLE);
            }
        });

        mTreadButton = (ImageButton) findViewById(R.id.treadmill_button_history);
        mTreadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleView.setText("Treadmill History");
                mQueryView.setText("");

                if(mLoggedIn == false){
                    Toast.makeText(GymHistoryActivity.this, "Not Logged In", Toast.LENGTH_SHORT).show();
                    finish();
                }

                Cursor res = getAllTreadmillData();
                if(res.getCount() == 0){
                    Toast.makeText(GymHistoryActivity.this, "No Results", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("Date : " + res.getString(1) + "\n");
                    buffer.append("Time Taken : " + res.getString(2) + "\n");
                    buffer.append("Distance : " + res.getString(3) + "\n");
                    buffer.append("Average Speed : " + res.getString(4) + "\n");
                    buffer.append("Calories : " + res.getString(5) + "\n");
                    buffer.append("Average Heart Rate : " + res.getString(6) + "\n\n");
                }

                mQueryView.setText(buffer.toString());

                mTreadButton.setVisibility(View.GONE);
                mChestButton.setVisibility(View.VISIBLE);
                mBicepButton.setVisibility(View.VISIBLE);
            }
        });

        mBicepButton = (ImageButton) findViewById(R.id.bicep_button_history);
        mBicepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleView.setText("Bicep Curl Machine History");
                mQueryView.setText("");

                if(mLoggedIn == false){
                    Toast.makeText(GymHistoryActivity.this, "Not Logged In", Toast.LENGTH_SHORT).show();
                    finish();
                }

                Cursor res = getAllBicepData();
                if(res.getCount() == 0){
                    Toast.makeText(GymHistoryActivity.this, "No Results", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("Date : " + res.getString(1) + "\n");
                    buffer.append("Sets : " + res.getString(2) + "\n");
                    buffer.append("Reps : " + res.getString(3) + "\n");
                    buffer.append("Weights : " + res.getString(4) + "\n\n");
                }
                mQueryView.setText(buffer.toString());

                mTreadButton.setVisibility(View.VISIBLE);
                mChestButton.setVisibility(View.VISIBLE);
                mBicepButton.setVisibility(View.GONE);

            }
        });

    }

    private void addDrawerItems() {
        String[] osArray = {"Home", "Chest Press Machine", "Treadmill", "Bicep Curl", "Goals" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent c = new Intent(GymHistoryActivity.this, MainActivity.class);
                        startActivity(c);
                        break;
                    case 1:
                        Intent t = new Intent(GymHistoryActivity.this, ChestPressActivity.class);
                        startActivity(t);
                        break;
                    case 2:
                        Intent l = new Intent(GymHistoryActivity.this, TreadmillActivity.class);
                        startActivity(l);
                        break;
                    case 3:
                        Intent h = new Intent(GymHistoryActivity.this, BicepCurlActivity.class);
                        startActivity(h);
                        break;
                    case 4:
                        Intent g = new Intent(GymHistoryActivity.this, GoalsActivity.class);
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

    public Cursor getAllTreadmillData() {
        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DbSchema.TreadmillTable.NAME + " where " + DbSchema.TreadmillTable.Cols.EMAIL + " = " + "'" + mEmail + "'",null);
        return res;
    }

    public Cursor getAllBicepData() {
        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DbSchema.BicepTable.NAME + " where " + DbSchema.BicepTable.Cols.EMAIL + " = " + "'" + mEmail + "'",null);
        return res;
    }
}
