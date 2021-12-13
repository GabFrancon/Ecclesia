package com.example.ecclesia.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.ecclesia.R;
import com.example.ecclesia.activity.ProfileActivity;
import com.example.ecclesia.adapter.ViewHolderFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class FriendRequestNotif implements RowType
{
    private User applicant;
    private String timeStamping;
    private Context context;
    private User acceptor;
    private boolean isNew;

    public FriendRequestNotif(Context context, User acceptor, User applicant, String timeStamp, boolean isNew)
    {
        this.context=context;
        this.acceptor=acceptor;
        this.applicant=applicant;
        Timestamp ts = Timestamp.valueOf(timeStamp);
        this.timeStamping= (String) new TimeHandler().getRelativeTime(ts.getTime());
    }

    public FriendRequestNotif(Context context, User acceptor, JSONObject json) throws JSONException
    {
        this(context,
                acceptor,
                new User(json, false),
                json.getString("time_stamping"),
                false);

        String aNew = json.getString("new");
        isNew = aNew.equals("1");
        applicant.setID(json.getString("sender"));
    }

    public User getApplicant()
    {
        return applicant;
    }
    public String getTimeStamping(){return timeStamping;}

    public View.OnClickListener getOnClickListener() {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (isNew)
                {
                    ServerRequest request = new ServerRequest();
                    request.setNotificationRead(acceptor.getToken(), timeStamping, applicant.getID(), 0);
                    while (!request.isCompleted()) {}
                    JSONObject result = request.getResult();
                    try {
                        boolean success = (result.getBoolean("success"));
                        if (!success) Log.e("FAIL", result.getString("message"));
                        isNew=false;
                    } catch (JSONException e) {e.printStackTrace();}
                }

                Intent intent = new Intent(context, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", acceptor);
                bundle.putString("secondUserID",applicant.getID());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        };
    }
    public int getItemViewType()
    {
        return RowType.REQUEST_ROW_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder)
    {
        ViewHolderFactory.NotifsViewHolder holder = (ViewHolderFactory.NotifsViewHolder) viewHolder;

        Log.e("IS NEW", String.valueOf(isNew));
        if (isNew) holder.cardView.setCardBackgroundColor(Color.parseColor("#FAEDE5"));
        holder.notifTexts.setText(applicant.getName()+" vous a demandé en ami");
        holder.notifTimeStamping.setText(timeStamping);

        RequestOptions ro = new RequestOptions();
        ro.error(R.drawable.default_profile).circleCrop();
        Glide.with(context)
                .load(applicant.getProfilePicture())
                .apply(ro)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.notifImages);

        View.OnClickListener listener = this.getOnClickListener();
        holder.notifLayout.setOnClickListener(listener);
    }
}
