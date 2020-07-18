package com.roshendilan.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Roshen Dilan on 18/07/2020 20:55
 * LinkedIn: https://www.linkedin.com/in/roshen-dilan/
 */

import com.roshendilan.inventoryapp.data.InventoryContract;


public class InventoryCursorAdapter extends CursorAdapter implements View.OnClickListener {

    Cursor currentCursor;
    int quantity;
    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productNameListView = (TextView) view.findViewById(R.id.productNameListView);
        TextView productPriceListView = (TextView) view.findViewById(R.id.productPriceListView);
        TextView productQuantityListView = (TextView) view.findViewById(R.id.productQuantityListView);
        ImageView saleImageButton = (ImageView) view.findViewById(R.id.saleImageButton);

        int productNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IN_STOCK);

        String productName = cursor.getString(productNameColumnIndex);
        String productPrice = cursor.getString(productPriceColumnIndex);
        String productQuantity = cursor.getString(productQuantityColumnIndex);

        productNameListView.setText(productName);
        productPriceListView.setText("Price " + productPrice);
        productQuantityListView.setText(productQuantity);

        saleImageButton.setOnClickListener(this);
        currentCursor = cursor;
        quantity = Integer.parseInt(productQuantity);
    }

    @Override
    public void onClick(View v) {

        if (quantity > 0) {
            int idColumnIndex = currentCursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
            int nameColumnIndex = currentCursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int descColumnIndex = currentCursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_DESCRIPTION);
            int priceColumnIndex = currentCursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int inStockColumnIndex = currentCursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IN_STOCK);
            int imageColumnIndex = currentCursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE);

            ContentValues values = new ContentValues();
            int id = currentCursor.getInt(idColumnIndex);
            String name = currentCursor.getString(nameColumnIndex);
            String desc = "";
            if (descColumnIndex != -1) {
                desc = currentCursor.getString(descColumnIndex);
                values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_DESCRIPTION, desc);
            }
            double price = currentCursor.getDouble(priceColumnIndex);
            int inStock = currentCursor.getInt(inStockColumnIndex);
            byte[] imgByte = null;
            if (imageColumnIndex != -1) {
            imgByte = currentCursor.getBlob(imageColumnIndex);
            }
            inStock = inStock -1;

            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, name);
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, price);
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IN_STOCK, inStock);

            if (imgByte != null) {
                values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE, imgByte);
            }

            Uri currentProductUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);

            int rowsAffected = v.getContext().getContentResolver().update(currentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(v.getContext(), "Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "Product sale!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
