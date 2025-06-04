package com.murti.murtiorder;

public class OrderModel {
    public String customerName;
    public String mobileNumber;
    public String murtiType;
    public String height;
    public String quantity;
    public String totalAmount;
    public String paidAmount;
    public String remainingAmount;
    public String deliveryDate;
    public String orderStatus;
    public String timestamp;

    public OrderModel() {
        // Default constructor required for calls to DataSnapshot.getValue(OrderModel.class)
    }
}
