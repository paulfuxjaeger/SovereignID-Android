package com.danubetech.navigator.fragment;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.danubetech.navigator.R;

import java.util.Map;

public abstract class AbstractSchemaClaimObjectView extends AbstractObjectView<Map.Entry<String, String>> {

    private SchemaOnEditorActionListener schemaOnEditorActionListener;

    // UI references.
    protected TextView attributeTextView;
    protected TextView literalTextView;
    protected EditText literalEditText;

    public AbstractSchemaClaimObjectView(Context context, Map.Entry<String, String> object, int resource, SchemaOnEditorActionListener schemaOnEditorActionListener) {

        super(context, object, resource);

        this.schemaOnEditorActionListener = schemaOnEditorActionListener;
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

        if (literalEditText != null) {

            literalEditText.setOnEditorActionListener((new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (schemaOnEditorActionListener != null)
                        return schemaOnEditorActionListener.schemaOnEditorAction(v, actionId, event);
                    else
                        return false;
                }
            }));
        }
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

        return this.getObject().getKey();
    }

    protected String constructLiteralTextViewText() {

        return this.getObject().getValue();
    }

    protected String constructLiteralEditTextText() {

        return this.getObject().getValue();
    }

    public interface SchemaOnEditorActionListener {

        boolean schemaOnEditorAction(TextView v, int actionId, KeyEvent event);
    }
}
