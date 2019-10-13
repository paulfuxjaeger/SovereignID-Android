package com.danubetech.navigator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.danubetech.navigator.agent.XDIAgentInformation;
import com.danubetech.navigator.util.DIDImages;

import org.SovereignID.common.DIDs;
import org.SovereignID.common.event.SchemaEvent;
import org.SovereignID.common.schema.Schema;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class ConfigurationActivity extends Activity {

    // UI references.
    private TextView didTextView;
    private TextView seedTextView;
    private TextView info1TextView;
    private TextView info2TextView;
    private ImageView agencyImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        didTextView = (TextView) findViewById(R.id.didTextView);
        seedTextView = (TextView) findViewById(R.id.seedTextView);
        info1TextView = (TextView) findViewById(R.id.info1TextView);
        info2TextView = (TextView) findViewById(R.id.info2TextView);
        agencyImageView = (ImageView) findViewById(R.id.agencyImageView);

        initUi();
    }

    public void initUi() {

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);

        String agency = null;
        if (xdiAgentInformation.getXdiEndpointUri().toString().contains("raiffeisen")) agency = DIDs.DID_RAIFFEISEN;
        if (xdiAgentInformation.getXdiEndpointUri().toString().contains("post")) agency = DIDs.DID_POST;
        if (xdiAgentInformation.getXdiEndpointUri().toString().contains("ntb")) agency = DIDs.DID_NTB;

        didTextView.setText(xdiAgentInformation.getDid().toString());
        seedTextView.setText(xdiAgentInformation.getSeed());
        info1TextView.setText(xdiAgentInformation.getXdiEndpointUri().toString());
        info2TextView.setText(xdiAgentInformation.getAppSessionCid().toString());
        info1TextView.setMovementMethod(new ScrollingMovementMethod());
        info2TextView.setMovementMethod(new ScrollingMovementMethod());
        agencyImageView.setImageResource(DIDImages.imageResource(agency));
        info2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyButtonOnClick(v);
            }
        });
    }

    public void verifyButtonOnClick(final View view) {

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);

        // execute verify task.

        verifyTask = new VerifyTask(xdiAgentInformation);
        verifyTask.executeOnExecutor(THREAD_POOL_EXECUTOR, (Void) null);
    }

    /*
     * VerifyTask
     */

    private VerifyTask verifyTask = null;

    public class VerifyTask extends AsyncTask<Void, Void, Object> {

        private final XDIAgentInformation xdiAgentInformation;

        VerifyTask(XDIAgentInformation xdiAgentInformation) {
            this.xdiAgentInformation = xdiAgentInformation;
        }

        @Override
        protected Object doInBackground(Void... params) {

            try {

                Thread.sleep(3000);

                HttpURLConnection conn = (HttpURLConnection) new URL("https://ntb.SovereignID.danubetech.com/verify?" + URLEncoder.encode(xdiAgentInformation.getDid().toString(), "UTF-8")).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                conn.setDoInput(true);

                if (conn.getResponseCode() != 200) throw new RuntimeException("Error " + conn.getResponseCode() + ": " + conn.getResponseMessage());

                return Void.TYPE;

            } catch (Exception ex) {

                return ex;
            }
        }

        @Override
        protected void onPostExecute(final Object ret) {

            // clean up task.

            verifyTask = null;

            // task error?

            if (ret instanceof Exception) {

                Exception ex = (Exception) ret;
                Log.e(ConfigurationActivity.class.getCanonicalName(), "onPostExecute: Exception during verify task: " + ex.getMessage(), ex);

                alert("Exception: " + ex.getMessage());
                return;
            }
        }

        @Override
        protected void onCancelled() {

            verifyTask = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Util
     */

    public void alert(String message) {

        Log.i("ALERT", message);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
