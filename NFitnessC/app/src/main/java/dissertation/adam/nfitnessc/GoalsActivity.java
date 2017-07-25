package dissertation.adam.nfitnessc;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GoalsActivity extends AppCompatActivity {
    private ArrayAdapter<String> mAdapter;
    private ListView mDrawerList;
    private ImageView mWeightInstructions;
    private ImageView mGoalInstructions;
    private ImageButton mAddWeight;
    private ImageButton mAddGoal;
    private ImageButton mAddGoalButton;
    private TextView mLastWeight;
    private TextView mGoal;
    private ImageView mCongrats;
    private EditText mAddWeightInput;
    private EditText mAddGoalInput;
    private String mEmail;
    private boolean mLoggedIn;
    DateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();
    private String mDate = mDateFormat.format(date);
    private String mLastWeightTrack;
    private String mLastGoalTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        mDrawerList = (ListView) findViewById(R.id.navList_goal);
        addDrawerItems();
        mLoggedIn = ((MyApplication) this.getApplication()).getLoggedIn();

        if(mLoggedIn == false){
            Toast.makeText(GoalsActivity.this, "Log in Please",Toast.LENGTH_SHORT).show();
            finish();
        }
        mEmail = ((MyApplication) this.getApplication()).getEmailLoggedIn();
        mCongrats = (ImageView) findViewById(R.id.congratulations);
        mCongrats.setVisibility(View.GONE);

        mWeightInstructions = (ImageView) findViewById(R.id.instructions_weight);

        mGoalInstructions = (ImageView) findViewById(R.id.instructions_goal);
        mGoalInstructions.setVisibility(View.GONE);

        mAddWeightInput = (EditText) findViewById(R.id.weight_input_goal);
        mAddWeight = (ImageButton) findViewById(R.id.add_body_weight_button);
        mAddWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(mLoggedIn == true){
                    UserBaseHelper mDbHelper = new UserBaseHelper(getApplicationContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(DbSchema.WeightTable.Cols.EMAIL, mEmail);
                    values.put(DbSchema.WeightTable.Cols.DATE, mDate);
                    values.put(DbSchema.WeightTable.Cols.WEIGHT, mAddWeightInput.getText().toString());

                    db.insert(DbSchema.WeightTable.NAME, null, values);

                    Toast.makeText(GoalsActivity.this, "Added Weight", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(GoalsActivity.this, "Log in", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        mLastWeight = (TextView) findViewById(R.id.last_body_weight);

        Cursor res = getAllWeightData();
        if(res.getCount() != 0){
            StringBuffer buffer = new StringBuffer();
            res.moveToLast();
            buffer.append("\n");
            buffer.append("LAST WEIGHT\n\n");
            buffer.append("Date : " + res.getString(1) + "\n");
            buffer.append("Last Weight : " + res.getString(2) + " kg\n");
            mLastWeightTrack = res.getString(2);

            mLastWeight.setText(buffer.toString());
        }

        mAddGoalInput = (EditText) findViewById(R.id.goal_weight);
        mAddGoalInput.setVisibility(View.GONE);
        mAddGoalButton = (ImageButton) findViewById(R.id.add_goal_weight_button);
        mAddGoalButton.setVisibility(View.GONE);

        mAddGoal = (ImageButton) findViewById(R.id.add_goal_button);
        mAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeightInstructions.setVisibility(View.GONE);
                mGoalInstructions.setVisibility(View.VISIBLE);
                mAddGoal.setVisibility(View.GONE);
                mAddWeightInput.setVisibility(View.GONE);
                mAddWeight.setVisibility(View.GONE);
                mAddGoalInput.setVisibility(View.VISIBLE);
                mAddGoalButton.setVisibility(View.VISIBLE);

            }
        });

        mAddGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoggedIn == true) {
                    UserBaseHelper mDbHelper = new UserBaseHelper(getApplicationContext());
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(DbSchema.GoalTable.Cols.EMAIL, mEmail);
                    values.put(DbSchema.GoalTable.Cols.DATE, mDate);
                    values.put(DbSchema.GoalTable.Cols.GOAL, mAddGoalInput.getText().toString());

                    db.insert(DbSchema.GoalTable.NAME, null, values);

                    Toast.makeText(GoalsActivity.this, "Added Goal", Toast.LENGTH_SHORT).show();
                    recreate();
                } else {
                    Toast.makeText(GoalsActivity.this, "Log in", Toast.LENGTH_SHORT).show();
                    finish();
                }

                mAddWeightInput.setVisibility(View.VISIBLE);
                mAddWeight.setVisibility(View.VISIBLE);
                mAddGoalButton.setVisibility(View.GONE);
                mAddGoalInput.setVisibility(View.GONE);
                mAddGoal.setVisibility(View.VISIBLE);
                mWeightInstructions.setVisibility(View.VISIBLE);
                mGoalInstructions.setVisibility(View.GONE);
            }
        });

        mGoal = (TextView) findViewById(R.id.goal);
        Cursor res2 = getAllGoalData();
        if(res2.getCount() != 0){
            StringBuffer buffer2 = new StringBuffer();
            res2.moveToLast();
            buffer2.append("\n");
            buffer2.append("CURRENT GOAL\n\n");
            buffer2.append("Date : " + res2.getString(1) + "\n");
            buffer2.append("Goal : " + res2.getString(2) + " kg\n");
            mLastGoalTrack = res2.getString(2);

            mGoal.setText(buffer2.toString());
        }

        if (res.getCount() !=0 && res2.getCount() !=0 ){
            int weight = Integer.parseInt(mLastWeightTrack);
            int goal = Integer.parseInt(mLastGoalTrack);

            if (weight <= goal) {
                mCongrats.setVisibility(View.VISIBLE);
            }
        }




    }
    private void addDrawerItems() {
        String[] osArray = { "Home", "Chest Press Machine", "Treadmill", "Bicep Curl", "History" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent c = new Intent(GoalsActivity.this, MainActivity.class);
                        startActivity(c);
                        break;
                    case 1:
                        Intent t = new Intent(GoalsActivity.this, ChestPressActivity.class);
                        startActivity(t);
                        break;
                    case 2:
                        Intent l = new Intent(GoalsActivity.this, TreadmillActivity.class);
                        startActivity(l);
                        break;
                    case 3:
                        Intent h = new Intent(GoalsActivity.this, BicepCurlActivity.class);
                        startActivity(h);
                        break;
                    case 4:
                        Intent g = new Intent(GoalsActivity.this, GymHistoryActivity.class);
                        startActivity(g);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public Cursor getAllWeightData() {
        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DbSchema.WeightTable.NAME + " where " + DbSchema.ChestTable.Cols.EMAIL + " = " + "'" + mEmail + "'", null);
        return res;
    }
    public Cursor getAllGoalData() {
        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DbSchema.GoalTable.NAME + " where " + DbSchema.ChestTable.Cols.EMAIL + " = " + "'" + mEmail + "'", null);
        return res;
    }
}
