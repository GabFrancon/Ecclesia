package com.example.ecclesia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.ecclesia.model.Classification;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.TimeHandler;
import com.example.ecclesia.model.User;

import java.util.ArrayList;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectsViewHolder>
{
    private Context context;
    private ArrayList<Project> projects;
    private User mUser;

    public ProjectListAdapter(Context context, ArrayList<Project> projects, User user)
    {
        this.context = context;
        this.projects=projects;
        this.mUser = user;
    }

    @NonNull
    @Override
    public ProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.project_recycler_row, parent, false);
        return new ProjectsViewHolder(view);
    }

    /** Sets the holder that contains the row dynamically */
    @Override
    public void onBindViewHolder(@NonNull ProjectsViewHolder holder, int position)
    {
        Project project = projects.get(position);
        holder.projectTitle.setText(project.getTitle());
        holder.projectDate.setText(project.getDate());
        holder.projectPlace.setText(project.getPlace());


        RequestOptions ro = new RequestOptions();
        ro.error(R.drawable.default_project).transforms(new CenterCrop());
        Glide.with(context)
                .applyDefaultRequestOptions(ro)
                .load(project.getPicture())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.projectPicture);

        Classification area = project.getAreas().get(0);
        Classification category = project.getCategories().get(0);
        holder.projectArea.setText(area.getName());
        holder.projectAreaPicture.setImageResource(area.getIcon());
        holder.projectCategory.setText(category.getName());
        holder.projectCategoryPicture.setImageResource(category.getIcon());
        holder.projectLikeNumber.setText(String.valueOf(project.getLikes()));

        /** When clicks on a project, a new page appears (ProjectActivity)
         * collecting projectId, title, summary, image, url in a bundle to pass to ProjectActivity */
        holder.projectLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, ProjectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", mUser);
                bundle.putInt("position",position);
                bundle.putParcelableArrayList("projects",projects);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }


    /**
     * Class ProjectsViewHolder and its constructor, attributes are elements that are shown in an item
     */
    public class ProjectsViewHolder extends RecyclerView.ViewHolder
    {

        TextView projectTitle, projectDate, projectPlace, projectArea, projectCategory, projectLikeNumber;
        ImageView projectPicture, projectAreaPicture, projectCategoryPicture;
        ConstraintLayout projectLayout;

        public ProjectsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            this.projectTitle = itemView.findViewById(R.id.recommanded_projects_txt);
            this.projectDate = itemView.findViewById(R.id.date_txt);
            this.projectPlace = itemView.findViewById(R.id.place_txt);
            this.projectArea = itemView.findViewById(R.id.area_of_project);
            this.projectCategory = itemView.findViewById(R.id.category_of_project);
            this.projectLikeNumber = itemView.findViewById(R.id.number_of_likes);
            this.projectAreaPicture = itemView.findViewById(R.id.area_icon);
            this.projectCategoryPicture = itemView.findViewById(R.id.category_icon);
            this.projectPicture = itemView.findViewById(R.id.project_image);
            this.projectLayout = itemView.findViewById(R.id.projectLayout);
        }
    }
}
