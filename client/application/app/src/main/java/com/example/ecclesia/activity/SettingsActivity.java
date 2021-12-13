package com.example.ecclesia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.ecclesia.R;
import com.example.ecclesia.model.User;
import com.r0adkll.slidr.Slidr;

import static com.example.ecclesia.model.Notifications.CHANNEL_1_ID;
import static com.example.ecclesia.model.Notifications.CHANNEL_2_ID;

public class SettingsActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private ConstraintLayout emailButton;
    private ConstraintLayout passwordButton;
    private Switch notificationSwitch;
    private Switch geolocationSwitch;
    private NotificationManagerCompat notificationManager;

    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Slidr.attach(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            Bundle bundle = intent.getExtras();
            mUser = bundle.getParcelable("user");
        }

        toolbar = findViewById(R.id.toolbar);
        emailButton = findViewById(R.id.change_email_button);
        passwordButton = findViewById(R.id.change_password_button);
        geolocationSwitch = findViewById(R.id.geolocationSwitch);
        notificationSwitch = findViewById(R.id.notificationSwitch);

        geolocationSwitch.setChecked(true);

        //Toolbar with up button to return home
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationManager = NotificationManagerCompat.from(this);

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeEmailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", mUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", mUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) (findViewById(android.R.id.content))).getChildAt(0);
                String message;
                if (isChecked) message = "Notifications activées";
                else message = "Notifications désactivées";
                sendOnChannel1(viewGroup, message);
            }
        });

        geolocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) (findViewById(android.R.id.content))).getChildAt(0);
                String message;
                if (isChecked) message = "Géolocalisation activée";
                else message = "Géolocalisation désactivée";
                sendOnChannel1(viewGroup, message);
            }
        });
    }

    public void sendOnChannel1(View v, String message)
    {
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Préférences mises à jour")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);

    }

    public void sendOnChannel2(View v, String message)
    {
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_2_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Préférences mises à jour")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        notificationManager.notify(2, notification);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}