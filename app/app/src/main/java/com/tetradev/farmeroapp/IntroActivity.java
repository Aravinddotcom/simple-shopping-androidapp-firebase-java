package com.tetradev.farmeroapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 4000;

    Animation topAnim;

    ImageView img;
    TextView text1,text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);

        img= findViewById(R.id.img);
        text1= findViewById(R.id.text1);
        text2= findViewById(R.id.text2);

        img.setAnimation(topAnim);
        text1.setAnimation(topAnim);
        text2.setAnimation(topAnim);

        img.animate().translationY(1500).setDuration(2000).setStartDelay(4000);
        text1.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);
        text2.animate().translationY(-1600).setDuration(1000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}