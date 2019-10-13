package com.danubetech.navigator.fragment;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.danubetech.navigator.MainActivity;
import com.danubetech.navigator.R;

import org.SovereignID.common.event.SchemaEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SchemaEventObjectView extends AbstractObjectView<SchemaEvent> {

    private MainActivity mainActivity;

    // UI references.
    private ImageView imageView;
    private TextView timeTextView;
    private TextView schemaTextView;

    public SchemaEventObjectView(Context context, SchemaEvent object, MainActivity mainActivity) {

        super(context, object, R.layout.view_schemaevent);

        this.mainActivity = mainActivity;
    }

    public static SchemaEventObjectView newInstance(Context context, SchemaEvent object, MainActivity mainActivity) {

        SchemaEventObjectView fragment = new SchemaEventObjectView(context, object, mainActivity);

        return fragment;
    }

    protected void initializeViews(Context context) {

        super.initializeViews(context);

        imageView = (ImageView) findViewById(R.id.imageView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        schemaTextView = (TextView) findViewById(R.id.schemaTextView);

        initUi();
    }

    private static final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    public void initUi() {

        if (SchemaEvent.EVENT_OFFER.equals(this.getObject().getEvent()))
            imageView.setImageResource(R.drawable.arrow_down_small);
        else if (SchemaEvent.EVENT_REQUEST.equals(this.getObject().getEvent()))
            imageView.setImageResource(R.drawable.arrow_up_small);
        else
            imageView.setVisibility(INVISIBLE);

        timeTextView.setText(SimpleDateFormat.getInstance().format(new Date(this.getObject().getTime())));

        if (this.getObject().getSchema() != null && ! this.getObject().getSchema().trim().isEmpty())
            schemaTextView.setText(this.getObject().getSchema());
        else
            schemaTextView.setText("(login)");
    }
}
