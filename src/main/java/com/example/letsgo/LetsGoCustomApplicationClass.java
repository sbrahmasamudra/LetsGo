package com.example.letsgo;


import android.app.Application;
import io.branch.referral.Branch;


public class LetsGoCustomApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Branch logging for debugging
        Branch.enableDebugMode();

        // Branch object initialization
        Branch.getAutoInstance(this);

    }

}
