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
        holder.txtCustomerName.setText("नाव: " + order.customerName);
        holder.txtMobile.setText("मोबाईल: " + order.mobileNumber);
        holder.txtMurtiType.setText("प्रकार: " + order.murtiType);
        holder.txtHeight.setText("उंची: " + order.height);
        holder.txtQuantity.setText("प्रमाण: " + order.quantity);
        holder.txtTotal.setText("एकूण: ₹" + order.totalAmount);
        holder.txtPaid.setText("भरलेले: ₹" + order.paidAmount);
        holder.txtRemaining.setText("शिल्लक: ₹" + order.remainingAmount);
        holder.txtDelivery.setText("डिलिव्हरी: " + order.deliveryDate);
        holder.txtStatus.setText("स्थिती: " + order.orderStatus);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView txtCustomerName, txtMobile, txtMurtiType, txtHeight, txtQuantity,
                txtTotal, txtPaid, txtRemaining, txtDelivery, txtStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtMobile = itemView.findViewById(R.id.txtMobile);
            txtMurtiType = itemView.findViewById(R.id.txtMurtiType);
            txtHeight = itemView.findViewById(R.id.txtHeight);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtPaid = itemView.findViewById(R.id.txtPaid);
            txtRemaining = itemView.findViewById(R.id.txtRemaining);
            txtDelivery = itemView.findViewById(R.id.txtDelivery);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
