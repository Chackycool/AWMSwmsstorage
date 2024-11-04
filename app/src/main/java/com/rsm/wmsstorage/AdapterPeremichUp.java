package com.rsm.wmsstorage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rsm.wmsequip.R;

import java.util.ArrayList;

public class AdapterPeremichUp extends RecyclerView.Adapter<AdapterPeremichUp.ViewHolder> {


    private ArrayList<PeremichUpData> dataList;
    private OnDownClickListener downClickListener;
    private OnDetailClickListener detailClickListener;
    // Інтерфейс для обробки натискання на кнопку "Деталі"
    public interface OnDetailClickListener {
        void onDetailClick(int position);
    }
    public interface OnDownClickListener {
        void onDownlClick(int position); // Змінено з onDownClick на onDownlClick
    }

    public AdapterPeremichUp(ArrayList<PeremichUpData> dataList, OnDetailClickListener detailClickListener,OnDownClickListener downClickListener) {
        this.dataList = dataList;
        this.detailClickListener = detailClickListener;
        this.downClickListener =downClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peremich_style_up, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeremichUpData data = dataList.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView SC, ProdName, qty;
        private ImageView detail,down;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            SC = itemView.findViewById(R.id.SC);
            ProdName = itemView.findViewById(R.id.ProdName);
            qty = itemView.findViewById(R.id.qty);
            detail = itemView.findViewById(R.id.detail);
            down=itemView.findViewById(R.id.down);
            // Обробник натискання на кнопку "Деталі"
            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detailClickListener != null) {
                        detailClickListener.onDetailClick(getAdapterPosition());
                    }
                }
            });
            // Обробник натискання на кнопку "Down"
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downClickListener != null) {
                        downClickListener.onDownlClick(getAdapterPosition());
                    }
                }
            });
        }


        public void bind(PeremichUpData data) {
            SC.setText(String.valueOf(data.getSC()));
            ProdName.setText(String.valueOf(data.getProdName()));
            qty.setText(String.valueOf(data.getQty()));
        }
    }

}