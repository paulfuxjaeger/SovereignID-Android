package com.danubetech.navigator.fragment;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danubetech.navigator.MainActivity;
import com.danubetech.navigator.R;
import com.danubetech.navigator.agent.XDIAgentInformation;
import com.danubetech.navigator.util.DIDImages;

import org.apache.log4j.chainsaw.Main;
import org.SovereignID.common.schema.Adresse;
import org.SovereignID.common.schema.Schema;

import java.util.Map;

import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.SetOperation;

public class SchemaObjectView extends AbstractObjectView<Schema> implements AbstractSchemaClaimObjectView.SchemaOnEditorActionListener {

    private MainActivity mainActivity;

    // UI references.
    private TextView schemaTextView;
    private ImageView verifierImageView;
    private ImageView delImageView;
    private LinearLayout verifyLayout;
    private ImageView verifyImageView;
    private Button verifyButton;
    private LinearLayout schemaLayout;

    public SchemaObjectView(Context context, Schema object, MainActivity mainActivity) {

        super(context, object, R.layout.view_schema);

        this.mainActivity = mainActivity;
    }

    public static SchemaObjectView newInstance(Context context, Schema object, MainActivity mainActivity) {

        SchemaObjectView fragment = new SchemaObjectView(context, object, mainActivity);

        return fragment;
    }

    protected void initializeViews(Context context) {

        super.initializeViews(context);

        schemaTextView = (TextView) findViewById(R.id.schemaTextView);
        verifierImageView = (ImageView) findViewById(R.id.verifierImageView);
        delImageView = (ImageView) findViewById(R.id.delImageView);
        verifyLayout = (LinearLayout) findViewById(R.id.verifylayout);
        verifyImageView = (ImageView) findViewById(R.id.verifyImageView);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        schemaLayout = (LinearLayout) findViewById(R.id.schemaLayout);

        schemaTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                schemaTextViewOnClick();
            }
        });
        delImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                delImageViewOnClick();
            }
        });
        verifyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyButtonOnClick();
            }
        });

        initUi();
    }

    public void initUi() {

        schemaTextView.setText(this.constructSchemaTextViewText());

        for (Map.Entry<String, String> claim : this.getObject().getClaims().entrySet()) {

            AbstractObjectView schemaClaimObjectView = null;

            if (this.mainActivity != null) {

                delImageView.setVisibility(View.VISIBLE);
                schemaClaimObjectView = SchemaClaimEditTextObjectView.newInstance(SchemaObjectView.this.getContext(), claim, SchemaObjectView.this);
            } else {

                delImageView.setVisibility(View.GONE);
                schemaClaimObjectView = SchemaClaimTextViewObjectView.newInstance(SchemaObjectView.this.getContext(), claim, SchemaObjectView.this);
            }

            schemaLayout.addView(schemaClaimObjectView);
        }

        if (this.getObject().getIssuer() != null) {

            verifierImageView.setImageResource(DIDImages.imageResource(this.getObject().getIssuer()));
        }

        verifyLayout.setVisibility(View.GONE);
        schemaLayout.setVisibility(View.GONE);
    }

    protected String constructSchemaTextViewText() {

        return "#" + Schema.getName(this.getObject());
    }

    public void schemaTextViewOnClick() {

        if (schemaLayout.getVisibility() == View.GONE) {

            schemaLayout.setVisibility(View.VISIBLE);

            if (this.mainActivity != null && this.getObject().getIssuer() == null && this.getObject() instanceof Adresse) {

                verifyLayout.setVisibility(VISIBLE);
            } else {

                verifyLayout.setVisibility(GONE);
            }
        } else {

            schemaLayout.setVisibility(View.GONE);
            verifyLayout.setVisibility(GONE);
        }
    }

    public void delImageViewOnClick() {

        if (mainActivity != null) {

            mainActivity.delSchema(this.getObject());
        }
    }

    public void verifyButtonOnClick() {

        if (mainActivity != null) {

            mainActivity.verifySchema(this.getObject());
        }
    }

    /*
     * AbstractSchemaClaimObjectView
     */

    public boolean schemaOnEditorAction(TextView v, int actionId, KeyEvent event) {

        Schema schema = this.getObject();
        AbstractSchemaClaimObjectView v2 = (AbstractSchemaClaimObjectView) v.getParent().getParent();
        schema.setClaim(v2.getObject().getKey(), v.getText().toString());
        schema.setIssuer(null);

        XDIAgentInformation xdiAgentInformation = XDIAgentInformation.load(this.getContext());
        xdiAgentInformation.getSchemas().put(schema.getId(), schema);
        xdiAgentInformation.save(this.getContext());

        verifierImageView.setVisibility(INVISIBLE);

        return false;
    }
}
