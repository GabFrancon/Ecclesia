package com.example.ecclesia.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecclesia.R;

public class SpinnerAdapter extends BaseAdapter
{
    Context context;
    int symbols[];
    String[] cityNames;
    LayoutInflater inflater;

    public SpinnerAdapter(Context applicationContext, int[] symbols, String[] cityNames)
    {
        this.context = applicationContext;
        this.symbols = symbols;
        this.cityNames = cityNames;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return symbols.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.e("DROP MENU","menu déroulant actionné");
        convertView = inflater.inflate(R.layout.custom_spinner_city_items, null);
        ImageView icon = (ImageView) convertView.findViewById(R.id.imageView);
        TextView name = (TextView) convertView.findViewById(R.id.textView);
        icon.setImageResource(symbols[position]);
        name.setText(cityNames[position]);
        return convertView;
    }
}
