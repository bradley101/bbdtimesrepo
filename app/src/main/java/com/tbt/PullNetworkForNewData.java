package com.tbt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
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

/**
 * Created by bradley on 04-09-2016.
 */
public class PullNetworkForNewData extends BroadcastReceiver {
    Context context;
    Intent intent;
    final String server = "http://www.thebbdtimes.com/app_content_4_1_1";
    final String[] pullURLs = {
            "/Events/getEventsData.php",
            "/Writers/getWritersData.php",
            "/GloryNews/getGloryNewsData.php",
            "/Forms/getForms.php",
            "/ImportantNews/getImpNewsData.php"
    };
    final String[] fileNames = {
            "com.tbt.saveddata.EVENTS",
            "com.tbt.saveddata.PENPOINT",
            "com.tbt.saveddata.GLORYNEWS",
            "com.tbt.saveddata.FORMS",
            "com.tbt.saveddata.IMPNEWS"
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        Toast.makeText(context, "Called", Toast.LENGTH_SHORT).show();
        new DownloadNewData(server + pullURLs[0], fileNames[0]).execute();
        new DownloadNewData(server + pullURLs[1], fileNames[1]).execute();
        new DownloadNewData(server + pullURLs[2], fileNames[2]).execute();
        new DownloadNewData(server + pullURLs[3], fileNames[3]).execute();
        new DownloadNewData(server + pullURLs[4], fileNames[4]).execute();
    }

    class DownloadNewData extends AsyncTask<Object, Object, Object> {
        String pullResult;
        String fileName;
        String url;

        DownloadNewData(String url, String file) {
            this.url = url;
            this.fileName = file;
        }

        @Override
        protected Object doInBackground(Object... objects) {
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

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            boolean newData = false;
            String oldData = null;
            if (pullResult != null) {
                StorageHandler storage = new StorageHandler(context, fileName, pullResult);
                oldData = storage.readFile();
                storage.saveFile();
                if(oldData == null || !oldData.equals(pullResult)) {
                    newData = true;
                }
            }

            if(newData) {
                try {
                    JSONObject jsonObject;
                    Log.d("tbt debug tag", "reached!");
                    if (fileName.equals("com.tbt.saveddata.PENPOINT")) {
                        jsonObject = new JSONObject(pullResult);
                        checkPenPointData(jsonObject, oldData);
                    } else if (fileName.equals("com.tbt.saveddata.EVENTS")) {
                        jsonObject = new JSONObject(pullResult);
                        checkEventsData(jsonObject, oldData);
                    } else if (fileName.equals("com.tbt.saveddata.GLORYNEWS")) {
                        jsonObject = new JSONObject(pullResult);
                        checkGloryNewsData(jsonObject, oldData);
                    } else if (fileName.equals("com.tbt.saveddata.FORMS")) {
                        jsonObject = new JSONObject(pullResult);
                        checkFormsData(jsonObject, oldData);
                    } else if (fileName.equals("com.tbt.saveddata.IMPNEWS")) {
                        jsonObject = new JSONObject(pullResult);
                        checkImpNewsData(jsonObject, oldData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        /*
        void handleNewData(String fileName, String pullResult, StorageHandler storageHandler) {
            try {
                JSONObject jsonObject;
                Log.d("tbt debug tag", "reached!");
                if (fileName.equals("com.tbt.saveddata.PENPOINT")) {
                    jsonObject = new JSONObject(pullResult);
                    checkPenPointData(jsonObject, storageHandler.readFile());
                } else if (fileName.equals("com.tbt.saveddata.EVENTS")) {
                    jsonObject = new JSONObject(pullResult);
                    checkEventsData(jsonObject, pullResult);
                } else if (fileName.equals("com.tbt.saveddata.GLORYNEWS")) {
                    jsonObject = new JSONObject(pullResult);
                    checkGloryNewsData(jsonObject, pullResult);
                } else if (fileName.equals("com.tbt.saveddata.FORMS")) {
                    jsonObject = new JSONObject(pullResult);
                    checkFormsData(jsonObject, pullResult);
                } else if (fileName.equals("com.tbt.saveddata.IMPNEWS")) {
                    jsonObject = new JSONObject(pullResult);
                    checkImpNewsData(jsonObject, pullResult);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        */
        void checkPenPointData(JSONObject p, String c) {    // p is new, c is old
            boolean notify = false;
            try {
                if (c == null) {
                    notify = true;
                } else {
                    JSONObject savedObject = new JSONObject(c);
                    int savedArrayLength = savedObject.getJSONArray("penpoint").length();
                    int newArrayLength = p.getJSONArray("penpoint").length();
                    if (newArrayLength > savedArrayLength) {
                        notify = true;
                    }
                }

                if (notify) {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                    notification.setContentTitle("New Pen Point Article : The BBD Times App");
                    notification.setSmallIcon(R.drawable.ic_penpoint);
                    notification.setContentText("A new article on the Pen Point Section has just arrived. Click to check");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).putExtra("open", "penpoint"), 0);
                    notification.setContentIntent(pendingIntent);
                    notificationManager.notify(0, notification.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        void checkEventsData(JSONObject p, String c) {    // p is new, c is old
            boolean notify = false;
            try {
                if (c == null) {
                    notify = true;
                } else {
                    JSONObject savedObject = new JSONObject(c);
                    int savedArrayLength = savedObject.getJSONArray("events").length();
                    int newArrayLength = p.getJSONArray("events").length();
                    if (newArrayLength > savedArrayLength) {
                        notify = true;
                    }
                }

                if (notify) {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                    notification.setContentTitle("New Event : The BBD Times App");
                    notification.setSmallIcon(R.drawable.ic_event);
                    notification.setContentText("A new event on the Event Section has just arrived. Click to check");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).putExtra("open", "event"), 0);
                    notification.setContentIntent(pendingIntent);
                    notificationManager.notify(1, notification.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        void checkGloryNewsData(JSONObject p, String c) {    // p is new, c is old
            boolean notify = false;
            try {
                if (c == null) {
                    notify = true;
                } else {
                    JSONObject savedObject = new JSONObject(c);
                    int savedArrayLength = savedObject.getJSONArray("glory").length();
                    int newArrayLength = p.getJSONArray("glory").length();
                    if (newArrayLength > savedArrayLength) {
                        notify = true;
                    }
                }

                if (notify) {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                    notification.setContentTitle("New Glory News : The BBD Times App");
                    notification.setSmallIcon(R.drawable.ic_glorynews);
                    notification.setContentText("A new Glory News on the Glory News Section has just arrived. Click to check");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).putExtra("open", "glory"), 0);
                    notification.setContentIntent(pendingIntent);
                    notificationManager.notify(2, notification.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        void checkFormsData(JSONObject p, String c) {    // p is new, c is old
            boolean notify = false;
            try {
                if (c == null) {
                    notify = true;
                } else {
                    JSONObject savedObject = new JSONObject(c);
                    int savedArrayLength = savedObject.getJSONArray("forms").length();
                    int newArrayLength = p.getJSONArray("forms").length();
                    if (newArrayLength > savedArrayLength) {
                        notify = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (notify) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                notification.setContentTitle("New Form : The BBD Times App");
                notification.setSmallIcon(R.drawable.ic_form);
                notification.setContentText("A new form on the Form Section has just arrived. Click to check");
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).putExtra("open", "forms"), 0);
                notification.setContentIntent(pendingIntent);
                notificationManager.notify(3, notification.build());
            }
        }

        void checkImpNewsData(JSONObject p, String c) {    // p is new, c is old
            boolean notify = false;
            try {
                if (c == null) {
                    notify = true;
                } else {
                    JSONObject savedObject = new JSONObject(c);
                    int savedArrayLength = savedObject.getJSONArray("news").length();
                    int newArrayLength = p.getJSONArray("news").length();
                    if (newArrayLength != savedArrayLength) {
                        notify = true;
                    }
                }

                if (notify) {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                    notification.setContentTitle("New Important News : The BBD Times App");
                    notification.setSmallIcon(R.drawable.ic_notification);
                    notification.setContentText("A new important news on the Important News Section has just arrived. Click to check");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, ImportantNewsActivity.class), 0);
                    notification.setContentIntent(pendingIntent);
                    notificationManager.notify(4, notification.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
