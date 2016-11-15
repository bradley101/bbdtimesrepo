package com.tbt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by bradley on 26-08-2016.
 */
public class EventActivity extends AppCompatActivity {
    TextView eventName, eventDetails;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_layout);
        eventName = (TextView) findViewById(R.id.event_name_text_view);
        eventDetails = (TextView) findViewById(R.id.event_details_text_view);
        Bundle extras = getIntent().getExtras();
        eventName.setText(extras.getString("eventname"));
        eventDetails.setText(extras.getString("eventdetails"));

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
