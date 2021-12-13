package com.example.ecclesia.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecclesia.R;


public class CustomToast {

    private Context context;

    public CustomToast (Context context, String text, int icon)
    {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast, null);

        ImageView iconToast = layout.findViewById(R.id.toast_image);
        iconToast.setImageResource(icon);
        TextView textViewToast = layout.findViewById(R.id.toast_text);
        textViewToast.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }


}
