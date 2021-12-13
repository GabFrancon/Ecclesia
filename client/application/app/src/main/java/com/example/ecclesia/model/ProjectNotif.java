package com.example.ecclesia.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.ecclesia.R;
import com.example.ecclesia.activity.ProfileActivity;
import com.example.ecclesia.activity.ProjectActivity;
import com.example.ecclesia.adapter.ViewHolderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

public class ProjectNotif implements RowType
{
    private Project sharedProject;
    private User sender;
    private String timeStamping;
    private Context context;
    private User acceptor;
    private boolean isNew;

    public ProjectNotif(Context context, User acceptor, Project sharedProject, User sender, String timeStamp, boolean isNew)
    {
        this.context=context;
        this.acceptor=acceptor;
        this.sharedProject=sharedProject;
        this.sender=sender;
        this.timeStamping = timeStamp;
        this.isNew=isNew;
    }

    public String getRelativeTimeStamping()
    {
        Timestamp ts = Timestamp.valueOf(timeStamping);
        String time = (String) new TimeHandler().getRelativeTime(ts.getTime());
        return time;
    }

    public ProjectNotif(Context context, User acceptor, JSONObject json) throws JSONException
    {
        this(context,
                acceptor,
                new Project(json),
                new User(json, false),
                json.getString("time_stamping"),
                json.getString("new").equals("1"));

        sender.setID(json.getString("sender"));
    }

    public Project getSharedProject()
    {
        return sharedProject;
    }
    public User getSender()
    {
        return sender;
    }
    public String getTimeStamping(){return timeStamping;}
    public boolean isNew(){return isNew;}

    public void setRead(){isNew=false;}

    public View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (isNew)
                {
                    ServerRequest request = new ServerRequest();
                    request.setNotificationRead(acceptor.getToken(), timeStamping, sender.getID(), 1);
                    while (!request.isCompleted()) {}
                    JSONObject result = request.getResult();
                    try {
                        boolean success = (result.getBoolean("success"));
                        if (!success) Log.e("FAIL", result.getString("message"));
                        isNew=false;
                    } catch (JSONException e) {e.printStackTrace();}
                }
                Intent intent = new Intent(context, ProjectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", acceptor);
                ArrayList<Project> projectArrayList= new ArrayList<>();
                projectArrayList.add(sharedProject);
                bundle.putParcelableArrayList("projects",projectArrayList);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        };
    }
    public int getItemViewType()
    {
        return RowType.SHARING_ROW_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder)
    {
        ViewHolderFactory.ProjectNotifsViewHolder holder = (ViewHolderFactory.ProjectNotifsViewHolder) viewHolder;
        Log.e("NEW", String.valueOf(isNew));
        if (isNew) holder.cardView.setCardBackgroundColor(Color.parseColor("#FAEDE5"));
        holder.notifTexts.setText(sender.getName()+" vous a partagé un projet : \""+sharedProject.getTitle()+"\"");
        holder.notifTimeStamping.setText(this.getRelativeTimeStamping());

        RequestOptions roProject = new RequestOptions();
        roProject.error(R.drawable.default_project).transform(new RoundedCorners(25));
        Glide.with(context)
                .load(sharedProject.getPicture())
                .apply(roProject)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.projectNotifImages);

        RequestOptions roSender = new RequestOptions();
        roSender.error(R.drawable.default_profile).circleCrop();
        Glide.with(context)
                .load(sender.getProfilePicture())
                .apply(roSender)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.senderNotifImage);

        View.OnClickListener listener = this.getOnClickListener();
        holder.notifLayout.setOnClickListener(listener);
    }
}
