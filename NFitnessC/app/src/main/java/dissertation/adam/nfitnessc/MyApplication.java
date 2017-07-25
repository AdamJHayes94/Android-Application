package dissertation.adam.nfitnessc;

import android.app.Application;

/**
 * Created by Adam on 08/03/2016.
 */
public class MyApplication extends Application {
    private String mEmailLoggedIn;
    private boolean mLoggedIn = false;

    public String getEmailLoggedIn() {
        return mEmailLoggedIn;
    }

    public void setEmailLoggedIn(String email) {
        this.mEmailLoggedIn = email;
    }

    public boolean getLoggedIn() {
        return mLoggedIn;
    }

    public void setLoggedIn(boolean mLogged) {
        this.mLoggedIn = mLogged;
    }
}
