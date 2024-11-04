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

public class AdapterPeremichDown extends RecyclerView.Adapter<AdapterPeremichDown.ViewHolder> {

    private ArrayList<PeremichDownData> dataList;
    private OnUpClickListener upClickListener;
    private OnDetailClickListener detailClickListener;

    public interface OnDetailClickListener {
        void onDetailClick(int position);
    }
    public interface OnUpClickListener {
        void onUplClick(int position);
    }
    public AdapterPeremichDown(ArrayList<PeremichDownData> dataList, AdapterPeremichDown.OnDetailClickListener detailClickListener, AdapterPeremichDown.OnUpClickListener upClickListener) {
        this.dataList = dataList;
        this.detailClickListener = detailClickListener;
        this.upClickListener =upClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peremich_style_down, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeremichDownData data = dataList.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView SC, Prodname,Qty;
        private ImageView detail,up;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            SC = itemView.findViewById(R.id.SC);
            Prodname = itemView.findViewById(R.id.ProdName);
            Qty = itemView.findViewById(R.id.QTY);
            detail = itemView.findViewById(R.id.detail);
            up=itemView.findViewById(R.id.up);
            // Обробник натискання на кнопку "Деталі"
            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detailClickListener != null) {
                        detailClickListener.onDetailClick(getAdapterPosition());
                    }
                }
            });
            // Обробник натискання на кнопку "Up"
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (upClickListener != null) {
                        upClickListener.onUplClick(getAdapterPosition());
                    }
                }
            });
        }

        public void bind(PeremichDownData data) {
            SC.setText(String.valueOf(data.getSC()));
            Prodname.setText(String.valueOf(data.getProdName()));
            Qty.setText(String.valueOf(data.getQty()));
        }
    }

}