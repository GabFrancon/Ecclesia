package com.example.ecclesia.fragment;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.example.ecclesia.R;
import com.example.ecclesia.adapter.RecyclerViewAdapter;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HistoricFragment extends Fragment
{
    private User mUser;
    private RecyclerView mRecyclerView;
    private RecyclerRefreshLayout pullRefresh;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ProgressBar progressBar;
    private Handler handler;
    LayoutAnimationController controller;

    //For the favorite projects
    private ArrayList<Project> mProjects;
    private boolean onSaved;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_historic, container, false);

        pullRefresh = view.findViewById(R.id.pull_refresh_projects_list);
        mRecyclerView = view.findViewById(R.id.recycleMyProjects);
        progressBar = view.findViewById(R.id.progress_bar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();
        mUser = bundle.getParcelable("user");
        onSaved = bundle.getBoolean("onSaved");

        handler = new Handler();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                mProjects = new ArrayList<>();
                controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
                JSONObject result = loadProjectList();
                fetchProjectList(result);

                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        configureSwipeRefreshLayout();
                        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), mProjects, mUser);
                        mRecyclerView.setAdapter(recyclerViewAdapter);
                        mRecyclerView.setLayoutAnimation(controller);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                        if (onSaved)
                        {
                            CoordinatorLayout layout = view.findViewById(R.id.fav_coordinator_layout);
                            layout.bringToFront();
                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(getItemTouchCallback(layout));
                            itemTouchHelper.attachToRecyclerView(mRecyclerView);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        thread.start();
    }

    private ItemTouchHelper.SimpleCallback getItemTouchCallback(CoordinatorLayout layout)
    {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                int position = viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        Project deletedProject = mProjects.get(position);
                        ServerRequest deleteRequest = new ServerRequest();
                        deleteRequest.deletePrefProject(mUser.getToken(), deletedProject.getID());
                        while (!deleteRequest.isCompleted()) {
                            Log.e("WAIT", "...");
                        }
                        JSONObject result = deleteRequest.getResult();
                        try {
                            boolean success = result.getBoolean("success");
                            if (!success) {
                                Log.e("FAIL", result.getString("message"));
                            } else {
                                mProjects.remove(position);
                                recyclerViewAdapter.notifyItemRemoved(position);
                                recyclerViewAdapter.notifyItemRangeChanged(position, recyclerViewAdapter.getItemCount());

                                Snackbar snackbar = Snackbar.make(layout, "\"" + deletedProject.getTitle() + "\" retiré des favoris", Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.camel_ecclesia))
                                        .setActionTextColor(ContextCompat.getColor(getContext(), R.color.black_ecclesia))
                                        .setAction("Annuler", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ServerRequest cancelRequest = new ServerRequest();
                                                cancelRequest.addPrefProject(mUser.getToken(), deletedProject.getID());
                                                while (!cancelRequest.isCompleted()) {
                                                    Log.e("WAIT", "...");
                                                }
                                                JSONObject result = cancelRequest.getResult();

                                                try {
                                                    boolean cancelSuccess = result.getBoolean("success");
                                                    if (!cancelSuccess) {
                                                        String message = result.getString("message");
                                                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        mProjects.add(position, deletedProject);
                                                        recyclerViewAdapter.notifyItemInserted(position);
                                                        recyclerViewAdapter.notifyItemRangeChanged(position, recyclerViewAdapter.getItemCount());
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                //To put the snackbar action to the top of the layout
                                final Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                                for (int i = 0; i < snackbarLayout.getChildCount(); i++) {
                                    View parent = snackbarLayout.getChildAt(i);
                                    if (parent instanceof LinearLayout) {
                                        parent.setRotation(180);
                                        break;
                                    }
                                }
                                snackbar.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.quantum_orange))
                        .addSwipeLeftActionIcon(R.drawable.ic_favorite)
                        .addSwipeLeftLabel("Retirer des favoris")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white_ecclesia))
                        .setSwipeLeftLabelTypeface(Typeface.SERIF)

                        .setActionIconTint(ContextCompat.getColor(getContext(), R.color.white_ecclesia))

                        .create()
                        .decorate();


                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        return simpleCallback;
    }

    private JSONObject loadProjectList()
    {
        ServerRequest request = new ServerRequest();
        if(!onSaved) { request.getLikedProjects(mUser.getToken()); }
        else { request.getPrefProjects(mUser.getToken()); }
        while (! request.isCompleted() ) { }
        return request.getResult();

    }


    private void fetchProjectList(JSONObject result)
    {
        try {
            boolean success = result.getBoolean("success");
            if (success)
            {
                JSONArray projectList = result.getJSONArray("projects");

                int n = projectList.length();
                for (int i=0 ; i<n ; i++)
                {
                    JSONObject json = (JSONObject) projectList.get(i);

                    Project project = new Project(json);
                    mProjects.add(project);
                }
            }
            else
            {
                Log.e("MESSAGE",result.getString("message"));
            }

        }catch (JSONException e){e.printStackTrace();}
    }

    private void updateProjectList()
    {
        mProjects.clear();
        JSONObject result = loadProjectList();
        fetchProjectList(result);
        mRecyclerView.setLayoutAnimation(controller);
        recyclerViewAdapter.notifyDataSetChanged();
        pullRefresh.setRefreshing(false);
        mRecyclerView.scheduleLayoutAnimation();
    }

    private void configureSwipeRefreshLayout()
    {
        pullRefresh.setAnimateToRefreshDuration(600);
        pullRefresh.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                updateProjectList();
            }
        });
    }
}
