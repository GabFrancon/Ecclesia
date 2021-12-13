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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.ecclesia.activity.ProfileActivity;
import com.example.ecclesia.R;
import com.example.ecclesia.model.User;

import java.util.ArrayList;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> {

    //For the projects
    private ArrayList<User> friendList;

    //for the user
    private User mUser;

    Context context;

    public FriendListAdapter(Context context, ArrayList<User> friendList, User user)
    {
        this.context = context;
        this.friendList = friendList;
        this.mUser=user;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contacts_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        User user = friendList.get(position);
        holder.contactName.setText(user.getName());

        RequestOptions ro = new RequestOptions();
        ro.error(R.drawable.default_profile).circleCrop();
        Glide.with(context)
                .load(user.getProfilePicture())
                .apply(ro)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.contactPicture);


        holder.contactLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("secondUserID", user.getID());
                bundle.putParcelable("user", mUser);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return friendList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView contactName;
        ImageView contactPicture;
        ConstraintLayout contactLayout;


        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            this.contactName = itemView.findViewById(R.id.name);
            this.contactPicture = itemView.findViewById(R.id.profile_picture);
            this.contactLayout=itemView.findViewById(R.id.contact_layout);
        }
    }
}
