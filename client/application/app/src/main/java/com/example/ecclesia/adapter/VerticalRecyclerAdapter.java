package com.example.ecclesia.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecclesia.R;
import com.example.ecclesia.model.Classification;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.ProjectCollection;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VerticalRecyclerAdapter extends RecyclerView.Adapter<VerticalRecyclerAdapter.VerticalHolder>
{
    ArrayList<ProjectCollection> projectCollections;
    Context context;
    User mUser;

    public VerticalRecyclerAdapter(ArrayList<ProjectCollection> projectCollections, Context context, User user)
    {
        this.projectCollections=projectCollections;
        this.context=context;
        this.mUser=user;
    }

    @NonNull
    @Override
    public VerticalRecyclerAdapter.VerticalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.discover_row_layout, parent, false);
        return new VerticalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalRecyclerAdapter.VerticalHolder holder, int position)
    {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.projects.setLayoutManager(layoutManager);
        holder.projects.setHasFixedSize(true);
        layoutManager.scrollToPosition(Integer.MAX_VALUE/2);

        ProjectCollection projectCollection = projectCollections.get(position);
        ArrayList<Project> projectList = projectCollection.getProjectList();
        Classification collection = projectCollection.getCollection();

        holder.itemName.setText(collection.getName());
        holder.itemPicture.setImageResource(collection.getIcon());

        HorizontalRecyclerAdapter horizontalAdapter = new HorizontalRecyclerAdapter(projectList, context, mUser);
        holder.projects.setAdapter(horizontalAdapter);
        horizontalAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return projectCollections.size();
    }

    public class VerticalHolder extends RecyclerView.ViewHolder
    {
        ImageView itemPicture;
        TextView itemName;
        RecyclerView projects;
        CardView cardView;

        public VerticalHolder(@NonNull View itemView) {
            super(itemView);
            itemPicture = itemView.findViewById(R.id.item_picture);
            itemName = itemView.findViewById(R.id.item_name);
            projects = itemView.findViewById(R.id.projects);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
