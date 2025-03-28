package itstep.learning.android_213;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById( R.id.main_btn_calc).setOnClickListener(this::onButtonCalc);
        findViewById( R.id.main_btn_game).setOnClickListener(this::onButtonGame);
        findViewById( R.id.main_btn_anim).setOnClickListener(this::onButtonAnim);
        findViewById( R.id.main_btn_rate).setOnClickListener(this::onButtonRate);
        findViewById( R.id.main_btn_chat).setOnClickListener(this::onButtonChat);
    }

    private void onButtonCalc(View view){
        startActivity(new Intent(MainActivity.this, CalcActivity.class));
    }
    private void onButtonGame(View view){
        startActivity(new Intent(MainActivity.this, GameActivity.class));
    }
    private void onButtonAnim(View view){
        startActivity(new Intent(MainActivity.this, AnimActivity.class));
    }
    private void onButtonRate(View view){
        startActivity(new Intent(MainActivity.this, RateActivity.class));
    }
    private void onButtonChat(View view){
        startActivity(new Intent(MainActivity.this, ChatActivity.class));
    }

}