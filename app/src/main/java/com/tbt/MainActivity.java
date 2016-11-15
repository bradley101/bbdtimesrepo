package com.tbt;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    FrameLayout contentFrame;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    ListView drawerListView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawerListView = (ListView) findViewById(R.id.navigation_drawer_list_view);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerListView.setAdapter(new NavigationDrawerListAdapter(MainActivity.this, R.layout.navigation_drawer_list_view_items));
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 1);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.closeDrawers();
        drawerListView.setOnItemClickListener(new NavigationDrawerListItemListener());

        fragmentManager = getSupportFragmentManager();
        if(savedInstanceState == null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_content_frame, new HomeFragment());
            fragmentTransaction.commit();
        }

        if(getIntent().hasExtra("open")) {
            String open = getIntent().getExtras().getString("open");
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (open) {
                case "events":
                    fragmentTransaction.replace(R.id.main_content_frame, new EventFragment());
                    break;
                case "penpoint":
                    fragmentTransaction.replace(R.id.main_content_frame, new PenPointFragment());
                    break;
                case "glory":
                    fragmentTransaction.replace(R.id.main_content_frame, new GloryNewsFragment());
                    break;
                case "forms":
                    fragmentTransaction.replace(R.id.main_content_frame, new FormsFragment());
                    break;
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            drawerToggle.syncState();
            return true;
        }
        if(item.getItemId() == R.id.important_news_icon) {
            startActivity(new Intent(MainActivity.this, ImportantNewsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class NavigationDrawerListAdapter extends ArrayAdapter<String> {
        String[] listViewNames = {"Home", "Events", "Pen Point", "Glory News", "Forms", "Share Your Content",
                                    "Communities", "About The BBD Times", "Team", "Submit Feedback", "Sponsors"};
        int[] listViewIcons = {R.drawable.ic_home, R.drawable.ic_event, R.drawable.ic_penpoint, R.drawable.ic_glorynews,
                                R.drawable.ic_form, R.drawable.ic_share, R.drawable.ic_community, R.drawable.ic_about,
                                R.drawable.ic_team, R.drawable.ic_feedback, R.drawable.ic_sponsor};
        Context context;
        int inflaterResource;
        public NavigationDrawerListAdapter(Context context, int resource) {
            super(context, resource);
            this.context = context;
            inflaterResource = resource;
        }

        @Override
        public int getCount() {
            return listViewIcons.length + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(position == 0) {
                    convertView = inflater.inflate(R.layout.blank_frame_layout, parent, false);
                    FrameLayout frameLayout = (FrameLayout) convertView.findViewById(R.id.blank_frame_layout);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.blank_frame_layout, new AppFacebookFragment()).commit();
                } else {
                    convertView = inflater.inflate(inflaterResource, parent, false);
                    ImageView icon = (ImageView) convertView.findViewById(R.id.navigation_drawer_list_view_icon);
                    TextView name = (TextView) convertView.findViewById(R.id.navigation_drawer_list_view_name);
                    icon.setImageDrawable(getResources().getDrawable(listViewIcons[position - 1]));
                    name.setText(listViewNames[position - 1]);
                }
            return convertView;
        }
    }

    class NavigationDrawerListItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            switch(i) {
                case 6: // share your content activity
                    openURLInTBTWebView("https://docs.google.com/forms/d/e/1FAIpQLSeAnZxhcKq9d3cDi9Hne8-98ya5HEYnUkTvlCl9yemv74NEyg/viewform");
                    break;
                case 10: // feedback activity
                    openURLInTBTWebView("https://docs.google.com/forms/d/e/1FAIpQLSefJeiqw0ZQIikgpg4Bx-LWIepubfT9AwRM27UjYirquVbCYQ/viewform");
                    break;
                default:
                    selectItem(i);
            }
        }

        void openURLInTBTWebView(String destinationURL) {
            Intent webIntent = new Intent(MainActivity.this, TBTWebView.class);
            webIntent.putExtra("url", destinationURL);
            startActivity(webIntent);
        }
    }

    void selectItem(int position) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case 0: // fb_fragment
                break;
            case 1: // home fragment
                fragmentTransaction.replace(R.id.main_content_frame, new HomeFragment()).commit();
                break;
            case 2: // events fragment
                fragmentTransaction.replace(R.id.main_content_frame, new EventFragment()).commit();
                break;
            case 3: // pen point fragment
                fragmentTransaction.replace(R.id.main_content_frame, new PenPointFragment()).commit();
                break;
            case 4: // glory news fragment
                fragmentTransaction.replace(R.id.main_content_frame, new GloryNewsFragment()).commit();
                break;
            case 5: // forms fragment
                fragmentTransaction.replace(R.id.main_content_frame, new FormsFragment()).commit();
                break;
            case 7: // communities fragment
                fragmentTransaction.replace(R.id.main_content_frame, new CommunitiesFragment()).commit();
                break;
            case 8: // about fragment
                break;
            case 9: // team fragment
                fragmentTransaction.replace(R.id.main_content_frame, new TeamFragment()).commit();
                break;
            case 11: // sponsor fragment
                fragmentTransaction.replace(R.id.main_content_frame, new SponsorsFragment()).commit();
                break;
        }
        drawerLayout.closeDrawers();
    }
}
