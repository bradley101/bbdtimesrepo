package com.tbt;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
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
 * Created by bradley on 31-08-2016.
 */
public class GloryNewsFragment extends Fragment {
    SwipeRefreshLayout gloryNewsSwipeRefreshLayout;
    ListView gloryNewsListView;
    ArrayList<GloryNewsDetail> arrayList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.glory_news_fragment_layout, container, false);
        gloryNewsListView = (ListView) v.findViewById(R.id.glory_news_list_view);
        gloryNewsSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.glory_news_swipe_refresh_layout);
        gloryNewsSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE);
        StorageHandler storage = new StorageHandler(getContext(), "com.tbt.saveddata.GLORYNEWS");
        String result = storage.readFile();
        if(result == null) {
            new DownloadGloryNewsData().execute();
        } else {
            constructGloryNewsList(getJsonArray(result));
            constructListView();
        }
        gloryNewsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DownloadGloryNewsData().execute();
            }
        });
        return v;
    }

    class GloryNewsListViewAdapter extends ArrayAdapter<String> {
        Context context;
        int resource;
        public GloryNewsListViewAdapter(Context context, int resource) {
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
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.glory_news_list_view_item, parent, false);
                TextView gloryNews = (TextView) convertView.findViewById(R.id.glory_news_list_view_item_text_view);
                TextView expander = (TextView) convertView.findViewById(R.id.glory_news_list_view_item_text_view_expander);
                GloryNewsDetail object = arrayList.get(position);
                gloryNews.setText(object.getGloryNews());
                expander.setOnClickListener(new ExpanderOnClickListener());
            }
            return convertView;
        }

        class ExpanderOnClickListener implements View.OnClickListener {

            @Override
            public void onClick(View view) {
                animate((TextView) view);
            }

            void animate(TextView v) {
                if(v.getText().toString().equals("Show More")) {
                    ObjectAnimator animator = ObjectAnimator.ofInt(v, "maxLines", 100);
                    animator.setDuration(500);
                    v.setText("Show Less");
                    animator.start();
                } else {
                    ObjectAnimator animator = ObjectAnimator.ofInt(v, "maxLines", 2);
                    animator.setDuration(500);
                    v.setText("Show More");
                    animator.start();
                }
            }
        }
    }

    class DownloadGloryNewsData extends AsyncTask<Object, Object, Object> {
        final String server = "http://www.thebbdtimes.com";
        final String homeDirectory = "/app_content_4_1_1";
        final String localDirectory = "/GloryNews";
        final String fileName = "/getGloryNewsData.php";
        String pullResult;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gloryNewsSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    gloryNewsSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            gloryNewsSwipeRefreshLayout.setRefreshing(false);
            if(pullResult == null) {
                // show error toast
            } else {
                StorageHandler storageHandler = new StorageHandler(getContext(), "com.tbt.saveddata.GLORYNEWS", pullResult);
                storageHandler.saveFile();
                constructGloryNewsList(getJsonArray(pullResult));
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
            array = new JSONObject(parentJSON).getJSONArray("glory");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    void constructGloryNewsList(JSONArray parentJsonArray) {
        arrayList = new ArrayList<GloryNewsDetail>();
        for(int i = 0 ; i < parentJsonArray.length() ; i++) {
            try {
                JSONObject object = parentJsonArray.getJSONObject(i);
                arrayList.add(new GloryNewsDetail(object.getString("news"), object.getString("status")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void constructListView() {
        gloryNewsListView.setAdapter(new GloryNewsListViewAdapter(getContext(), R.layout.glory_news_list_view_item));
    }
}
