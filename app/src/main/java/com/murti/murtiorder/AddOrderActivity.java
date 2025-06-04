package com.murti.murtiorder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class AddOrderActivity extends AppCompatActivity {

    private EditText etCustomerName, etMobileNumber, etHeight, etTotalAmount, etPaidAmount, etQuantity;
    private TextView tvDeliveryDate, tvRemainingAmount;
    private Spinner spinnerMurtiType, spinnerOrderStatus;
    private Button btnSaveOrder;

    private String selectedMurtiType = "", selectedOrderStatus = "";
    private int pendingHeight = -1;
    private String pendingTotal = "", pendingPaid = "", pendingQuantity = "", pendingDeliveryDate = "";

    private DatabaseReference ordersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        // Firebase Initialization
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Initialize views
        etCustomerName = findViewById(R.id.etCustomerName);
        etMobileNumber = findViewById(R.id.etPhone);
        etHeight = findViewById(R.id.etHeight);
        etTotalAmount = findViewById(R.id.etTotalPrice);
        etPaidAmount = findViewById(R.id.etPaidAmount);
        etQuantity = findViewById(R.id.etQuantity);
        tvDeliveryDate = findViewById(R.id.tvDeliveryDate);
        tvRemainingAmount = findViewById(R.id.tvRemainingAmount);
        spinnerMurtiType = findViewById(R.id.spinnerType);
        spinnerOrderStatus = findViewById(R.id.spinnerOrderStatus);
        btnSaveOrder = findViewById(R.id.btnSaveOrder);

        // Spinner for Murti Type
        String[] murtiTypes = {"सकाळी मूर्ती", "संध्याकाळी मूर्ती", "विशेष मूर्ती"};
        spinnerMurtiType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, murtiTypes));
        spinnerMurtiType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMurtiType = murtiTypes[position];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner for Order Status
        String[] orderStatuses = {"प्रलंबित", "पूर्ण", "रद्द"};
        spinnerOrderStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, orderStatuses));
        spinnerOrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOrderStatus = orderStatuses[position];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Delivery Date Picker
        tvDeliveryDate.setOnClickListener(v -> showDatePicker());

        // Auto calculate remaining amount
        TextWatcher amountWatcher = new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(android.text.Editable s) {
                calculateRemainingAmount();
            }
        };
        etTotalAmount.addTextChangedListener(amountWatcher);
        etPaidAmount.addTextChangedListener(amountWatcher);

        // Save Button
        btnSaveOrder.setOnClickListener(v -> validateAndSaveOrder());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            tvDeliveryDate.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void calculateRemainingAmount() {
        try {
            double total = Double.parseDouble(etTotalAmount.getText().toString().trim());
            double paid = Double.parseDouble(etPaidAmount.getText().toString().trim());
            double remaining = total - paid;
            tvRemainingAmount.setText("₹ " + remaining);
        } catch (Exception e) {
            tvRemainingAmount.setText("₹ 0");
        }
    }

    private void validateAndSaveOrder() {
        String name = etCustomerName.getText().toString().trim();
        String mobile = etMobileNumber.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String totalStr = etTotalAmount.getText().toString().trim();
        String paidStr = etPaidAmount.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String deliveryDate = tvDeliveryDate.getText().toString().trim();

        if (name.isEmpty()) {
            etCustomerName.setError("कृपया नाव भरा");
            return;
        }

        if (mobile.length() != 10) {
            etMobileNumber.setError("10 अंकी मोबाईल नंबर भरा");
            return;
        }

        if (heightStr.isEmpty()) {
            etHeight.setError("उंची आवश्यक आहे");
            return;
        }

        int height;
        try {
            height = Integer.parseInt(heightStr);
        } catch (NumberFormatException e) {
            etHeight.setError("योग्य उंची भरा");
            return;
        }

        if (height > 0 && height <= 12) {
            pendingHeight = height;
            pendingTotal = totalStr;
            pendingPaid = paidStr;
            pendingQuantity = quantityStr;
            pendingDeliveryDate = deliveryDate;
            showHeightUnitDialog();
            return;
        }

        if (height < 1 || height > 300) {
            etHeight.setError("1 ते 300 दरम्यान उंची भरा");
            return;
        }

        saveOrder(name, mobile, height + " से.मी.", totalStr, paidStr, quantityStr, deliveryDate);
    }

    private void showHeightUnitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("उंची एकक निवडा")
                .setMessage(pendingHeight + " टाकले आहे. हे फूट आहे की से.मी. ?")
                .setPositiveButton("फूट", (dialog, which) -> saveOrder(
                        etCustomerName.getText().toString().trim(),
                        etMobileNumber.getText().toString().trim(),
                        pendingHeight + " फूट",
                        pendingTotal, pendingPaid, pendingQuantity, pendingDeliveryDate))
                .setNegativeButton("से.मी.", (dialog, which) -> saveOrder(
                        etCustomerName.getText().toString().trim(),
                        etMobileNumber.getText().toString().trim(),
                        pendingHeight + " से.मी.",
                        pendingTotal, pendingPaid, pendingQuantity, pendingDeliveryDate))
                .setCancelable(false)
                .show();
    }

    private void saveOrder(String name, String mobile, String height, String totalAmount,
                           String paidAmount, String quantity, String deliveryDate) {

        String remainingAmount = tvRemainingAmount.getText().toString().replace("₹ ", "");
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> orderData = new HashMap<>();
        orderData.put("customerName", name);
        orderData.put("mobileNumber", mobile);
        orderData.put("murtiType", selectedMurtiType);
        orderData.put("height", height);
        orderData.put("quantity", quantity);
        orderData.put("totalAmount", totalAmount);
        orderData.put("paidAmount", paidAmount);
        orderData.put("remainingAmount", remainingAmount);
        orderData.put("deliveryDate", deliveryDate);
        orderData.put("orderStatus", selectedOrderStatus);
        orderData.put("timestamp", timestamp);

        ordersRef.child(mobile)
                .setValue(orderData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "ऑर्डर सेव झाली!", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "सेव्ह करण्यात अयशस्वी: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void clearForm() {
        etCustomerName.setText("");
        etMobileNumber.setText("");
        etHeight.setText("");
        etTotalAmount.setText("");
        etPaidAmount.setText("");
        etQuantity.setText("");
        tvDeliveryDate.setText("");
        tvRemainingAmount.setText("₹ 0");
        spinnerMurtiType.setSelection(0);
        spinnerOrderStatus.setSelection(0);
    }

    // Utility for simplified text watcher
    abstract class SimpleTextWatcher implements TextWatcher {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
