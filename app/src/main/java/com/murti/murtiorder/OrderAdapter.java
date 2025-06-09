// OrderAdapter.java
package com.murti.murtiorder;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<OrderModel> orderList;
    private List<OrderModel> fullOrderList;
    private String searchQuery = "";

    public OrderAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.fullOrderList = new ArrayList<>(orderList);
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
        holder.txtCustomerName.setText(highlight("नाव: " + order.customerName));
        holder.txtMobile.setText(highlight("मोबाईल: " + order.mobileNumber));
        holder.txtMurtiType.setText(highlight("प्रकार: " + order.murtiType));
        holder.txtHeight.setText(highlight("उंची: " + order.height));
        holder.txtQuantity.setText(highlight("प्रमाण: " + order.quantity));
        holder.txtTotal.setText(highlight("एकूण: ₹" + order.totalAmount));
        holder.txtPaid.setText(highlight("भरलेले: ₹" + order.paidAmount));
        holder.txtRemaining.setText(highlight("शिल्लक: ₹" + order.remainingAmount));
        holder.txtDelivery.setText(highlight("डिलिव्हरी: " + order.deliveryDate));
        holder.txtStatus.setText(highlight("स्थिती: " + order.orderStatus));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void filter(String text) {
        searchQuery = text.toLowerCase();
        orderList.clear();
        if (searchQuery.isEmpty()) {
            orderList.addAll(fullOrderList);
        } else {
            for (OrderModel order : fullOrderList) {
                if (
                        (order.customerName != null && order.customerName.toLowerCase().contains(searchQuery)) ||
                                (order.mobileNumber != null && order.mobileNumber.toLowerCase().contains(searchQuery)) ||
                                (order.murtiType != null && order.murtiType.toLowerCase().contains(searchQuery)) ||
                                (order.height != null && order.height.toLowerCase().contains(searchQuery)) ||
                                (order.quantity != null && order.quantity.toLowerCase().contains(searchQuery)) ||
                                (order.totalAmount != null && order.totalAmount.toLowerCase().contains(searchQuery)) ||
                                (order.paidAmount != null && order.paidAmount.toLowerCase().contains(searchQuery)) ||
                                (order.remainingAmount != null && order.remainingAmount.toLowerCase().contains(searchQuery)) ||
                                (order.deliveryDate != null && order.deliveryDate.toLowerCase().contains(searchQuery)) ||
                                (order.orderStatus != null && order.orderStatus.toLowerCase().contains(searchQuery))
                ) {
                    orderList.add(order);
                }
            }
        }
        notifyDataSetChanged();
    }

    private Spannable highlight(String text) {
        Spannable spannable = new SpannableString(text);
        if (!searchQuery.isEmpty()) {
            int start = text.toLowerCase().indexOf(searchQuery);
            if (start >= 0) {
                int end = start + searchQuery.length();
                spannable.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        start,
                        end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }
        return spannable;
    }

    public void sortByTotalAmount() {
        Collections.sort(orderList, (o1, o2) -> {
            try {
                int amt1 = Integer.parseInt(o1.totalAmount);
                int amt2 = Integer.parseInt(o2.totalAmount);
                return Integer.compare(amt2, amt1);
            } catch (Exception e) {
                return 0;
            }
        });
        notifyDataSetChanged();
    }

    public void sortByDeliveryDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Collections.sort(orderList, (o1, o2) -> {
            try {
                Date d1 = sdf.parse(o1.deliveryDate);
                Date d2 = sdf.parse(o2.deliveryDate);
                return d1.compareTo(d2);
            } catch (Exception e) {
                return 0;
            }
        });
        notifyDataSetChanged();
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
