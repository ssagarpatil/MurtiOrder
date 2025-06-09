package com.murti.murtiorder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    RecyclerView recyclerOrders;
    SearchView searchView;
    List<OrderModel> orderList;
    OrderAdapter orderAdapter;
    DatabaseReference ordersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        searchView = findViewById(R.id.searchView);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        recyclerOrders.setAdapter(orderAdapter);

        // Firebase reference
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Load data
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot mobileSnapshot : snapshot.getChildren()) {
                    OrderModel order = mobileSnapshot.getValue(OrderModel.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                orderAdapter = new OrderAdapter(OrderListActivity.this, orderList); // refresh adapter
                recyclerOrders.setAdapter(orderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderListActivity.this, "डेटा लोड करण्यात अडचण: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                orderAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                orderAdapter.filter(newText);
                return true;
            }
        });
    }
}
