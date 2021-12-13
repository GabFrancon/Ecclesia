package com.example.ecclesia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.ecclesia.R;
import com.example.ecclesia.activity.ProjectActivity;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.User;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HorizontalRecyclerAdapter extends RecyclerView.Adapter<HorizontalRecyclerAdapter.HorizontalHolder>
{
    ArrayList<Project> projectList;
    Context context;
    User mUser;

    public HorizontalRecyclerAdapter(ArrayList<Project> projectList, Context context, User user)
    {
        this.projectList=projectList;
        this.mUser=user;
        this.context=context;
    }

    @NonNull
    @Override
    public HorizontalRecyclerAdapter.HorizontalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_project_card, parent, false);
        return new HorizontalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRecyclerAdapter.HorizontalHolder holder, int position)
    {
        int realPosition = position % projectList.size();
        Project project = projectList.get(realPosition);
        holder.title.setText(project.getTitle());
        holder.date.setText(project.getDate());
        holder.place.setText(project.getPlace());

        RequestOptions ro = new RequestOptions();
        ro.error(R.drawable.default_project).transform(new CenterCrop());
        Glide.with(context)
                .applyDefaultRequestOptions(ro)
                .load(project.getPicture())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.picture);

        holder.cardLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, ProjectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", mUser);
                bundle.putInt("position",realPosition);
                bundle.putParcelableArrayList("projects",projectList);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class HorizontalHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        ImageView picture;
        TextView date;
        TextView place;
        ConstraintLayout cardLayout;

        public HorizontalHolder(@NonNull View itemView)
        {
            super(itemView);
             title = itemView.findViewById(R.id.project_title);
             picture = itemView.findViewById(R.id.project_picture);
             date = itemView.findViewById(R.id.project_date);
             place = itemView.findViewById(R.id.project_place);
             cardLayout = itemView.findViewById(R.id.projectLayout);
        }
    }
}
