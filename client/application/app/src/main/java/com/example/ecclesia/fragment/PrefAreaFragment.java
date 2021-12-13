package com.example.ecclesia.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.adapter.PreferencesAdapter;
import com.example.ecclesia.R;
import com.example.ecclesia.activity.PreferencesActivity;
import com.example.ecclesia.model.Classification;
import com.example.ecclesia.model.User;

import java.util.ArrayList;

public class PrefAreaFragment extends Fragment {

    private RecyclerView recyclerView;
    private PreferencesAdapter preferencesAdapter;
    private Button button_area;
    private ArrayList<Classification> prefAreas;
    private ArrayList<String> prefAreaList = new ArrayList<>();

    private boolean fromMain;
    private User mUser;

    String areas[];
    int images[] = {R.drawable.ic_work,
            R.drawable.ic_tech, R.drawable.ic_ecological,
            R.drawable.ic_security, R.drawable.ic_health,
            R.drawable.ic_education, R.drawable.ic_urbanism, R.drawable.ic_music,
            R.drawable.ic_drawing, R.drawable.ic_audiovisual,
            R.drawable.ic_show, R.drawable.ic_litterature, R.drawable.ic_cooking,
            R.drawable.ic_fashion, R.drawable.ic_physics,
            R.drawable.ic_nature, R.drawable.ic_history,
            R.drawable.ic_spirituality, R.drawable.ic_game, R.drawable.ic_sport};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pref_area, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPrefArea);

        Bundle bundle = getArguments();
        fromMain = bundle.getBoolean("from_main");
        mUser = bundle.getParcelable("user");

        if (fromMain)
        {
            prefAreas = bundle.getParcelableArrayList("pref_areas");
        }

        //On instancie les propriétés de la RecyclerView :
        //  - Le type de remplissage des données (en grille avec 3 cellules par ligne)
        //  - Le type d'animation de remplissage par défaut
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        areas = getResources().getStringArray(R.array.areas);

        //On instancie l'adapter, on lui fournit la liste de domaines et les images correspondantes.
        //Cet adapter permet de remplir le RecyclerView.
        preferencesAdapter = new PreferencesAdapter(getContext(), areas, images, prefAreas);
        //When click on the "Enregistrer" button for AREAS, change fragment to preferences of categories
        button_area = view.findViewById(R.id.save_pref_area_button);
        button_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ArrayList<String> checkedPreferences = preferencesAdapter.getCheckedPreferences();
                for(String area : checkedPreferences)
                {
                    prefAreaList.add(area);
                }
                if(checkedPreferences.size()>0)
                {
                    if (fromMain)
                    {
                        boolean success = mUser.setPreferences(prefAreaList, null);

                        if (success)
                        {
                            new CustomToast(getContext(), "Vos préférences ont bien été mises à jour", R.drawable.ic_baseline_favorite_24);
                            Intent intent_preferences = new Intent(getContext(), PreferencesActivity.class);
                            Bundle bundle_preferences = new Bundle();
                            bundle_preferences.putParcelable("user",mUser);
                            bundle_preferences.putBoolean("from_main",true);
                            intent_preferences.putExtras(bundle_preferences);
                            getActivity().onBackPressed();
                            startActivity(intent_preferences);
                        }
                        else { Log.e("FAIL","une erreur est survenue lors de la mise à jour des préférences"); }
                    }
                    else
                    {
                        PrefCategoryFragment prefCategoryFragment = new PrefCategoryFragment();
                        Bundle bundle = getArguments();
                        bundle.putStringArrayList("areas", prefAreaList);
                        prefCategoryFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_preferences, prefCategoryFragment).commit();
                    }
                } else {
                    new CustomToast(getContext(), "Vous devez sélectionner au moins un centre d'intérêt", R.drawable.ic_baseline_warning_24);
                }
            }
        });
        recyclerView.setAdapter(preferencesAdapter);

        return view;
    }
}
