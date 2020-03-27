package bll.buu.medicinalwatcher;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MedApplication extends Application {

    public void setUsername(String username) {
        this.username = username;
    }

    public String username;
    private SharedPreferences spf;
    private SharedPreferences.Editor editor;
    @Override
    public void onCreate() {
        super.onCreate();
        spf = PreferenceManager.getDefaultSharedPreferences(this);
        editor = spf.edit();
    }

    public String getUsername() {
        return username;
    }

    public SharedPreferences getSpf() {
        return spf;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }


}
