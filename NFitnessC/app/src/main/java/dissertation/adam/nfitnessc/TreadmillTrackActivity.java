package dissertation.adam.nfitnessc;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandIOException;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TreadmillTrackActivity extends AppCompatActivity {

    private Context mContext;
    private SQLiteDatabase mDatabaseL;
    private boolean mLoggedIn;

    private BandClient bandClient = null; //set bandclient to null at first
    private ImageButton mStartButton; //initialise buttons
    private ImageButton mStopButton;
    private ImageButton mConsentButton;
    private ImageButton mAddButton;
    private ImageView mAskingConsent; //initialise text views
    private TextView mHeartRateView;
    private TextView mDistanceView;
    private TextView mCalorieView;
    private TextView mSpeedView;
    private TextView mTimeView;
    List<Integer> mHeartRates = new ArrayList<Integer>(); //ArrayList of heart rates
    List<Double> mSpeeds = new ArrayList<Double>(); //ArrayList of speeds
    private long mStartDistance; //start distance when activated
    private long mStartCalories; //start calories when activated
    private boolean setStartD = false; //used to help get start distance
    private boolean setStartC = false; //used to help get start calories
    DecimalFormat distanceFormat = new DecimalFormat("#.##"); //decimal format for distance
    DecimalFormat heartFormat = new DecimalFormat("#"); //format for heart format
    DecimalFormat speedFormat = new DecimalFormat("#.#");
    DateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private long mStartTime;

    private String mAverageHeartRate; //the final average heart rate
    private String mAverageSpeed; //the final average speed
    private String mFinalDistance; //the final distance
    private long mFinalCalories; //the final calorie count
    private String mElapsedTime; //the final time
    private String mEmail; //email address of logged in member
    Date date = new Date();
    private String mDate = mDateFormat.format(date); //date formatted

    //Listener that listens for the heart beat sensors
    private BandHeartRateEventListener mHeartRateListener = new BandHeartRateEventListener() {
        //method that is used when the heart rate changes
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent bandHeartRateEvent) {
            if (bandHeartRateEvent != null) {
                appendToHeartUI("Heart rate = " + bandHeartRateEvent.getHeartRate()); //output heartrate
                int heart = bandHeartRateEvent.getHeartRate(); //initialies heart to heartbeat
                mHeartRates.add(heart); //add current heartrate to ArrayList
            }
        }
    };

    //Listener that listens for the distance/speed sensors
    private BandDistanceEventListener mDistanceListener = new BandDistanceEventListener() {
        //method that is used when the distance/speed changes
        @Override
        public void onBandDistanceChanged(BandDistanceEvent bandDistanceEvent) {
            if (bandDistanceEvent!= null) {

                //does a check for if this is the start of the activity
                if (setStartD==false){
                    mStartDistance = bandDistanceEvent.getTotalDistance(); //set the start distance
                    setStartD = true; //set boolean to true so its not the start of activity
                }
                appendToSpeedUI("Speed = " + speedFormat.format((bandDistanceEvent.getSpeed()*0.036)) + "km/h"); //output speed
                appendToDistanceUI("Distance = " + distanceFormat.format((bandDistanceEvent.getTotalDistance() - mStartDistance) * 0.00001) + "km"); //output distance
                mFinalDistance = distanceFormat.format((bandDistanceEvent.getTotalDistance() - mStartDistance) * 0.00001); //set final distance
                mSpeeds.add((bandDistanceEvent.getSpeed() * 0.036)); //add current speed to ArrayList
            }
        }
    };

    //same as other listeners
    private BandCaloriesEventListener mCalorieListener = new BandCaloriesEventListener() {
        @Override
        public void onBandCaloriesChanged(BandCaloriesEvent bandCaloriesEvent) {
            if (bandCaloriesEvent!=null){
                if (setStartC==false){
                    mStartCalories = bandCaloriesEvent.getCalories();
                    setStartC = true;
                }
                appendToCaloriesUI("Calories = " + (bandCaloriesEvent.getCalories() - mStartCalories) );
                mFinalCalories = (bandCaloriesEvent.getCalories() - mStartCalories);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treadmill_track);

        mEmail = ((MyApplication) this.getApplication()).getEmailLoggedIn();
        mLoggedIn = ((MyApplication) this.getApplication()).getLoggedIn();

        mAddButton = (ImageButton) findViewById(R.id.add_button);
        mAddButton.setVisibility(View.GONE);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext = getApplicationContext();
                mDatabaseL = new UserBaseHelper(mContext).getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(DbSchema.TreadmillTable.Cols.EMAIL, mEmail);
                values.put(DbSchema.TreadmillTable.Cols.DATE, mDate);
                values.put(DbSchema.TreadmillTable.Cols.TIME, mElapsedTime);
                values.put(DbSchema.TreadmillTable.Cols.DISTANCE, mFinalDistance);
                values.put(DbSchema.TreadmillTable.Cols.SPEED, mAverageSpeed);
                values.put(DbSchema.TreadmillTable.Cols.CALORIES, mFinalCalories);
                values.put(DbSchema.TreadmillTable.Cols.HEARTRATE, mAverageHeartRate);

                if (mLoggedIn == false) {
                    Toast.makeText(TreadmillTrackActivity.this, "Log In", Toast.LENGTH_SHORT).show();
                } else {
                    mDatabaseL.insert(DbSchema.TreadmillTable.NAME, null, values);
                    Toast.makeText(TreadmillTrackActivity.this, "Added To Database", Toast.LENGTH_SHORT).show();
                    finish();
                }


            }
        });

        mTimeView = (TextView) findViewById(R.id.time_text);
        mAskingConsent = (ImageView) findViewById(R.id.consent_text);
        mDistanceView = (TextView) findViewById(R.id.distance_text);
        mHeartRateView = (TextView) findViewById(R.id.heartrate_text);
        mCalorieView = (TextView) findViewById(R.id.calories_text);
        mSpeedView = (TextView) findViewById(R.id.speed_text);

        mStartButton = (ImageButton) findViewById(R.id.start_button);
        mStartButton.setVisibility(View.GONE);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartTime = System.currentTimeMillis();
                mTimeView.setText("");
                mHeartRateView.setText("");
                mDistanceView.setText("");
                mCalorieView.setText("");
                mSpeedView.setText("");
                new HeartRateSubscriptionTask().execute(); //start a heart rate subscription
                new DistanceSubscriptionTask().execute(); //start a distance subscription
                new CalorieSubscriptionTask().execute(); //start a calorie subscription
                mStartButton.setVisibility(View.GONE); //set the start button to disappear
                mStopButton.setVisibility(View.VISIBLE); //set the stop button to appear
            }
        });

        mStopButton = (ImageButton) findViewById(R.id.stop_button);
        mStopButton.setVisibility(View.GONE);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bandClient != null) {
                    try {
                        bandClient.getSensorManager().unregisterHeartRateEventListener(mHeartRateListener); //unregister from listeners
                        bandClient.getSensorManager().unregisterDistanceEventListener(mDistanceListener);
                        bandClient.getSensorManager().unregisterCaloriesEventListener(mCalorieListener);
                    } catch (BandIOException e) {
                        Toast.makeText(TreadmillTrackActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show(); //exception toast
                    }
                }
                mStopButton.setVisibility(View.GONE); //set stop button to disappear
                mStartButton.setVisibility(View.VISIBLE); //set start button to appear
                mAddButton.setVisibility(View.VISIBLE); //set the add button to appear
                averageHeartRate(mHeartRates); //get the average heart rate
                String newFinalCalories = Long.toString(mFinalCalories); //change long into a string to print out
                appendToDistanceUI("Final Distance = " + mFinalDistance + " km"); //output final distance
                appendToCaloriesUI("Calories Burnt = " + newFinalCalories); //output final calorie count
                averageSpeed(mSpeeds); //get the average speed
                mElapsedTime = heartFormat.format((((System.currentTimeMillis() - mStartTime) * 0.001)/60));
                appendToTimeUI("Time taken = " + mElapsedTime + " minutes");
                mStartButton.setVisibility(View.GONE);

            }
        });

        final WeakReference<Activity> reference = new WeakReference<Activity>(this);

        mConsentButton = (ImageButton) findViewById(R.id.consent_button);
        mConsentButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
        public void onClick(View v) {
                new HeartRateConsentTask().execute(reference); //run consent task
                mAskingConsent.setVisibility(View.GONE); //set button and text to disappear
                mConsentButton.setVisibility(View.GONE);
                mStartButton.setVisibility(View.VISIBLE);
            }
        });

    }

    //output to heart text view
    private void appendToHeartUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHeartRateView.setText(string);
            }
        });
    }
    //output to distance text view
    private void appendToDistanceUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDistanceView.setText(string);
            }
        });
    }
    //output to calorie text view
    private void appendToCaloriesUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCalorieView.setText(string);
            }
        });
    }
    //output to speed text view
    private void appendToSpeedUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSpeedView.setText(string);
            }
        });
    }
    //output to time text view
    private void appendToTimeUI(final String string) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTimeView.setText(string);
            }
        });
    }

    //method to find average speed
    private void averageSpeed(List<Double> speeds) {
        int total = 0;
        //loop through ArrayList and calculate average
        for (Double s : speeds) total += s;
        double average = 1.0d * total/ speeds.size();
        mAverageSpeed = speedFormat.format(average);
        appendToSpeedUI("Average Speed = " + speedFormat.format(average) + "km/h"); //output average speed

    }

    //method to find average heart rate
    private void averageHeartRate(List<Integer> heartRates) {
        int total = 0;
        for (Integer h : heartRates) total += h;
        double average = 1.0d * total/ heartRates.size();
        mAverageHeartRate = heartFormat.format(average); //set the final heart rate
        appendToHeartUI("Average Heart Rate = " + heartFormat.format(average));

    }

    //when app closes, disconnect band
    @Override
    protected void onDestroy() {
        if (bandClient != null) {
            try {
                bandClient.disconnect().await();
            } catch (InterruptedException e) {
                // Do nothing as this is happening during destroy
            } catch (BandException e) {
                // Do nothing as this is happening during destroy
            }
        }
        super.onDestroy();
    }

    //method to get the connected band client and return true if the band is connected
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

    //Subscription to heart rate sensor
    private class HeartRateSubscriptionTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (isBandConnected()) {
                    if (bandClient.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        bandClient.getSensorManager().registerHeartRateEventListener(mHeartRateListener); //register to heart rate listener
                    } else {
                        Toast.makeText(TreadmillTrackActivity.this, "Press Accept", Toast.LENGTH_SHORT).show(); //toast to accept consent
                    }

                } else {
                    Toast.makeText(TreadmillTrackActivity.this, "Band not connected", Toast.LENGTH_SHORT).show();
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
                    } Toast.makeText(TreadmillTrackActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(TreadmillTrackActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

    }

    //Subscribe to distance sensor
    private class DistanceSubscriptionTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (isBandConnected()) {
                    if (bandClient.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        bandClient.getSensorManager().registerDistanceEventListener(mDistanceListener);
                    } else {
                        Toast.makeText(TreadmillTrackActivity.this, "Press Accept", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(TreadmillTrackActivity.this, "Band not connected", Toast.LENGTH_SHORT).show();
                }
            } catch (BandException e) {
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
                } Toast.makeText(TreadmillTrackActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(TreadmillTrackActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

    }

    //subscribe to calorie sensor
    private class CalorieSubscriptionTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (isBandConnected()) {
                    if (bandClient.getSensorManager().getCurrentHeartRateConsent() == UserConsent.GRANTED) {
                        bandClient.getSensorManager().registerCaloriesEventListener(mCalorieListener);
                    } else {
                        Toast.makeText(TreadmillTrackActivity.this, "Press Accept", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(TreadmillTrackActivity.this, "Band not connected", Toast.LENGTH_SHORT).show();
                }
            } catch (BandException e) {
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
                } Toast.makeText(TreadmillTrackActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(TreadmillTrackActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

    }

    //class to get consent for heart rate sensors
    private class HeartRateConsentTask extends AsyncTask<WeakReference<Activity>, Void, Void> {
        @Override
        protected Void doInBackground(WeakReference<Activity>... params) {
            try {
                if (isBandConnected()) {

                    if (params[0].get() != null) {
                        bandClient.getSensorManager().requestHeartRateConsent(params[0].get(), new HeartRateConsentListener() {
                            @Override
                            public void userAccepted(boolean consentGiven) {
                            }
                        });
                    }
                } else {
                    Toast.makeText(TreadmillTrackActivity.this, "Band not connected", Toast.LENGTH_SHORT).show();
                }
            } catch (BandException e) {
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
                } Toast.makeText(TreadmillTrackActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(TreadmillTrackActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }
}
