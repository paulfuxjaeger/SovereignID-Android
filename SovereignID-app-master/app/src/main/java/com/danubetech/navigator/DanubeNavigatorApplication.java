package com.danubetech.navigator;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import io.branch.referral.Branch;

/**
 * Created by markus on 7/5/16.
 */
public class DanubeNavigatorApplication extends MultiDexApplication {

    @Override
    public void onCreate() {

        super.onCreate();

        // Branch logging for debugging
        Branch.enableLogging();

        // Branch object initialization
        Branch.getAutoInstance(this);

    }

    @Override
    public void onTerminate() {

        super.onTerminate();
    }
}
