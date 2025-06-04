package com.murti.murtiorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderModel> orderList;

    public OrderAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        holder.tvCustomerName.setText("ग्राहक: " + order.getCustomerName());
        holder.tvMurtiType.setText("प्रकार: " + order.getMurtiType());
        holder.tvSize.setText("आकार: " + order.getSize());
        holder.tvPrice.setText("किंमत: ₹" + order.getPrice());
        holder.tvDeliveryDate.setText("डिलिव्हरी दिनांक: " + order.getDeliveryDate());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvMurtiType, tvSize, tvPrice, tvDeliveryDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvMurtiType = itemView.findViewById(R.id.tvMurtiType);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDeliveryDate = itemView.findViewById(R.id.tvDeliveryDate);
        }
    }
}
