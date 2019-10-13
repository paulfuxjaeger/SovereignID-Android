package com.danubetech.navigator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.danubetech.navigator.agent.XDIAgentInformation;
import com.danubetech.navigator.fragment.AbstractObjectView;
import com.danubetech.navigator.fragment.SchemaObjectView;
import com.danubetech.navigator.util.DIDImages;

import org.SovereignID.common.DIDXDI;
import org.SovereignID.common.DIDs;
import org.SovereignID.common.event.SchemaEvent;
import org.SovereignID.common.message.Message;
import org.SovereignID.common.schema.Adresse;
import org.SovereignID.common.schema.Schema;
import org.SovereignID.xdi.SovereignIDXDI;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class OfferActivity extends Activity {

    private Message message;

    // UI references.
    private ImageView issuerImageView;
    private TextView issuerTextView;
    private ImageButton acceptButton;
    private ImageButton rejectButton;
    private LinearLayout schemasLayout;
    private LinearLayout autoUpdateLinearLayout;
    private ToggleButton autoUpdateRaiffeisenToggleButton;
    private ToggleButton autoUpdateUniqaToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        issuerImageView = (ImageView) findViewById(R.id.issuerImageView);
        issuerTextView = (TextView) findViewById(R.id.issuerTextView);
        acceptButton = (ImageButton) findViewById(R.id.acceptButton);
        rejectButton = (ImageButton) findViewById(R.id.rejectButton);
        schemasLayout = (LinearLayout) findViewById(R.id.schemasLayout);
        autoUpdateLinearLayout = (LinearLayout) findViewById(R.id.autoUpdateLinearLayout);
        autoUpdateRaiffeisenToggleButton = (ToggleButton) findViewById(R.id.autoUpdateRaiffeisenToggleButton);
        autoUpdateUniqaToggleButton = (ToggleButton) findViewById(R.id.autoUpdateUniqaToggleButton);

        Bundle b = getIntent().getExtras();
        this.message = Message.fromJson(b.getString("message"));

        initUi();
    }

    public void initUi() {

        this.issuerImageView.setImageResource(DIDImages.imageResource(this.message.getFrom()));
        this.issuerTextView.setText(this.message.getFrom());

        this.schemasLayout.removeAllViews();

        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();

        for (Schema schema : this.message.getSchemas()) {

            AbstractObjectView objectView = null;

            objectView = SchemaObjectView.newInstance(getApplicationContext(), schema, null);

            Log.w(MainActivity.class.getCanonicalName(), "adding view: " + objectView.getClass().getCanonicalName());

            schemasLayout.addView(objectView);
        }

        if (this.message.getSchemas().size() == 1 && this.message.getSchemas().get(0) instanceof Adresse && this.message.getSchemas().get(0).getIssuer().equals(DIDs.DID_POST)) {

            boolean enableRaiffeisen = false;
            boolean enableUniqa = false;
            XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this);
            for (Schema schema : xdiAgentInformation.getSchemas().values()) {

                if (schema.getIssuer() == null) continue;
                if (schema.getIssuer().contains(DIDs.DID_RAIFFEISEN)) enableRaiffeisen = true;
                if (schema.getIssuer().contains(DIDs.DID_UNIQA)) enableUniqa = true;
            }

            autoUpdateLinearLayout.setVisibility((enableRaiffeisen || enableUniqa) ? View.VISIBLE : View.GONE);
            autoUpdateRaiffeisenToggleButton.setVisibility(enableRaiffeisen ? View.VISIBLE : View.GONE);
            autoUpdateUniqaToggleButton.setVisibility(enableUniqa ? View.VISIBLE : View.GONE);
        } else {

            autoUpdateLinearLayout.setVisibility(View.GONE);
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
        for (Schema schema : this.message.getSchemas()) {
            xdiAgentInformation.acceptSchema(schema);
        }
        String schemaString = "";
        for (Schema schema : this.message.getSchemas()) {
            schemaString += "#" + Schema.getName(schema) + " ";
        }
        xdiAgentInformation.addSchemaEvent(new SchemaEvent(SchemaEvent.EVENT_OFFER, this.message.getFrom(), schemaString));
        xdiAgentInformation.save(this);

        notifyTask = new NotifyTask(xdiAgentInformation, this.message.getSchemas(), autoUpdateRaiffeisenToggleButton.isChecked(), autoUpdateUniqaToggleButton.isChecked());
        notifyTask.executeOnExecutor(THREAD_POOL_EXECUTOR, (Void) null);

        finish();
    }

    public void rejectButtonOnClick(final View view) {

        finish();
    }

    /*
     * Notify
     */

    private NotifyTask notifyTask = null;

    public class NotifyTask extends AsyncTask<Void, Void, Object> {

        private final XDIAgentInformation xdiAgentInformation;
        private final List<Schema> schemas;
        private final boolean notifyRaiffeisen;
        private final boolean notifyUniqa;

        NotifyTask(XDIAgentInformation xdiAgentInformation, List<Schema> schemas, boolean notifyRaiffeisen, boolean notifyUniqa) {
            this.xdiAgentInformation = xdiAgentInformation;
            this.schemas = schemas;
            this.notifyRaiffeisen = notifyRaiffeisen;
            this.notifyUniqa = notifyUniqa;
        }

        @Override
        protected Object doInBackground(Void... params) {

            for (Schema schema : this.schemas) {

                try {

                    SovereignIDXDI.writeSchema(xdiAgentInformation.getDid().toString(), xdiAgentInformation.getDid().toString(), schema, xdiAgentInformation.getXdiEndpointUri());
                } catch (Exception ex) {

                    Log.e("XDI", ex.getMessage());
                }
            }

            if (this.notifyRaiffeisen) {

                try {

                    Thread.sleep(4000);

                    HttpURLConnection conn = (HttpURLConnection) new URL("https://raiffeisen.SovereignID.danubetech.com/notify?" + xdiAgentInformation.getDid()).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                    conn.setDoInput(true);

                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                    writer.println(this.schemas.get(0).toJson());
                    writer.flush();

                    if (conn.getResponseCode() != 200) throw new RuntimeException("Error " + conn.getResponseCode() + ": " + conn.getResponseMessage());

                    XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(OfferActivity.this);

                    String schemaString = "";
                    schemaString += "#" + Schema.getName(this.schemas.get(0)) + " ";
                    xdiAgentInformation.addSchemaEvent(new SchemaEvent(SchemaEvent.EVENT_REQUEST, DIDs.DID_RAIFFEISEN, schemaString));
                    xdiAgentInformation.save(OfferActivity.this);
                } catch (Exception ex) {

                    Log.e("raiffeisen", ex.getMessage());
                }
            }

            if (this.notifyUniqa) {

                try {

                    Thread.sleep(4000);

                    HttpURLConnection conn = (HttpURLConnection) new URL("https://uniqa.SovereignID.danubetech.com/notify?" + xdiAgentInformation.getDid()).openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
                    conn.setDoInput(true);

                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                    writer.println(this.schemas.get(0).toJson());
                    writer.flush();

                    XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(OfferActivity.this);

                    String schemaString = "";
                    schemaString += "#" + Schema.getName(this.schemas.get(0)) + " ";
                    xdiAgentInformation.addSchemaEvent(new SchemaEvent(SchemaEvent.EVENT_REQUEST, DIDs.DID_UNIQA, schemaString));
                    xdiAgentInformation.save(OfferActivity.this);

                    if (conn.getResponseCode() != 200) throw new RuntimeException("Error " + conn.getResponseCode() + ": " + conn.getResponseMessage());
                } catch (Exception ex) {

                    Log.e("uniqa", ex.getMessage());
                }
            }

            return Void.TYPE;
        }

        @Override
        protected void onPostExecute(final Object ret) {

            // clean up task.

            notifyTask = null;

            // task error?

            if (ret instanceof Exception) {

                Exception ex = (Exception) ret;
                Log.e(MainActivity.class.getCanonicalName(), "onPostExecute: Exception during notify task: " + ex.getMessage(), ex);

                alert("Exception: " + ex.getMessage());
                return;
            }
        }

        @Override
        protected void onCancelled() {

            notifyTask = null;
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
