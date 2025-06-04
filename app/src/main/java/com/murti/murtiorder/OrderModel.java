package com.murti.murtiorder;
public class OrderModel {
    private String orderId;
    private String customerName;
    private String murtiType;
    private String size;
    private String price;
    private String deliveryDate;

    public OrderModel() {
        // Required for Firebase
    }

    public OrderModel(String orderId, String customerName, String murtiType, String size, String price, String deliveryDate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.murtiType = murtiType;
        this.size = size;
        this.price = price;
        this.deliveryDate = deliveryDate;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getMurtiType() { return murtiType; }
    public String getSize() { return size; }
    public String getPrice() { return price; }
    public String getDeliveryDate() { return deliveryDate; }
}
