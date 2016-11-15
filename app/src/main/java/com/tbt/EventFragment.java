package com.tbt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by bradley on 22-08-2016.
 */
public class EventFragment extends Fragment{
    final String server = "http://www.thebbdtimes.com";
    final String appDirectory = "/app_content_4_1_1";
    ArrayList<EventDetail> arrayList;
    ListView eventListView;
    SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_fragment_layout, container, false);
        eventListView = (ListView) v.findViewById(R.id.event_list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.event_swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE);
        StorageHandler storage = new StorageHandler(getContext(), "com.tbt.saveddata.EVENTS");
        String result = storage.readFile();
        if(result == null) {
            new DownloadEventData().execute();
        } else {
            constructEventArrayList(getJSONArray(result));
            constructListView();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DownloadEventData().execute();
            }
        });
        return v;
    }

    class DownloadEventData extends AsyncTask<Object, Object, Object> {
        final String eventDirectory = "/Events";
        final String eventFile = "/getEventsData.php";
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
                showErrorToast("Some error occurred. Please try again!");
            } else {
                StorageHandler storageHandler = new StorageHandler(getContext(), "com.tbt.saveddata.EVENTS", pullResult);
                storageHandler.saveFile();
                constructEventArrayList(getJSONArray(pullResult));
                constructListView();
            }
        }

        @Override
        protected Object doInBackground(Object... objects) {
            String eventURL = server + appDirectory + eventDirectory + eventFile;
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(eventURL);
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
            array = new JSONObject(parentJSON).getJSONArray("events");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    void constructEventArrayList(JSONArray parentJsonArray) {
        ArrayList<EventDetail> list = new ArrayList<EventDetail>();
        try {
            for(int i = 0 ; i < parentJsonArray.length() ; i++) {
                JSONObject object = parentJsonArray.getJSONObject(i);
                if(object.getString("status").equals("active")) {
                    list.add(new EventDetail(object.getString("name"), object.getString("details"), object.getString("status")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arrayList = list;
    }

    void constructListView() {
        eventListView.setAdapter(new EventListViewAdapter(getContext(), R.layout.event_list_view_item));
        eventListView.setOnItemClickListener(new EventOnClickListener());
    }

    class EventListViewAdapter extends ArrayAdapter<String> {
        public EventListViewAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.event_list_view_item, parent, false);
                TextView alphabet = (TextView) convertView.findViewById(R.id.event_list_item_alphabet);
                TextView name = (TextView) convertView.findViewById(R.id.event_list_item_name);
                EventDetail event = arrayList.get(position);
                alphabet.setText(Character.toString(event.getEventName().charAt(0)));
                alphabet.setBackground(getBackgroundDrawable());
                name.setText(event.getEventName());
            }
            return convertView;
        }

        GradientDrawable getBackgroundDrawable() {
            GradientDrawable shape = (GradientDrawable) getResources().getDrawable(R.drawable.alphabet_bg);
            shape.setColor(getRandomColor());
            return shape;
        }
    }

    class EventOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            EventDetail event = arrayList.get(i);
            Intent intent = new Intent(getContext(), EventActivity.class);
            intent.putExtra("eventname", event.getEventName());
            intent.putExtra("eventdetails", event.getEventDetails());
            startActivity(intent);
        }
    }

    void showErrorToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
}
