package dissertation.adam.nfitnessc;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static dissertation.adam.nfitnessc.DbSchema.UserTable;
import static dissertation.adam.nfitnessc.DbSchema.TreadmillTable;
import static dissertation.adam.nfitnessc.DbSchema.ChestTable;
import static dissertation.adam.nfitnessc.DbSchema.BicepTable;
import static dissertation.adam.nfitnessc.DbSchema.WeightTable;
import static dissertation.adam.nfitnessc.DbSchema.GoalTable;

/**
 * Created by Adam on 17/02/2016.
 */
public class UserBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "FitnessBase.db";
    private static final String CREATE_TABLE_1 = "CREATE TABLE " + UserTable.NAME + "(" + UserTable.Cols.EMAIL + ", " + UserTable.Cols.FIRSTNAME + ", " + UserTable.Cols.SURNAME + ", " + UserTable.Cols.PASSWORD + ")";
    private static final String CREATE_TABLE_2 = "CREATE TABLE " + TreadmillTable.NAME + "(" + TreadmillTable.Cols.EMAIL + ", " + TreadmillTable.Cols.DATE + ", " + TreadmillTable.Cols.TIME + ", " + TreadmillTable.Cols.DISTANCE +  ", " + TreadmillTable.Cols.SPEED + ", " + TreadmillTable.Cols.CALORIES + ", " + TreadmillTable.Cols.HEARTRATE +  ")";
    private static final String CREATE_TABLE_3 = "CREATE TABLE " + ChestTable.NAME + "(" + ChestTable.Cols.EMAIL + ", " + ChestTable.Cols.DATE + ", " + ChestTable.Cols.SETS + ", " + ChestTable.Cols.REPS +  ", " + ChestTable.Cols.WEIGHTS + ")";
    private static final String CREATE_TABLE_4 = "CREATE TABLE " + BicepTable.NAME + "(" + BicepTable.Cols.EMAIL + ", " + BicepTable.Cols.DATE + ", " + BicepTable.Cols.SETS + ", " + BicepTable.Cols.REPS +  ", " + BicepTable.Cols.WEIGHTS + ")";
    private static final String CREATE_TABLE_5 = "CREATE TABLE " + WeightTable.NAME + "(" + WeightTable.Cols.EMAIL + ", " + WeightTable.Cols.DATE + ", " + WeightTable.Cols.WEIGHT + ")";
    private static final String CREATE_TABLE_6 = "CREATE TABLE " + GoalTable.NAME + "(" + GoalTable.Cols.EMAIL + ", " + GoalTable.Cols.DATE + ", " + GoalTable.Cols.GOAL + ")";
    public UserBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_1);
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(CREATE_TABLE_3);
        db.execSQL(CREATE_TABLE_4);
        db.execSQL(CREATE_TABLE_5);
        db.execSQL(CREATE_TABLE_6);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TreadmillTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ChestTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BicepTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeightTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoalTable.NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
