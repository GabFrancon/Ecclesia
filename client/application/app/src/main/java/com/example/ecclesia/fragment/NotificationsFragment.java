package com.example.ecclesia.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.example.ecclesia.R;
import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.adapter.MultipleNotifTypesAdapter;
import com.example.ecclesia.model.FriendRequestNotif;
import com.example.ecclesia.model.Notifications;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.ProjectNotif;
import com.example.ecclesia.model.RowType;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.ecclesia.model.RowType.SHARING_ROW_TYPE;

public class NotificationsFragment extends Fragment
{
    public RecyclerView notificationRecyclerView;
    private ArrayList<RowType> notificationList;
    private RecyclerRefreshLayout pullRefresh;
    private MultipleNotifTypesAdapter adapter;
    private ProgressBar progressBar;
    private Handler handler;
    private User mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationRecyclerView = view.findViewById(R.id.recycler_view_notifications);
        pullRefresh = view.findViewById(R.id.pull_refresh_projects_list);
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
            public void run() {
                notificationList = new ArrayList<>();
                notificationList.clear();
                JSONObject json = loadNotifications();
                fetchNotificationData(json);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        configureSwipeRefreshLayout();
                        adapter = new MultipleNotifTypesAdapter(notificationList);
                        notificationRecyclerView.setAdapter(adapter);
                        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        thread.start();
    }

    private JSONObject loadNotifications()
    {
        ServerRequest request = new ServerRequest();
        request.retrieveNotifications(mUser.getToken());
        while (! request.isCompleted() )
        { }
        return request.getResult();
    }

    private void fetchNotificationData(JSONObject result)
    {
        try
        {
            boolean success = result.getBoolean("success");
            if (success)
            {
                JSONArray list = result.getJSONArray("notifications");

                int n = list.length();
                for (int i=0 ; i<n ; i++)
                {
                    JSONObject json = (JSONObject) list.get(i);
                    boolean isFriendRequest = json.getString("id").equals("null");
                    RowType notification;

                    if (!isFriendRequest)
                    {
                        notification = new ProjectNotif(getContext(), mUser, json);
                    }
                    else
                    {
                        notification = new FriendRequestNotif(getContext(), mUser, json);
                    }
                    notificationList.add(notification);
                }
            }
            else
            {
                new CustomToast(getContext(), "Aucune notification pour le moment", R.drawable.ic_baseline_warning_24);
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }

    private void updateProjectList()
    {
        notificationList.clear();
        JSONObject result = loadNotifications();
        fetchNotificationData(result);
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
        adapter = new MultipleNotifTypesAdapter(notificationList);
        notificationRecyclerView.setLayoutAnimation(controller);
        notificationRecyclerView.setAdapter(adapter);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pullRefresh.setRefreshing(false);
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
                pullRefresh.setRefreshing(false);
            }
        });
    }
}
