package com.tbt;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by bradley on 26-08-2016.
 */
public class ImportantNewsActivity extends AppCompatActivity {
    ListView importantNewsListView;
    SwipeRefreshLayout swipeRefreshLayout;
    String[] arrayList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.important_news_layout);
        importantNewsListView = (ListView) findViewById(R.id.important_news_list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.important_news_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE);
        StorageHandler storageHandler = new StorageHandler(ImportantNewsActivity.this, "com.tbt.saveddata.IMPNEWS");
        String result = storageHandler.readFile();
        if(result == null) {
            new DownloadImportantNewsData().execute();
        } else {
            constructImpNewsList(getJSONArray(result));
            constructListView();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DownloadImportantNewsData().execute();
            }
        });
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

    class ImportantNewsArrayAdapter extends ArrayAdapter<String> {
        String list[];
        int resource;
        Context context;
        public ImportantNewsArrayAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            list = objects;
            this.resource = resource;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.important_news_list_view_item, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.important_news_list_view_item_text_view);
                textView.setText(list[position]);
            }
            return convertView;
        }
    }

    class DownloadImportantNewsData extends AsyncTask<Object, Object, Object> {
        final String server = "http://www.thebbdtimes.com";
        final String homeDirectory = "/app_content_4_1_1";
        final String localDirectory = "/ImportantNews";
        final String fileName = "/getImpNewsData.php";
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
                StorageHandler storage = new StorageHandler(ImportantNewsActivity.this, "com.tbt.saveddata.IMPNEWS", pullResult);
                storage.saveFile();
                constructImpNewsList(getJSONArray(pullResult));
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
            array = new JSONObject(parentJSON).getJSONArray("news");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    void constructImpNewsList(JSONArray parentJsonArray) {
        arrayList = new String[parentJsonArray.length()];
        for(int i = 0 ; i < parentJsonArray.length() ; i++) {
            try {
                arrayList[i] = (parentJsonArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void constructListView() {
        importantNewsListView.setAdapter(new ImportantNewsArrayAdapter(ImportantNewsActivity.this, R.layout.important_news_list_view_item, arrayList));
    }
}
