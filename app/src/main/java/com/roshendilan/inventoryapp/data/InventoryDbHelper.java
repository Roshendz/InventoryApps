package com.roshendilan.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Roshen Dilan on 18/07/2020 20:55
 * LinkedIn: https://www.linkedin.com/in/roshen-dilan/
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "store.db";
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    public InventoryDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE =  "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + " ("
                + InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_DESCRIPTION + " TEXT, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE + " DOUBLE NOT NULL, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_IN_STOCK + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE + " BLOB, "
                + InventoryContract.InventoryEntry.COLUMN_ORDER_PHONE_NUM + " TEXT, "
                + InventoryContract.InventoryEntry.COLUMN_ORDER_EMAIL+ " TEXT );";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
