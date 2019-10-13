package com.danubetech.navigator.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.danubetech.navigator.R;

import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.util.XDIAddressUtil;

public abstract class AbstractProfileAttributeObjectView extends AbstractObjectView<XdiAttribute> {

    private ProfileOnEditorActionListener profileOnEditorActionListener;

    // UI references.
    protected TextView attributeTextView;
    protected TextView literalTextView;
    protected EditText literalEditText;

    public AbstractProfileAttributeObjectView(Context context, XdiAttribute object, int resource, ProfileOnEditorActionListener profileOnEditorActionListener) {

        super(context, object, resource);

        this.profileOnEditorActionListener = profileOnEditorActionListener;
    }

    protected void initializeViews(Context context) {

        super.initializeViews(context);

        attributeTextView = (TextView) findViewById(R.id.attributeTextView);
        literalTextView = (TextView) findViewById(R.id.literalTextView);
        literalEditText = (EditText) findViewById(R.id.literalEditText);

        initUi();
    }

    public void initUi() {

        attributeTextView.setText(this.constructAttributeTextViewText());
        if (literalTextView != null) literalTextView.setText(this.constructLiteralTextViewText());
        if (literalEditText != null) literalEditText.setText(this.constructLiteralEditTextText());

        flash(false);

        if (literalEditText != null) literalEditText.setOnEditorActionListener((new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (profileOnEditorActionListener != null)
                    return profileOnEditorActionListener.profileOnEditorAction(v, actionId, event);
                else
                    return false;
            }
        }));
    }

    public void flash(boolean flash) {

        if (flash) {

            this.setBackgroundColor(getResources().getColor(R.color.profile_3));
        } else {

            this.setBackgroundColor(getResources().getColor(R.color.profile_4));
        }
    }

    public String getAttributeText() {

        return attributeTextView.getText().toString();
    }

    public String getLiteralText() {

        return literalEditText != null ? literalEditText.getText().toString() : literalTextView.getText().toString();
    }

    protected String constructAttributeTextViewText() {

        return XDIAddressUtil.localXDIAddress(this.getObject().getXDIAddress(), -1).toString();
    }

    protected String constructLiteralTextViewText() {

        return this.getObject().getLiteralDataString();
    }

    protected String constructLiteralEditTextText() {

        return this.getObject().getLiteralDataString();
    }

    public interface ProfileOnEditorActionListener {

        boolean profileOnEditorAction(TextView v, int actionId, KeyEvent event);
    }
}
