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

public class PrihodList extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<PrihodHead> list;
    MyAdapterHeadPrih myAdapterHeadPrih;
    ImageView searchButton;
    static Button buttonStartWork;
    Switch switch1, switch2, switch3, switch4,switch5,switch6;
    SwipeRefreshLayout refresh;
    public void onSelectedItemsChanged(ArrayList<Integer> selectedItems) {
        // Тут ви отримаєте список виділених номерів документів
        // Можете передати цей список в метод StartWorkPrih
    }
    public PrihodList() {
        super();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Викликати методи ActionBar
            actionBar.hide();
        }
        setContentView(R.layout.doc_prih_list);
        refresh = findViewById(R.id.refresh);
        buttonStartWork=findViewById(R.id.buttonStartWork);
        list = new ArrayList<>();
        if (DB.Connect()) {
            DB.curSQL = "SELECT [ID], [DocNumber], [DocName], REPLACE(FORMAT([DocTime], 'MMM dd HH:mm', 'uk-UA'), '.', '') AS FormattedDocTime, [DocStatus], BookUsers.Username FROM [dbo].[DocPrih] LEFT OUTER JOIN BookUsers ON [dbo].[DocPrih].[UserID] = BookUsers.UserID";
            List<PrihodHead> prihodHeadList = DB.getPrihodList(DB.curSQL);
            list.addAll(prihodHeadList); // Додаємо результати до списку list
        }
        myAdapterHeadPrih = new MyAdapterHeadPrih(this, list);
        recyclerView = findViewById(R.id.recycle);
        searchButton=findViewById(R.id.search_b);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                long startTime = System.currentTimeMillis(); // Початковий час оновлення

                if (DB.Connect()) {
                    DB.curSQL = "SELECT [ID], [DocNumber], [DocName], REPLACE(FORMAT([DocTime], 'MMM dd HH:mm', 'uk-UA'), '.', '') AS FormattedDocTime, [DocStatus], BookUsers.Username FROM [dbo].[DocPrih] LEFT OUTER JOIN BookUsers ON [dbo].[DocPrih].[UserID] = BookUsers.UserID";
                    List<PrihodHead> prihodHeadList = DB.getPrihodList(DB.curSQL);

                    // Очистіть список перед додаванням нових даних
                    list.clear();
                    list.addAll(prihodHeadList); // Додаємо результати до списку list

                    // Оновити адаптер на головному потоці (UI-потоці)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapterHeadPrih.notifyDataSetChanged();
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
        myAdapterHeadPrih = new MyAdapterHeadPrih(this, list); // Передаємо список list до адаптера
        try {
            recyclerView.setAdapter(myAdapterHeadPrih);
        }catch (Exception e){
            String s=e.getMessage().toString();
        }
        myAdapterHeadPrih = new MyAdapterHeadPrih(this, list);
        recyclerView.setAdapter(myAdapterHeadPrih);

        myAdapterHeadPrih.setOnItemLongClickListener(new MyAdapterHeadPrih.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                // Отримайте список виділених елементів з адаптера
                ArrayList<Integer> selectedItemsList = myAdapterHeadPrih.getSelectedItems();


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
        myAdapterHeadPrih.filter(searchText);
    }
    private void filterAndRefreshList(boolean switch1State, boolean switch2State, boolean switch3State, boolean switch4State, boolean switch5State, boolean switch6State) {
        list.clear(); // Очистіть поточний список
        List<PrihodHead> filteredList = new ArrayList<>();

        for (PrihodHead item : myAdapterHeadPrih.getOriginalList()) {
            if ((switch1State && item.getDocStatus() == 0) ||
                    (switch2State && item.getDocStatus() == 1) ||
                    (switch3State && item.getDocStatus() == 2) ||
                    (switch4State && item.getDocStatus() == 3)) {
                filteredList.add(item);
            }
        }

        if (switch5State || switch6State) {
            // Виконуйте сортування за полем "time" для нових переключателів 5 і 6
            Collections.sort(filteredList, new Comparator<PrihodHead>() {
                @Override
                public int compare(PrihodHead o1, PrihodHead o2) {
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
        myAdapterHeadPrih.notifyDataSetChanged(); // Оновіть RecyclerView
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
            DB.curSQL = "SELECT [ID], [DocNumber], [DocName], REPLACE(FORMAT([DocTime], 'MMM dd HH:mm', 'uk-UA'), '.', '') AS FormattedDocTime, [DocStatus], BookUsers.Username FROM [dbo].[DocPrih] LEFT OUTER JOIN BookUsers ON [dbo].[DocPrih].[UserID] = BookUsers.UserID";
            List<PrihodHead> prihodHeadList = DB.getPrihodList(DB.curSQL);
            list.addAll(prihodHeadList); // Додаємо результати до списку list
        }

        filterAndSortList(switch1State, switch2State, switch3State, switch4State, switch5State, switch6State);

        myAdapterHeadPrih.notifyDataSetChanged();
    }

    private void filterAndSortList(boolean switch1State, boolean switch2State, boolean switch3State, boolean switch4State, boolean switch5State, boolean switch6State) {
        List<PrihodHead> filteredList = new ArrayList<>(list); // Створіть копію поточного списку

        // Виконуйте фільтрацію
        for (PrihodHead item : list) {
            if (!((switch1State && item.getDocStatus() == 0) ||
                    (switch2State && item.getDocStatus() == 1) ||
                    (switch3State && item.getDocStatus() == 2) ||
                    (switch4State && item.getDocStatus() == 3))) {
                filteredList.remove(item);
            }
        }

        if (switch5State || switch6State) {
            // Виконуйте сортування за полем "time" для нових переключателів 5 і 6
            Collections.sort(filteredList, new Comparator<PrihodHead>() {
                @Override
                public int compare(PrihodHead o1, PrihodHead o2) {
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
        myAdapterHeadPrih.notifyDataSetChanged(); // Оновіть RecyclerView
    }
    public void StartWorkPrih(View view) {
        try {
            ArrayList<String> selectedDocumentNumbers = myAdapterHeadPrih.getSelectedDocumentNumbers();
            Intent intent = new Intent(this, PrihodWork.class);
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
