package com.example.ecclesia.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ecclesia.R;
import com.example.ecclesia.adapter.FriendListAdapter;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendListFragment extends Fragment
{
    RecyclerView recyclerView;
    ProgressBar progressBar;

    //For the projects
    private ArrayList<User> friendList;

    //for the user ID;
    private User mUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_contacts, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();
        mUser = bundle.getParcelable("user");
        friendList = new ArrayList<>();

        Handler handler = new Handler();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                ServerRequest request = new ServerRequest();
                request.getFriends(mUser.getToken());
                while (! request.isCompleted() ) { }
                setContactsList(request.getResult());
                FriendListAdapter friendListAdapter = new FriendListAdapter(getContext(), friendList, mUser);

                handler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        recyclerView.setAdapter(friendListAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        thread.start();
    }

    private void setContactsList(JSONObject result)
    {
        try
        {
            boolean success = result.getBoolean("success");
            if (success)
            {
                JSONArray jsonFriendsList = result.getJSONArray("friends");

                for (int i=0 ; i<jsonFriendsList.length() ; i++)
                {
                    JSONObject json = (JSONObject) jsonFriendsList.get(i);
                    User friend = new User(json, false);
                    friendList.add(friend);
                }
            }
            else
            {Log.e("FAIL",result.getString("message"));}
        }
        catch (JSONException e) {e.printStackTrace();}
    }
}