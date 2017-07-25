package dissertation.adam.nfitnessc;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BicepTrackNoBandActivity extends AppCompatActivity {
    private TextView mInstructionText;
    private EditText mWeightInput;
    private EditText mRepsInput;
    private ImageButton mAddWeightButton;
    private ImageButton mFinishedButton;
    private ImageButton mAddRepsButton;
    private ImageButton mAddSetButton;
    private int mSets;
    private String mEmail;
    DateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();
    private String mDate = mDateFormat.format(date);
    private boolean mLoggedIn;
    List<String> mReps = new ArrayList<>();
    List<String> mWeightList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_no_band);

        mEmail = ((MyApplication) this.getApplication()).getEmailLoggedIn();
        mLoggedIn = ((MyApplication) this.getApplication()).getLoggedIn();

        mInstructionText = (TextView) findViewById(R.id.instructions_text_noband);
        mInstructionText.setText("Add the weight in kg (just number)");
        mWeightInput = (EditText) findViewById(R.id.weight_input_noband);
        mAddWeightButton = (ImageButton) findViewById(R.id.add_weight_button_noband);
        mAddWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeightList.add(mWeightInput.getText().toString());
                mInstructionText.setText("Add reps");
                mWeightInput.setVisibility(View.GONE);
                mAddWeightButton.setVisibility(View.GONE);
                mRepsInput.setVisibility(View.VISIBLE);
                mAddRepsButton.setVisibility(View.VISIBLE);
            }
        });

        mRepsInput = (EditText) findViewById(R.id.reps_input_noband);
        mRepsInput.setVisibility(View.GONE);
        mAddRepsButton = (ImageButton) findViewById(R.id.add_reps_button_noband);
        mAddRepsButton.setVisibility(View.GONE);
        mAddRepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReps.add(mRepsInput.getText().toString());
                mRepsInput.setVisibility(View.GONE);
                mAddRepsButton.setVisibility(View.GONE);
                mFinishedButton.setVisibility(View.VISIBLE);
                mAddSetButton.setVisibility(View.VISIBLE);
                mInstructionText.setText("Do you want to add a new set? If so click the add set button, if not, click finished");
            }
        });
        mAddSetButton = (ImageButton) findViewById(R.id.addset_button_noband);
        mAddSetButton.setVisibility(View.GONE);
        mAddSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddSetButton.setVisibility(View.GONE);
                mInstructionText.setText("Add the weight in kg (just number)");
                mWeightInput.setVisibility(View.VISIBLE);
                mAddWeightButton.setVisibility(View.VISIBLE);
            }
        });

        mFinishedButton = (ImageButton) findViewById(R.id.finished_button_noband);
        mFinishedButton.setVisibility(View.GONE);
        mFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBaseHelper mDbHelper = new UserBaseHelper(getApplicationContext());
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                StringBuilder mFinalRepCountBuilder = new StringBuilder();
                for (String r : mReps){
                    mFinalRepCountBuilder.append(r + ", ");
                }
                String mFinalRepCount = mFinalRepCountBuilder.toString();
                StringBuilder mFinalWeightBuilder = new StringBuilder();
                for (String w : mWeightList){
                    mFinalWeightBuilder.append(w + "kg, ");
                }
                String mFinalWeightCount = mFinalWeightBuilder.toString();
                mSets = mReps.size();

                ContentValues values = new ContentValues();
                values.put(DbSchema.BicepTable.Cols.EMAIL, mEmail);
                values.put(DbSchema.BicepTable.Cols.DATE, mDate);
                values.put(DbSchema.BicepTable.Cols.SETS, mSets);
                values.put(DbSchema.BicepTable.Cols.REPS, mFinalRepCount);
                values.put(DbSchema.BicepTable.Cols.WEIGHTS, mFinalWeightCount);

                if (mLoggedIn == false) {
                    Toast.makeText(BicepTrackNoBandActivity.this, "Log In", Toast.LENGTH_SHORT).show();
                } else {
                    db.insert(DbSchema.BicepTable.NAME, null, values);
                    Toast.makeText(BicepTrackNoBandActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
