package com.example.ecclesia.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecclesia.R;
import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.adapter.ShareProjectAdapter;
import com.example.ecclesia.adapter.DepthPageTransformer;
import com.example.ecclesia.adapter.FullProjectCollectionAdapter;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity {

    //for the website button
    private Button websiteButton;

    //for the projects
    private int currentPosition;
    private Project currentProject;
    private ArrayList<Project> mProjects;

    //share project with friends
    private ArrayList<User> mFriends = new ArrayList<>();

    //for the dialog
    private RecyclerView recyclerView;
    private ShareProjectAdapter shareProjectAdapter;
    private Button shareProjectButton, cancelShareButton;
    private ArrayList<User> friendsToSend = new ArrayList<>();

    // return on MainActivity with the return button
    private Toolbar toolbar;

    // Variables for animations for FloatingActionButton
    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private boolean isButtonOpen;
    private FloatingActionButton addButton, shareButton, dislikeButton, saveButton;
    private ProgressBar progressBar;

    //user data
    private User mUser;

    private ViewPager2 projectViewPager;
    FullProjectCollectionAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        //To create a swipe for returning on ProjectsFragment, only usable from left edge (+15%)
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .edge(true)
                .edgeSize(0.15f)
                .build();

        Slidr.attach(this, config);

        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra("projects"))
        {
            Bundle bundle = intent.getExtras();
            mProjects = bundle.getParcelableArrayList("projects");
            currentPosition = bundle.getInt("position");
            currentProject=mProjects.get(currentPosition);
            mUser = bundle.getParcelable("user");

            projectViewPager = findViewById(R.id.project_view_pager);
            progressBar = findViewById(R.id.progress_bar);

            Handler handler = new Handler();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    loadClassification();
                    adapter = new FullProjectCollectionAdapter(ProjectActivity.this, mProjects, mUser);

                    ServerRequest request = new ServerRequest();
                    request.getFriends(mUser.getToken());
                    while (! request.isCompleted() ) {Log.e("GETTING FRIENDS","..."); }
                    JSONObject result = request.getResult();

                    setContactsList(result);

                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            projectViewPager.setPageTransformer(new DepthPageTransformer());
                            projectViewPager.setAdapter(adapter);
                            projectViewPager.setCurrentItem(currentPosition, false);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            });
            thread.start();

            ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback()
            {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                {
                }

                @Override
                public void onPageSelected(int position)
                {
                    super.onPageSelected(position);
                    currentPosition = position;
                    currentProject = mProjects.get(currentPosition);
                }
                @Override
                public void onPageScrollStateChanged(int state)
                {
                    switch(state)
                    {
                        case ViewPager2.SCROLL_STATE_IDLE:

                            int previousPosition = currentPosition - 1;
                            if(previousPosition < 0) previousPosition = 0;
                            Project previousItem = mProjects.get(previousPosition);
                            if(previousItem.isSaved())
                            {
                                boolean success = previousItem.save(mUser);
                                if (success)
                                {
                                    new CustomToast(ProjectActivity.this, "Ajouté aux favoris", R.drawable.ic_favorite);
                                    removeProject(previousItem, previousPosition);
                                }
                            }
                            if(previousItem.isDeleted())
                            {
                                boolean success = previousItem.dislike(mUser);
                                if (success)
                                {
                                    new CustomToast(ProjectActivity.this, "Supprimé", R.drawable.ic_hide);
                                    removeProject(previousItem, previousPosition);
                                }
                            }
                            break;
                    }
                }
            };

            projectViewPager.registerOnPageChangeCallback(callback);
        }

        //layout
        toolbar = findViewById(R.id.toolbar);
        websiteButton = findViewById(R.id.find_out_more_button);
        addButton = findViewById(R.id.add_button);
        shareButton = findViewById(R.id.share_button);
        dislikeButton = findViewById(R.id.dislike_button);
        saveButton = findViewById(R.id.save_button);

        /** Toolbar with up button to return home */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /** Animation for floating action button */
        rotateOpen = AnimationUtils.loadAnimation(ProjectActivity.this,R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(ProjectActivity.this,R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(ProjectActivity.this,R.anim.from_botton_anim);
        toBottom = AnimationUtils.loadAnimation(ProjectActivity.this,R.anim.to_botton_anim);

        isButtonOpen = false;

        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                visibility(isButtonOpen);
                animation(isButtonOpen);
                clickable(isButtonOpen);
                isButtonOpen = !isButtonOpen;
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //builder for the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ProjectActivity.this);
                View shareLayout = getLayoutInflater().inflate(R.layout.dialog_share_project, null);

                recyclerView = shareLayout.findViewById(R.id.recyclerViewShareProject);
                shareProjectButton = shareLayout.findViewById(R.id.share_project_button);
                cancelShareButton = shareLayout.findViewById(R.id.cancel_share_project_button);


                shareProjectAdapter = new ShareProjectAdapter(ProjectActivity.this, mFriends, mUser);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProjectActivity.this));
                recyclerView.setAdapter(shareProjectAdapter);

                //show the dialog
                builder.setView(shareLayout);
                AlertDialog dialog = builder.create();
                dialog.show();

                //share the project with the friends in checkedFriends
                shareProjectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        friendsToSend = shareProjectAdapter.getCheckedFriends();
                        if(friendsToSend.size()>0) {
                            for(User friend : friendsToSend) {
                                boolean shareProjectBoolean;
                                shareProjectBoolean = mUser.shareProject(friend, currentProject);
                                if(shareProjectBoolean) {
                                    dialog.cancel();
                                    new CustomToast(ProjectActivity.this, "Envoyé", R.drawable.ic_baseline_check_24);
                                }
                            }
                        } else {
                            new CustomToast(ProjectActivity.this, "Vous devez sélectionner au moins un ami", R.drawable.ic_baseline_warning_24);
                        }
                    }
                });

                //cancel sharing
                cancelShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

            }
        });
        
        dislikeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mProjects.size()>1)
                {
                    currentProject.setDeleted();
                    scrollToNextPage();
                }
                else
                {
                    boolean success = currentProject.dislike(mUser);
                    if (success)
                    {
                        new CustomToast(ProjectActivity.this, "Supprimé", R.drawable.ic_hide);
                    }
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mProjects.size()>1)
                {
                    currentProject.setSaved();
                    scrollToNextPage();
                }
                else
                {
                    boolean success = currentProject.save(mUser);
                    if (success)
                    {
                        new CustomToast(ProjectActivity.this, "Ajouté aux favoris", R.drawable.ic_favorite);
                    }
                }
            }
        });


        websiteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(currentProject.getWebsite()));
                startActivity(viewIntent);
            }
        });
    }


    /** Visibility of actions of the floating button */
    private void visibility(boolean isButtonOpen)
    {
        if(!isButtonOpen) {
            shareButton.setVisibility(View.GONE);
            dislikeButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
        } else {
            shareButton.setVisibility(View.VISIBLE);
            dislikeButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
        }
    }

    /** Animations of the floating button */
    private void animation(boolean isButtonOpen)
    {
        if(!isButtonOpen)
        {
            shareButton.startAnimation(fromBottom);
            dislikeButton.startAnimation(fromBottom);
            saveButton.startAnimation(fromBottom);
            addButton.startAnimation(rotateOpen);
        }
        else
         {
            shareButton.startAnimation(toBottom);
            dislikeButton.startAnimation(toBottom);
            saveButton.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);
        }
    }

    /** When floating button is closed, don't want to have toast messages */
    private void clickable(boolean isButtonOpen) {
        if(!isButtonOpen) {
            shareButton.setClickable(true);
            dislikeButton.setClickable(true);
            saveButton.setClickable(true);
        } else {
            shareButton.setClickable(false);
            dislikeButton.setClickable(false);
            saveButton.setClickable(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setContactsList(JSONObject result)
    {
        try
        {
            boolean success = result.getBoolean("success");
            if (success)
            {
                JSONArray friendsList = result.getJSONArray("friends");

                int n = friendsList.length();
                for (int i=0 ; i<n ; i++)
                {
                    JSONObject json = (JSONObject) friendsList.get(i);
                    User friend = new User(json, false);
                    mFriends.add(friend);
                }
            }
            else { Log.e("MESSAGE",result.getString("message")); }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void scrollToNextPage()
    {
        Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        projectViewPager.setCurrentItem(currentPosition + 1, true);
                    }
                });
            }
        });
        thread.start();

    }
    private void removeProject(Project project, int position)
    {
        mProjects.remove(project);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
    }

    public void loadClassification()
    {
        ServerRequest request = new ServerRequest();
        JSONArray projectIDs=new JSONArray();
        for (Project project : mProjects)
        {
            projectIDs.put(project.getID());
        }
        request.getClassification(projectIDs, mUser.getToken());
        while (! request.isCompleted()) { }
        JSONObject result = request.getResult();

        try {
            boolean success = result.getBoolean("success");

            if (success)
            {
                JSONArray classifications = result.getJSONArray("classifications");
                for (int i=0;i<classifications.length();i++)
                {
                    JSONObject json = (JSONObject) classifications.get(i);
                    mProjects.get(i).loadClassifFromJSON(json);
                }

            } else {
                Log.e("FAIL", result.getString("message"));
            }
        } catch (JSONException e) {e.printStackTrace();}
    }

}
