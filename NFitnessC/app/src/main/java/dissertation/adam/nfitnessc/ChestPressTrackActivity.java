package dissertation.adam.nfitnessc;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.SampleRate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChestPressTrackActivity extends AppCompatActivity {
    private BandClient bandClient = null;
    private boolean mLoggedIn;
    private TextView mInstructionsView;
    private EditText mWeightInput;
    private ImageButton mStartButton;
    private ImageButton mStopButton;
    private ImageButton mFinishedButton;
    private ImageButton mAddWeightButton;
    private ImageButton mYesButton;
    private ImageButton mNoButton;
    private boolean mFirst = false;
    private boolean mSecond = false;
    private boolean mThird = false;
    private int mRepCount = 0;
    private int mSets;
    private String mEmail;
    DateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();
    private String mDate = mDateFormat.format(date);
    List<Integer> mReps = new ArrayList<Integer>();
    List<String> mWeightList = new ArrayList<>();

    List<Float> mAccDataX = new ArrayList<Float>();
    private BandAccelerometerEventListener mAccelerometerListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(BandAccelerometerEvent bandAccelerometerEvent) {
            if (bandAccelerometerEvent != null) {
                appendToUI("Begin Exercise");
                mAccDataX.add(bandAccelerometerEvent.getAccelerationX());
            }
        }
    };

    public void appendToUI(final String string){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInstructionsView.setText(string);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscle_track);

        mYesButton = (ImageButton) findViewById(R.id.yes_button);
        mYesButton.setVisibility(View.GONE);
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBaseHelper mDbHelper = new UserBaseHelper(getApplicationContext());
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                StringBuilder mFinalRepCountBuilder = new StringBuilder();
                for (Integer r : mReps){
                    mFinalRepCountBuilder.append(r + ", ");
                }
                String mFinalRepCount = mFinalRepCountBuilder.toString();
                StringBuilder mFinalWeightBuilder = new StringBuilder();
                for (String w : mWeightList){
                    mFinalWeightBuilder.append(w + "kg, ");
                }
                String mFinalWeightCount = mFinalWeightBuilder.toString();

                ContentValues values = new ContentValues();
                values.put(DbSchema.ChestTable.Cols.EMAIL, mEmail);
                values.put(DbSchema.ChestTable.Cols.DATE, mDate);
                values.put(DbSchema.ChestTable.Cols.SETS, mSets);
                values.put(DbSchema.ChestTable.Cols.REPS, mFinalRepCount);
                values.put(DbSchema.ChestTable.Cols.WEIGHTS, mFinalWeightCount);

                mLoggedIn = ((MyApplication) ChestPressTrackActivity.this.getApplication()).getLoggedIn();
                if (mLoggedIn == false) {
                    Toast.makeText(ChestPressTrackActivity.this, "Log In", Toast.LENGTH_SHORT).show();
                } else {
                    db.insert(DbSchema.ChestTable.NAME, null, values);
                    Toast.makeText(ChestPressTrackActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        mNoButton = (ImageButton) findViewById(R.id.no_button);
        mNoButton.setVisibility(View.GONE);
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChestPressTrackActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });

        mWeightInput = (EditText) findViewById(R.id.weight_input);

        mEmail = ((MyApplication) this.getApplication()).getEmailLoggedIn();

        mAddWeightButton = (ImageButton) findViewById(R.id.add_weight_button);
        mAddWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeightList.add(mWeightInput.getText().toString());
                mStartButton.setVisibility(View.VISIBLE);
                mAddWeightButton.setVisibility(View.GONE);
                mWeightInput.setVisibility(View.GONE);
                mInstructionsView.setText("Press Start, get into position and begin in 3 seconds.");

            }
        });

        mFinishedButton = (ImageButton) findViewById(R.id.finished_button_chest);
        mFinishedButton.setVisibility(View.GONE);
        mFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder mFinalRepCountBuilder = new StringBuilder();
                for (Integer r : mReps){
                    mFinalRepCountBuilder.append(r + ", ");
                }
                String mFinalRepCount = mFinalRepCountBuilder.toString();
                StringBuilder mFinalWeightBuilder = new StringBuilder();
                for (String w : mWeightList){
                    mFinalWeightBuilder.append(w + "kg, ");
                }
                String mFinalWeightCount = mFinalWeightBuilder.toString();
                mSets = mReps.size();
                mInstructionsView.setText("You did " + mSets + " sets with reps of " + mFinalRepCount + " with weights of: " + mFinalWeightCount + "\n Is this correct?");
                mYesButton.setVisibility(View.VISIBLE);
                mNoButton.setVisibility(View.VISIBLE);
                mFinishedButton.setVisibility(View.GONE);
                mWeightInput.setVisibility(View.GONE);
                mAddWeightButton.setVisibility(View.GONE);

            }
        });
        mInstructionsView = (TextView) findViewById(R.id.instructions_text);
        mInstructionsView.setText("Insert Weight to Start (Just number)");
        mStartButton = (ImageButton) findViewById(R.id.start_button_chest);
        mStartButton.setVisibility(View.GONE);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAccDataX.clear();
                mFirst = false;
                mSecond = false;
                mThird = false;
                mRepCount = 0;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mInstructionsView.setText("Begin Exercise");
                        new AccelerometerSubscriptionTask().execute();

                    }
                }, 3000);


                mStartButton.setVisibility(View.GONE);
                mStopButton.setVisibility(View.VISIBLE);

            }
        });

        mStopButton = (ImageButton) findViewById(R.id.stop_button_chest);
        mStopButton.setVisibility(View.GONE);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeightInput.setVisibility(View.VISIBLE);
                mAddWeightButton.setVisibility(View.VISIBLE);
                mInstructionsView.setText("Want to add a new set? Input weight again. If not, click Finish");

                if (bandClient != null) {
                    try {
                        bandClient.getSensorManager().unregisterAccelerometerEventListener(mAccelerometerListener); //unregister from listeners

                    } catch (BandIOException e) {
                        Toast.makeText(ChestPressTrackActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show(); //exception toast
                    }
                }
                mStopButton.setVisibility(View.GONE);
                mFinishedButton.setVisibility(View.VISIBLE);

                for (Float x : mAccDataX) {
                    if (mFirst == true && mSecond == true && mThird == true) {
                        mRepCount++;
                        mFirst = false;
                        mSecond = false;
                        mThird = false;
                    }

                    if (mFirst == false) {
                        if (x < 0) {
                            mFirst = true;
                            continue;
                        }
                    } else if (mSecond == false) {
                        if (x > 0) {
                            mSecond = true;
                            continue;
                        } else if (x < 0) {
                            continue;
                        }
                    } else if (mThird == false) {
                        if (x < 0) {
                            mThird = true;
                            continue;
                        }
                    }
                }
                mReps.add(mRepCount);

            }
        });




    }

    private class AccelerometerSubscriptionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (isBandConnected()) {
                    bandClient.getSensorManager().registerAccelerometerEventListener(mAccelerometerListener, SampleRate.MS128);

                } else {
                    Toast.makeText(ChestPressTrackActivity.this, "Band not connected", Toast.LENGTH_SHORT).show();
                }
            } catch (BandException e) { //exception lists
                String exceptionMessage = "";
                switch (e.getErrorType()){
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage="Unsupported SDK version";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage="Service not available";
                        break;
                    default:
                        exceptionMessage="Unknown error occured: " + e.getMessage() + "\n";
                        break;
                } Toast.makeText(ChestPressTrackActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(ChestPressTrackActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

    }

    @Override
    protected void onDestroy() {
        if (bandClient != null) {
            try {bandClient.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    private boolean isBandConnected() throws InterruptedException, BandException {
        //check to see if the bandclient hasnt been set yet (bandClient is set to null at start)
        if (bandClient == null) {
            BandInfo[] pairedBands = BandClientManager.getInstance().getPairedBands();
            //checks if no bands are paired
            if (pairedBands.length == 0) {
                Toast.makeText(this, "Band Not Connected", Toast.LENGTH_LONG).show(); //toast to show user band not connected
                return false; //return false to say band not connected therefore cant use the bandclient
            }
            bandClient = BandClientManager.getInstance().create(getBaseContext(), pairedBands[0]); //get the first paired band

        } else if (ConnectionState.CONNECTED == bandClient.getConnectionState()) {
            return true; //band is connected so return true
        }
        return ConnectionState.CONNECTED == bandClient.connect().await();
    }
}
