package com.example.ecclesia.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecclesia.R;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {

    // Timer d'écoulement du chargement
    private Timer timerBar;

    private int cpt = 0;

    private TextView viewBar;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Permet de récupérer le titre dans le splash
        viewBar = findViewById(R.id.viewBar);
        logo = findViewById(R.id.imgLogo);

        if(viewBar == null){
            Log.e("WelcomeActivity", "la variable viewBar est null");
            Log.e("WelcomeActivity", "R.id.viewBar : " + R.id.viewBar);
            while(viewBar == null){
                logo = findViewById(R.id.imgLogo);
                viewBar = findViewById(R.id.viewBar);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }




        // Déclaration de l'animation
        Animation fade;
        fade = AnimationUtils.loadAnimation(this,R.anim.fade_in);

        //Application de l'animation sur le texte

        logo.setAnimation(fade);
        viewBar.setAnimation(fade);


                // Ecoulement du timer
        final long period = 15;
        timerBar = new Timer();
        timerBar.schedule(new TimerTask() {
            @Override
            public void run() {
                if (cpt < 100) {
                    cpt++;
                } else {
                    timerBar.cancel();
                    Intent loginActivity = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(loginActivity);
                    finish();
                }
            }
        }, 0, period);

    }
}