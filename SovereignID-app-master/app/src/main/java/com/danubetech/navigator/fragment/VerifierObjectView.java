package com.danubetech.navigator.fragment;

import android.app.FragmentTransaction;
import android.content.Context;
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

import org.SovereignID.common.DIDNames;
import org.SovereignID.common.event.SchemaEvent;
import org.SovereignID.common.schema.Adresse;
import org.SovereignID.common.schema.Schema;

import java.util.List;
import java.util.Map;

public class VerifierObjectView extends AbstractObjectView<Map.Entry<String, List<SchemaEvent>>> {

    private MainActivity mainActivity;

    // UI references.
    private TextView verifierTextView;
    private ImageView verifierImageView;
    private LinearLayout verifierLayout;

    public VerifierObjectView(Context context, Map.Entry<String, List<SchemaEvent>> object, MainActivity mainActivity) {

        super(context, object, R.layout.view_verifier);

        this.mainActivity = mainActivity;
    }

    public static VerifierObjectView newInstance(Context context, Map.Entry<String, List<SchemaEvent>> object, MainActivity mainActivity) {

        VerifierObjectView fragment = new VerifierObjectView(context, object, mainActivity);

        return fragment;
    }

    protected void initializeViews(Context context) {

        super.initializeViews(context);

        verifierTextView = (TextView) findViewById(R.id.verifierTextView);
        verifierImageView = (ImageView) findViewById(R.id.verifierImageView);
        verifierLayout = (LinearLayout) findViewById(R.id.verifierLayout);

        verifierTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                verifierTextViewOnClick();
            }
        });

        initUi();
    }

    public void initUi() {

        if (this.getObject().getKey() != null && ! this.getObject().getKey().isEmpty()) {

            verifierTextView.setText(this.constructVerifierTextViewText());
        }

        if (this.getObject().getKey() != null && ! this.getObject().getKey().isEmpty()) {

            verifierImageView.setImageResource(DIDImages.imageResource(this.getObject().getKey()));
        }

        verifierLayout.removeAllViews();

        for (SchemaEvent object : this.getObject().getValue()) {

            AbstractObjectView objectView = null;

            objectView = SchemaEventObjectView.newInstance(mainActivity, object, mainActivity);

            Log.w(MainActivity.class.getCanonicalName(), "adding view: " + objectView.getClass().getCanonicalName());

            verifierLayout.addView(objectView);
        }

        verifierLayout.setVisibility(View.GONE);
    }

    protected String constructVerifierTextViewText() {

        return DIDNames.name(this.getObject().getKey());
    }

    public void verifierTextViewOnClick() {

        if (verifierLayout.getVisibility() == View.GONE) {

            verifierLayout.setVisibility(View.VISIBLE);
        } else {

            verifierLayout.setVisibility(View.GONE);
        }
    }
}
