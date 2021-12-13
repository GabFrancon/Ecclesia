package com.example.ecclesia.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecclesia.R;
import com.example.ecclesia.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HistoricMenuFragment extends Fragment {

    //For the projects
    private User mUser;
    private boolean onSaved = true;

    private ViewPager2 historicViewPager;
    private HistoricMenuFragment.HistoricMenuAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historic_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();
        mUser = bundle.getParcelable("user");
        adapter = new HistoricMenuFragment.HistoricMenuAdapter(this);
        historicViewPager = view.findViewById(R.id.historic_menu_view_pager);
        historicViewPager.setAdapter(adapter);
        historicViewPager.setUserInputEnabled(false);

        TabLayout tabLayout = view.findViewById(R.id.historic_menu_tab_layout);

        new TabLayoutMediator(tabLayout, historicViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position==0)
                {
                    tab.setText("Mes favoris");
                    tab.setIcon(R.drawable.ic_favorite_selected);
                }
                if (position==1)
                {
                    tab.setText("Projets aimés");
                    tab.setIcon(R.drawable.ic_like_selected);
                }
            }
        }
        ).attach();

        ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);
                onSaved =(position==0);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                switch (state)
                {
                    case ViewPager2.SCROLL_STATE_DRAGGING :
                        onSaved =!onSaved;
                }
            }
        };
        historicViewPager.registerOnPageChangeCallback(callback);
    }

    public class HistoricMenuAdapter extends FragmentStateAdapter
    {
        public HistoricMenuAdapter(Fragment fragment) {
            super(fragment);
        }

        @Override
        public Fragment createFragment(int position) {

            Fragment fragment = new HistoricFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", mUser);
            bundle.putBoolean("onSaved", onSaved);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

}
