package com.rsm.wmsstorage;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.rsm.wmsequip.R;

public class MainMenu extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final int MIN_DISTANCE = 150;
    TextView titleName, NameUser;
    RelativeLayout vuddCard, inventdCard, peremidCard, pruhidCard;
    Animation scaleUp, scaleDown, TopScreanLogin, BottomScreanLogin, LeftScreanWiFi, RightScreanBattery;
    NavigationView nav_view;
    private DrawerLayout drawerLayout;
    private GestureDetectorCompat gestureDetector;
    private float x1, x2, y1, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.main_manu);
        if (actionBar != null) {
            // Викликати методи ActionBar
            actionBar.hide();
        }

        this.gestureDetector = new GestureDetectorCompat(MainMenu.this, this);


        nav_view = findViewById(R.id.nav_view);
        vuddCard = findViewById(R.id.vuddCard);
        pruhidCard = findViewById(R.id.pruhidCard);
        inventdCard = findViewById(R.id.inventdCard);
        peremidCard = findViewById(R.id.peremidCard);
        titleName = findViewById(R.id.titleName);

        //ADD Animation
        TopScreanLogin = AnimationUtils.loadAnimation(this, R.anim.top_screan_login);
        BottomScreanLogin = AnimationUtils.loadAnimation(this, R.anim.bottom_screan_login);
        LeftScreanWiFi = AnimationUtils.loadAnimation(this, R.anim.left_image_wifi);
        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        LeftScreanWiFi = AnimationUtils.loadAnimation(this, R.anim.left_image_wifi);
        RightScreanBattery = AnimationUtils.loadAnimation(this, R.anim.right_image_battery);

        //Animation
        titleName.startAnimation(TopScreanLogin);
        pruhidCard.startAnimation(LeftScreanWiFi);
        peremidCard.setAnimation(RightScreanBattery);
        inventdCard.startAnimation(LeftScreanWiFi);
        vuddCard.setAnimation(RightScreanBattery);
         pruhidCard = findViewById(R.id.pruhidCard);

    }
    public void GoPrih(View view){
        Intent intent = new Intent(this, PrihodList.class);
        startActivity(intent);
    }
    public void GOinventor(View view){
        Intent intent = new Intent(this, Inventory.class);
        startActivity(intent);
    }
    public void GOPeremich(View view){
        Intent intent = new Intent(this, Peremich.class);
        startActivity(intent);
    }
    public void GOVudacha(View view){
        Intent intent = new Intent(this, Vudacha.class);
        startActivity(intent);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ViewGroup.LayoutParams params = nav_view.getLayoutParams();
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
                //get value for horizontal
                float valueX = x2 - x1;
                //get value for vertical
                float valueY = y2 - y1;
                if (Math.abs(valueX) > MIN_DISTANCE) {
                    if (x2 > x1) {

                        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, getResources().getDisplayMetrics());
                        nav_view.setLayoutParams(params);
                        TextView headerTitle = (TextView) nav_view.getHeaderView(0).findViewById(R.id.NameUser);
                        headerTitle.setText(DB.getUser[0][1]);
                    } else {
                        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());
                        nav_view.setLayoutParams(params);

                    }
                } else if (Math.abs(valueY) > MIN_DISTANCE) {
                    if (y2 > y1) {

                    } else {

                    }
                }

        }


        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
