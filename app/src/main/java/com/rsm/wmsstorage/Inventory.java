package com.rsm.wmsstorage;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rsm.wmsequip.R;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;

public class Inventory extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText searchEditText;
    ArrayList<InventoryData> dataList = new ArrayList<>(); // Оголошуємо список dataList
    MyAdapterHeadInventory adapter; // Замініть YourAdapter на ваш клас адаптера
    Button plus,minus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.inventory);
        recyclerView = findViewById(R.id.recyclerView);
        searchEditText = findViewById(R.id.Search);
        adapter = new MyAdapterHeadInventory(dataList); // Ініціалізуємо ваш адаптер і передаємо йому список dataList
        recyclerView.setAdapter(adapter); // Встановлюємо адаптер для RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Реєстрація обробника контекстного меню для RecyclerView
        registerForContextMenu(recyclerView);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    public void performSearch() {
        String searchText = searchEditText.getText().toString();
        if (searchText.isEmpty()) {
            MotionToast.Companion.createColorToast(Inventory.this, "Введіть щось",
                    MotionToast.Companion.getTOAST_ERROR(),
                    MotionToast.Companion.getGRAVITY_TOP(),
                    MotionToast.Companion.getSHORT_DURATION(),
                    ResourcesCompat.getFont(Inventory.this, www.sanju.motiontoast.R.font.helvetica_regular));
            searchEditText.setText("");
            return;
        }

        if (DB.Connect()) {
            DB.curSQL = "Select ProdName, Qty, SC  from Analit where (SC like '" + searchText + "' or CellName like '" + searchText + "') AND Qty>0";
            DB.getInventoryData = DB.executeSQLQuery(DB.curSQL);

            if (DB.getInventoryData != null && DB.getInventoryData.length > 0) {
                dataList.clear();
                for (int i = 0; i < DB.getInventoryData.length; i++) {
                    String prodName = DB.getInventoryData[i][0];
                    double qty = Double.parseDouble(DB.getInventoryData[i][1]);
                    int SC= Integer.parseInt(DB.getInventoryData[i][2]);
                    InventoryData item = new InventoryData(prodName, qty,SC);
                    dataList.add(item);
                }
                adapter.notifyDataSetChanged();
                searchEditText.setText("");
            }
        }
    }



    // Обробляє вибір пунктів контекстного меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Отримання позиції вибраного елемента
        int position = adapter.getSelectedPosition();

        switch (item.getItemId()) {
            case R.id.changeQty:
                // Отримайте значення SC з вибраного елемента
                int SC = dataList.get(position).getSC();
                // Передайте значення SC разом зі старою кількістю в метод showChangeQuantityDialog()
                showChangeQuantityDialog(dataList.get(position).getQty(), SC);
                return true;
            case R.id.changeName:
                // Дії для пункту меню "Змінити назву"
                // Робіть необхідні зміни у назві
                return true;
            case R.id.ShowDetails:
                // Дії для пункту меню "Подробиці"
                // Показати деталі елемента
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    private void showChangeQuantityDialog(double oldQuantity,int SC) {
        ChangeQuantityDialog dialog = new ChangeQuantityDialog(this, oldQuantity, SC);
        dialog.setAdapter(adapter);
        dialog.show();

        // Отримати нову кількість з діалогового вікна після його закриття та виконати необхідні дії
        dialog.setOnDismissListener(dialogInterface -> {
            Double newQuantity = dialog.getNewQuantity();
            adapter.updateData(newQuantity,SC);
            Toast.makeText(this, "Збережено", Toast.LENGTH_SHORT).show();
        });

    }



}
