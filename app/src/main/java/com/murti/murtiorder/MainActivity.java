package com.murti.murtiorder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


public class MainActivity extends AppCompatActivity {

    CardView cardAddOrder, cardViewOrders, cardReports, cardInventory;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardAddOrder = findViewById(R.id.cardAddOrder);
        cardViewOrders = findViewById(R.id.cardViewOrders);
        cardReports = findViewById(R.id.cardReports);
        cardInventory = findViewById(R.id.cardInventory);

        cardAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open AddOrderActivity
                Intent intent = new Intent(MainActivity.this, AddOrderActivity.class);
                startActivity(intent);
            }
        });

        cardViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open OrderListActivity
                Intent intent = new Intent(MainActivity.this, OrderListActivity.class);
                startActivity(intent);
            }
        });

        cardReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open ReportsActivity
                Intent intent = new Intent(MainActivity.this, ReportsActivity.class);
                startActivity(intent);
            }
        });

        cardInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Open InventoryActivity
                Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });
    }
}
