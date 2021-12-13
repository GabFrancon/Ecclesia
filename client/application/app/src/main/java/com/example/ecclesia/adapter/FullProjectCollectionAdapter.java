package com.example.ecclesia.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecclesia.fragment.FullProjectFragment;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.User;

import java.util.ArrayList;

public class FullProjectCollectionAdapter extends FragmentStateAdapter
{
    private ArrayList<Project> projects;
    private User user;

    public FullProjectCollectionAdapter(@NonNull FragmentActivity activity, ArrayList<Project> projects, User user)
    {
        super(activity);
        this.projects = projects;
        this.user=user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        FullProjectFragment fullProjectFragment = new FullProjectFragment(projects.get(position), user);
        return fullProjectFragment;
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

}

