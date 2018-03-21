package com.example.sourav.emergency2;

import android.content.SharedPreferences;

public class UserSharedPref {
    private SharedPreferences sharedPreferences;
    private final String UserName = "KeyName";
    private final String Lat = "KeyLat" ;
    private final String Lng = "KeyLng" ;
    //  private final String Email = "KeyEmail";
    private final String Uid = "KeyUid";
    private String USER_NAME, USER_EMAIL, USER_UID;
    private Float USER_LAT,USER_LNG;

    public UserSharedPref(SharedPreferences sharedPreference) {
        this.sharedPreferences = sharedPreference;
        USER_NAME = sharedPreferences.getString(UserName, null);
        USER_LAT = sharedPreferences.getFloat(Lat,(float)0.0);
        USER_LNG = sharedPreferences.getFloat(Lng,(float)0.0);
        //  USER_EMAIL = sharedPreferences.getString(Email, null);
        USER_UID = sharedPreferences.getString(Uid, null);
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public Float getUSER_LAT() {
        return USER_LAT;
    }

    public Float getUSER_LNG() {
        return USER_LNG;
    }

    public String getUSER_UID() {
        return USER_UID;
    }
}
