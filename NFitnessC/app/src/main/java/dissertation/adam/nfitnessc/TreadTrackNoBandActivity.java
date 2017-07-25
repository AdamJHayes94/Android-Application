package dissertation.adam.nfitnessc;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TreadTrackNoBandActivity extends AppCompatActivity {
    private String mEmail;
    private boolean mLoggedIn;
    DateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();
    private String mDate = mDateFormat.format(date);
    private EditText mTimeInput;
    private EditText mDistanceInput;
    private EditText mSpeedInput;
    private EditText mCaloriesInput;
    private ImageButton mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tread_track_no_band);

        mEmail = ((MyApplication) this.getApplication()).getEmailLoggedIn();
        mLoggedIn = ((MyApplication) this.getApplication()).getLoggedIn();

        mTimeInput = (EditText) findViewById(R.id.time_taken_noband);
        mDistanceInput = (EditText) findViewById(R.id.distance_noband);
        mSpeedInput = (EditText) findViewById(R.id.average_speed_noband);
        mCaloriesInput = (EditText) findViewById(R.id.calories_noband);
        mAddButton = (ImageButton) findViewById(R.id.add_treadmill_button_noband);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBaseHelper mDbHelper = new UserBaseHelper(getApplicationContext());
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(DbSchema.TreadmillTable.Cols.EMAIL, mEmail);
                values.put(DbSchema.TreadmillTable.Cols.DATE, mDate);
                values.put(DbSchema.TreadmillTable.Cols.TIME, mTimeInput.getText().toString());
                values.put(DbSchema.TreadmillTable.Cols.DISTANCE, mDistanceInput.getText().toString());
                values.put(DbSchema.TreadmillTable.Cols.SPEED, mSpeedInput.getText().toString());
                values.put(DbSchema.TreadmillTable.Cols.CALORIES, mCaloriesInput.getText().toString());
                values.put(DbSchema.TreadmillTable.Cols.HEARTRATE, "N/A");

                if (mLoggedIn == false) {
                    Toast.makeText(TreadTrackNoBandActivity.this, "Log In", Toast.LENGTH_SHORT).show();
                } else {
                    db.insert(DbSchema.TreadmillTable.NAME, null, values);
                    Toast.makeText(TreadTrackNoBandActivity.this, "Added To Database", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });


    }
}
