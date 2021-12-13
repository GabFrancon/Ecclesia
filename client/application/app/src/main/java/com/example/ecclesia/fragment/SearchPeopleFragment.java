package com.example.ecclesia.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecclesia.R;
import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.adapter.SearchPeopleAdapter;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchPeopleFragment extends Fragment
{
    RecyclerView recyclerView;

    //For the projects
    private ArrayList<User> userList;

    //for the user ID;
    private User mUser;
    private String search;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_people, container, false);

        userList = new ArrayList<>();

        Bundle bundle = getArguments();
        mUser = bundle.getParcelable("user");
        search = bundle.getString("search");

        recyclerView = view.findViewById(R.id.recycleurView);

        ServerRequest request = new ServerRequest();
        if (!search.isEmpty())
        {
            request.searchUsers(search, mUser.getToken());
            while (!request.isCompleted()) { }
            setUsersList(request.getResult());
        }

        SearchPeopleAdapter contactsAdapter = new SearchPeopleAdapter(getContext(), userList, mUser);
        recyclerView.setAdapter(contactsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void setUsersList(JSONObject result)
    {
        try
        {
            boolean success = result.getBoolean("success");
            if (success)
            {
                JSONArray usersList = result.getJSONArray("users");

                int n = usersList.length();
                for (int i=0 ; i<n ; i++)
                {
                    JSONObject json = (JSONObject) usersList.get(i);
                    User userFound = new User(json, false);
                    userList.add(userFound);
                }
            }
            else {new CustomToast(getContext(), result.getString("message"), R.drawable.ic_hide);}
        }
        catch (JSONException e) {e.printStackTrace();}
    }
}
