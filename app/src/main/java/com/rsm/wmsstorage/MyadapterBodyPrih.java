package com.rsm.wmsstorage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rsm.wmsequip.R;

import java.util.ArrayList;
import java.util.Collections;

public class MyadapterBodyPrih extends RecyclerView.Adapter<MyadapterBodyPrih.MyViewHolder> {
    Context context;
    ArrayList<PrihodBody> list;
    private OnItemSelectedListener itemSelectedListener;
    public ArrayList<PrihodBody> originalList;
    private static OnItemClickListener clickListener;
    RecyclerView recyclerView;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface OnItemSelectedListener {
        void onItemSelected(PrihodBody selectedPrihodBody);
    }
    public void setItemSelectedListener(OnItemSelectedListener listener) {
        this.itemSelectedListener = listener;
    }
    public void updateListAndScrollToTop(String searchText) {
        int index = findItemIndexBySearchText(searchText);
        if (index != -1) {
            moveItemToTop(index);
            notifyDataSetChanged();
            // Прокрутіть до першого елемента без анімації
            recyclerView.scrollToPosition(0);
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.requestLayout();
            // Додайте затримку перед викликом анімації
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Застосувати анімацію до першого елемента
                    animateItem(0);
                }
            }, 100); // Затримка у мілісекундах (можна налаштувати)
        }
    }

    public void animateItem(int position) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_selected_animation);
            viewHolder.itemView.startAnimation(animation);
        }
    }

    private void moveItemToTop(int index) {
        if (index >= 0 && index < list.size()) {
            Collections.swap(list, 0, index);
        }
    }

    private int findItemIndexBySearchText(String searchText) {
        for (int i = 0; i < list.size(); i++) {
            PrihodBody prihodBody = list.get(i);
            if (String.valueOf(prihodBody.getSC()).equals(searchText)) {
                return i;
            }
        }
        return -1;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
    private ArrayList<Integer> selectedItems = new ArrayList<>();
    public MyadapterBodyPrih(Context context, ArrayList<PrihodBody> list,RecyclerView recyclerView) {
        this.context = context;
        this.list = list;
        this.originalList = new ArrayList<>(list);
        this.recyclerView = recyclerView;
    }
    @NonNull
    @Override
    public MyadapterBodyPrih.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.body_prih_list, parent, false);
        final MyadapterBodyPrih.MyViewHolder viewHolder = new MyadapterBodyPrih.MyViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onItemClick(view, position);
                }
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyadapterBodyPrih.MyViewHolder holder, int position) {
        try {
            PrihodBody prihodBody =list.get(position);
            holder.SC.setText(String.valueOf(prihodBody.getSC()));
            holder.ProdName.setText(prihodBody.getProdName());
            holder.plan.setText(String.valueOf(prihodBody.getPlan()));
            holder.fact.setText(String.valueOf(prihodBody.getFact()));

            // Додайте обробник натискань для елемента
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemSelectedListener != null) {
                        itemSelectedListener.onItemSelected(prihodBody);
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_selected_animation);
                        holder.itemView.startAnimation(animation);
                    }
                }
            });
        }catch (Exception ex){
            String s= ex.getMessage().toString();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public  static  class MyViewHolder extends RecyclerView.ViewHolder {

        TextView SC,ProdName,plan,fact;
        ImageView statusImage,ok;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            SC = itemView.findViewById(R.id.SC);
            ProdName = itemView.findViewById(R.id.ProdName);
            plan = itemView.findViewById(R.id.Plan);
            fact = itemView.findViewById(R.id.Fact);
            // Додайте обробник подій простого натискання для кожного елемента списку
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && clickListener != null) {
                        clickListener.onItemClick(view, position);
                    }
                }
            });
        }
    }
}
