package com.tbt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
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

/**
 * Created by bradley on 30-08-2016.
 */
public class PenPointFragment extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    ListView penPointListView;
    ArrayList<PenPointDetail> arrayList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pen_point_fragment_layout, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.pen_point_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE);
        penPointListView = (ListView) v.findViewById(R.id.pen_point_list_view);

        StorageHandler storage = new StorageHandler(getContext(), "com.tbt.saveddata.PENPOINT");
        String result = storage.readFile();
        if(result == null) {
            new DownloadPenPointData().execute();
        } else {
            constructPenPointArrayList(getJSONArray(result));
            constructListView();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DownloadPenPointData().execute();
            }
        });
        return v;
    }

    class DownloadPenPointData extends AsyncTask<Object, Object, Object> {
        final String server = "http://www.thebbdtimes.com";
        final String homeDirectory = "/app_content_4_1_1";
        final String localDirectory = "/Writers";
        final String fileName = "/getWritersData.php";
        String pullResult;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            swipeRefreshLayout.setRefreshing(false);
            if(pullResult == null) {
                // show error toast
            } else {
                StorageHandler storage = new StorageHandler(getContext(), "com.tbt.saveddata.PENPOINT", pullResult);
                storage.saveFile();
                constructPenPointArrayList(getJSONArray(pullResult));
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

    JSONArray getJSONArray(String parentJSON) {
        JSONArray array = null;
        try {
            array = new JSONObject(parentJSON).getJSONArray("penpoint");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    void constructPenPointArrayList(JSONArray parentJsonArray) {
        arrayList = new ArrayList<PenPointDetail>();
        for(int i = 0 ; i < parentJsonArray.length() ; i++) {
            try {
                JSONObject object = parentJsonArray.getJSONObject(i);
                arrayList.add(new PenPointDetail(object.getString("name"), object.getString("article"),
                                                 object.getString("author"),
                                                 object.getString("date"), object.getString("status"),
                                                 getTeamMemberResId(object.getString("author"))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void constructListView() {
        penPointListView.setAdapter(new PenPointListViewAdapter(getContext(), R.layout.pen_point_list_view_item));
        penPointListView.setOnItemClickListener(new PenPointOnClickListener());
    }

    class PenPointOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            PenPointDetail object = arrayList.get(i);
            Intent intent = new Intent(getContext(), PenPointActivity.class);
            intent.putExtra("articleName", object.getName());
            intent.putExtra("article", object.getContent());
            startActivity(intent);
        }
    }

    class PenPointListViewAdapter extends ArrayAdapter<String> {
        Context context;
        int resource;
        public PenPointListViewAdapter(Context context, int resource) {
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView == null) {
                convertView = inflater.inflate(resource, parent, false);
                TextView name = (TextView) convertView.findViewById(R.id.pen_point_article_name);
                TextView dated = (TextView) convertView.findViewById(R.id.pen_point_article_date);
                ImageView img = (ImageView) convertView.findViewById(R.id.pen_point_article_author_image);
                PenPointDetail object = arrayList.get(position);
                name.setText(object.getName());
                dated.setText(object.getDated());
                img.setImageDrawable(getResources().getDrawable(object.getImgID()));
            }
            return convertView;
        }
    }

    int getTeamMemberResId(String name) {
        int id = -1;
        switch(name) {
            case "Ambica Singh":
                id = R.drawable.ambica;
                break;
            case "Jayati Sinha":
                id = R.drawable.jayati;
                break;
            case "Shubham Agrawal":
                id = R.drawable.shubham;
                break;
            case "Yash Srivastava":
                id = R.drawable.yash;
                break;
            case "Zainab Siddiqui":
                id = R.drawable.zainab;
                break;
            case "Abhishek Pandey":
                id = R.drawable.abhishek;
                break;
            case "Adarsh Sharma":
                id = R.drawable.adarsh;
                break;
            case "Govind Pratap Tomar":
                id = R.drawable.govind;
                break;
            case "Kaushiki Pandey":
                id = R.drawable.kaushiki;
                break;
            case "Saharsh Rastogi":
                id = R.drawable.saharsh;
                break;
            case "Sandeep Nandan Mishra":
                id = R.drawable.sandeep;
                break;
            case "Saurabh Chandra Rai":
                id = R.drawable.saurabh;
                break;
            case "Shantanu Banerjee":
                id = R.drawable.shantanu;
                break;
            case "Shweta Tripathi":
                id = R.drawable.shweta;
                break;
            case "Bikash Mandal":
                id = R.drawable.bikash;
                break;
            case "Yash Asthana":
                id = R.drawable.asthana;
                break;
            case "Mohd. Fahad":
                id = R.drawable.fahad;
                break;
            case "Saransh Srivastava":
                id = R.drawable.saransh;
                break;
            default:
                id = R.drawable.ic_penpoint;
                break;
        }
        return id;
    }
}
