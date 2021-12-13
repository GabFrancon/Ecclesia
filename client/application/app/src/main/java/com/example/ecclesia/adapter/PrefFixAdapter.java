package com.example.ecclesia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecclesia.R;
import com.example.ecclesia.model.Classification;

import java.util.ArrayList;

public class PrefFixAdapter extends RecyclerView.Adapter<PrefFixAdapter.PrefFixViewHolder>
{
    private ArrayList<Classification> prefAreas;
    private Context context;

    public PrefFixAdapter(Context context, ArrayList<Classification> prefAreas)
    {
        this.context=context;
        this.prefAreas=prefAreas;
    }

    @NonNull
    @Override
    public PrefFixAdapter.PrefFixViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fix_preferences_row, parent, false);
        return new PrefFixAdapter.PrefFixViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrefFixAdapter.PrefFixViewHolder holder, int position)
    {
        Classification currentArea = prefAreas.get(position);
        holder.prefAreaName.setText(currentArea.getName());
        holder.prefAreaPicture.setImageResource(currentArea.getIcon());
    }

    @Override
    public int getItemCount()
    {
        return prefAreas.size();
    }

    public class PrefFixViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView prefAreaPicture;
        private TextView prefAreaName;
        private RelativeLayout preferencesLayout;

        public PrefFixViewHolder(@NonNull View itemView)
        {
            super(itemView);
            this.prefAreaPicture = itemView.findViewById(R.id.pref_area_picture);
            this.prefAreaName = itemView.findViewById(R.id.pref_area_name);
            this.preferencesLayout = itemView.findViewById(R.id.preferencesLayout);
        }
    }
}
