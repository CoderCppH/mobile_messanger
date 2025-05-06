package com.example.myapplication.LIST_LENTA;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;


public class LentaAdapter extends RecyclerView.Adapter<com.example.myapplication.LIST_LENTA.LentaAdapter.ViewHolder> {
    private List<p_lenta_item> list_lenta;
    private LayoutInflater lInFlater;
    public  LentaAdapter(LayoutInflater inflater, List<p_lenta_item> list_user) {
        this.lInFlater = inflater;
        this.list_lenta = list_user;
    }

    @NonNull
    @Override
    public com.example.myapplication.LIST_LENTA.LentaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = this.lInFlater.inflate(R.layout.my_custom_item_list_lenta, parent, false);


        return new com.example.myapplication.LIST_LENTA.LentaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.myapplication.LIST_LENTA.LentaAdapter.ViewHolder holder, int position) {
        p_lenta_item item = list_lenta.get(position);
        holder.image.setImageResource(item.image);
        holder.name.setText(item.name);

    }

    @Override
    public int getItemCount() {
        return list_lenta.size();
    }
    public void AddItem(p_lenta_item item) {
        list_lenta.add(item);
        notifyItemInserted(list_lenta.size() - 1);
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.id_item_lenta_name);
            image = itemView.findViewById(R.id.id_item_lenta_image);
        }
    }
}
