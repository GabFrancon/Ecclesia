package com.example.ecclesia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecclesia.fragment.PrefAreaFragment;
import com.example.ecclesia.fragment.PrefCategoryFragment;
import com.example.ecclesia.R;
import com.example.ecclesia.adapter.PrefFixAdapter;
import com.example.ecclesia.model.Classification;
import com.example.ecclesia.model.User;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;

public class PreferencesActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private ImageButton setAreaPreferencesButton;
    private  ImageButton setCategoryPreferencesButton;
    private RecyclerView areaList;
    private RecyclerView categoryList;

    private boolean fromMain=false;
    private User mUser;

    boolean onSettings = false;
    Intent intent;

    private ArrayList<Classification> prefAreas = new ArrayList<>();
    private ArrayList<Classification> prefCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        intent = getIntent();
        if (intent!=null && intent.hasExtra("user") && intent.hasExtra("from_main"))
        {
            Bundle bundle = intent.getExtras();
            fromMain = bundle.getBoolean("from_main");
            mUser= bundle.getParcelable("user");
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!fromMain)
        {
            PrefAreaFragment prefAreaFragment = new PrefAreaFragment();
            Bundle areaBundle = new Bundle();
            areaBundle.putParcelable("user",mUser);
            prefAreaFragment.setArguments(areaBundle);

            //Display fragment preferences of areas, replaced by PrefCategoryFragment when clicks on button from PrefAreaFragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.fade_in_animator, R.animator.fade_out_animator)
                    .add(R.id.fragment_preferences, prefAreaFragment).commit();
        }
        else
        {
            setAreaPreferencesButton = findViewById(R.id.change_pref_area_button);
            setCategoryPreferencesButton = findViewById(R.id.change_pref_category_button);

            Slidr.attach(this);

            Handler handler = new Handler();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    ArrayList<Classification> preferences[] = mUser.getPreferences();

                    prefAreas = preferences[0];
                    PrefFixAdapter prefAreaAdapter = new PrefFixAdapter(PreferencesActivity.this, prefAreas);

                    prefCategories = preferences[1];
                    PrefFixAdapter prefCategoryAdapter = new PrefFixAdapter(PreferencesActivity.this, prefCategories);

                    areaList = findViewById(R.id.recyclerViewPrefAreaActivity);

                    categoryList = findViewById(R.id.recyclerViewPrefCategoryActivity);
                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            areaList.setLayoutManager(new GridLayoutManager(PreferencesActivity.this, 5));
                            areaList.setItemAnimator(new DefaultItemAnimator());

                            categoryList.setLayoutManager(new GridLayoutManager(PreferencesActivity.this, 5));
                            categoryList.setItemAnimator(new DefaultItemAnimator());

                            areaList.setAdapter(prefAreaAdapter);
                            categoryList.setAdapter(prefCategoryAdapter);
                        }
                    });
                }
            });
            thread.start();


            setAreaPreferencesButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    PrefAreaFragment prefAreaFragment = new PrefAreaFragment();
                    Bundle areaBundle = new Bundle();
                    areaBundle.putParcelable("user",mUser);
                    areaBundle.putBoolean("from_main",true);
                    areaBundle.putParcelableArrayList("pref_areas", prefAreas);
                    prefAreaFragment.setArguments(areaBundle);

                    //Display fragment preferences of areas, replaced by PrefCategoryFragment when clicks on button from PrefAreaFragment
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.animator.fade_in_animator, R.animator.fade_out_animator)
                            .add(R.id.fragment_preferences, prefAreaFragment).commit();
                    setAreaPreferencesButton.setVisibility(View.GONE);
                    setCategoryPreferencesButton.setVisibility(View.GONE);
                    onSettings=true;
                }
            });

            setCategoryPreferencesButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    PrefCategoryFragment prefCategoryFragment = new PrefCategoryFragment();
                    Bundle categoryBundle = new Bundle();
                    categoryBundle.putParcelable("user",mUser);
                    categoryBundle.putBoolean("from_main",true);
                    categoryBundle.putParcelableArrayList("pref_categories", prefCategories);
                    prefCategoryFragment.setArguments(categoryBundle);

                    //Display fragment preferences of areas, replaced by PrefCategoryFragment when clicks on button from PrefAreaFragment
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.animator.fade_in_animator, R.animator.fade_out_animator)
                            .add(R.id.fragment_preferences, prefCategoryFragment).commit();
                    setAreaPreferencesButton.setVisibility(View.GONE);
                    setCategoryPreferencesButton.setVisibility(View.GONE);
                    onSettings = true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();

                if (onSettings)
                {
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    onSettings=false;
                }

        }
        return true;
    }
}