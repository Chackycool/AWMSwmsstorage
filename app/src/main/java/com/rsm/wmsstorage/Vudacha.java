package com.rsm.wmsstorage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rsm.wmsequip.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Vudacha extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<VudachaHeadData> list;
    MyAdapterVudachaHead myAdapterVudachaHead;
    ImageView searchButton;
    static Button buttonStartWork;
    Switch switch1, switch2, switch3, switch4,switch5,switch6;
    SwipeRefreshLayout refresh;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Викликати методи ActionBar
            actionBar.hide();
        }
        setContentView(R.layout.vudacha);
        refresh = findViewById(R.id.refresh);
        buttonStartWork=findViewById(R.id.buttonStartWork);
        list = new ArrayList<>();
        if (DB.Connect()) {
            DB.curSQL = "SELECT  [DocNumber], [DocName], REPLACE(FORMAT([DocTime], 'MMM dd HH:mm', 'uk-UA'), '.', '') AS FormattedDocTime, [DocStatus],[Client] FROM [dbo].[DocVudacha]";
            List<VudachaHeadData> vudachaHeadData = DB.getVudachaHeadList(DB.curSQL);
            list.addAll(vudachaHeadData); // Додаємо результати до списку list
        }
        myAdapterVudachaHead = new MyAdapterVudachaHead(this, list);
        recyclerView = findViewById(R.id.recycle);
        searchButton=findViewById(R.id.search_b);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                long startTime = System.currentTimeMillis(); // Початковий час оновлення

                if (DB.Connect()) {
                    DB.curSQL = "SELECT  [DocNumber], [DocName], REPLACE(FORMAT([DocTime], 'MMM dd HH:mm', 'uk-UA'), '.', '') AS FormattedDocTime, [DocStatus],[Client] FROM [dbo].[DocVudacha]";
                    List<VudachaHeadData> vudachaHeadData = DB.getVudachaHeadList(DB.curSQL);

                    // Очистіть список перед додаванням нових даних
                    list.clear();
                    list.addAll(vudachaHeadData); // Додаємо результати до списку list

                    // Оновити адаптер на головному потоці (UI-потоці)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapterVudachaHead.notifyDataSetChanged();
                        }
                    });
                }

                long endTime = System.currentTimeMillis(); // Кінцевий час оновлення
                long elapsedTime = endTime - startTime; // Обчислення часу оновлення в мілісекундах

                // Зупинити анімацію оновлення
                refresh.setRefreshing(false);

                // Вивести час оновлення в Toast
                Toast.makeText(getApplicationContext(), "Час оновлення: " + elapsedTime + " мс", Toast.LENGTH_SHORT).show();
            }
        });




        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapterVudachaHead = new MyAdapterVudachaHead(this, list); // Передаємо список list до адаптера
        try {
            recyclerView.setAdapter(myAdapterVudachaHead);
        }catch (Exception e){
            String s=e.getMessage().toString();
        }
        myAdapterVudachaHead = new MyAdapterVudachaHead(this, list);
        recyclerView.setAdapter(myAdapterVudachaHead);

        myAdapterVudachaHead.setOnItemLongClickListener(new MyAdapterVudachaHead.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                // Отримайте список виділених елементів з адаптера
                ArrayList<Integer> selectedItemsList = myAdapterVudachaHead.getSelectedItems();


                // Тепер ви можете працювати зі списком виділених елементів
                for (Integer selectedItemPosition : selectedItemsList) {
                    // Обробляйте виділений елемент за позицією selectedItemPosition

                }
            }
        });
    }
    public void Search(View view){
        EditText searchEditText = findViewById(R.id.searchDcc);
        String searchText = searchEditText.getText().toString();
        // Викличте метод фільтрації адаптера з введеним текстом пошуку
        myAdapterVudachaHead.filter(searchText);
    }
    private void filterAndRefreshList(boolean switch1State, boolean switch2State, boolean switch3State, boolean switch4State, boolean switch5State, boolean switch6State) {
        list.clear(); // Очистіть поточний список
        List<VudachaHeadData> filteredList = new ArrayList<>();

        for (VudachaHeadData item : myAdapterVudachaHead.getOriginalList()) {
            if ((switch1State && item.getDocStatus() == 0) ||
                    (switch2State && item.getDocStatus() == 1) ||
                    (switch3State && item.getDocStatus() == 2) ||
                    (switch4State && item.getDocStatus() == 3)) {
                filteredList.add(item);
            }
        }

        if (switch5State || switch6State) {
            // Виконуйте сортування за полем "time" для нових переключателів 5 і 6
            Collections.sort(filteredList, new Comparator<VudachaHeadData>() {
                @Override
                public int compare(VudachaHeadData o1, VudachaHeadData o2) {
                    if (switch5State && switch6State) {
                        // Обидва переключателі активні, сортувати від нових до старих
                        return o2.getDocTime().compareTo(o1.getDocTime());
                    } else if (switch5State) {
                        // Сортувати лише за новими
                        return o2.getDocTime().compareTo(o1.getDocTime());
                    } else {
                        // Сортувати лише за старими
                        return o1.getDocTime().compareTo(o2.getDocTime());
                    }
                }
            });
        }

        list.addAll(filteredList);
        myAdapterVudachaHead.notifyDataSetChanged(); // Оновіть RecyclerView
    }

    public void filterAndSort(View view) {
        // Створіть діалогове вікно та встановіть його макет
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.filter_sort, null);
        builder.setView(dialogView);

        // Знайдіть переключателі в макеті dialogView
        switch1 = dialogView.findViewById(R.id.switch1);
        switch2 = dialogView.findViewById(R.id.switch2);
        switch3 = dialogView.findViewById(R.id.switch3);
        switch4 = dialogView.findViewById(R.id.switch4);
        switch5 = dialogView.findViewById(R.id.switch5);
        switch6 = dialogView.findViewById(R.id.switch6);

        Button saveFilterButton = dialogView.findViewById(R.id.saveFilter);

        // Відобразіть модальне вікно
        AlertDialog dialog = builder.create();
        dialog.show();

        // Додайте обробник подій для кнопки "Зберегти"
        saveFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Отримайте стан переключателів
                boolean switch1State = switch1.isChecked();
                boolean switch2State = switch2.isChecked();
                boolean switch3State = switch3.isChecked();
                boolean switch4State = switch4.isChecked();
                boolean switch5State = switch5.isChecked();
                boolean switch6State = switch6.isChecked();

                // Закрийте модальне вікно
                dialog.dismiss();

                // Оновіть список
                refreshList(switch1State, switch2State, switch3State, switch4State, switch5State, switch6State);
            }
        });
    }

    private void refreshList(boolean switch1State, boolean switch2State, boolean switch3State, boolean switch4State, boolean switch5State, boolean switch6State) {
        list.clear();
        if (DB.Connect()) {
            DB.curSQL = "SELECT  [DocNumber], [DocName], REPLACE(FORMAT([DocTime], 'MMM dd HH:mm', 'uk-UA'), '.', '') AS FormattedDocTime, [DocStatus],[Client] FROM [dbo].[DocVudacha]";
            List<VudachaHeadData> vudachaHeadDataList = DB.getVudachaHeadList(DB.curSQL);
            list.addAll(vudachaHeadDataList); // Додаємо результати до списку list
        }

        filterAndSortList(switch1State, switch2State, switch3State, switch4State, switch5State, switch6State);

        myAdapterVudachaHead.notifyDataSetChanged();
    }

    private void filterAndSortList(boolean switch1State, boolean switch2State, boolean switch3State, boolean switch4State, boolean switch5State, boolean switch6State) {
        List<VudachaHeadData> filteredList = new ArrayList<>(list); // Створіть копію поточного списку

        // Виконуйте фільтрацію
        for (VudachaHeadData item : list) {
            if (!((switch1State && item.getDocStatus() == 0) ||
                    (switch2State && item.getDocStatus() == 1) ||
                    (switch3State && item.getDocStatus() == 2) ||
                    (switch4State && item.getDocStatus() == 3))) {
                filteredList.remove(item);
            }
        }

        if (switch5State || switch6State) {
            // Виконуйте сортування за полем "time" для нових переключателів 5 і 6
            Collections.sort(filteredList, new Comparator<VudachaHeadData>() {
                @Override
                public int compare(VudachaHeadData o1, VudachaHeadData o2) {
                    if (switch5State && switch6State) {
                        // Обидва переключателі активні, сортувати від нових до старих
                        return o2.getDocTime().compareTo(o1.getDocTime());
                    } else if (switch5State) {
                        // Сортувати лише за новими
                        return o2.getDocTime().compareTo(o1.getDocTime());
                    } else {
                        // Сортувати лише за старими
                        return o1.getDocTime().compareTo(o2.getDocTime());
                    }
                }
            });
        }

        list.clear();
        list.addAll(filteredList);
        myAdapterVudachaHead.notifyDataSetChanged(); // Оновіть RecyclerView
    }
    public void StartWorkPrih(View view) {
        try {
            ArrayList<String> selectedDocumentNumbers = myAdapterVudachaHead.getSelectedDocumentNumbers();
            Intent intent = new Intent(this, VudachaWork.class);
            intent.putStringArrayListExtra("selectedDocumentNumbers", selectedDocumentNumbers);
            startActivity(intent);
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

