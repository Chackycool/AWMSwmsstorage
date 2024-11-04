package com.rsm.wmsstorage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.rsm.wmsequip.R;

import www.sanju.motiontoast.MotionToast;

public class MainActivity extends AppCompatActivity {
    Button buttonPlay;
    TextView StrenghtWiFi,nameFirst,nameLast,StrenghtBattery,NameUser;
    ImageButton showPass,imageButtonWifi,ImageButtonBattery;
    ImageView ImageInLogin;
    EditText editTextNumberPassword2;
    Animation scaleUp,scaleDown,TopScreanLogin,BottomScreanLogin,LeftScreanWiFi,RightScreanBattery;
    private Handler handler;
    private Runnable networkStatusRunnable;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Викликати методи ActionBar
            actionBar.hide();
        }
        setContentView(R.layout.activity_main);
        TopScreanLogin=AnimationUtils.loadAnimation(this,R.anim.top_screan_login);
        BottomScreanLogin=AnimationUtils.loadAnimation(this,R.anim.bottom_screan_login);
        LeftScreanWiFi=AnimationUtils.loadAnimation(this,R.anim.left_image_wifi);
        buttonPlay=findViewById(R.id.login);
        StrenghtBattery=findViewById(R.id.textView5);
        imageButtonWifi=findViewById(R.id.WIfi);
        ImageButtonBattery=findViewById(R.id.Battery);
        ImageInLogin=findViewById(R.id.imageView);
        StrenghtWiFi= findViewById(R.id.textView4);
        nameFirst=findViewById(R.id.textView2);
        nameLast=findViewById(R.id.textView3);
        scaleUp= AnimationUtils.loadAnimation(this,R.anim.scale_up);
        scaleDown= AnimationUtils.loadAnimation(this,R.anim.scale_down);
        LeftScreanWiFi=AnimationUtils.loadAnimation(this,R.anim.left_image_wifi);
        RightScreanBattery=AnimationUtils.loadAnimation(this,R.anim.right_image_battery);
        showPass=findViewById(R.id.passeye);
        editTextNumberPassword2 = findViewById(R.id.editTextNumberPassword2);
        StrenghtWiFi.setText("-");
        ImageInLogin.setAnimation(TopScreanLogin);
        buttonPlay.startAnimation(BottomScreanLogin);
        imageButtonWifi.startAnimation(LeftScreanWiFi);
        StrenghtWiFi.startAnimation(LeftScreanWiFi);
        ImageButtonBattery.startAnimation(RightScreanBattery);
        StrenghtBattery.startAnimation(RightScreanBattery);

        handler = new Handler();

        networkStatusRunnable = new Runnable() {
            @Override
            public void run() {
                int wifiStrength = getWifiStrength();
                StrenghtWiFi.setText(wifiStrength + "%");
                handler.postDelayed(this, 1000); // Оновлюємо стан мережі кожну секунду
            }
        };

        handler.post(networkStatusRunnable);
        // Створення та реєстрація BroadcastReceiver для моніторингу рівня заряду батареї
        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Отримуємо рівень заряду батареї
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                if (level != -1 && scale != -1) {
                    // Розраховуємо відсоток рівня заряду
                    int batteryPercent = (int) ((level / (float) scale) * 100);
                    // Вставляємо значення в TextView
                    StrenghtBattery.setText(batteryPercent + "%");
                }
            }
        };
        // Реєстрація BroadcastReceiver для моніторингу змін рівня заряду батареї
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPasswordVisible = !isPasswordVisible;

                if (isPasswordVisible) {
                    // Змінюємо картинку на приховування паролю
                    showPass.setImageResource(R.drawable.eye);

                    // Встановлюємо видимий текст у полі EditText
                    editTextNumberPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Змінюємо картинку на відображення паролю
                    showPass.setImageResource(R.drawable.eyehide);

                    // Приховуємо текст паролю
                    editTextNumberPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Оновлюємо поле введеного тексту
                editTextNumberPassword2.setSelection(editTextNumberPassword2.getText().length());
            }
        });

    }

    public void login(View view){

        if(editTextNumberPassword2.getText().length()>0){
            if(DB.Connect()){
                DB.curSQL="SELECT [dbo].[BookUsers].[UserID], [Username], [Permission],[dbo].[UserImages].[ImagePath],[dbo].[UserImages].[ImageName]\n" +
                        "FROM [dbo].[BookUsers]\n" +
                        "LEFT OUTER JOIN [dbo].[UserImages] ON [dbo].[UserImages].[UserID] = [dbo].[BookUsers].[UserID]\n" +
                        "WHERE [UserPasswod] LIKE '"+editTextNumberPassword2.getText().toString()+"'";
                DB.getUser= DB.executeSQLQuery(DB.curSQL);
                if(DB.getUser != null && DB.getUser.length > 0 && DB.getUser[0] != null && DB.getUser[0].length > 0 && DB.getUser[0][0] != null) {
                        MotionToast.Companion.createColorToast(MainActivity.this,"Успішно",
                                MotionToast.Companion.getTOAST_SUCCESS(),
                                MotionToast.Companion.getGRAVITY_TOP(),
                                MotionToast.Companion.getSHORT_DURATION(),
                                ResourcesCompat.getFont(MainActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));


                    Intent intent = new Intent(this, MainMenu.class);
                    startActivity(intent);

                }
                else {
                    MotionToast.Companion.createColorToast(MainActivity.this,"Не вірний пароль",
                            MotionToast.Companion.getTOAST_ERROR(),
                            MotionToast.Companion.getGRAVITY_TOP(),
                            MotionToast.Companion.getSHORT_DURATION(),
                            ResourcesCompat.getFont(MainActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    editTextNumberPassword2.setText("");
                }
            }else {

            }
        }else{
            MotionToast.Companion.createColorToast(MainActivity.this,"Введіть пароль!",
                    MotionToast.Companion.getTOAST_ERROR(),
                    MotionToast.Companion.getGRAVITY_TOP(),
                    MotionToast.Companion.getSHORT_DURATION(),
                    ResourcesCompat.getFont(MainActivity.this, www.sanju.motiontoast.R.font.helvetica_regular));
            editTextNumberPassword2.setText("");

        }

    }


    private int getWifiStrength() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null) {
            try {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int rssi = wifiInfo.getRssi();

                // Перетворюємо сила сигналу в відсотки (припустимо, що максимальний рівень сигналу -0 дБм)
                int wifiStrength = 100 + rssi;

                // Обмежуємо значення в діапазоні від 0 до 100
                wifiStrength = Math.max(0, Math.min(100, wifiStrength));
                return wifiStrength;
            }catch (Exception ex){
                Toast.makeText(MainActivity.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Немає доступу до Wi-Fi
            return -1;
        }
        return 0;
    }


    @Override
    protected void onRestart() {
      //  DB.Connect();
        super.onRestart();
    }

}