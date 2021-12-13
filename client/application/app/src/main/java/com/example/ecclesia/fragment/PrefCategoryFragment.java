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
import com.example.ecclesia.activity.MainActivity;
import com.example.ecclesia.activity.PreferencesActivity;
import com.example.ecclesia.model.Classification;
import com.example.ecclesia.model.User;

import java.util.ArrayList;

public class PrefCategoryFragment  extends Fragment {

    private RecyclerView recyclerView;
    private PreferencesAdapter preferencesAdapter;
    private Button button_category;
    private ArrayList<String> prefCategoryList;
    private boolean fromMain;
    private User mUser;
    private ArrayList<Classification> prefCategories;


    String categories[];
    int images[] = {R.drawable.ic_conference, R.drawable.ic_building, R.drawable.ic_festival,
            R.drawable.ic_entertainment, R.drawable.ic_meet, R.drawable.ic_exposition,
            R.drawable.ic_social_action, R.drawable.ic_competition, R.drawable.ic_excursion,
            R.drawable.ic_classroom, R.drawable.ic_experiment};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pref_category, container, false);

        Bundle bundle = getArguments();
        fromMain = bundle.getBoolean("from_main");
        mUser = bundle.getParcelable("user");

        if (fromMain)
        {
            prefCategories = bundle.getParcelableArrayList("pref_categories");
        }

        recyclerView = view.findViewById(R.id.recyclerViewPrefCategory);

        //On instancie les propriétés de la RecyclerView :
        //  - Le type de remplissage des données (en grille avec 3 cellules par ligne)
        //  - Le type d'animation de remplissage par défaut
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        categories = getResources().getStringArray(R.array.categories);

        //On instancie l'adapter, on lui fournit la liste de domaines et les images correspondantes.
        //Cet adapter permet de remplir le RecyclerView.
        preferencesAdapter = new PreferencesAdapter(getContext(), categories, images, prefCategories);
        button_category = view.findViewById(R.id.save_pref_category_button);
        button_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefCategoryList = new ArrayList<>();
                ArrayList<String> checkedPreferences = preferencesAdapter.getCheckedPreferences();

                for(String category : checkedPreferences) {
                    prefCategoryList.add(category);
                }
                if(checkedPreferences.size()>0)
                {
                    if (fromMain)
                    {
                        boolean success = mUser.setPreferences(null, prefCategoryList);

                        if (success) {
                            new CustomToast(getContext(), "Vos préférences ont bien été mises à jour", R.drawable.ic_baseline_favorite_24);
                            Intent intent_preferences = new Intent(getContext(), PreferencesActivity.class);
                            Bundle bundle_preferences = new Bundle();
                            bundle_preferences.putParcelable("user", mUser);
                            bundle_preferences.putBoolean("from_main", true);
                            intent_preferences.putExtras(bundle_preferences);
                            getActivity().onBackPressed();
                            startActivity(intent_preferences);
                        } else { Log.e("FAIL","une erreur est survenue lors de la mise à jour des préférences"); }
                    }
                    else
                    {
                        Bundle bundle = getArguments();
                        bundle.putStringArrayList("categories", prefCategoryList);
                        Intent mainActivity = new Intent(getContext(), MainActivity.class);
                        mainActivity.putExtras(bundle);
                        startActivity(mainActivity);
                    }

                } else
                {
                    new CustomToast(getContext(), "Vous devez sélectionner au moins une catégorie d'évènements", R.drawable.ic_baseline_warning_24);
                }
            }
        });
        recyclerView.setAdapter(preferencesAdapter);

        return view;
    }

}
