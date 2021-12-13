package com.example.ecclesia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.fragment.HistoricMenuFragment;
import com.example.ecclesia.fragment.MapsFragment;
import com.example.ecclesia.fragment.NotificationsFragment;
import com.example.ecclesia.fragment.ProjectMenuFragment;
import com.example.ecclesia.R;
import com.example.ecclesia.fragment.SearchProjectFragment;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SearchView mSearchView;
    BottomNavigationView bottomNav;
    User mUser;
    BadgeDrawable badge;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(this);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("user"))
        {
            try {
                Bundle bundle = intent.getExtras();
                mUser = bundle.getParcelable("user");

                if (intent.hasExtra("areas") && intent.hasExtra("categories"))
                {
                    ArrayList<String> areas = bundle.getStringArrayList("areas");
                    ArrayList<String> categories = bundle.getStringArrayList("categories");

                    boolean success = mUser.setPreferences(areas, categories);

                    if (success)
                    {
                        new CustomToast(MainActivity.this, "Vos préférences ont bien été mis à jour",R.drawable.ic_baseline_favorite_24);
                    }
                }

            } catch (Exception e) {e.printStackTrace();}
        }


        /* Hooks */
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNav= findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);

        /* ---------- Toolbar --------------------------- */
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /* --------- Navigation drawer menu ------------ */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);


        /* ------------ Bottom Navigation View ------------- */
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        startMapFragment();

        ServerRequest request = new ServerRequest();
        request.countNewNotifications(mUser.getToken());
        while (!request.isCompleted()) {}
        JSONObject result = request.getResult();
        try {
            int notificationCount = result.getInt("count");
            if( notificationCount>0)
            {
                int itemID=bottomNav.getMenu().getItem(2).getItemId();
                badge = bottomNav.getOrCreateBadge(itemID);
                badge.setVisible(true);
                badge.setNumber(notificationCount);
            }
        } catch (JSONException e) {e.printStackTrace();}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_activity_main_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint("Rechercher un projet...");
        mSearchView.setIconified(false);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                startMapFragment();
                return true;
            }
        });


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                ServerRequest request = new ServerRequest();
                request.searchProjects(query, mUser.getToken());
                while (!request.isCompleted()){}
                JSONObject result = request.getResult();
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", mUser);
                bundle.putParcelableArrayList("projects",fetchSearchResult(result));

                Fragment searchProjectFragment = new SearchProjectFragment();;
                searchProjectFragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, searchProjectFragment, "SEARCH")
                        .commit();

                return false;
                }

            @Override
            public boolean onQueryTextChange(String s)
            {
                return false;
            }
        });

        return true;
    }

    public ArrayList<Project> fetchSearchResult (JSONObject result)
    {
        ArrayList<Project> projects = new ArrayList<>();
        try
        {
            boolean success = result.getBoolean("success");

            if (success)
            {
                JSONArray projectList = result.getJSONArray("projects");

                int n = projectList.length();
                for (int i = 0; i < n; i++)
                {
                    JSONObject json = (JSONObject) projectList.get(i);
                    projects.add(new Project(json));
                }
            }
            else { Log.e("MESSAGE", result.getString("message")); }
        } catch (JSONException e) {e.printStackTrace();}
        return projects;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_activity_main_search:
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", mUser);
        switch (item.getItemId())
        {
            case R.id.nav_profile:
                Intent intent_profile = new Intent(MainActivity.this, ProfileActivity.class);
                intent_profile.putExtras(bundle);
                startActivity(intent_profile);
                break;
            case R.id.nav_preferences:
                Intent intent_preferences = new Intent(MainActivity.this, PreferencesActivity.class);
                bundle.putBoolean("from_main",true);
                intent_preferences.putExtras(bundle);
                startActivity(intent_preferences);
                break;
            case R.id.nav_contacts:
                Intent intent_contacts = new Intent(MainActivity.this, FriendsActivity.class);
                intent_contacts.putExtras(bundle);
                startActivity(intent_contacts);
                break;
            case R.id.nav_settings:
                Intent intent_settings = new Intent(MainActivity.this, SettingsActivity.class);
                intent_settings.putExtras(bundle);
                startActivity(intent_settings);
                break;
            case R.id.nav_logout:
                LoginManager.getInstance().logOut(); //logout de facebook
                Intent intent_login_again = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent_login_again);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Fragment selectedFragment;
            Bundle bundle = new Bundle();
            bundle.putParcelable("user",mUser);

            switch (item.getItemId())
            {
                case R.id.nav_maps:
                    selectedFragment = new MapsFragment();
                    selectedFragment.setArguments(bundle);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.animator.fade_in_animator, R.animator.fade_out_animator)
                            .replace(R.id.fragment_container, selectedFragment, "MAP")
                            .commit();
                    break;

                case R.id.nav_projects:
                    selectedFragment=new ProjectMenuFragment();
                    selectedFragment.setArguments(bundle);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.animator.fade_in_animator, R.animator.fade_out_animator)
                            .replace(R.id.fragment_container, selectedFragment, "PROJECT")
                            .commit();
                    break;

                case R.id.nav_notifications :
                    if (badge!=null) badge.setVisible(false);
                    bottomNav.removeBadge(R.id.nav_notifications);

                    selectedFragment = new NotificationsFragment();
                    selectedFragment.setArguments(bundle);
                    getSupportFragmentManager()

                            .beginTransaction()
                            .setCustomAnimations(R.animator.fade_in_animator, R.animator.fade_out_animator)
                            .replace(R.id.fragment_container, selectedFragment, "NOTIFICATION")
                            .commit();
                    break;

                case R.id.nav_historic:
                    selectedFragment=new HistoricMenuFragment();
                    selectedFragment.setArguments(bundle);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.animator.fade_in_animator, R.animator.fade_out_animator)
                            .replace(R.id.fragment_container, selectedFragment, "HISTORIC")
                            .commit();
            }

            return true;
        }
    };

    public void startMapFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", mUser);
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in_animator, R.animator.fade_out_animator)
                .replace(R.id.fragment_container, fragment).commit();
    }

}