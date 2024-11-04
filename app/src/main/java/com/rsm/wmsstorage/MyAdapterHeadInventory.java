package com.rsm.wmsstorage;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rsm.wmsequip.R;

import java.util.ArrayList;

public class MyAdapterHeadInventory extends RecyclerView.Adapter<MyAdapterHeadInventory.ViewHolder> {

    private ArrayList<InventoryData> dataList;
    private int selectedPosition = -1;

    // Конструктор, який приймає список даних
    public MyAdapterHeadInventory(ArrayList<InventoryData> dataList) {
        this.dataList = dataList;
    }

    // Клас ViewHolder для кожного елемента RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView prodNameTextView;
        public TextView qtyTextView;
        public ImageView plusButton;
        public ImageView minusButton;

        public ViewHolder(View itemView) {
            super(itemView);
            prodNameTextView = itemView.findViewById(R.id.ProdName);
            qtyTextView = itemView.findViewById(R.id.QTY);
            try {
                plusButton = itemView.findViewById(R.id.Plus);
                minusButton = itemView.findViewById(R.id.Minus);

            }catch (Exception ex){
                String s=ex.getMessage().toString();
            }

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View dataItemView = inflater.inflate(R.layout.inventory_style_list, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(dataItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        InventoryData dataItem = dataList.get(position);

        // Set item views based on your views and data model
        TextView prodNameTextView = holder.prodNameTextView;
        prodNameTextView.setText(dataItem.getProdName());

        TextView qtyTextView = holder.qtyTextView;
        qtyTextView.setText(String.valueOf(dataItem.getQty()));

        // Clear the text color before setting a new one
        qtyTextView.setTextColor(Color.BLACK);

        // Set text color based on quantity change
        if (dataItem.getQty() > dataItem.getInitialQty()) {
            qtyTextView.setTextColor(Color.GREEN); // Set to green if quantity increased
        } else if (dataItem.getQty() < dataItem.getInitialQty()) {
            qtyTextView.setTextColor(Color.RED); // Set to red if quantity decreased
        }

        // Додайте обробник події для кнопки "+"
        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Отримайте контекст з елемента view
                Context context = view.getContext();
                // Збільште кількість на 1 та оновіть базу даних
                Double newQty = dataItem.getQty() + 1;
                dataItem.setQty(newQty);
                Fastchange(context, dataItem.getSC(), newQty);
                // Оновіть відображення списку
                updateData(newQty, dataItem.getSC());
            }
        });

        // Додайте обробник події для кнопки "-"
        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Отримайте контекст з елемента view
                Context context = view.getContext();
                // Зменште кількість на 1 та оновіть базу даних
                Double newQty = dataItem.getQty() - 1;
                dataItem.setQty(newQty);
                Fastchange(context, dataItem.getSC(), newQty);
                // Оновіть відображення списку
                updateData(newQty, dataItem.getSC());
            }
        });

        // Додайте обробник контекстного меню
        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {

            // Заповнюємо меню з файлу ресурсів XML
            MenuInflater inflater = new MenuInflater(v.getContext());
            inflater.inflate(R.menu.context_menu, menu);
            selectedPosition = holder.getAdapterPosition();

            // Відтворіть анімацію блимання
            Animation animation = AnimationUtils.loadAnimation(v.getContext(), R.anim.item_selected_animation);
            v.startAnimation(animation);
        });
    }

    private void Fastchange(Context context, int SC, Double qty) {
        // Ваш код для обробки натискання кнопки "Зберегти"
        String sql = "UPDATE Analit SET Qty = " + qty + " WHERE SC = " + SC;
        DB.updateBD(sql);

    }

    public void updateData(Double newQuantity, int SC) {
        // Оновіть дані у вашому списку dataList
        for (InventoryData item : dataList) {
            if (item.getSC() == SC) {
                item.setQty(newQuantity);
                break; // Якщо ви знайшли відповідний елемент, завершіть цикл
            }
        }
        // Після оновлення даних, викличте notifyDataSetChanged() для поновлення відображення
        notifyDataSetChanged();
    }


    public int getSelectedPosition() {
        return selectedPosition;
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
