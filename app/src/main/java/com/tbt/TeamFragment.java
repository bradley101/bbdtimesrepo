package com.tbt;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bradley on 01-09-2016.
 */
public class TeamFragment extends Fragment {
    ListView teamListView;
    ArrayList<TeamMemberDetail> teamList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.team_fragment_layout, container, false);
        constructTeamList();
        teamListView = (ListView) v.findViewById(R.id.team_list_view);
        teamListView.setAdapter(new TeamListViewAdapter(getContext(), R.layout.team_list_view_item, teamList));
        return v;
    }

    class TeamListViewAdapter extends ArrayAdapter<TeamMemberDetail> {
        Context context;
        int resource;
        ArrayList<TeamMemberDetail> list;
        public TeamListViewAdapter(Context context, int resource, ArrayList<TeamMemberDetail> list) {
            super(context, resource);
            this.context = context;
            this.resource = resource;
            this.list = list;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return list.size();
        }

        @Override
        public int getCount() {
            return teamList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.team_list_view_item, parent, false);
                ImageView photo = (ImageView) convertView.findViewById(R.id.team_list_view_item_photo);
                TextView name = (TextView) convertView.findViewById(R.id.team_list_view_item_name);
                TextView designation = (TextView) convertView.findViewById(R.id.team_list_view_item_designation_details);
                TeamMemberDetail object = teamList.get(position);
                photo.setImageDrawable(getResources().getDrawable(object.imgResId));
                name.setText(object.getMemberName());
                designation.setText(object.getYear() + " Year, " + object.getBranch());
            }
            return convertView;
        }


    }
    void constructTeamList() {
        teamList = new ArrayList<TeamMemberDetail>();
        addMember("Ambica Singh", "4th", "AE", R.drawable.ambica);
        addMember("Jayati Sinha", "4th", "AE", R.drawable.jayati);
        addMember("Shubham Agrawal", "4th", "CS", R.drawable.shubham);
        addMember("Yash Srivastava", "4th", "CS", R.drawable.yash);
        addMember("Zainab Siddiqui", "4th", "CHE", R.drawable.zainab);
        addMember("Abhishek Pandey", "3rd", "CS", R.drawable.abhishek);
        addMember("Adarsh Sharma", "3rd", "EC", R.drawable.adarsh);
        addMember("Bikash Mandal", "3rd", "EC", R.drawable.bikash);
        addMember("Govind Pratap Tomar", "3rd", "EN", R.drawable.govind);
        addMember("Kaushiki Pandey", "3rd", "BA LLB", R.drawable.kaushiki);
        addMember("Saharsh Rastogi", "3rd", "CS", R.drawable.saharsh);
        addMember("Sandeep Nandan Mishra", "3rd", "EI", R.drawable.sandeep);
        addMember("Saurabh Chandra Rai", "3rd", "CS", R.drawable.saurabh);
        addMember("Shantanu Banerjee", "3rd", "CS", R.drawable.shantanu);
        addMember("Shweta Tripathi", "3rd", "EC", R.drawable.shweta);
        addMember("Yash Asthana", "3rd", "IT", R.drawable.asthana);
        addMember("Mohd. Fahad", "2nd", "BCA", R.drawable.fahad);
        addMember("Saransh Srivastava", "2nd", "BA LLB", R.drawable.saransh);
    }
    void addMember(String name, String year, String branch, int resId) {
        TeamMemberDetail member = new TeamMemberDetail(name, year, branch, resId);
        teamList.add(member);
    }
}
