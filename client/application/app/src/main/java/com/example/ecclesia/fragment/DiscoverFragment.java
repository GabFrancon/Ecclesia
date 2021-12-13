package com.example.ecclesia.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.example.ecclesia.R;
import com.example.ecclesia.adapter.ProjectListAdapter;
import com.example.ecclesia.adapter.VerticalRecyclerAdapter;
import com.example.ecclesia.model.Classification;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.ProjectCollection;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class DiscoverFragment extends Fragment
{
    private User mUser;
    private ArrayList<ProjectCollection> projectCollections;
    private RecyclerView recyclerView;
    private RecyclerRefreshLayout pullRefresh;
    private VerticalRecyclerAdapter adapter;
    private LayoutAnimationController controller;
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        recyclerView = view.findViewById(R.id.vertical_recycler_view);
        pullRefresh = view.findViewById(R.id.pullRefresh);
        progressBar = view.findViewById(R.id.progress_bar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();
        mUser = bundle.getParcelable("user");

        handler = new Handler();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                projectCollections = new ArrayList<>();
                controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

                updateProjectCollection();

                handler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        configureSwipeRefreshLayout();
                        adapter = new VerticalRecyclerAdapter(projectCollections, getContext(), mUser);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutAnimation(controller);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        thread.start();
    }

    public void updateProjectCollection()
    {
        projectCollections.clear();
        ServerRequest request = new ServerRequest();
        request.getProjectsByClassification(mUser.getToken());

        while (!request.isCompleted()) {}
        JSONObject result = request.getResult();
        try
        {
            boolean success = result.getBoolean("success");

            if (success)
            {
                JSONArray collections = result.getJSONArray("collections");

                int n = collections.length();
                for (int i = 0; i < n; i++)
                {
                    JSONObject json = (JSONObject) collections.get(i);
                    ProjectCollection collection = new ProjectCollection(json);
                    projectCollections.add(collection);
                }
            }
            else { Log.e("FAIL", result.getString("message")); }
        } catch (JSONException e) {e.printStackTrace();}
    }

    private void configureSwipeRefreshLayout()
    {
        pullRefresh.setAnimateToRefreshDuration(600);
        pullRefresh.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                updateProjectCollection();
                recyclerView.setLayoutAnimation(controller);
                adapter.notifyDataSetChanged();
                pullRefresh.setRefreshing(false);
                recyclerView.scheduleLayoutAnimation();
            }
        });
    }
}