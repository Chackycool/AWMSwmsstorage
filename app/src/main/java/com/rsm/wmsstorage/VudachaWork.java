package com.rsm.wmsstorage;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.rsm.wmsequip.R;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import www.sanju.motiontoast.MotionToast;

public class VudachaWork extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private ArrayList<Integer> selectedDocumentNumbers = new ArrayList<>();
    private Set<String> uniqueFailedSCEntries = new HashSet<>();
    private boolean allRecordsSuccessfullyInserted = true;
    private StringBuilder failedSCList = new StringBuilder();
    public VudachaBodyData vudachaBodyData;
    private static final int MIN_DISTANCE = 150;
    private RecyclerView recyclerView;
    ArrayList<VudachaBodyData> list;
    public VudachaBodyData selectedVudachaBodyData;
    List<VudachaBodyData> localList = new ArrayList<>();
    NavigationView nav_view;
    MyAdapterVudachaBody myAdapterVudachaBody;
    TextView ProdName,ProdEd,Part,Calibr,Ton,SumPoz;
    EditText Plan,Fact,SC,Cellname;
    private float x1, x2, y1, y2;
    private GestureDetectorCompat gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        setContentView(R.layout.activity_vudacha_work);
        this.gestureDetector = new GestureDetectorCompat(VudachaWork.this, this);
        nav_view=findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recyclerView);
        ProdName = findViewById(R.id.ProdName);
        ProdEd = findViewById(R.id.ProdEd);
        Part = findViewById(R.id.Part);
        Calibr = findViewById(R.id.Calibr);
        Cellname=findViewById(R.id.Cellname);
        SC=findViewById(R.id.SC);
        Ton= findViewById(R.id.Ton);
        Plan = findViewById(R.id.Plan);
        SumPoz=findViewById(R.id.SumPoz);
        Fact = findViewById(R.id.Fact);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        ArrayList<String> selectedDocumentNumbers = getIntent().getStringArrayListExtra("selectedDocumentNumbers");
        list = new ArrayList<>();
        String inClause = getInClause(selectedDocumentNumbers);
        if (DB.Connect()) {
            DB.curSQL = "Select [docNumber],[SC],[ProdName],[ProdEd],[Part],[Calibr],[Ton],[Plans],[Facts],[CellName],[Dttm],[UserChange],[DocStatus] from [dbo].[DocVudachaChield]  where [docNumber] IN("+  inClause +") AND [DocStatus] = 0  order by SC asc";
            List<VudachaBodyData> vudachaBodyDataList = DB.getVudachaBodyList(DB.curSQL);
            list.addAll(vudachaBodyDataList);
            SumPoz.setText("Найменування "+"("+list.size()+")");
        }
        myAdapterVudachaBody = new MyAdapterVudachaBody(this, list, recyclerView);
        recyclerView.setAdapter(myAdapterVudachaBody);

        MyAdapterVudachaBody adapter = new MyAdapterVudachaBody(this, list,recyclerView);
        adapter.setItemSelectedListener(new MyAdapterVudachaBody.OnItemSelectedListener() {
            @Override
            public void onItemSelected(VudachaBodyData selectedVudachaBody) {
                // Оновіть значення EditText з вибраними даними
                VudachaWork.this.selectedVudachaBodyData = selectedVudachaBody;
                ProdName.setText(String.valueOf(selectedVudachaBody.getProdName()));
                ProdEd.setText(String.valueOf(selectedVudachaBody.getProdEd()));
                Part.setText("Партія ["+String.valueOf(selectedVudachaBody.getPart())+"]");
                Calibr.setText("Калібр ["+String.valueOf(selectedVudachaBody.getCalibr())+"]");
                Ton.setText("Тон ["+String.valueOf(selectedVudachaBody.getTon())+"]");

                Plan.setText(String.valueOf(selectedVudachaBody.getPlan()));
                Cellname.setText(null);
                Fact.setText(null);
            }
        });
        recyclerView.setAdapter(adapter);


        SC.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 5) {
                    myAdapterVudachaBody.updateListAndScrollToTop(SC.getText().toString());
                    return true;
                }
                return false;
            }
        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Обробка подій кліків на пунктах меню
                switch (menuItem.getItemId()) {
                    case R.id.nav_cancel:
                        nav_view.setVisibility(View.INVISIBLE);
                        return true;
                }
                return false;
            }
        });

    }


    private String getInClause(List<String> values) {
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            inClause.append("").append(values.get(i)).append("");
            if (i < values.size() - 1) {
                inClause.append(",");
            }
        }
        return inClause.toString();
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
                        try {
                            nav_view.setVisibility(View.VISIBLE);
                        }catch (Exception ex)
                        {
                            String s= ex.getMessage().toString();
                        }
                    } else {
                        nav_view.setVisibility(View.INVISIBLE);

                    }
                } else if (Math.abs(valueY) > MIN_DISTANCE) {
                    if (y2 > y1) {

                    } else {

                    }
                }

        }


        return super.onTouchEvent(event);
    }
    public void SaveToList(View view) {
        if (selectedVudachaBodyData != null) {
            EditText cellnameEditText = findViewById(R.id.Cellname);
            EditText factEditText = findViewById(R.id.Fact);
            EditText planEditText = findViewById(R.id.Plan);
            String cellname = cellnameEditText.getText().toString();
            String fact = factEditText.getText().toString();
            String plan = planEditText.getText().toString();

            if (cellname.isEmpty()) {
                cellnameEditText.setError("Це поле не може бути порожнім");
                cellnameEditText.setHintTextColor(Color.RED);
            } else if (fact.isEmpty()) {
                factEditText.setError("Це поле не може бути порожнім");
                factEditText.setHintTextColor(Color.RED);
            } else {
                String factText = Fact.getText().toString();
                double factValue = Double.parseDouble(factText);
                String planText = Plan.getText().toString();
                double planValue = Double.parseDouble(planText);

                if (factText.equals("0") || factText.equals("0.0")) {
                    showAlertDialog("Попередження", "Ви впевнені, що хочете зберегти кількість 0?", factValue);
                } else {
                    // Оновлення даних елемента
                    selectedVudachaBodyData.setFact(factValue);
                    selectedVudachaBodyData.setCellName(cellname);
                    selectedVudachaBodyData.setUser(DB.getUser[0][1]);
                    selectedVudachaBodyData.setTime(getCurrentTime());
                    selectedVudachaBodyData.setStatus(1);

                    // Перевірка на різницю між фактом і планом
                    if (!(factValue==planValue)) {
                        // Якщо факт не дорівнює плану, показати діалогове вікно
                        showAlertDialog("Попередження", "Фактична кількість не збігається з плановою. Це остання дія з цим товаром? Бажаєте завершити?", factValue);
                    } else {
                        // Додавання до localList лише якщо користувач зберігає зміни
                        localList.add(selectedVudachaBodyData);

                        // Видалення з основного списку
                        int selectedIndex = list.indexOf(selectedVudachaBodyData);
                        if (selectedIndex != -1) {
                            list.remove(selectedIndex);
                            myAdapterVudachaBody.notifyItemRemoved(selectedIndex);
                        }

                        // Очистіть текст помилок та встановіть звичайний колір для hint
                        cellnameEditText.setError(null);
                        cellnameEditText.setHintTextColor(Color.BLACK);
                        factEditText.setError(null);
                        factEditText.setHintTextColor(Color.BLACK);
                        SumPoz.setText("Найменування " + "(" + list.size() + ")");

                    }
                }
            }
        } else {
            // Обробте відповідно, якщо selectedPrihodBody дорівнює null.
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private int updateAnalit(VudachaBodyData vudachaBodyData, int docNum, int sc, String prodName, String prodEd, String part,
                             String calibr, String ton, float qty) {
        int result = -1; // Значення за замовчуванням (наприклад, якщо сталася помилка)

        try {
            // Встановлення з'єднання з базою даних
            Connection connection = DriverManager.getConnection(DB.connString);

            // Виклик збереженої процедури
            String storedProcedure = "{call updateAnalit(?, ?, ?, ?, ?, ?)}";

            try (CallableStatement stmt = connection.prepareCall(storedProcedure)) {
                // Передача параметрів

                stmt.setInt(1, docNum);
                stmt.setInt(2, sc);

                stmt.setString(3, part);
                stmt.setString(4, calibr);
                stmt.setString(5, ton);
                stmt.setFloat(6, qty); // Встановлення сьомого параметра @Qty

                // Виклик збереженої процедури
                result = stmt.executeUpdate();

                if (result != 1) {
                    // Якщо результат не дорівнює 1, встановіть флаг на false
                    allRecordsSuccessfullyInserted = false;
                    // Construct the unique key for the failed entry
                    String uniqueKey = "[" + vudachaBodyData.getSC() + "], " + vudachaBodyData.getProdName() + "\n";

                    // Check if the entry is not already in the set before adding it to failedSCList
                    if (uniqueFailedSCEntries.add(uniqueKey)) {
                        failedSCList.append(uniqueKey);
                    }
                }
            }

            // Закриття з'єднання
            connection.close();
        } catch (SQLException e) {
            // Обробка помилок
            e.printStackTrace();
            allRecordsSuccessfullyInserted = false;
        }

        return result;
    }
    /* private int updateAnalit(VudachaBodyData vudachaBodyData, int docNum, int sc, String prodName, String prodEd, String part,
                             String calibr, String ton, float qty) {
        int result = -1; // Значення за замовчуванням (наприклад, якщо сталася помилка)

        try {
            // Встановлення з'єднання з базою даних
            Connection connection = DriverManager.getConnection(DB.connString);

            // Виклик збереженої процедури
            String storedProcedure = "{call updateAnalit(?, ?, ?, ?, ?, ?)}";

            try (CallableStatement stmt = connection.prepareCall(storedProcedure)) {
                // Передача параметрів
                stmt.setInt(1, docNum);
                stmt.setInt(2, sc);
                stmt.setString(3, part);
                stmt.setString(4, calibr);
                stmt.setString(5, ton);
                stmt.setFloat(6, qty); // Встановлення сьомого параметра @Qty

                // Виклик збереженої процедури
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    result = rs.getInt(0); // Отримання першого значення з першого стовпця результату
                }
            }

            // Закриття з'єднання
            connection.close();
        } catch (SQLException e) {
            // Обробка помилок
            e.printStackTrace();
            allRecordsSuccessfullyInserted = false;
        }

        return result;
    }
*/



    private int callUpdateDocVudachaChieldTransaction(VudachaBodyData VudachaBodyData, int docNum, int sc, String prodName,String prodEd,
                                                   String part, String calibr, String ton, float plans,
                                                   float facts, String cellName, String userChange,
                                                   int statusPoz) {
        int result = -1; // Значення за замовчуванням (наприклад, якщо сталася помилка)

        try {
            // Встановлення з'єднання з базою даних
            Connection connection = DriverManager.getConnection(DB.connString);

            // Виклик збереженої процедури
            String storedProcedure = "{call UpdateDocVudachaChieldTransaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = connection.prepareCall(storedProcedure)) {
                // Передача параметрів
                stmt.setInt(1, docNum);
                stmt.setInt(2, sc);
                stmt.setString(3, prodName);
                stmt.setString(4, prodEd);
                stmt.setString(5, part);
                stmt.setString(6, calibr);
                stmt.setString(7, ton);
                stmt.setFloat(8, plans);
                stmt.setFloat(9, facts);
                stmt.setString(10, cellName);
                stmt.setString(11, userChange);
                stmt.setInt(12, statusPoz);

                // Виклик збереженої процедури
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs != null && rs.next()) {
                        result = rs.getInt("SuccessFlag");
                        if (result != 1) {
                            // Construct the unique key for the failed entry
                            String uniqueKey = "[" + VudachaBodyData.getSC() + "], " + VudachaBodyData.getProdName()+"\n";

                            // Check if the entry is not already in the set before adding it to failedSCList
                            if (uniqueFailedSCEntries.add(uniqueKey)) {
                                failedSCList.append(uniqueKey);
                            }
                        }
                    }
                }
            }

            // Закриття з'єднання
            connection.close();
        } catch (SQLException e) {
            // Обробка помилок
            e.printStackTrace();

        }
        return result;
    }

    private void showResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Результати транзакцій");

        if (allRecordsSuccessfullyInserted) {
            builder.setMessage("Всі записи успішно збережені!");
        } else {
            String failedSCList = getFailedSCList().toString();
            failedSCList = failedSCList.substring(1, failedSCList.length() - 1);
            builder.setMessage("Деякі записи не були збережені.\n" + failedSCList);
        }

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }





    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Ви впевнені, що хочете вийти? \nВсі внесені зміни будуть втрачені!")
                .setPositiveButton("Так", (dialog, which) -> {
                    // Закриття активності
                    finish();
                })
                .setNegativeButton("Ні", (dialog, which) -> {
                    // Скасування виходу, користувач продовжить роботу в активності
                    dialog.dismiss();
                })
                .show();
    }


    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }


    private CountDownTimer timer;

    private void showAlertDialog(String title, String message, double factValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Так", (dialog, which) -> {
                    // Запускаємо таймер
                    startTimer(factValue);
                    // Показуємо Snackbar
                    showSnackbar(factValue);
                })
                .setNegativeButton("Ні", (dialog, which) -> {
                    // Виконуємо код, який потрібно виконати, якщо натиснуто "Ні"
                    int selectedIndex = list.indexOf(selectedVudachaBodyData);
                    if (selectedIndex != -1) {
                        // Оновлення фактичного значення у масиві даних
                        VudachaBodyData data = list.get(selectedIndex);
                        data.setFact(factValue);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        recyclerView.requestLayout();
                    }
                })
                .show();
    }

    private void startTimer(double factValue) {
        // Встановлюємо таймер на 5 секунд
        timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Кожної секунди можна виконати додаткові дії, якщо потрібно
            }

            public void onFinish() {
                // Виконуємо код, який потрібно виконати, якщо час вийшов без натискання "ОК" на Snackbar
                String cellname = Cellname.getText().toString();
                if (!isSCAlreadyExists(selectedVudachaBodyData.getSC())) {
                    selectedVudachaBodyData.setFact(factValue);
                    selectedVudachaBodyData.setCellName(cellname);
                    selectedVudachaBodyData.setUser(DB.getUser[0][1]);
                    selectedVudachaBodyData.setTime(getCurrentTime());
                    selectedVudachaBodyData.setStatus(1);
                    localList.add(selectedVudachaBodyData);
                    list.removeAll(localList);
                    SumPoz.setText("Найменування " + "(" + list.size() + ")");
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.requestLayout();
            }
        }.start();
    }

    private void cancelTimer() {
        // Відміна таймера
        if (timer != null) {
            timer.cancel();
        }
    }

    private void showSnackbar(double factValue) {
        // Показ Snackbar
        Snackbar snackbar = Snackbar.make(recyclerView, "Повернути...?", Snackbar.LENGTH_LONG);
        snackbar.setAction("ОК", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Відміна таймера
                cancelTimer();
                // Опрацювання натискання "ОК" на Snackbar
                // Додаткові дії, які потрібно виконати
            }
        });
        snackbar.show();
    }


    /*
    // Метод для виведення модального вікна
    private void showAlertDialog(String title, String message, double factValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Так", (dialog, which) -> {
                    String cellname = Cellname.getText().toString();
                    // Тут ви можете виконати операції, які пов'язані з позитивною відповіддю користувача
                    if (!isSCAlreadyExists(selectedVudachaBodyData.getSC())){
                        selectedVudachaBodyData.setFact(factValue);
                        selectedVudachaBodyData.setCellName(cellname);
                        selectedVudachaBodyData.setUser(DB.getUser[0][1]);
                        selectedVudachaBodyData.setTime(getCurrentTime());
                        selectedVudachaBodyData.setStatus(1);
                        localList.add(selectedVudachaBodyData);
                        list.removeAll(localList);


                        // Показ Snackbar для відновлення елемента
                        Snackbar snackbar = Snackbar.make(recyclerView, "Повернути...?", Snackbar.LENGTH_LONG);
                        snackbar.setAction("ОК", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int selectedIndex = list.indexOf(selectedVudachaBodyData);
                                if (selectedIndex != -1) {
                                    // Повернення елемента у список
                                    list.add(selectedIndex, selectedVudachaBodyData);
                                    recyclerView.getAdapter().notifyItemInserted(selectedIndex);
                                    recyclerView.requestLayout();
                                }
                            }
                        });
                        snackbar.show();
                    }
                })
                .setNegativeButton("Ні", (dialog, which) -> {
                            int selectedIndex = list.indexOf(selectedVudachaBodyData);
                            if (selectedIndex != -1) {
                                // Оновлення фактичного значення у масиві даних
                                VudachaBodyData data = list.get(selectedIndex);
                                data.setFact(factValue);
                                recyclerView.getAdapter().notifyDataSetChanged();
                                recyclerView.requestLayout();
                            }
                })
                .show();

    }
*/

    // Метод для перевірки, чи SC вже існує в списку
    private boolean isSCAlreadyExists(int sc) {
        for (VudachaBodyData body : localList) {
            if (body.getSC() == sc) {
                return true;
            }
        }
        return false;
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    // Клас для виконання фонових запитів
    private class TransactionTask extends AsyncTask<Void, Integer, Void> {

        private ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Відображення прогресу перед початком виконання транзакцій
            progressDialog = new ProgressDialog(VudachaWork.this);
            progressDialog.setMessage("Виконання транзакцій...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int totalRecords = localList.size();
            // Симуляція тривалості роботи
            for (int i = 0; i < totalRecords; i++) {
                VudachaBodyData vudachaBodyData = localList.get(i);

                // Витягніть дані з prihodBody та викличте метод
                int docNum = vudachaBodyData.getDocNumber();
                int sc = vudachaBodyData.getSC();
                String prodName = vudachaBodyData.getProdName();
                String prodEd = vudachaBodyData.getProdEd();
                String part = vudachaBodyData.getPart();
                String calibr = vudachaBodyData.getCalibr();
                String ton = vudachaBodyData.getTon();
                float plan = vudachaBodyData.getPlan().intValue();
                float qty = vudachaBodyData.getFact().intValue();
                String cellName = vudachaBodyData.getCellName();
                String userChange = vudachaBodyData.getUser();
                int status = vudachaBodyData.getStatus();
                // Виклик методу для вставки або оновлення запису в базі даних
                int result = updateAnalit(vudachaBodyData, docNum, sc, prodName, prodEd, part, calibr, ton, (float) qty);
                int resultforPrihChield = callUpdateDocVudachaChieldTransaction(vudachaBodyData, docNum, sc, prodName, prodEd, part, calibr, ton, (float) plan, (float) qty, cellName, userChange, status);
                // Виведення результату
                Log.d("DB_RESULT", "Result for record with SC " + sc + ": " + result);
                // Публікація прогресу
                publishProgress((i + 1) * 100 / totalRecords);

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Оновлення відсотка завершення в ProgressBar
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Закриття ProgressDialog після завершення виконання транзакцій
            progressDialog.dismiss();
            showResultDialog();
        }
    }

    public void onButtonClick(View view) {
        if (localList.isEmpty()) {
            MotionToast.Companion.createColorToast(VudachaWork.this,"Відстутні дані для оновлення БД",
                    MotionToast.Companion.getTOAST_WARNING(),
                    MotionToast.Companion.getGRAVITY_BOTTOM(),
                    MotionToast.Companion.getSHORT_DURATION(),
                    ResourcesCompat.getFont(VudachaWork.this, www.sanju.motiontoast.R.font.helvetica_regular));
            return;
        }
        // Пройдіть по всіх елементах localList та викличте метод для кожного
        for (VudachaBodyData vudachaBodyData : localList) {
            // Витягніть дані з prihodBody та викличте метод
            int docNum = vudachaBodyData.getDocNumber();
            int sc = vudachaBodyData.getSC();
            String prodName = vudachaBodyData.getProdName();
            String prodEd = vudachaBodyData.getProdEd();
            String part = vudachaBodyData.getPart();
            String calibr = vudachaBodyData.getCalibr();
            String ton = vudachaBodyData.getTon();
            float plan= vudachaBodyData.getPlan().intValue();
            String userChange=vudachaBodyData.getUser();
            float qty = vudachaBodyData.getFact().intValue();// Припускаю, що у вас є метод для отримання кількості (getQty)
            String cellName = vudachaBodyData.getCellName();
            int status=vudachaBodyData.getStatus();


            // Виклик методу для вставки або оновлення запису в базі даних
            int result = updateAnalit(vudachaBodyData, docNum, sc, prodName, prodEd, part, calibr, ton, (float) qty);
            int resultforPrihChield = callUpdateDocVudachaChieldTransaction(vudachaBodyData, docNum, sc, prodName, prodEd, part, calibr, ton, (float) plan, (float) qty, cellName, userChange, status);
            // Виведення результату
            Log.d("DB_RESULT", "Result for record with SC " + sc + ": " + result);

        }

        // Очистіть localList після того, як всі дані будуть відправлені на сервер (опціонально)
        localList.clear();
        new TransactionTask().execute();
    }
    private List<String> getFailedSCList() {


        for (VudachaBodyData vudachaBodyData : localList) {
            int result = updateAnalit(
                    vudachaBodyData,
                    vudachaBodyData.getDocNumber(),
                    vudachaBodyData.getSC(),
                    vudachaBodyData.getProdName(),
                    vudachaBodyData.getProdEd(),
                    vudachaBodyData.getPart(),
                    vudachaBodyData.getCalibr(),
                    vudachaBodyData.getTon(),
                    vudachaBodyData.getFact().floatValue()

            );

            if (result != 1) {
                // Додаємо SC та ProdName до списку, якщо вставка не була успішною
                failedSCList.append("SC: ").append(vudachaBodyData.getSC()).append(", ProdName: ").append(vudachaBodyData.getProdName()).append("\n");
            }
        }

        return Collections.singletonList(failedSCList.toString());
    }

}