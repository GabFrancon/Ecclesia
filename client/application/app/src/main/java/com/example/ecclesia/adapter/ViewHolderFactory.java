package com.example.ecclesia.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecclesia.R;
import com.example.ecclesia.model.RowType;

public class ViewHolderFactory {

    public static class ProjectNotifsViewHolder extends RecyclerView.ViewHolder
    {

        public TextView notifTexts;
        public TextView notifTimeStamping;
        public ImageView projectNotifImages;
        public ImageView senderNotifImage;
        public ConstraintLayout notifLayout;
        public CardView cardView;

        public ProjectNotifsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            this.notifTexts = itemView.findViewById(R.id.notif_txt);
            this.notifTimeStamping = itemView.findViewById(R.id.date_txt);
            this.projectNotifImages = itemView.findViewById(R.id.project_picture_notif);
            this.senderNotifImage = itemView.findViewById(R.id.sender_picture);
            this.notifLayout = itemView.findViewById(R.id.notifsLayout);
            this.cardView = itemView.findViewById(R.id.card_sharing_notif);
        }
    }

    public static class NotifsViewHolder extends RecyclerView.ViewHolder
    {

        public TextView notifTexts;
        public TextView notifTimeStamping;
        public ImageView notifImages;
        public ConstraintLayout notifLayout;
        public CardView cardView;

        public NotifsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.notifTexts = itemView.findViewById(R.id.notif_txt);
            this.notifTimeStamping = itemView.findViewById(R.id.date_txt);
            this.notifImages = itemView.findViewById(R.id.notif_image);
            this.notifLayout = itemView.findViewById(R.id.notifsLayout);
            this.cardView = itemView.findViewById(R.id.card_request_notif);
        }
    }

    public static RecyclerView.ViewHolder create(ViewGroup parent, int viewType) {

        switch (viewType) {
            case RowType.REQUEST_ROW_TYPE:
                View requestTypeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_notifications_row, parent, false);
                return new ViewHolderFactory.NotifsViewHolder(requestTypeView);

            case RowType.SHARING_ROW_TYPE:
                View sharingTypeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sharing_notifications_row, parent, false);
                return new ViewHolderFactory.ProjectNotifsViewHolder(sharingTypeView);

            default:
                return null;
        }

    }
}