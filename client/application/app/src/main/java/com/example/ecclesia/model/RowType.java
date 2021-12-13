package com.example.ecclesia.model;

import androidx.recyclerview.widget.RecyclerView;

public interface RowType
{
    int SHARING_ROW_TYPE = 0;
    int REQUEST_ROW_TYPE = 1;

    int getItemViewType();

    void onBindViewHolder(RecyclerView.ViewHolder viewHolder);
}
