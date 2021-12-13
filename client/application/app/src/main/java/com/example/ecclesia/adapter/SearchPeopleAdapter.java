package com.example.ecclesia.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class SearchPeopleAdapter extends RecyclerView.Adapter<SearchPeopleAdapter.NewViewHolder> {

    //For the projects
    private ArrayList<User> userList;

    //for the user ID;
    private User mUser;

    Context context;

    public SearchPeopleAdapter(Context context, ArrayList<User> userList, User user)
    {
        this.context = context;
        this.userList = userList;
        this.mUser = user;
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.new_contacts_list_row, parent, false);
        return new NewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int position)
    {
        User user = userList.get(position);
        holder.userName.setText(user.getName());

        RequestOptions ro = new RequestOptions();
        ro.error(R.drawable.default_profile).circleCrop();
        Glide.with(context)
                .load(user.getProfilePicture())
                .apply(ro)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.userPicture);

        holder.userLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
    public int getItemCount() {
        return userList.size();
    }

    public class NewViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        ImageView userPicture;
        ConstraintLayout userLayout;


        public NewViewHolder(@NonNull View itemView) {
            super(itemView);
            this.userName = itemView.findViewById(R.id.user_name);
            this.userPicture = itemView.findViewById(R.id.user_picture);
            this.userLayout = itemView.findViewById(R.id.user_layout);

        }
    }
}