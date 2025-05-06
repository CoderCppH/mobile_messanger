package com.example.myapplication.LIST_USER;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<p_user_item> items;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public UserAdapter(LayoutInflater inflater, List<p_user_item> items) {
        this.inflater = inflater;
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_custom_item_list_user, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        p_user_item item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Добавим метод для добавления элементов в адаптер
    public void addItem(p_user_item item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView nameText;
        TextView emailText;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            icon = itemView.findViewById(R.id.id_const_item_user_list_image_profile);
            nameText = itemView.findViewById(R.id.id_const_item_user_list_full_name);
            emailText = itemView.findViewById(R.id.id_const_item_user_list_email);

            if (listener != null) {
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });
            }
        }

        public void bind(p_user_item item) {
            icon.setImageResource(item.getImage());
            nameText.setText(item.getFullName());
            emailText.setText(item.getEmail());
        }
    }
}
