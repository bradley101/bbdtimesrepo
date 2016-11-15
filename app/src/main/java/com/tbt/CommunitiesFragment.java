package com.tbt;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by bradley on 21-09-2016.
 */
public class CommunitiesFragment extends Fragment {
    ArrayList<CommunityDetail> arrayList;
    ListView communityListView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.communities_fragment, container, false);
        communityListView = (ListView) v.findViewById(R.id.communities_list_view);
        constructArrayList();
        constructListView();
        return v;
    }

    void constructArrayList() {
        try {
            InputStream inputStream = getContext().getAssets().open("communities.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "", json = "";
            while ((line = br.readLine()) != null) {
                json = json + line;
            }
            br.close();
            inputStream.close();
            arrayList = new ArrayList<>();
            JSONArray array = new JSONObject(json).getJSONArray("communities");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                arrayList.add(new CommunityDetail(object.getString("name"),
                                                  object.getString("branch"),
                                                  object.getString("college"),
                                                  object.getString("gs"),
                                                  object.getString("mob")
                                                 )
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void constructListView() {
        communityListView.setAdapter(new CommunitiesListViewAdapter(getContext(), R.layout.communities_list_view_item, arrayList));
        communityListView.setOnItemClickListener(new CommunitiesListViewOnClickListener());
    }

    class CommunitiesListViewAdapter extends ArrayAdapter<CommunityDetail> {
        Context context;
        int resource;
        ArrayList<CommunityDetail> list;
        public CommunitiesListViewAdapter(Context context, int resource, ArrayList<CommunityDetail> list) {
            super(context, resource);
            this.context = context;
            this.resource = resource;
            this.list = list;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource, parent, false);
                TextView name = (TextView) convertView.findViewById(R.id.communities_list_item_name);
                TextView alphabet = (TextView) convertView.findViewById(R.id.communities_list_item_alphabet);
                TextView college = (TextView) convertView.findViewById(R.id.communities_list_item_college);
                CommunityDetail object = arrayList.get(position);
                name.setText(object.getName());
                college.setText(object.getCollege());
                alphabet.setText(Character.toString(object.getName().charAt(0)));
                alphabet.setBackground(getBackgroundDrawable());
                convertView.setTag(object);
            }
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return list.size();
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

        GradientDrawable getBackgroundDrawable() {
            GradientDrawable shape = (GradientDrawable) getResources().getDrawable(R.drawable.alphabet_bg);
            shape.setColor(getRandomColor());
            return shape;
        }
    }

    class CommunitiesListViewOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            CommunityDetail object = arrayList.get(i);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.communities_list_view_item_dialog, null, false);
            TextView name = (TextView) v.findViewById(R.id.communities_list_item_dialog_name);
            TextView college = (TextView) v.findViewById(R.id.communities_list_item_dialog_college);
            TextView gs = (TextView) v.findViewById(R.id.communities_list_item_dialog_gs);
            TextView mob = (TextView) v.findViewById(R.id.communities_list_item_dialog_mobile);
            TextView branch = (TextView) v.findViewById(R.id.communities_list_item_dialog_branch);
            name.setText(object.getName());
            college.setText("College " + object.getCollege());
            gs.setText("GS: " + object.getGs());
            mob.setText("Mobile: " + object.getMob());

            if(!object.getBranch().equals("all")) {
                branch.setText("Branch: " + object.getBranch());
            } else {
                branch.setVisibility(View.GONE);
            }
            builder.setView(v);
            builder.show();
        }
    }
}
