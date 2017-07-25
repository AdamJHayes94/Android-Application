package dissertation.adam.nfitnessc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.w3c.dom.UserDataHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmailInput;
    private String mEmail;
    private EditText mPasswordInput;
    private String mPassword;
    private ImageButton mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailInput = (EditText) findViewById(R.id.email_input_login);

        mPasswordInput = (EditText) findViewById(R.id.password_input_login);

        mLogin = (ImageButton) findViewById(R.id.login_button);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mEmailInput.getText().toString();
                mPassword = mPasswordInput.getText().toString();
                boolean mCorrectEmail = checkEmail(mEmail);
                boolean mCorrectPassword = checkPassword(mPassword);

                if (mEmailInput.getText().toString().matches("") | mPasswordInput.getText().toString().matches("")){
                    Toast.makeText(LoginActivity.this, "Fill in all boxes", Toast.LENGTH_SHORT).show();

                } else {
                    if(isEmailValid(mEmailInput.getText().toString()) == false){
                        Toast.makeText(LoginActivity.this, "Enter correct email", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mCorrectEmail == true && mCorrectPassword == true) {
                            ((MyApplication) LoginActivity.this.getApplication()).setEmailLoggedIn(mEmailInput.getText().toString());
                            ((MyApplication) LoginActivity.this.getApplication()).setLoggedIn(true);

                            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }
    public boolean checkEmail(String email) {

        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String Query = "Select * from " + DbSchema.UserTable.NAME + " where " + DbSchema.UserTable.Cols.EMAIL + " = " + "'" + email + "'";
        Cursor cursor = db.rawQuery(Query, null);

        if(cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }
    public boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean checkPassword(String password) {

        UserBaseHelper mDbHelper = new UserBaseHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String passwordToHash = password;
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());

            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        String Query = "Select * from " + DbSchema.UserTable.NAME + " where " + DbSchema.UserTable.Cols.PASSWORD + " = " + "'" + generatedPassword + "'";
        Cursor cursor = db.rawQuery(Query, null);

        if(cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }

}