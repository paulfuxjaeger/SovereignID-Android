package com.danubetech.navigator.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public abstract class AbstractObjectView<T> extends LinearLayout {

    private Context context;
    private T object;
    private int resource;

    public AbstractObjectView(Context context, T object, int resource) {

        super(context);

        this.context = context;
        this.object = object;
        this.resource = resource;
    }

    protected void onAttachedToWindow() {

        super.onAttachedToWindow();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(resource, this);

        this.initializeViews(context);
    }

    protected void initializeViews(Context context) {

    }

    public T getObject() {

        return this.object;
    }
}
