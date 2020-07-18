package com.roshendilan.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Roshen Dilan on 18/07/2020 20:55
 * LinkedIn: https://www.linkedin.com/in/roshen-dilan/
 */

public class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.roshendilan.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventory";

    private InventoryContract() {}

    public static final class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public final static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME ="product_name";

        public final static String COLUMN_PRODUCT_DESCRIPTION ="product_desc";

        public final static String COLUMN_PRODUCT_PRICE ="product_price";

        public final static String COLUMN_PRODUCT_IN_STOCK="product_in_stock";

        public final static String COLUMN_PRODUCT_IMAGE="product_image";

        public final static String COLUMN_ORDER_PHONE_NUM="product_phone";

        public final static String COLUMN_ORDER_EMAIL="product_email";

    }

}
