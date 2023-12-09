package com.example.sqlite;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper dbHelper;
    private TextView totalCostView;
    private TextView totalCountView;
    private CustomAdapter adapter;
    private String currentSortOrder = DatabaseHelper.COLUMN_ITEM_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        totalCostView = findViewById(R.id.totalCost);
        totalCountView = findViewById(R.id.totalCount);

        dbHelper = new DatabaseHelper(this);
        updateListView(currentSortOrder);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(view -> showAddItemDialog());

        Button sortByNameButton = findViewById(R.id.sortByNameButton);
        Button sortByTypeButton = findViewById(R.id.sortByTypeButton);
        Button sortByPriceButton = findViewById(R.id.sortByPriceButton);

        sortByNameButton.setOnClickListener(view -> {
            currentSortOrder = DatabaseHelper.COLUMN_ITEM_NAME;
            updateListView(currentSortOrder);
        });
        sortByTypeButton.setOnClickListener(view -> {
            currentSortOrder = DatabaseHelper.COLUMN_ITEM_TYPE;
            updateListView(currentSortOrder);
        });
        sortByPriceButton.setOnClickListener(view -> {
            currentSortOrder = DatabaseHelper.COLUMN_PRICE;
            updateListView(currentSortOrder);
        });
    }

    private void updateListView(String sortBy) {
        Cursor cursor = dbHelper.getAllItems(sortBy);
        if (adapter == null) {
            adapter = new CustomAdapter(this, cursor, dbHelper, sortBy);
            listView.setAdapter(adapter);
        } else {
            adapter.setCurrentSortOrder(sortBy);
            adapter.changeCursor(cursor);
        }
        updateTotalCost();
        updateTotalCount();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    void updateTotalCost() {
        double totalCost = dbHelper.getTotalCost();
        totalCostView.setText("Общая стоимость: " + String.format("%.2f",totalCost));
    }

    @SuppressLint("SetTextI18n")
    void updateTotalCount() {
        int totalCount = dbHelper.getTotalCount();
        totalCountView.setText("Всего записей: " + totalCount);
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить элемент");
        LayoutInflater inflater = this.getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.item_input, null);

        final EditText inputName = viewInflated.findViewById(R.id.input_item_name);
        final EditText inputType = viewInflated.findViewById(R.id.input_item_type);
        final EditText inputPrice = viewInflated.findViewById(R.id.input_item_price);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String itemName = inputName.getText().toString();
            String itemType = inputType.getText().toString();
            double price = Double.parseDouble(inputPrice.getText().toString());
            dbHelper.addItem(itemName, itemType, price);
            updateListView(currentSortOrder);
            dialog.dismiss();
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
