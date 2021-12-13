package com.example.ecclesia.adapter;

import android.inputmethodservice.Keyboard;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecclesia.model.RowType;

import java.util.ArrayList;
import java.util.List;

public class MultipleNotifTypesAdapter extends RecyclerView.Adapter
{
    private ArrayList<RowType> dataSet;

    public MultipleNotifTypesAdapter(ArrayList<RowType> dataSet)
    {
        this.dataSet = dataSet;
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getItemViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return ViewHolderFactory.create(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        RowType notification = dataSet.get(position);
        notification.onBindViewHolder(holder);
    }

    @Override
    public int getItemCount()
    {
        return dataSet.size();
    }
}
