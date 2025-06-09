package com.murti.murtiorder;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportsActivity extends AppCompatActivity {

    private TextView tvTotalOrders, tvTotalAmount, tvTotalPaid, tvTotalRemaining, tvTotalMurtiCount;

    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // Initialize views
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvTotalPaid = findViewById(R.id.tvTotalPaid);
        tvTotalRemaining = findViewById(R.id.tvTotalRemaining);
        tvTotalMurtiCount = findViewById(R.id.tvTotalMurtiCount);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        fetchReportData();
    }

    private void fetchReportData() {
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalOrders = 0;
                double totalAmount = 0.0;
                double totalPaid = 0.0;
                double totalRemaining = 0.0;
                int totalMurtiCount = 0;

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    totalOrders++;

                    String totalAmountStr = orderSnapshot.child("totalAmount").getValue(String.class);
                    String paidAmountStr = orderSnapshot.child("paidAmount").getValue(String.class);
                    String remainingAmountStr = orderSnapshot.child("remainingAmount").getValue(String.class);
                    String quantityStr = orderSnapshot.child("quantity").getValue(String.class);

                    // Parse safely with defaults
                    totalAmount += parseDoubleSafe(totalAmountStr);
                    totalPaid += parseDoubleSafe(paidAmountStr);
                    totalRemaining += parseDoubleSafe(remainingAmountStr);
                    totalMurtiCount += parseIntSafe(quantityStr);
                }

                // Update UI
                tvTotalOrders.setText("एकूण ऑर्डर: " + totalOrders);
                tvTotalAmount.setText("एकूण रक्कम: ₹" + (int)totalAmount);
                tvTotalPaid.setText("भरलेले: ₹" + (int)totalPaid);
                tvTotalRemaining.setText("शिल्लक: ₹" + (int)totalRemaining);
                tvTotalMurtiCount.setText("एकूण मूर्ती: " + totalMurtiCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportsActivity.this, "डेटा लोड करण्यात अडचण: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double parseDoubleSafe(String val) {
        try {
            if (val != null && !val.isEmpty()) {
                return Double.parseDouble(val);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private int parseIntSafe(String val) {
        try {
            if (val != null && !val.isEmpty()) {
                return Integer.parseInt(val);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
