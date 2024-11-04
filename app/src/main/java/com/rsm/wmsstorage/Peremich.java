package com.rsm.wmsstorage;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rsm.wmsequip.R;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;

public class Peremich extends AppCompatActivity {

    RecyclerView recyclerView1, recyclerView2;
    EditText search1, search2;
    ArrayList<PeremichUpData> dataList1 = new ArrayList<>();
    ArrayList<PeremichDownData> dataList2 = new ArrayList<>();
    AdapterPeremichUp adapter1;
    AdapterPeremichDown adapter2;
    ImageView details;
    String tempText1,tempText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.peremich);

        recyclerView1 = findViewById(R.id.recyclerView1);
        recyclerView2 = findViewById(R.id.recyclerView2);
        search1 = findViewById(R.id.Search1);
        search2 = findViewById(R.id.Search2);

        adapter1 = new AdapterPeremichUp(dataList1,null,null);
        adapter2 = new AdapterPeremichDown(dataList2,null,null);
        adapter1 = new AdapterPeremichUp(dataList1, new AdapterPeremichUp.OnDetailClickListener() {
            @Override
            public void onDetailClick(int position) {
                // Отримуємо дані для відображення деталей
                PeremichUpData data = dataList1.get(position);
                String prodEd = data.getProdEd();
                String part = data.getPart();
                String calibr = data.getCalibr();
                String ton = data.getTon();

                // Показуємо діалог з деталями
                showDetailsDialog(prodEd, part, calibr, ton);
            }
        },
      new AdapterPeremichUp.OnDownClickListener() {
            @Override
            public void onDownlClick(int position) {
                // Отримуємо дані для відображення деталей
                PeremichUpData data = dataList1.get(position);
                int SC = data.getSC();
                if(tempText2 == null){
                    MotionToast.Companion.createColorToast(Peremich.this, "Вкажіть куди перемістити",
                            MotionToast.Companion.getTOAST_WARNING(),
                            MotionToast.Companion.getGRAVITY_TOP(),
                            MotionToast.Companion.getSHORT_DURATION(),
                            ResourcesCompat.getFont(Peremich.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    return;
                }else {
                    InsertOtherCell(tempText2,SC);
                }

            }
        });
        adapter2 = new AdapterPeremichDown(dataList2, new AdapterPeremichDown.OnDetailClickListener() {
            @Override
            public void onDetailClick(int position) {
                // Отримуємо дані для відображення деталей
                PeremichDownData data = dataList2.get(position);
                String prodEd = data.getProdEd();
                String part = data.getPart();
                String calibr = data.getCalibr();
                String ton = data.getTon();

                // Показуємо діалог з деталями
                showDetailsDialog(prodEd, part, calibr, ton);
            }
        },  new AdapterPeremichDown.OnUpClickListener() {
            @Override
            public void onUplClick(int position) {
                // Отримуємо дані для відображення деталей
                PeremichDownData data = dataList2.get(position);
                int SC = data.getSC();
                if(tempText1==null){
                    MotionToast.Companion.createColorToast(Peremich.this, "Вкажіть куди перемістити",
                            MotionToast.Companion.getTOAST_WARNING(),
                            MotionToast.Companion.getGRAVITY_TOP(),
                            MotionToast.Companion.getSHORT_DURATION(),
                            ResourcesCompat.getFont(Peremich.this, www.sanju.motiontoast.R.font.helvetica_regular));
                    return;
                }else {
                    InsertOtherCell(tempText1,SC);
                }

            }
        });
        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);

        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));




        search1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(search1.getText().toString(), 1);
                    return true;
                }
                return false;
            }
        });

        search2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(search2.getText().toString(), 2);
                    return true;
                }
                return false;
            }
        });

    }

    public void performSearch(String searchText, int ho) {
        String tempText=searchText;
        if (searchText.isEmpty()) {
            MotionToast.Companion.createColorToast(Peremich.this, "Немає даних",
                    MotionToast.Companion.getTOAST_ERROR(),
                    MotionToast.Companion.getGRAVITY_TOP(),
                    MotionToast.Companion.getSHORT_DURATION(),
                    ResourcesCompat.getFont(Peremich.this, www.sanju.motiontoast.R.font.helvetica_regular));
            return;
        }
        if(ho==1){
         DB.curSQL = "Select Sc,ProdName,Qty,ProdEd,Part,Calibr,Ton from Analit where Qty>0 and not CellName like '' AND (SC like '" + searchText + "' or CellName like '" + searchText + "')";
            DB.getPeremichUpData = DB.executeSQLQuery(DB.curSQL);
            tempText1=tempText;
            search1.setHint(tempText1);
            if (DB.getPeremichUpData != null && DB.getPeremichUpData.length > 0) {

                dataList1.clear();
                for (int i = 0; i < DB.getPeremichUpData.length; i++) {
                    int SC = Integer.parseInt(DB.getPeremichUpData[i][0]);
                    String prodName = DB.getPeremichUpData[i][1];
                    double qty = Double.parseDouble(DB.getPeremichUpData[i][2]);
                    String prodEd = DB.getPeremichUpData[i][3];
                    String part = DB.getPeremichUpData[i][4];
                    String calibr = DB.getPeremichUpData[i][5];
                    String ton = DB.getPeremichUpData[i][6];
                    PeremichUpData item = new PeremichUpData(prodName,qty,SC,prodEd,part,calibr,ton);
                    dataList1.add(item);
                }
                try {
                    adapter1.notifyDataSetChanged();
                    search1.setText("");
                    search1.setHint(tempText1);
                }catch (Exception ex){
                    String s =ex.getMessage().toString();
                }

            }
            else {
                MotionToast.Companion.createColorToast(Peremich.this, "Немає даних",
                        MotionToast.Companion.getTOAST_ERROR(),
                        MotionToast.Companion.getGRAVITY_TOP(),
                        MotionToast.Companion.getSHORT_DURATION(),
                        ResourcesCompat.getFont(Peremich.this, www.sanju.motiontoast.R.font.helvetica_regular));
                dataList1.clear();
                adapter1.notifyDataSetChanged();
                search1.setText("");
            }
        }else {
            tempText2=tempText;
            search2.setHint(tempText2);
            DB.curSQL = "Select Sc,ProdName,Qty,ProdEd,Part,Calibr,Ton from Analit where Qty>0 and not CellName like '' AND  CellName like '" + searchText + "'";
            DB.getPeremichDownData = DB.executeSQLQuery(DB.curSQL);
            if (DB.getPeremichDownData != null && DB.getPeremichDownData.length > 0) {
                tempText2=tempText;
                dataList2.clear();
                for (int i = 0; i < DB.getPeremichDownData.length; i++) {
                    int SC = Integer.parseInt(DB.getPeremichDownData[i][0]);
                    String prodName = DB.getPeremichDownData[i][1];
                    double qty = Double.parseDouble(DB.getPeremichDownData[i][2]);
                    String prodEd = DB.getPeremichDownData[i][3];
                    String part = DB.getPeremichDownData[i][4];
                    String calibr = DB.getPeremichDownData[i][5];
                    String ton = DB.getPeremichDownData[i][6];
                    PeremichDownData item = new PeremichDownData(prodName,qty,SC,prodEd,part,calibr,ton);
                    dataList2.add(item);
                }
                adapter2.notifyDataSetChanged();
                search2.setText("");
                search2.setHint(tempText2);
            }
            else {
                MotionToast.Companion.createColorToast(Peremich.this, "Немає даних",
                        MotionToast.Companion.getTOAST_ERROR(),
                        MotionToast.Companion.getGRAVITY_TOP(),
                        MotionToast.Companion.getSHORT_DURATION(),
                        ResourcesCompat.getFont(Peremich.this, www.sanju.motiontoast.R.font.helvetica_regular));
                dataList2.clear();
                adapter2.notifyDataSetChanged();
                search2.setText("");
            }
        }
    }
    private void showDetailsDialog(String prodEd, String part, String calibr, String ton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Деталі");

        // Формуємо текст для відображення у діалозі
        StringBuilder details = new StringBuilder();
        details.append("Од.Вим. [").append(prodEd).append("]"+"\n");
        details.append("Партія: ").append(part).append("\n");
        details.append("Калібр: ").append(calibr).append("\n");
        details.append("Тон: ").append(ton);

        builder.setMessage(details.toString());

        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void InsertOtherCell(String Cellname,int SC) {
        // Ваш код для обробки натискання кнопки "Зберегти"
        String sql = "UPDATE Analit SET [CellName] = " + Cellname + " WHERE SC = " + SC;
        DB.updateBD(sql);
        updateRecyclerViews();
    }
    private void updateRecyclerViews() {
        // Оновлення першого RecyclerView
        if (dataList1 != null && adapter1 != null) {
            // Очищення поточного списку даних
            dataList1.clear();

            // Оновлення списку даних з використанням вашого методу performSearch для заповнення даних
            performSearch(search1.getHint().toString(), 1);

            // Повідомлення адаптера про зміну даних
            adapter1.notifyDataSetChanged();
        } else {
            // Якщо dataList1 або adapter1 є нульовими, слід звернути увагу на це і відповідно обробити ситуацію
        }

        // Оновлення другого RecyclerView
        if (dataList2 != null && adapter2 != null) {
            // Очищення поточного списку даних
            dataList2.clear();

            // Оновлення списку даних з використанням вашого методу performSearch для заповнення даних
            performSearch(search2.getHint().toString(), 2);

            // Повідомлення адаптера про зміну даних
            adapter2.notifyDataSetChanged();
        } else {
            // Якщо dataList2 або adapter2 є нульовими, слід звернути увагу на це і відповідно обробити ситуацію
        }
    }
}