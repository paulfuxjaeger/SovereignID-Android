package com.danubetech.navigator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danubetech.navigator.agent.XDIAgentInformation;
import com.danubetech.navigator.fragment.AbstractObjectView;
import com.danubetech.navigator.fragment.SchemaObjectView;
import com.danubetech.navigator.util.DIDImages;

import org.SovereignID.common.DIDNames;
import org.SovereignID.common.DIDs;
import org.SovereignID.common.event.SchemaEvent;
import org.SovereignID.common.message.Message;
import org.SovereignID.common.schema.ChangeAgent;
import org.SovereignID.common.schema.Schema;
import org.SovereignID.xdi.SovereignIDXDI;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import xdi2.core.security.ec25519.util.EC25519Base58;
import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.CloudNumber;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RequestActivity extends Activity {

    private Message message;
    private List<Schema> myschemas;

    // UI references.
    private TextView agentTextView;
    private ImageView verifierImageView;
    private TextView verifierTextView;
    private LinearLayout verifierSchemasLayout;
    private ImageButton acceptButton;
    private ImageButton rejectButton;
    private TextView didTextView;
    private TextView didAuthTextView;
    private TextView changeAgentTextView;
    private LinearLayout schemasLayout;
    private ImageView arrowImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        agentTextView = (TextView) findViewById(R.id.agentTextView);
        verifierImageView = (ImageView) findViewById(R.id.verifierImageView);
        verifierTextView = (TextView) findViewById(R.id.verifierTextView);
        verifierSchemasLayout = (LinearLayout) findViewById(R.id.verifierSchemasLayout);
        acceptButton = (ImageButton) findViewById(R.id.acceptButton);
        rejectButton = (ImageButton) findViewById(R.id.rejectButton);
        didTextView = (TextView) findViewById(R.id.didTextView);
        didAuthTextView = (TextView) findViewById(R.id.didAuthTextView);
        changeAgentTextView = (TextView) findViewById(R.id.changeAgentTextView);
        schemasLayout = (LinearLayout) findViewById(R.id.schemasLayout);
        arrowImageView = (ImageView) findViewById(R.id.arrowImageView);

        Bundle b = getIntent().getExtras();
        this.message = Message.fromJson(b.getString("message"));

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);
        this.myschemas = new ArrayList<Schema> ();
        for (Schema myschema : xdiAgentInformation.getSchemas().values()) {

            for (Schema schema : this.message.getSchemas()) {

                if (Schema.getName(schema).equals(Schema.getName(myschema))) this.myschemas.add(myschema);
                if (Schema.getName(schema).equals("changeagent")) this.myschemas.add(Schema.emptyForName("changeagent"));
            }
        }

        initUi();
    }

    public void initUi() {

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);

        this.didTextView.setText(xdiAgentInformation.getDid().toString());

        this.verifierImageView.setImageResource(DIDImages.imageResource(this.message.getFrom()));
        this.verifierTextView.setText(this.message.getFrom());

        this.verifierSchemasLayout.removeAllViews();
        if (this.message.getSenderschemas() != null) for (Schema schema : this.message.getSenderschemas()) {

            AbstractObjectView objectView = null;
            objectView = SchemaObjectView.newInstance(getApplicationContext(), schema, null);
            Log.w(MainActivity.class.getCanonicalName(), "adding view: " + objectView.getClass().getCanonicalName());
            verifierSchemasLayout.addView(objectView);
        }
        else ((View) verifierSchemasLayout.getParent()).setVisibility(GONE);

        this.schemasLayout.removeAllViews();
        if (this.myschemas != null) for (Schema schema : this.myschemas) {

            AbstractObjectView objectView = null;
            objectView = SchemaObjectView.newInstance(getApplicationContext(), schema, null);
            Log.w(MainActivity.class.getCanonicalName(), "adding view: " + objectView.getClass().getCanonicalName());
            schemasLayout.addView(objectView);
        }
        else ((View) schemasLayout.getParent()).setVisibility(GONE);

        // response case
        if (this.message.getSenderschemas() != null &&
                this.message.getSenderschemas().size() > 0 &&
                this.message.getSenderschemas().get(0).getId() != null &&
                (this.myschemas == null || this.myschemas.size() < 1)) {

            agentTextView.setText("Identitätsdaten");
            arrowImageView.setVisibility(GONE);
            didTextView.setVisibility(GONE);
            rejectButton.setVisibility(GONE);
        }

        // peer case
        if (this.message.getFrom().charAt(0) == '=' && this.message.getTo().charAt(0) == '=') {

            didAuthTextView.setVisibility(VISIBLE);
            didAuthTextView.setText("(Vermittelt via " + DIDNames.name(DIDs.DID_SOLLKAUFEN) + ")");
        }

        // did auth case
        if ((this.message.getSenderschemas() == null || this.message.getSenderschemas().size() < 1) &&
                (this.message.getSchemas() == null || this.message.getSchemas().size() < 1)) {

            didAuthTextView.setVisibility(VISIBLE);
        }

        // change agent case
        if (this.message.getSchemas() != null &&
                this.message.getSchemas().size() > 0 &&
                this.message.getSchemas().get(0) instanceof ChangeAgent) {

            verifierSchemasLayout.removeAllViews();
            schemasLayout.removeAllViews();
            didAuthTextView.setVisibility(GONE);
            agentTextView.setText("Agentwechsel");
            changeAgentTextView.setVisibility(VISIBLE);
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptButtonOnClick(v);
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectButtonOnClick(v);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void acceptButtonOnClick(final View view) {

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);

        if (this.message.getSenderschemas() != null &&
                this.message.getSenderschemas().size() > 0 &&
                this.message.getSenderschemas().get(0).getId() != null &&
                (this.myschemas == null || this.myschemas.size() < 1)) {

            finish();
            return;
        }

        // execute accept task.

        acceptTask = new AcceptTask();
        acceptTask.execute((Void) null);
    }

    public void rejectButtonOnClick(final View view) {

        finish();
    }

    /*
     * Accept
     */

    private AcceptTask acceptTask = null;

    public class AcceptTask extends AsyncTask<Void, Void, Object> {

        AcceptTask() {
        }

        @Override
        protected Object doInBackground(Void... params) {

            try {

                boolean changeAgent = changeAgentTextView.getVisibility() == VISIBLE;
                boolean peer = RequestActivity.this.message.getTo().charAt(0) == '=' && RequestActivity.this.message.getFrom().charAt(0) == '=';
                URL verifierUrl = null;

                if (changeAgent) {

                    XDIAgentInformation.agency = DIDs.DID_NTB;
                    XDIAgentInformation.trustanchor = DIDs.DID_NTB;
                    XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(RequestActivity.this);

                    URI oldXdiEndpointUri = xdiAgentInformation.getXdiEndpointUri();

                    URI xdiEndpointUri = xdiAgentInformation.getXdiEndpointUri();
                    xdiEndpointUri = URI.create(xdiEndpointUri.toString().replace("raiffeisen", "ntb").replace("post", "ntb"));
                    xdiAgentInformation.setXdiEndpointUri(xdiEndpointUri);

                    URI trustAnchorUri = xdiAgentInformation.getTrustanchorUri();
                    trustAnchorUri = URI.create(trustAnchorUri.toString().replace("raiffeisen", "ntb").replace("post", "ntb"));
                    xdiAgentInformation.setTrustanchorUri(trustAnchorUri);

                    xdiAgentInformation.save(RequestActivity.this);

                    // execute change agent task.

                    changeAgentTask = new ChangeAgentTask(xdiAgentInformation.getTrustanchorUri(), xdiAgentInformation.getDid(), xdiAgentInformation.getSeed(), xdiAgentInformation.getXdiEndpointUri(), xdiAgentInformation.getDidPublicKey(), oldXdiEndpointUri);
                    changeAgentTask.execute((Void) null);
                } else if (peer) {

                    Message message2 = new Message();
                    message2.setType("request");
                    message2.setFrom(message.getTo());
                    message2.setTo(message.getFrom());
                    for (Schema senderschema : message.getSenderschemas()) if (senderschema.getId() == null) message2.getSchemas().add(senderschema);
                    for (Schema myschema : myschemas) message2.getSenderschemas().add(myschema);

                    SovereignIDXDI.route(message2);
                } else {

                    if (message.getFrom().contains(DIDs.DID_NTB)) verifierUrl = new URL("https://ntb.SovereignID.danubetech.com/callback");
                    if (message.getFrom().contains(DIDs.DID_RAIFFEISEN)) verifierUrl = new URL("https://raiffeisen.SovereignID.danubetech.com/callback");
                    if (message.getFrom().contains(DIDs.DID_UNIQA)) verifierUrl = new URL("https://uniqa.SovereignID.danubetech.com/callback");
                    if (message.getFrom().contains(DIDs.DID_POST)) verifierUrl = new URL("https://post.SovereignID.danubetech.com/callback");
                    if (message.getFrom().contains(DIDs.DID_SOLLKAUFEN)) verifierUrl = new URL("https://sollkaufen.SovereignID.danubetech.com/callback");
                    if (verifierUrl == null) throw new NullPointerException();

                    Log.w("verifierUrl", "" + verifierUrl);

                    HttpURLConnection conn = (HttpURLConnection) verifierUrl.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                    conn.setDoInput(true);

                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                    writer.println(message.getTo());
                    for (Schema schema : RequestActivity.this.myschemas) {
                        writer.println(schema.toJson());
                    }
                    writer.flush();

                    if (conn.getResponseCode() != 200) throw new RuntimeException("Error " + conn.getResponseCode() + ": " + conn.getResponseMessage());

                    XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(RequestActivity.this);

                    String schemaString = "";
                    for (Schema schema : RequestActivity.this.myschemas) {
                        schemaString += "#" + Schema.getName(schema) + " ";
                    }
                    xdiAgentInformation.addSchemaEvent(new SchemaEvent(SchemaEvent.EVENT_REQUEST, message.getFrom(), schemaString));
                    xdiAgentInformation.save(RequestActivity.this);
               }

                return null;
            } catch (Exception ex) {

                return ex;
            }
        }

        @Override
        protected void onPostExecute(final Object ret) {

            // clean up task.

            acceptTask = null;

            // task error?

            if (ret instanceof Exception) {

                Exception ex = (Exception) ret;
                Log.e(MainActivity.class.getCanonicalName(), "onPostExecute: Exception during accept task: " + ex.getMessage(), ex);

                alert("Exception: " + ex.getMessage());
                return;
            }

            finish();
        }

        @Override
        protected void onCancelled() {

            acceptTask = null;
        }
    }

    /*
     * ChangeAgentTask
     */

    private ChangeAgentTask changeAgentTask = null;

    public class ChangeAgentTask extends AsyncTask<Void, Void, Object> {

        private final URI trustAnchorUri;
        private final CloudNumber did;
        private final String seed;
        private final URI endpoint;
        private final byte[] verkey;
        private final URI oldEndpoint;

        ChangeAgentTask(URI trustAnchorUri, CloudNumber did, String seed, URI endpoint, byte[] verkey, URI oldEndpoint) {
            this.trustAnchorUri = trustAnchorUri;
            this.did = did;
            this.seed = seed;
            this.endpoint = endpoint;
            this.verkey = verkey;
            this.oldEndpoint = oldEndpoint;
        }

        @Override
        protected Object doInBackground(Void... params) {

            try {

                HttpURLConnection connTrustanchor = (HttpURLConnection) new URL(trustAnchorUri.toString() + "?" +
                        "did=" + URLEncoder.encode(did.toString(), "UTF-8") + "&" +
                        "endpoint=" + URLEncoder.encode(endpoint.toString(), "UTF-8") + "&" +
                        "oldendpoint=" + URLEncoder.encode(oldEndpoint.toString(), "UTF-8") + "&" +
                        "seed=" + URLEncoder.encode(seed, "UTF-8") + "&" +
                        "verkey=" + URLEncoder.encode(EC25519Base58.encode(verkey), "UTF-8")).openConnection();
                connTrustanchor.setRequestMethod("POST");
                connTrustanchor.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                connTrustanchor.setDoInput(true);

                connTrustanchor.getOutputStream().flush();

                if (connTrustanchor.getResponseCode() != 200) throw new RuntimeException("Error " + connTrustanchor.getResponseCode() + ": " + connTrustanchor.getResponseMessage());

                return Void.TYPE;
            } catch (Exception ex) {

                return ex;
            }
        }

        @Override
        protected void onPostExecute(final Object object) {

            // clean up task.

            changeAgentTask = null;

            // load error?

            if (object == null || object instanceof Exception) {

                Exception ex = (Exception) object;
                Log.e(ConnectActivity.class.getCanonicalName(), "onPostExecute: Exception during connect task: " + ex.getMessage(), ex);

                return;
            }

        }

        @Override
        protected void onCancelled() {

            changeAgentTask = null;
        }
    }

    /*
     * Util
     */

    public void alert(String message) {

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
