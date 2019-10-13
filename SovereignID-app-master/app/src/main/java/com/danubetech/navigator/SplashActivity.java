package com.danubetech.navigator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.danubetech.navigator.agent.XDIAgentInformation;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // listener (within Main Activity's onStart)
        Branch.getInstance().initSession(new Branch.BranchReferralInitListener() {

            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", referringParams.toString());
                    try {
                        String trustanchor = referringParams.getString("trustanchor");
                        String agency = referringParams.getString("agency");
                        Log.i("BRANCH SDK", "trustanchor: " + trustanchor);
                        Log.i("BRANCH SDK", "agency: " + agency);
                        XDIAgentInformation.trustanchor = trustanchor;
                        XDIAgentInformation.agency = agency;
                    } catch (JSONException ex) {
                        Log.e("BRANCH SDK", ex.getMessage(), ex);
                    }
                } else {
                    Log.e("BRANCH SDK", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);

        // either show connect screen or main screen.

        new Thread(new Runnable() {

            @Override
            public void run() {

                // wait a moment.

                try {

                    Thread.sleep(3000);
                } catch (InterruptedException e) { }

                // start activity

                XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(SplashActivity.this);

                if (xdiAgentInformation == null) {

                    Intent intent = new Intent(SplashActivity.this, ConnectActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }
}
