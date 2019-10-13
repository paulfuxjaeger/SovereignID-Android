package com.danubetech.navigator.fragment;

import android.content.Context;

import com.danubetech.navigator.R;

import java.util.Map;

import xdi2.core.features.nodetypes.XdiAttribute;

public class SchemaClaimTextViewObjectView extends AbstractSchemaClaimObjectView {

    public SchemaClaimTextViewObjectView(Context context, Map.Entry<String, String> object, SchemaOnEditorActionListener schemaOnEditorActionListener) {

        super(context, object, R.layout.view_schema_textview, schemaOnEditorActionListener);
    }

    public static SchemaClaimTextViewObjectView newInstance(Context context, Map.Entry<String, String> object, SchemaOnEditorActionListener schemaOnEditorActionListener) {

        SchemaClaimTextViewObjectView fragment = new SchemaClaimTextViewObjectView(context, object, schemaOnEditorActionListener);

        return fragment;
    }

    protected void initializeViews(Context context) {

        super.initializeViews(context);
    }
}
