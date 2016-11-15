package com.tbt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by bradley on 08-09-2016.
 */
public class FormsFragment extends Fragment {
    SwipeRefreshLayout formsSwipeRefreshLayout;
    ListView formsListView;
    ArrayList<FormsDetail> arrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.forms_fragment_layout, container, false);
        formsSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.forms_swipe_refresh_layout);
        formsListView = (ListView) v.findViewById(R.id.forms_list_view);
        formsSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE);

        new DownloadFormsData().execute();

        formsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DownloadFormsData().execute();
            }
        });

        return v;
    }

    class FormsListViewAdapter extends ArrayAdapter<String> {
        Context context;
        int resource;

        public FormsListViewAdapter(Context context, int resource) {
            super(context, resource);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource, parent, false);
                TextView alphabet = (TextView) convertView.findViewById(R.id.forms_list_item_alphabet);
                TextView name = (TextView) convertView.findViewById(R.id.forms_list_item_name);
                //TextView status = (TextView) convertView.findViewById(R.id.forms_list_view_item_status);
                FormsDetail object = arrayList.get(position);
                alphabet.setText(Character.toString(object.getName().charAt(0)));
                alphabet.setBackground(getBackgroundDrawable());
                name.setText(object.getName());
                //status.setText(object.getCurrentStatus());
                if (object.getCurrentStatus().equals("disabled")) {
                    convertView.setBackgroundColor(Color.LTGRAY);
                    convertView.setEnabled(false);
                }
            }
            return convertView;
        }

        GradientDrawable getBackgroundDrawable() {
            GradientDrawable shape = (GradientDrawable) getResources().getDrawable(R.drawable.alphabet_bg);
            shape.setColor(getRandomColor());
            return shape;
        }
    }

    class DownloadFormsData extends AsyncTask<Object, Object, Object> {
        final String server = "http://www.thebbdtimes.com";
        final String homeDirectory = "/app_content_4_1_1";
        final String localDirectory = "/Forms";
        final String fileName = "/getForms.php";
        String pullResult;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            formsSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    formsSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            formsSwipeRefreshLayout.setRefreshing(false);
            if (pullResult == null) {
                // show error toast
            } else {
                StorageHandler storageHandler = new StorageHandler(getContext(), "com.tbt.saveddata.FORMS", pullResult);
                storageHandler.saveFile();
                constructFormsList(getJsonArray(pullResult));
                constructListView();
            }
        }

        @Override
        protected Object doInBackground(Object... objects) {
            String url = server + homeDirectory + localDirectory + fileName;
            try {
                HttpPost post = new HttpPost(url);
                DefaultHttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(post);
                pullResult = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    JSONArray getJsonArray(String parentJSON) {
        JSONArray array = null;
        try {
            array = new JSONObject(parentJSON).getJSONArray("forms");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    void constructFormsList(JSONArray parentJsonArray) {
        arrayList = new ArrayList<FormsDetail>();
        for (int i = 0; i < parentJsonArray.length(); i++) {
            try {
                JSONObject object = parentJsonArray.getJSONObject(i);
                String s = object.getString("current_status");
                if (s.equals("active")) {
                    arrayList.add(new FormsDetail(object.getString("name"), object.getString("link"), object.getString("current_status"), object.getString("status")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void constructListView() {
        formsListView.setAdapter(new FormsListViewAdapter(getContext(), R.layout.forms_list_view_item));
        formsListView.setOnItemClickListener(new FormsListViewOnClickListener());
    }

    int getRandomColor() {
        int i = new Random().nextInt(5);
        int color;
        switch (i) {
            case 0:
                color = 0xFFBA68C8;
                break;
            case 1:
                color = 0xFFAED581;
                break;
            case 2:
                color = 0xFFF6BF26;
                break;
            case 3:
                color = 0xFFF06292;
                break;
            case 4:
                color = 0xFFE06055;
                break;
            default:
                color = 0xFFBA68C8;
                break;
        }
        return color;
    }

    class FormsListViewOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getContext(), TBTWebView.class);
            intent.putExtra("url", arrayList.get(i).getUrl());
            startActivity(intent);
        }
    }
}
