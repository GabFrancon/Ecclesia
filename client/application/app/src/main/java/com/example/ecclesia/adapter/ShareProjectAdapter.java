package com.example.ecclesia.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecclesia.R;
import com.example.ecclesia.model.User;

import java.util.ArrayList;

public class ShareProjectAdapter extends RecyclerView.Adapter<ShareProjectAdapter.ShareViewHolder>{

    private ArrayList<User> friends;

    //for the user ID;
    private User mUser;

    Context context;

    //for the list of friends the user wants to share the project with
    private ArrayList<String> checkedFriends = new ArrayList<String>();
    private ArrayList<User> checkedUsers = new ArrayList<User>();

    public ShareProjectAdapter(Context context, ArrayList<User> friends, User user)
    {
        this.context = context;
        this.friends = friends;
        this.mUser = user;
    }

    public ArrayList<User> getCheckedFriends()
    {
        for(String friend : checkedFriends) {
            User mFriend = new User();
            mFriend.setID(friend);
            checkedUsers.add(mFriend);
        }
        return checkedUsers;
    }

    /** sets the holder that contains the row dynamically */
    @NonNull
    @Override
    public ShareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.share_project_to_friend_row, parent, false);
        return new ShareProjectAdapter.ShareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareProjectAdapter.ShareViewHolder holder, int position)
    {
        User friend = friends.get(position);
            holder.bindDataWithViewHolder(position);
            holder.userNameShare.setText(friend.getName());
            String picture = friend.getProfilePicture();

            if (!picture.equals("null") && !picture.equals(""))
            {
                Glide.with(context).load(picture).into(holder.userPictureShare);
            }

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClicked(View v, int position) {
                    CheckBox chk = (CheckBox)v;

                    //Check if it is checked or not
                    if(chk.isChecked()){
                        checkedFriends.add(friend.getID());
                    } else if(!chk.isChecked()){
                        checkedFriends.remove(friend.getID());
                    }
                }
            });
        }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ShareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView userNameShare;
        ImageView userPictureShare;
        ConstraintLayout userLayoutShare;
        CheckBox checkBoxShare;

        private ItemClickListener itemClickListener;

        public ShareViewHolder(@NonNull View itemView) {
            super(itemView);
            this.userNameShare = itemView.findViewById(R.id.share_to_user_name);
            this.userPictureShare = itemView.findViewById(R.id.share_to_user_picture);
            this.userLayoutShare = itemView.findViewById(R.id.share_project_layout);
            this.checkBoxShare = itemView.findViewById(R.id.checkBox_share_project);

            this.checkBoxShare.setOnClickListener(this);
            this.checkBoxShare.setChecked(false);
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClicked(v, getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        //checkbox matches correct data
        public void bindDataWithViewHolder(int position) {
            this.checkBoxShare.setChecked(checkedFriends.contains(friends.get(position).getID()));
        }

    }
}
