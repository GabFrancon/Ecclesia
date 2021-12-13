package com.example.ecclesia.fragment;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecclesia.R;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class ProjectMenuFragment extends Fragment
{
    //For the projects
    private User mUser;

    private ViewPager2 projectViewPager;
    private ProjectMenuAdapter adapter;


    public ProjectMenuFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        mUser = new User();
        Bundle bundle = getArguments();
        mUser = bundle.getParcelable("user");
        adapter = new ProjectMenuAdapter(this);
        projectViewPager = view.findViewById(R.id.project_menu_view_pager);
        projectViewPager.setAdapter(adapter);
        projectViewPager.setUserInputEnabled(false);

        TabLayout tabLayout = view.findViewById(R.id.project_menu_tab_layout);

        new TabLayoutMediator(tabLayout, projectViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position==0)
                {
                    tab.setText("Sélection");
                    tab.setIcon(R.drawable.ic_recommendation);
                }
                if (position==1)
                {
                    tab.setText("Découvrir");
                    tab.setIcon(R.drawable.ic_discover);
                }
            }
        }
        ).attach();
    }

    public class ProjectMenuAdapter extends FragmentStateAdapter
    {
        public ProjectMenuAdapter(Fragment fragment) {
            super(fragment);
        }

        @Override
        public Fragment createFragment(int position)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", mUser);

            switch (position) {
                case 0: {
                    Fragment fragment = new ProjectsFragment();
                    fragment.setArguments(bundle);
                    return fragment;
                }
                default: {
                    Fragment fragment = new DiscoverFragment();
                    fragment.setArguments(bundle);
                    return fragment;
                }
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}