package com.example.chotu.gelfie;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by CHOTU on 11/4/2016.
 */
public class Gelfie extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
