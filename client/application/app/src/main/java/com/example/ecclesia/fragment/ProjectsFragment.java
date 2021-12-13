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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.example.ecclesia.R;
import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.adapter.ProjectListAdapter;
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

public class ProjectsFragment extends Fragment
{

    private RecyclerView recyclerView;
    ProjectListAdapter recyclerViewAdapter;
    private RecyclerRefreshLayout pullRefresh;
    private ProgressBar progressBar;

    private ArrayList<Project> mProjects;
    private User mUser;
    private LayoutAnimationController controller;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recyclerView);
        pullRefresh = view.findViewById(R.id.pull_refresh_projects_list);
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
                mProjects = new ArrayList<>();
                controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);

                JSONObject result = loadProjectList();
                fetchProjectList(result);
                loadClassification();

                handler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        configureSwipeRefreshLayout();
                        recyclerViewAdapter = new ProjectListAdapter(getContext(), mProjects, mUser);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerView.setLayoutAnimation(controller);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                        CoordinatorLayout layout = view.findViewById(R.id.proj_coordinator_layout);
                        layout.bringToFront();
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(getItemTouchCallback(layout));
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        thread.start();
    }

    private JSONObject loadProjectList()
    {
        ServerRequest request = new ServerRequest();
        request.getRecommendedProjects(mUser.getToken());
        while (! request.isCompleted() ) { }
        return request.getResult();
    }

    private void fetchProjectList(JSONObject result)
    {
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
                    Project project = new Project(json);
                    mProjects.add(project);
                }
            }
            else { Log.e("MESSAGE", result.getString("message")); }
        } catch (JSONException e) {e.printStackTrace();}
    }

    private void updateProjectList()
    {
        mProjects.clear();
        JSONObject result = loadProjectList();
        fetchProjectList(result);
        loadClassification();
        recyclerView.setLayoutAnimation(controller);
        recyclerViewAdapter.notifyDataSetChanged();
        pullRefresh.setRefreshing(false);
        recyclerView.scheduleLayoutAnimation();
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

    public ItemTouchHelper.SimpleCallback getItemTouchCallback(CoordinatorLayout layout)
    {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
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
                switch (direction)
                {
                    case ItemTouchHelper.LEFT :
                        Project deletedProject = mProjects.get(position);
                        ServerRequest deleteRequest = new ServerRequest();
                        deleteRequest.hideProject(mUser.getToken(), deletedProject.getID());
                        while (!deleteRequest.isCompleted()) { }
                        JSONObject deleteResult = deleteRequest.getResult();
                        try
                        {
                            boolean hideSuccess = deleteResult.getBoolean("success");
                            if(!hideSuccess)
                            {
                                Log.e("FAIL", deleteResult.getString("message"));
                            }
                            else
                            {
                                mProjects.remove(position);
                                recyclerViewAdapter.notifyItemRemoved(position);
                                recyclerViewAdapter.notifyItemRangeChanged(position, recyclerViewAdapter.getItemCount());

                                Snackbar snackbar = Snackbar.make(layout, "\""+deletedProject.getTitle()+"\" supprimé", Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.camel_ecclesia))
                                        .setActionTextColor(ContextCompat.getColor(getContext(), R.color.black_ecclesia))
                                        .setAction("Annuler", new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                ServerRequest cancelRequest = new ServerRequest();
                                                cancelRequest.deleteLike(mUser.getToken(), deletedProject.getID());
                                                while (!cancelRequest.isCompleted())
                                                { }
                                                JSONObject result = cancelRequest.getResult();

                                                try
                                                {
                                                    boolean cancelSuccess = result.getBoolean("success");
                                                    if (!cancelSuccess)
                                                    {
                                                        Log.e("FAIL", result.getString("message"));
                                                    }
                                                    else
                                                    {
                                                        mProjects.add(position, deletedProject);
                                                        recyclerViewAdapter.notifyItemInserted(position);
                                                        recyclerViewAdapter.notifyItemRangeChanged(position, recyclerViewAdapter.getItemCount());
                                                    }
                                                } catch (JSONException e) {e.printStackTrace();}
                                            }
                                        });
                                //To put the snack bar action to the top of the layout
                                final Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                                for (int i=0;i<snackbarLayout.getChildCount();i++)
                                {
                                    View parent = snackbarLayout.getChildAt(i);
                                    if (parent instanceof LinearLayout)
                                    {
                                        parent.setRotation(180);
                                        break;
                                    }
                                }
                                snackbar.show();
                            }

                        } catch (JSONException e) {e.printStackTrace();}

                        break;

                    case ItemTouchHelper.RIGHT :
                        Project savedProject = mProjects.get(position);

                        ServerRequest favRequest = new ServerRequest();
                        favRequest.addPrefProject(mUser.getToken(), savedProject.getID());
                        while (!favRequest.isCompleted()) { }
                        JSONObject favResult = favRequest.getResult();
                        try {
                            boolean favSuccess = favResult.getBoolean("success");
                            if (!favSuccess)
                            {
                                Log.e("FAIL", favResult.getString("message"));
                            }
                            else
                            {
                                mProjects.remove(position);
                                recyclerViewAdapter.notifyItemRemoved(position);
                                recyclerViewAdapter.notifyItemRangeChanged(position, recyclerViewAdapter.getItemCount());

                                Snackbar snackbar = Snackbar.make(layout, "\""+savedProject.getTitle() + "\" enregistré", Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.camel_ecclesia))
                                        .setActionTextColor(ContextCompat.getColor(getContext(), R.color.black_ecclesia))
                                        .setAction("Annuler", new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                ServerRequest cancelRequest = new ServerRequest();
                                                cancelRequest.deletePrefProject(mUser.getToken(), savedProject.getID());
                                                while (!cancelRequest.isCompleted()) { }
                                                JSONObject result = cancelRequest.getResult();

                                                try {
                                                    boolean cancelSuccess = result.getBoolean("success");
                                                    if (!cancelSuccess) {
                                                        Log.e("FAIL", result.getString("message"));
                                                    } else {
                                                        mProjects.add(position, savedProject);

                                                        recyclerViewAdapter.notifyItemInserted(position);
                                                        recyclerViewAdapter.notifyItemRangeChanged(position, recyclerViewAdapter.getItemCount());
                                                    }
                                                } catch (JSONException e) {e.printStackTrace();}
                                            }
                                        });
                                //To put the snack bar action to the top of the layout
                                final Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                                for (int i=0;i<snackbarLayout.getChildCount();i++)
                                {
                                    View parent = snackbarLayout.getChildAt(i);
                                    if (parent instanceof LinearLayout)
                                    {
                                        parent.setRotation(180);
                                        break;
                                    }
                                }
                                snackbar.show();
                            }
                        } catch (JSONException e) {e.printStackTrace();}
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_ecclesia))
                        .addSwipeLeftActionIcon(R.drawable.ic_hide)
                        .addSwipeLeftLabel("Pas intéressé")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white_ecclesia))
                        .setSwipeLeftLabelTypeface(Typeface.SERIF)

                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.green_ecclesia))
                        .addSwipeRightActionIcon(R.drawable.ic_favorite)
                        .addSwipeRightLabel("Ajouter aux favoris")
                        .setSwipeRightLabelColor(ContextCompat.getColor(getContext(), R.color.white_ecclesia))
                        .setSwipeRightLabelTypeface(Typeface.SERIF)

                        .setActionIconTint(ContextCompat.getColor(getContext(), R.color.white_ecclesia))

                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        return simpleCallback;
    }

}
