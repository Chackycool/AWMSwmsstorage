package com.rsm.wmsstorage;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rsm.wmsequip.R;

import java.util.ArrayList;

public class MyAdapterVudachaHead extends RecyclerView.Adapter<MyAdapterVudachaHead.MyViewHolder> {
    Context context;
    ArrayList<VudachaHeadData> list;
    public ArrayList<VudachaHeadData> originalList;

    private ArrayList<Integer> selectedItems = new ArrayList<>();

    private ArrayList<String> selectedDocumentNumbers = new ArrayList<>();

    public ArrayList<String> getSelectedDocumentNumbers() {
        return selectedDocumentNumbers;
    }
    public MyAdapterVudachaHead(Context context, ArrayList<VudachaHeadData> list) {
        this.context = context;
        this.list = list;
        this.originalList = new ArrayList<>(list);
    }
    public interface OnSelectedItemChangeListener {
        void onSelectedItemsChanged(ArrayList<Integer> selectedItems);
    }

    public void filter(String text) {
        text = text.toLowerCase().trim(); // Перетворіть текст у нижній регістр та видаліть зайві пробіли

        list.clear(); // Очистіть поточний список
        if (text.isEmpty()) {
            // Якщо текст пошуку порожній, покажіть весь список
            list.addAll(originalList);
        } else {
            // Інакше додайте елементи, які відповідають тексту пошуку
            for (VudachaHeadData item : originalList) {
                if (String.valueOf(item.getDocNumber()).toLowerCase().contains(text)) {
                    list.add(item);
                }
            }
        }

        notifyDataSetChanged(); // Оновіть RecyclerView
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.vudacha_style, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v, 0);

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    longClickListener.onItemLongClick(view, position);

                }
                return true;
            }
        });

        return viewHolder;
    }
    public ArrayList<VudachaHeadData> getOriginalList() {
        return originalList;
    }
    public ArrayList<Integer> getSelectedItems() {
        return selectedItems;
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Перевірте, чи поточний елемент виділений
        // Перевірте, чи поточний елемент виділений
        if (selectedItems.contains(position)) {
            // Змініть фон виділеного елемента
            holder.ok.setVisibility(View.VISIBLE);
        } else {
            // Скиньте фон та відновіть оригінальні значення elevation та corner radius
            holder.ok.setVisibility(View.INVISIBLE);
        }

        try {
            VudachaHeadData vudachaHeadData =list.get(position);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (longClickListener != null) {
                        int clickedPosition = holder.getAdapterPosition();
                        String docNumber = String.valueOf(list.get(clickedPosition).getDocNumber());
                        // Перевірте, чи елемент вже був виділений
                        if (selectedItems.contains(clickedPosition)) {
                            // Якщо так, зняти виділення
                            selectedItems.remove(Integer.valueOf(clickedPosition));
                            selectedDocumentNumbers.remove(docNumber);
                        } else {
                            // Інакше додайте елемент до списку виділених
                            selectedItems.add(clickedPosition);
                            selectedDocumentNumbers.add(docNumber);
                        }

                        // Оновіть елемент RecyclerView, щоб оновити фон елементів
                        notifyDataSetChanged();
                        if (!selectedItems.isEmpty()) {
                            Vudacha.buttonStartWork.setVisibility(View.VISIBLE);
                        } else {
                            Vudacha.buttonStartWork.setVisibility(View.INVISIBLE);
                        }
                    }
                    return true;
                }
            });
            holder.docnum.setText(String.valueOf(vudachaHeadData.getDocNumber()));
            holder.docname.setText(vudachaHeadData.getDocName());
            holder.time.setText(vudachaHeadData.getDocTime());
            // Перевірте значення docStatus та встановіть відповідний текст
            if (vudachaHeadData.getDocStatus() == 0) {
                holder.docstatus.setText("В роботі");
                holder.statusImage.setImageResource(R.drawable.greenpoint);
            } else if (vudachaHeadData.getDocStatus() == 1) {
                holder.docstatus.setText("В комірці");
                holder.statusImage.setImageResource(R.drawable.bluepoint);
            }else if (vudachaHeadData.getDocStatus() == 2) {
                holder.docstatus.setText("Опрацьовується");
                holder.statusImage.setImageResource(R.drawable.yelowpoint);
            }
            else {
                // Додайте інші умови за необхідності
                holder.docstatus.setText("Закритий");
                holder.statusImage.setImageResource(R.drawable.redpoint);
            }
            holder.client.setText(vudachaHeadData.getClient());

        }catch (Exception e){
            String s= e.getMessage().toString();
        }


    }
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public  static  class MyViewHolder extends RecyclerView.ViewHolder {
        TextView docnum,docname,time,docstatus,client;
        ImageView statusImage,ok;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView,int status) {
            super(itemView);
            docnum = itemView.findViewById(R.id.textName);
            docname = itemView.findViewById(R.id.textNameRight);
            time = itemView.findViewById(R.id.texttime);
            docstatus = itemView.findViewById(R.id.StatusText);
            client = itemView.findViewById(R.id.UserWork);
            statusImage = itemView.findViewById(R.id.Status);
            cardView = itemView.findViewById(R.id.card);
            ok = itemView.findViewById(R.id.ok);

        }
    }
}
