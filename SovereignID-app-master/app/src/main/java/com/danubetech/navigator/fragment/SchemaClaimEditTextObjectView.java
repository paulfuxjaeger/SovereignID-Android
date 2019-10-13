package com.danubetech.navigator.fragment;

import android.content.Context;

import com.danubetech.navigator.R;

import java.util.Map;

import xdi2.core.features.nodetypes.XdiAttribute;

public class SchemaClaimEditTextObjectView extends AbstractSchemaClaimObjectView {

    public SchemaClaimEditTextObjectView(Context context, Map.Entry<String, String> object, SchemaOnEditorActionListener schemaOnEditorActionListener) {

        super(context, object, R.layout.view_schema_edittext, schemaOnEditorActionListener);
    }

    public static SchemaClaimEditTextObjectView newInstance(Context context, Map.Entry<String, String> object, SchemaOnEditorActionListener schemaOnEditorActionListener) {

        SchemaClaimEditTextObjectView fragment = new SchemaClaimEditTextObjectView(context, object, schemaOnEditorActionListener);

        return fragment;
    }

    protected void initializeViews(Context context) {

        super.initializeViews(context);
    }
}
