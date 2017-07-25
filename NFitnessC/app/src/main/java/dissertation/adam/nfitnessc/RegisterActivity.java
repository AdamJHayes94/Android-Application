package dissertation.adam.nfitnessc;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEmailEdit;
    private EditText mFirstNameEdit;
    private EditText mSurnameEdit;
    private EditText mPasswordEdit;
    private ImageButton mRegisterButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UserBaseHelper mDbHelper = new UserBaseHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        mEmailEdit = (EditText) findViewById(R.id.email_input);
        mFirstNameEdit = (EditText) findViewById(R.id.first_name_input);
        mSurnameEdit = (EditText) findViewById(R.id.surname_input);
        mPasswordEdit = (EditText) findViewById(R.id.password_input);

        mRegisterButton = (ImageButton) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserBaseHelper mDbHelper = new UserBaseHelper(getApplicationContext());
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                boolean validEmail = isEmailValid(mEmailEdit.getText().toString());

                if (mEmailEdit.getText().toString().matches("") | mFirstNameEdit.getText().toString().matches("") | mSurnameEdit.getText().toString().matches("") | mPasswordEdit.getText().toString().matches("")){
                    Toast.makeText(RegisterActivity.this, "Fill in all boxes", Toast.LENGTH_SHORT).show();

                } else {

                    if(validEmail == false){
                        Toast.makeText(RegisterActivity.this, "Enter correct email", Toast.LENGTH_SHORT).show();
                    } else {

                        if(userRegister(mEmailEdit.getText().toString()) == false){
                            Toast.makeText(RegisterActivity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                        } else {


                            //code taken from http://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
                            String passwordToHash = mPasswordEdit.getText().toString();
                            String generatedPassword = null;
                            try {
                                MessageDigest md = MessageDigest.getInstance("MD5");
                                md.update(passwordToHash.getBytes());

                                byte[] bytes = md.digest();
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < bytes.length; i++) {
                                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                                }
                                generatedPassword = sb.toString();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }

                            ContentValues values = new ContentValues();
                            values.put(DbSchema.UserTable.Cols.SURNAME, mSurnameEdit.getText().toString());
                            values.put(DbSchema.UserTable.Cols.FIRSTNAME, mFirstNameEdit.getText().toString());
                            values.put(DbSchema.UserTable.Cols.EMAIL, mEmailEdit.getText().toString());
                            values.put(DbSchema.UserTable.Cols.PASSWORD, generatedPassword);

                            db.insert(DbSchema.UserTable.NAME, null, values);

                            Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            }
        });


    }

    public boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean userRegister(String email){

        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DbSchema.UserTable.NAME + " where " + DbSchema.ChestTable.Cols.EMAIL + " = " + "'" + email + "'", null);
        if(res.getCount() == 0){
            return true;
        } else {
            return false;
        }
    }

}