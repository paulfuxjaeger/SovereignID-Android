package com.danubetech.navigator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.danubetech.navigator.agent.XDIAgentInformation;
import com.danubetech.navigator.util.DIDImages;

import org.SovereignID.common.DIDs;
import org.SovereignID.common.schema.Schema;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import io.branch.referral.Branch;
import xdi2.core.security.ec25519.util.EC25519Base58;
import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.CloudNumber;

public class ConnectActivity extends Activity {

    // UI references.
    private Button connectButton;
    private Button continueButton;
    private ProgressBar progressBar;
    private TextView info1TextView;
    private TextView info2TextView;
    private ImageView trustAnchorImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        connectButton = (Button) findViewById(R.id.connectButton);
        continueButton = (Button) findViewById(R.id.continueButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        info1TextView = (TextView) findViewById(R.id.info1TextView);
        info2TextView = (TextView) findViewById(R.id.info2TextView);
        trustAnchorImageView = (ImageView) findViewById(R.id.trustAnchorImageView);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectButtonOnClick();
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueButtonOnClick();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        initUi();
    }

    public void initUi() {

        showProgress(false);

        info1TextView.setVisibility(View.VISIBLE);
        info2TextView.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.GONE);

        Log.i("CONNECT", "trustanchor: " + XDIAgentInformation.trustanchor);
        info1TextView.setText("TRUST ANCHOR");
        info2TextView.setText(XDIAgentInformation.trustanchor);
        if (XDIAgentInformation.trustanchor != null) trustAnchorImageView.setImageResource(DIDImages.imageResource(XDIAgentInformation.trustanchor));
    }

    public void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            progressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {

            progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /*
     * Connect
     */

    private ConnectTask connectTask = null;

    public void connectButtonOnClick() {

        if (connectTask != null) return;

        // hide keyboard.

        View view = this.getCurrentFocus();

        if (view != null) {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        // execute connect task.

        showProgress(true);
        connectTask = new ConnectTask(null);
        connectTask.execute((Void) null);
    }

    public void continueButtonOnClick() {

        // switch to main activity.

        Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class ConnectTask extends AsyncTask<Void, Void, Object> {

        private final CloudName cloudName;

        ConnectTask(CloudName cloudName) {
            this.cloudName = cloudName;
        }

        @Override
        protected Object doInBackground(Void... params) {

            try {

                Thread.sleep(3000);

                String seed = null;
                XDIAgentInformation xdiAgentInformation = XDIAgentInformation.create(ConnectActivity.this, seed);

                HttpURLConnection connTrustanchor = (HttpURLConnection) new URL(xdiAgentInformation.getTrustanchorUri() + "?" +
                        "did=" + URLEncoder.encode(xdiAgentInformation.getDid().toString(), "UTF-8") + "&" +
                        "endpoint=" + URLEncoder.encode(xdiAgentInformation.getXdiEndpointUri().toString(), "UTF-8") + "&" +
                        "seed=" + URLEncoder.encode(xdiAgentInformation.getSeed(), "UTF-8") + "&" +
                        "verkey=" + URLEncoder.encode(EC25519Base58.encode(xdiAgentInformation.getDidPublicKey()), "UTF-8")).openConnection();
                connTrustanchor.setRequestMethod("POST");
                connTrustanchor.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                connTrustanchor.setDoInput(true);

                connTrustanchor.getOutputStream().flush();

                if (connTrustanchor.getResponseCode() != 200) throw new RuntimeException("Error " + connTrustanchor.getResponseCode() + ": " + connTrustanchor.getResponseMessage());

                return xdiAgentInformation;
            } catch (Exception ex) {

                return ex;
            }
        }

        @Override
        protected void onPostExecute(final Object object) {

            // clean up task.

            connectTask = null;
            showProgress(false);

            // load error?

            if (object == null || object instanceof Exception) {

                Exception ex = (Exception) object;
                Log.e(ConnectActivity.class.getCanonicalName(), "onPostExecute: Exception during connect task: " + ex.getMessage(), ex);

                AlertDialog alertDialog = new AlertDialog.Builder(ConnectActivity.this).create();
                alertDialog.setTitle("Connect");
                alertDialog.setMessage(ex.getMessage());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return;
            }

            // read task result.

            final XDIAgentInformation xdiAgentInformation = (XDIAgentInformation) object;
            xdiAgentInformation.save(ConnectActivity.this);

            // display agent information.

            CloudNumber did = xdiAgentInformation.getDid();
            URI xdiEndpointUri = xdiAgentInformation.getXdiEndpointUri();

            info1TextView.setVisibility(View.VISIBLE);
            info1TextView.setText(did.toString());
            info2TextView.setVisibility(View.VISIBLE);
            info2TextView.setText(xdiEndpointUri.getAuthority().toString());
            continueButton.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onCancelled() {

            connectTask = null;
            showProgress(false);
        }
    }
}
