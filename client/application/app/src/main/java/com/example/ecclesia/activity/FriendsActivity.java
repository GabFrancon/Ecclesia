package com.example.ecclesia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.ecclesia.fragment.FriendListFragment;
import com.example.ecclesia.R;
import com.example.ecclesia.fragment.SearchPeopleFragment;
import com.example.ecclesia.fragment.SearchProjectFragment;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;
import com.r0adkll.slidr.Slidr;

import org.json.JSONObject;

import static android.view.View.GONE;

public class FriendsActivity extends AppCompatActivity
{
    Toolbar toolbar;
    User mUser;
    SearchView searchView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_search);

        Slidr.attach(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user"))
        {
            Bundle bundle = intent.getExtras();
            mUser = bundle.getParcelable("user");
        }


        //Toolbar with up button to return home
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //When the app is opened, first screen shown is FriendListFragment
        startFriendListFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_friends_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_activity_main_search);
        searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.setQueryHint("Rechercher une personne...");
        searchView.setIconified(false);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                startFriendListFragment();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                if (!s.equals(""))
                {
                    Fragment searchPeopleFragment = new SearchPeopleFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", mUser);
                    bundle.putString("search", s);
                    searchPeopleFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2, searchPeopleFragment).commit();
                }
                else
                {
                    startFriendListFragment();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
        }
        return true;
    }

    public void startFriendListFragment()
    {
        FriendListFragment friendListFragment = new FriendListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", mUser);
        friendListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container2,
                friendListFragment).commit();
    }
}
