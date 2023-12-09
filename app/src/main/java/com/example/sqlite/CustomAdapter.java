package com.example.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CustomAdapter extends CursorAdapter {

    private DatabaseHelper dbHelper;
    private MainActivity mainActivity;
    private String currentSortOrder;

    public CustomAdapter(Context context, Cursor cursor, DatabaseHelper dbHelper, String currentSortOrder) {
        super(context, cursor, 0);
        this.dbHelper = dbHelper;
        this.mainActivity = (MainActivity) context;
        this.currentSortOrder = currentSortOrder;
    }

    public void setCurrentSortOrder(String sortOrder) {
        this.currentSortOrder = sortOrder;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView itemName = view.findViewById(R.id.itemName);
        TextView itemType = view.findViewById(R.id.itemType);
        TextView itemPrice = view.findViewById(R.id.itemPrice);
        Button deleteButton = view.findViewById(R.id.deleteButton);

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_NAME));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_TYPE));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE));

        itemName.setText(name);
        itemType.setText(type);
        itemPrice.setText(price);

        deleteButton.setOnClickListener(v -> {
            dbHelper.deleteItem(id);
            this.changeCursor(dbHelper.getAllItems(currentSortOrder));
            mainActivity.updateTotalCost();
            mainActivity.updateTotalCount();
        });
    }
}

