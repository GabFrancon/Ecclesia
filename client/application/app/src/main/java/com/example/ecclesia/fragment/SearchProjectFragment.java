package com.example.ecclesia.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecclesia.R;
import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.adapter.RecyclerViewAdapter;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.User;

import java.util.ArrayList;


public class SearchProjectFragment extends Fragment
{
    RecyclerView mRecyclerView;
    ArrayList<Project> mProjectsFound;
    User mUser;

    public SearchProjectFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mProjectsFound = bundle.getParcelableArrayList("projects");
        mUser = bundle.getParcelable("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_search_project, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerViewSearchProject);

        if (mProjectsFound.isEmpty())
        {
            new CustomToast(getContext(), "Aucun résultat", R.drawable.ic_hide);
        }
        else
        {
            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(), mProjectsFound, mUser);
            mRecyclerView.setAdapter(recyclerViewAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        return view;
    }
}