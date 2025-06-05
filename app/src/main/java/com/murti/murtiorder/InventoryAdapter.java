package com.murti.murtiorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<InventoryModel> inventoryList;
    private final OnInventoryActionListener actionListener;
    private int lastAnimatedPosition = -1;
    private final Context context;

    public interface OnInventoryActionListener {
        void onDelete(InventoryModel product);
        void onEdit(InventoryModel product);
    }

    public InventoryAdapter(List<InventoryModel> inventoryList, OnInventoryActionListener actionListener, Context context) {
        this.inventoryList = inventoryList;
        this.actionListener = actionListener;
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        InventoryModel item = inventoryList.get(position);
        holder.name.setText(item.getName());
        holder.price.setText("₹" + item.getPrice());

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .centerCrop()
                .into(holder.image);

        holder.deleteIcon.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                actionListener.onDelete(inventoryList.get(pos));
            }
        });

        holder.editIcon.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                actionListener.onEdit(inventoryList.get(pos));
            }
        });

        // Apply animation safely with position check
        if (position > lastAnimatedPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.shutter_down);
            animation.setStartOffset(position * 150); // Stagger effect
            holder.itemView.startAnimation(animation);
            lastAnimatedPosition = position;
        }
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull InventoryViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation(); // Clear animation when view is detached
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        ImageView image, deleteIcon, editIcon;
        TextView name, price;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageProduct);
            deleteIcon = itemView.findViewById(R.id.btnDelete);
            editIcon = itemView.findViewById(R.id.btnEdit);
            name = itemView.findViewById(R.id.textProductName);
            price = itemView.findViewById(R.id.textProductPrice);
        }
    }
}