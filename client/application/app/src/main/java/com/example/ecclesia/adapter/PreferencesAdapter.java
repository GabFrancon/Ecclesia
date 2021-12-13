package com.example.ecclesia.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecclesia.R;
import com.example.ecclesia.model.Classification;

import java.util.ArrayList;

public class PreferencesAdapter extends RecyclerView.Adapter<PreferencesAdapter.PreferencesViewHolder> {

    private String data[];
    private int images[];
    private Context context;

    //for the list of preferences of the user
    //private User user;
    ArrayList<String> checkedPreferences = new ArrayList<String>();

    public PreferencesAdapter(Context context, String data[], int images[], ArrayList<Classification> alreadyChecked)
    {
        this.context = context;
        this.data = data;
        this.images = images;

        if (alreadyChecked != null)
        {
            for (Classification area : alreadyChecked)
            {
                checkedPreferences.add(area.getName());
            }
        }
    }

    public ArrayList<String> getCheckedPreferences()
    {
        return checkedPreferences;
    }

    /** sets the holder that contains the row dynamically */
    @NonNull
    @Override
    public PreferencesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.preferences_row, parent, false);
        return new PreferencesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferencesViewHolder holder, int position)
    {
        holder.bindDataWithViewHolder(position);
        holder.checkBox.setText(data[position]);
        holder.preferencesImages.setImageResource(images[position]);


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClicked(View v, int position) {
                CheckBox chk = (CheckBox)v;

                //Check if it is checked or not
                if(chk.isChecked()){
                    checkedPreferences.add(data[position]);
                } else if(!chk.isChecked()){
                    holder.preferencesLayout.setFocusableInTouchMode(false);
                    checkedPreferences.remove(data[position]);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class PreferencesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        private CheckBox checkBox;
        private ImageView preferencesImages;
        private LinearLayout preferencesLayout;

        private ItemClickListener itemClickListener;

        public PreferencesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.pref_checkbox);
            this.preferencesImages = itemView.findViewById(R.id.preference_image);
            this.preferencesLayout = itemView.findViewById(R.id.prefLayout);

            this.checkBox.setOnClickListener(this);
            this.checkBox.setChecked(false);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClicked(v, getLayoutPosition());
        }

        //checkbox matches correct data (image and textview of checkbox)
        public void bindDataWithViewHolder(int position)
        {
            this.checkBox.setChecked(checkedPreferences.contains(data[position]));
        }
    }
}
