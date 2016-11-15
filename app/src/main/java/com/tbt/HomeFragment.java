package com.tbt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bradley on 20-08-2016.
 */

public class HomeFragment extends Fragment {
    FloatingActionButton facebookFAB, radioFAB;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment_layout, container, false);
        facebookFAB = (FloatingActionButton) v.findViewById(R.id.fab_facebook);
        radioFAB = (FloatingActionButton) v.findViewById(R.id.fab_radio);
        facebookFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openURLInTBTWebView("https://m.facebook.com/TheBBDTimes");
            }
        });
        radioFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RadioActivity.class));
            }
        });
        return v;
    }

    void openURLInTBTWebView(String destinationURL) {
        Intent webIntent = new Intent(getContext(), TBTWebView.class);
        webIntent.putExtra("url", destinationURL);
        startActivity(webIntent);
    }
}
