package com.rsm.wmsstorage;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class PrihodWork extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private ArrayList<Integer> selectedDocumentNumbers = new ArrayList<>();
    private Set<String> uniqueFailedSCEntries = new HashSet<>();
    private boolean allRecordsSuccessfullyInserted = true;
    private StringBuilder failedSCList = new StringBuilder();

    private static final int MIN_DISTANCE = 150;
    private RecyclerView recyclerView;
    ArrayList<PrihodBody> list;
    public PrihodBody selectedPrihodBody;
    List<PrihodBody> localList = new ArrayList<>();
    NavigationView nav_view;
    MyadapterBodyPrih myadapterBodyPrih;
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

        setContentView(R.layout.activity_prihod_work);
        this.gestureDetector = new GestureDetectorCompat(PrihodWork.this, this);
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
            DB.curSQL = "Select [DocNum],[SC],[ProdName],[ProdEd],[Part],[Calibr],[Ton],[Plans],[Facts],[CellName],[Dttm],[UserChange],[StatusPoz] from DocPrihChield where [DocNum] IN("+  inClause +") order by SC asc";
            List<PrihodBody> prihodBodyList = DB.getPrihodBodyList(DB.curSQL);
            list.addAll(prihodBodyList);
            SumPoz.setText("Найменування "+"("+list.size()+")");
        }
        myadapterBodyPrih = new MyadapterBodyPrih(this, list, recyclerView);
        recyclerView.setAdapter(myadapterBodyPrih);

        MyadapterBodyPrih adapter = new MyadapterBodyPrih(this, list,recyclerView);
        adapter.setItemSelectedListener(new MyadapterBodyPrih.OnItemSelectedListener() {
            @Override
            public void onItemSelected(PrihodBody selectedPrihodBody) {
                // Оновіть значення EditText з вибраними даними
                PrihodWork.this.selectedPrihodBody = selectedPrihodBody;
                ProdName.setText(String.valueOf(selectedPrihodBody.getProdName()));
                ProdEd.setText(String.valueOf(selectedPrihodBody.getProdEd()));
                Part.setText("Партія ["+String.valueOf(selectedPrihodBody.getPart())+"]");
                Calibr.setText("Калібр ["+String.valueOf(selectedPrihodBody.getCalibr())+"]");
                Ton.setText("Тон ["+String.valueOf(selectedPrihodBody.getTon())+"]");

                Plan.setText(String.valueOf(selectedPrihodBody.getPlan()));
                Cellname.setText(null);
                Fact.setText(null);
            }
        });
        recyclerView.setAdapter(adapter);


        SC.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 5) {
                    myadapterBodyPrih.updateListAndScrollToTop(SC.getText().toString());
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
        if (selectedPrihodBody != null) {
            EditText cellnameEditText = findViewById(R.id.Cellname);
            EditText factEditText = findViewById(R.id.Fact);
            String cellname = cellnameEditText.getText().toString();
            String fact = factEditText.getText().toString();

            if (cellname.isEmpty()) {
                cellnameEditText.setError("Це поле не може бути порожнім");
                cellnameEditText.setHintTextColor(Color.RED);
            } else {
                if (fact.isEmpty()) {
                    factEditText.setError("Це поле не може бути порожнім");
                    factEditText.setHintTextColor(Color.RED);
                } else {
                    String factText = Fact.getText().toString();
                    double factValue = Double.parseDouble(factText);

                    if (factText.equals("0") || factText.equals("0.0")) {
                        showAlertDialog("Попередження", "Ви впевнені, що хочете зберегти кількість 0?", factValue);
                    } else {
                        // Перевірте, чи SC вже існує в основному списку
                        int selectedIndex = list.indexOf(selectedPrihodBody);

                        if (selectedIndex != -1) {
                            // Видаліть елемент із списку
                            list.remove(selectedIndex);
                            // Оповістіть адаптер про зміни
                            myadapterBodyPrih.notifyItemRemoved(selectedIndex);
                            recyclerView.getAdapter().notifyDataSetChanged();
                            recyclerView.requestLayout();
                        }

                        selectedPrihodBody.setFact(factValue);
                        selectedPrihodBody.setCellName(cellname);
                        selectedPrihodBody.setUser(DB.getUser[0][1]);
                        selectedPrihodBody.setTime(getCurrentTime());
                        selectedPrihodBody.setStatus(1);

                        // Додайте оновлений елемент до localList
                        localList.add(selectedPrihodBody);

                        // Очистіть текст помилок та встановіть звичайний колір для hint
                        cellnameEditText.setError(null);
                        cellnameEditText.setHintTextColor(Color.BLACK);
                        factEditText.setError(null);
                        factEditText.setHintTextColor(Color.BLACK);
                        SumPoz.setText("Найменування "+"("+list.size()+")");
                    }
                }
            }
        } else {
            // Обробте відповідно, якщо selectedPrihodBody дорівнює null.
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private int callInsertOrUpdateAnalit(PrihodBody prihodBody,int docNum, int sc, String prodName, String prodEd, String part,
                                         String calibr, String ton, float qty, String cellName) {
        int result = -1; // Значення за замовчуванням (наприклад, якщо сталася помилка)

        try {
            // Встановлення з'єднання з базою даних
            Connection connection = DriverManager.getConnection(DB.connString);

            // Виклик збереженої процедури
            String storedProcedure = "{call InsertOrUpdateAnalit(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = connection.prepareCall(storedProcedure)) {
                // Передача параметрів
                stmt.setInt(1, docNum);
                stmt.setInt(2, sc);
                stmt.setString(3, prodName);
                stmt.setString(4, prodEd);
                stmt.setString(5, part);
                stmt.setString(6, calibr);
                stmt.setString(7, ton);
                stmt.setFloat(8, qty);
                stmt.setString(9, cellName);

                // Виклик збереженої процедури
                stmt.execute();

                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs != null && rs.next()) {
                        result = rs.getInt("Result");
                        if (result != 1) {
                            // Якщо результат не дорівнює 1, встановіть флаг на false
                            allRecordsSuccessfullyInserted = false;
                            // Construct the unique key for the failed entry
                            String uniqueKey = "[" + prihodBody.getSC() + "], " + prihodBody.getProdName()+"\n";

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
            allRecordsSuccessfullyInserted = false;
        }

        return result;
    }
    private int callUpdateDocPrihChieldTransaction(PrihodBody prihodBody, int docNum, int sc, String prodName,String prodEd,
                                                   String part, String calibr, String ton, float plans,
                                                   float facts, String cellName, String userChange,
                                                   int statusPoz) {
        int result = -1; // Значення за замовчуванням (наприклад, якщо сталася помилка)

        try {
            // Встановлення з'єднання з базою даних
            Connection connection = DriverManager.getConnection(DB.connString);

            // Виклик збереженої процедури
            String storedProcedure = "{call UpdateDocPrihChieldTransaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
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
                            String uniqueKey = "[" + prihodBody.getSC() + "], " + prihodBody.getProdName()+"\n";

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
    // Метод для виведення модального вікна
    private void showAlertDialog(String title, String message, double factValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Так", (dialog, which) -> {
                    String cellname = Cellname.getText().toString();
                    // Тут ви можете виконати операції, які пов'язані з позитивною відповіддю користувача
                        if (!isSCAlreadyExists(selectedPrihodBody.getSC())){
                            selectedPrihodBody.setFact(factValue);
                            selectedPrihodBody.setCellName(cellname);
                            selectedPrihodBody.setUser(DB.getUser[0][1]);
                            selectedPrihodBody.setTime(getCurrentTime());
                            selectedPrihodBody.setStatus(1);
                            localList.add(selectedPrihodBody);
                            list.removeAll(localList);

                            // Оновіть адаптер з новим списком

                        }
                })
                .setNegativeButton("Ні", (dialog, which) -> {
                    // Тут ви можете виконати операції, які пов'язані з відміною користувача
                })
                .show();
    }


    // Метод для перевірки, чи SC вже існує в списку
    private boolean isSCAlreadyExists(int sc) {
        for (PrihodBody body : localList) {
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
            progressDialog = new ProgressDialog(PrihodWork.this);
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
                PrihodBody prihodBody = localList.get(i);

                // Витягніть дані з prihodBody та викличте метод
                int docNum = prihodBody.getDocNumber();
                int sc = prihodBody.getSC();
                String prodName = prihodBody.getProdName();
                String prodEd = prihodBody.getProdEd();
                String part = prihodBody.getPart();
                String calibr = prihodBody.getCalibr();
                String ton = prihodBody.getTon();
                float plan = prihodBody.getPlan().intValue();
                float qty = prihodBody.getFact().intValue();
                String cellName = prihodBody.getCellName();
                String userChange = prihodBody.getUser();
                int status = prihodBody.getStatus();
                // Виклик методу для вставки або оновлення запису в базі даних
                int result = callInsertOrUpdateAnalit(prihodBody, docNum, sc, prodName, prodEd, part, calibr, ton, (float) qty, cellName);
                int resultforPrihChield = callUpdateDocPrihChieldTransaction(prihodBody, docNum, sc, prodName, prodEd, part, calibr, ton, (float) plan, (float) qty, cellName, userChange, status);
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
            MotionToast.Companion.createColorToast(PrihodWork.this,"Відстутні дані для оновлення БД",
                    MotionToast.Companion.getTOAST_WARNING(),
                    MotionToast.Companion.getGRAVITY_BOTTOM(),
                    MotionToast.Companion.getSHORT_DURATION(),
                    ResourcesCompat.getFont(PrihodWork.this, www.sanju.motiontoast.R.font.helvetica_regular));
            return;
        }
            // Пройдіть по всіх елементах localList та викличте метод для кожного
            for (PrihodBody prihodBody : localList) {
                // Витягніть дані з prihodBody та викличте метод
                int docNum = prihodBody.getDocNumber();
                int sc = prihodBody.getSC();
                String prodName = prihodBody.getProdName();
                String prodEd = prihodBody.getProdEd();
                String part = prihodBody.getPart();
                String calibr = prihodBody.getCalibr();
                String ton = prihodBody.getTon();
                float plan= prihodBody.getPlan().intValue();
                String userChange=prihodBody.getUser();
                float qty = prihodBody.getFact().intValue();// Припускаю, що у вас є метод для отримання кількості (getQty)
                String cellName = prihodBody.getCellName();
                int status=prihodBody.getStatus();


                    // Виклик методу для вставки або оновлення запису в базі даних
                    int result = callInsertOrUpdateAnalit(prihodBody, docNum, sc, prodName, prodEd, part, calibr, ton, (float) qty, cellName);
                    int resultforPrihChield = callUpdateDocPrihChieldTransaction(prihodBody, docNum, sc, prodName, prodEd, part, calibr, ton, (float) plan, (float) qty, cellName, userChange, status);
                    // Виведення результату
                    Log.d("DB_RESULT", "Result for record with SC " + sc + ": " + result);

            }

            // Очистіть localList після того, як всі дані будуть відправлені на сервер (опціонально)
            localList.clear();
        new TransactionTask().execute();
    }
    private List<String> getFailedSCList() {


        for (PrihodBody prihodBody : localList) {
            int result = callInsertOrUpdateAnalit(
                    prihodBody,
                    prihodBody.getDocNumber(),
                    prihodBody.getSC(),
                    prihodBody.getProdName(),
                    prihodBody.getProdEd(),
                    prihodBody.getPart(),
                    prihodBody.getCalibr(),
                    prihodBody.getTon(),
                    prihodBody.getFact().floatValue(),
                    prihodBody.getCellName()
            );

            if (result != 1) {
                // Додаємо SC та ProdName до списку, якщо вставка не була успішною
                failedSCList.append("SC: ").append(prihodBody.getSC()).append(", ProdName: ").append(prihodBody.getProdName()).append("\n");
            }
        }

        return Collections.singletonList(failedSCList.toString());
    }

}