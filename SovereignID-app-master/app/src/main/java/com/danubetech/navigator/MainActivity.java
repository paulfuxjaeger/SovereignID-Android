package com.danubetech.navigator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.danubetech.navigator.agent.XDIAgentInformation;
import com.danubetech.navigator.fragment.AbstractObjectView;
import com.danubetech.navigator.fragment.SchemaObjectView;
import com.danubetech.navigator.fragment.VerifierObjectView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.SovereignID.common.DIDs;
import org.SovereignID.common.event.SchemaEvent;
import org.SovereignID.common.message.Message;
import org.SovereignID.common.schema.Basisdaten;
import org.SovereignID.common.schema.Schema;
import org.SovereignID.xdi.SovereignIDXDI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import xdi2.core.syntax.XDIAddress;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static android.view.View.VISIBLE;

public class MainActivity extends Activity {

    public static final XDIAddress XDI_ADD_CARD = XDIAddress.create("$card");

    // UI references.
    private ImageButton schemasButton;
    private ImageButton verifiersButton;
    private ImageButton requestsButton;
    private ImageView configurationButton;
    private ImageView cloudImageView;
    private ProgressBar progressBar;
    private LinearLayout schemasLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schemasButton = (ImageButton) findViewById(R.id.schemasButton);
        verifiersButton = (ImageButton) findViewById(R.id.verifiersButton);
        requestsButton = (ImageButton) findViewById(R.id.requestsButton);
        configurationButton = (ImageView) findViewById(R.id.configurationButton);
        cloudImageView = (ImageView) findViewById(R.id.cloudImageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        schemasLayout = (LinearLayout) findViewById(R.id.schemasLayout);

        initUi();

        // check for messages

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);
        messagesTask = new MessagesTask(xdiAgentInformation);
        messagesTask.executeOnExecutor(THREAD_POOL_EXECUTOR, (Void) null);
    }

    public void initUi() {

        showProgress(false);

        schemasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schemasButtonOnClick(v);
            }
        });
        verifiersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifiersButtonOnClick(v);
            }
        });
        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrButtonOnClick(v);
            }
        });
        configurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configurationButtonOnClick(v);
            }
        });
    }

    public void showProgress(final boolean show) {

        if (show && progressBar.getVisibility() == View.VISIBLE) return;
        if ((! show) && (progressBar.getVisibility() == View.INVISIBLE || progressBar.getVisibility() == View.GONE)) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {

            //showMenuDialog();
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    public void qrButtonOnClick(final View view) {

        // scan QR

        IntentIntegrator i = new IntentIntegrator(this);
        i.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null && scanResult.getContents() != null) {

            XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);

            if (scanResult.getContents().contains("plinth")) {

                // execute change agent task.

                postDidTask = new PostDidTask(xdiAgentInformation, scanResult.getContents());
                postDidTask.execute((Void) null);
                return;
            }

            String qrrequest = scanResult.getContents().contains("|") ? scanResult.getContents().split("\\|")[0] : scanResult.getContents();
            String qrverifierrequest = scanResult.getContents().contains("|") ? scanResult.getContents().split("\\|")[1] : null;

            String[] qrparts = qrrequest.split(" ");
            String qrdid = qrparts[0];
            List<String> qrschemas = new LinkedList<String>(Arrays.asList(qrparts));
            qrschemas.remove(0);

            String[] qrverifierparts = qrverifierrequest == null ? null : qrverifierrequest.split(" ");
            List<String> qrverifierschemas = qrverifierparts == null ? null : new LinkedList<String>(Arrays.asList(qrverifierparts));

            String verifier = qrdid;
            List<Schema> schemas = qrschemas == null ? null : new ArrayList<Schema> ();
            if (qrschemas != null) for (String qrschema : qrschemas) schemas.add(Schema.templateForName(qrschema));
            List<Schema> verifierschemas = qrverifierschemas == null ? null : new ArrayList<Schema> ();
            if (verifierschemas != null) for (String qrverifierschema : qrverifierschemas) verifierschemas.add(Schema.templateForName(qrverifierschema));

            org.SovereignID.common.message.Message message = new org.SovereignID.common.message.Message();
            message.setType("request");
            message.setFrom(verifier);
            message.setTo(xdiAgentInformation.getDid().toString());
            message.setSchemas(schemas);
            message.setSenderschemas(verifierschemas);

            Intent intent2 = new Intent(MainActivity.this, RequestActivity.class);
            Bundle b = new Bundle();
            b.putString("message", message.toJson());
            intent2.putExtras(b);
            startActivity(intent2);
        }
    }

    /*
     * PostDid
     */

    private PostDidTask postDidTask = null;

    public class PostDidTask extends AsyncTask<Void, Void, Object> {

        private final XDIAgentInformation xdiAgentInformation;
        private final String url;

        PostDidTask(XDIAgentInformation xdiAgentInformation, String url) {
            this.xdiAgentInformation = xdiAgentInformation;
            this.url = url;
        }

        @Override
        protected Object doInBackground(Void... params) {

            try {

                String did = xdiAgentInformation.getDid().toString().replace(":did:sov:", ":did:sov:SovereignID:");
                String csrfmiddlewaretoken = this.url.substring(this.url.indexOf("csrfmiddlewaretoken=") + "csrfmiddlewaretoken=".length());
                String cookie = "csrftoken=" + csrfmiddlewaretoken;

                StringBuffer body = new StringBuffer();
                body.append("xdi-did=" + URLEncoder.encode(did, "UTF-8") + "&");
                body.append("csrftoken=" + URLEncoder.encode(csrfmiddlewaretoken, "UTF-8"));
                Log.i("postDid REQUEST", "URL: " + this.url);
                Log.i("postDid REQUEST", "COOKIE: " + cookie);
                Log.i("postDid REQUEST", "BODY: " + body);

                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                conn.setRequestProperty("Referer", this.url);
                conn.setRequestProperty("Cookie", cookie);
                conn.setDoInput(true);

                PrintWriter writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                writer.println(body.toString());
                writer.flush();
                writer.close();

                if (conn.getResponseCode() != 200)
                    throw new RuntimeException("Error " + conn.getResponseCode() + ": " + conn.getResponseMessage());

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = reader.readLine()) != null) {

                    response.append(line);
                }

                Log.i("postDid RESPONSE", conn.getResponseCode() + " - " + response);

                return Void.TYPE;
            } catch (Exception ex) {

                return ex;
            }
        }

        @Override
        protected void onPostExecute(final Object ret) {

            // clean up task.

            postDidTask = null;

            // task error?

            if (ret instanceof Exception) {

                Exception ex = (Exception) ret;
                Log.e(MainActivity.class.getCanonicalName(), "onPostExecute: Exception during postDid task: " + ex.getMessage(), ex);

                alert("Exception: " + ex.getMessage());
                return;
            }

            alert("Success");
        }

        @Override
        protected void onCancelled() {

            postDidTask = null;
        }
    }

    /*
     * MessagesTask
     */

    private MessagesTask messagesTask = null;

    private static class MessageObject { private List<String> messages; private MessageObject(List<String> messages) { this.messages = messages; } }

    public class MessagesTask extends AsyncTask<Void, Void, Object> {

        private final XDIAgentInformation xdiAgentInformation;

        MessagesTask(XDIAgentInformation xdiAgentInformation) {
            this.xdiAgentInformation = xdiAgentInformation;
        }

        @Override
        protected Object doInBackground(Void... params) {

            // sleep

            try {

                Thread.sleep(8000);
            } catch (Exception ex) {

                return ex;
            }

            // load

            List<String> messages = new ArrayList<String>();

            try {

                HttpURLConnection connRaiffeisen = (HttpURLConnection) new URL("https://raiffeisen.SovereignID.danubetech.com/message?" + URLEncoder.encode(xdiAgentInformation.getDid().toString(), "UTF-8")).openConnection();

                if (connRaiffeisen.getResponseCode() == 200) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connRaiffeisen.getInputStream(), "UTF-8"));
                    String line;

                    while ((line = reader.readLine()) != null) messages.add(line);
                    return new MessageObject(messages);
                } else if (connRaiffeisen.getResponseCode() >= 400) {

                    Log.e("message", connRaiffeisen.getResponseMessage());
                }
            } catch (Exception ex) {

                Log.e("message", ex.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Object ret) {

            // clean up task.

            messagesTask = null;
            showProgress(false);

            // task error?

            if (ret instanceof Exception) {

                Exception ex = (Exception) ret;
                Log.e(MainActivity.class.getCanonicalName(), "onPostExecute: Exception during offers task: " + ex.getMessage(), ex);

                alert("Exception: " + ex.getMessage());
            } else {

                // read task result.

                final MessageObject messageObject = (MessageObject) ret;

                // start activity.

                if (messageObject != null) {

                    Log.i("messageobject", "" + messageObject.messages);

                    for (String messageString : messageObject.messages) {

                        org.SovereignID.common.message.Message message = org.SovereignID.common.message.Message.fromJson(messageString);

                        Intent intent;

                        if ("offer".equals(message.getType())) intent = new Intent(MainActivity.this, OfferActivity.class);
                        else if ("request".equals(message.getType())) intent = new Intent(MainActivity.this, RequestActivity.class);
                        else throw new IllegalArgumentException(message.getType());

                        Bundle b = new Bundle();
                        b.putString("message", messageString);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
            }

            // check for messages

            XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(MainActivity.this);
            messagesTask = new MessagesTask(xdiAgentInformation);
            messagesTask.executeOnExecutor(THREAD_POOL_EXECUTOR, (Void) null);
        }

        @Override
        protected void onCancelled() {

            messagesTask = null;
            showProgress(false);
        }
    }

    /*
     * SchemasTask
     */

    private SchemasTask schemasTask = null;

    public void schemasButtonOnClick(final View view) {

        if (schemasTask != null) return;

        // prepare data for schemas task.

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);

        // execute messages task.

        showProgress(true);
        schemasTask = new SchemasTask(xdiAgentInformation);
        schemasTask.executeOnExecutor(THREAD_POOL_EXECUTOR, (Void) null);
    }

    public class SchemasTask extends AsyncTask<Void, Void, Object> {

        private final XDIAgentInformation xdiAgentInformation;

        SchemasTask(XDIAgentInformation xdiAgentInformation) {
            this.xdiAgentInformation = xdiAgentInformation;
        }

        @Override
        protected Object doInBackground(Void... params) {

            try {

                Thread.sleep(1000);

                Collection<Schema> schemas = this.xdiAgentInformation.getSchemas().values();

                return schemas;
            } catch (Exception ex) {

                return ex;
            }
        }

        @Override
        protected void onPostExecute(final Object ret) {

            // clean up task.

            schemasTask = null;
            showProgress(false);

            // task error?

            if (ret instanceof Exception) {

                Exception ex = (Exception) ret;
                Log.e(MainActivity.class.getCanonicalName(), "onPostExecute: Exception during schemas task: " + ex.getMessage(), ex);

                alert("Exception: " + ex.getMessage());
                return;
            }

            // read task result.

            final Collection<Schema> objects = (Collection<Schema>) ret;

            // display fragments.

            MainActivity.this.cloudImageView.setVisibility(View.GONE);
            MainActivity.this.schemasLayout.removeAllViews();

            FragmentTransaction fragmentTransaction = MainActivity.this.getFragmentManager().beginTransaction();

            for (Schema object : objects) {

                AbstractObjectView objectView = null;

                objectView = SchemaObjectView.newInstance(getApplicationContext(), object, MainActivity.this);

                Log.w(MainActivity.class.getCanonicalName(), "adding view: " + objectView.getClass().getCanonicalName());

                schemasLayout.addView(objectView);
            }

            fragmentTransaction.commit();
        }

        @Override
        protected void onCancelled() {

            schemasTask = null;
            showProgress(false);
        }
    }

    /*
     * VerifiersTask
     */

    private VerifiersTask verifiersTask = null;

    public void verifiersButtonOnClick(final View view) {

        if (verifiersTask != null) return;

        // prepare data for verifiers task.

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);

        // execute messages task.

        showProgress(true);
        verifiersTask = new VerifiersTask(xdiAgentInformation);
        verifiersTask.executeOnExecutor(THREAD_POOL_EXECUTOR, (Void) null);
    }

    public class VerifiersTask extends AsyncTask<Void, Void, Object> {

        private final XDIAgentInformation xdiAgentInformation;

        VerifiersTask(XDIAgentInformation xdiAgentInformation) {
            this.xdiAgentInformation = xdiAgentInformation;
        }

        @Override
        protected Object doInBackground(Void... params) {

            try {

                Thread.sleep(1000);

                List<SchemaEvent> schemaEvents = this.xdiAgentInformation.getSchemaEvents();
                Collections.reverse(schemaEvents);

                Map<String, List<SchemaEvent>> schemaEventsMap = new HashMap<String, List<SchemaEvent>>();

                for (SchemaEvent schemaEvent : schemaEvents) {

                    String did = schemaEvent.getDid();
                    if (did == null) continue;

                    List<SchemaEvent> schemaEventsList = schemaEventsMap.get(did);
                    if (schemaEventsList == null) { schemaEventsList = new ArrayList<SchemaEvent>(); schemaEventsMap.put(did, schemaEventsList); }

                    schemaEventsList.add(schemaEvent);
                }

                return schemaEventsMap;
            } catch (Exception ex) {

                return ex;
            }
        }

        @Override
        protected void onPostExecute(final Object ret) {

            // clean up task.

            verifiersTask = null;
            showProgress(false);

            // task error?

            if (ret instanceof Exception) {

                Exception ex = (Exception) ret;
                Log.e(MainActivity.class.getCanonicalName(), "onPostExecute: Exception during verifiers task: " + ex.getMessage(), ex);

                alert("Exception: " + ex.getMessage());
                return;
            }

            // read task result.

            final Map<String, List<SchemaEvent>> objects = (Map<String, List<SchemaEvent>>) ret;

            // display fragments.

            MainActivity.this.cloudImageView.setVisibility(View.GONE);
            MainActivity.this.schemasLayout.removeAllViews();

            FragmentTransaction fragmentTransaction = MainActivity.this.getFragmentManager().beginTransaction();

            for (Map.Entry<String, List<SchemaEvent>> object : objects.entrySet()) {

                AbstractObjectView objectView = null;

                objectView = VerifierObjectView.newInstance(getApplicationContext(), object, MainActivity.this);

                Log.w(MainActivity.class.getCanonicalName(), "adding view: " + objectView.getClass().getCanonicalName());

                schemasLayout.addView(objectView);
            }

            fragmentTransaction.commit();
        }

        @Override
        protected void onCancelled() {

            schemasTask = null;
            showProgress(false);
        }
    }

    /*
     * VerifyTask
     */

    private VerifyTask verifyTask = null;

    public class VerifyTask extends AsyncTask<Void, Void, Object> {

        private final XDIAgentInformation xdiAgentInformation;
        private final List<Schema> schemas;

        VerifyTask(XDIAgentInformation xdiAgentInformation, List<Schema> schemas) {
            this.xdiAgentInformation = xdiAgentInformation;
            this.schemas = schemas;
        }

        @Override
        protected Object doInBackground(Void... params) {

            try {

                Thread.sleep(3000);

                HttpURLConnection conn = (HttpURLConnection) new URL("https://post.SovereignID.danubetech.com/verify?" + URLEncoder.encode(xdiAgentInformation.getDid().toString(), "UTF-8")).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                conn.setDoInput(true);

                PrintWriter writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                for (Schema schema : schemas) writer.println(schema.toJson());
                writer.flush();

                if (conn.getResponseCode() != 200) throw new RuntimeException("Error " + conn.getResponseCode() + ": " + conn.getResponseMessage());

                XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(MainActivity.this);

                String schemaString = "";
                for (Schema schema : schemas) {
                    schemaString += "#" + Schema.getName(schema) + " ";
                }
                xdiAgentInformation.addSchemaEvent(new SchemaEvent(SchemaEvent.EVENT_REQUEST, DIDs.DID_POST, schemaString));
                xdiAgentInformation.save(MainActivity.this);

                return Void.TYPE;

            } catch (Exception ex) {

                return ex;
            }
        }

        @Override
        protected void onPostExecute(final Object ret) {

            // clean up task.

            verifyTask = null;
            showProgress(false);

            // task error?

            if (ret instanceof Exception) {

                Exception ex = (Exception) ret;
                Log.e(MainActivity.class.getCanonicalName(), "onPostExecute: Exception during verify task: " + ex.getMessage(), ex);

                alert("Exception: " + ex.getMessage());
                return;
            }
        }

        @Override
        protected void onCancelled() {

            verifyTask = null;
            showProgress(false);
        }
    }

    /*
     * Configuration
     */

    public void configurationButtonOnClick(final View view) {

        // start configuration activity.

        startActivity(new Intent(MainActivity.this, ConfigurationActivity.class));
    }

    /*
     * SchemaObjectListener
     */

    public void delSchema(Schema schema) {

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);
        xdiAgentInformation.getSchemas().remove(schema.getId());
        xdiAgentInformation.save(this);

        if (schemasTask != null) return;

        // execute messages task.

        showProgress(true);
        schemasTask = new SchemasTask(xdiAgentInformation);
        schemasTask.executeOnExecutor(THREAD_POOL_EXECUTOR, (Void) null);
    }

    public void verifySchema(Schema schema) {

        if (verifyTask != null) return;

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(MainActivity.this);
        Basisdaten basisdaten = xdiAgentInformation.findSchema(Basisdaten.class);

        List<Schema> schemas = new ArrayList<Schema> ();
        if (basisdaten != null) schemas.add(basisdaten);
        schemas.add(schema);

        // execute verify task.

        showProgress(true);
        verifyTask = new VerifyTask(xdiAgentInformation, schemas);
        verifyTask.executeOnExecutor(THREAD_POOL_EXECUTOR, (Void) null);
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
