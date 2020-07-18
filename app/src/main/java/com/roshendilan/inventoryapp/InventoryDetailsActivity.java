package com.roshendilan.inventoryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.roshendilan.inventoryapp.data.InventoryContract;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Roshen Dilan on 18/07/2020 20:55
 * LinkedIn: https://www.linkedin.com/in/roshen-dilan/
 */

public class InventoryDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    ImageView addImageImageView, retrievePhotoImageView;
    EditText productNameEditText,productDescEditText,productPriceEditText,quantityEditText, contactsNumberEditText, contactsEmailEditText;
    ImageButton addQuantityImageButton, reduceQuantityImageButton;
    Button productSaveButton;
    byte[] dataImage;

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri mCurrentInventoryUri;
    private boolean mInventoryHasChanged = false;

    private int currentProducts_in_Stock = 0;
    private final int PICK_IMAGE_GALLERY = 2;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_details);

        Intent intent1 = getIntent();
        mCurrentInventoryUri = intent1.getData();

        productNameEditText = (EditText) findViewById(R.id.productNameEditText);
        productDescEditText = (EditText) findViewById(R.id.productDescEditText);
        productPriceEditText = (EditText) findViewById(R.id.productPriceEditText);
        quantityEditText = (EditText) findViewById(R.id.quantityEditText);
        addQuantityImageButton = (ImageButton) findViewById(R.id.addQuantityImageButton);
        reduceQuantityImageButton = (ImageButton) findViewById(R.id.reduceQuantityImageButton);
        addImageImageView = (ImageView) findViewById(R.id.addImageImageView);
        retrievePhotoImageView = (ImageView) findViewById(R.id.retrievePhotoImageView);
        addImageImageView.setOnClickListener(this);
        productSaveButton = (Button) findViewById(R.id.productSaveButton);
        contactsNumberEditText = (EditText) findViewById(R.id.contactsNumberEditText);
        contactsEmailEditText = (EditText) findViewById(R.id.contactsEmailEditText);

        try {
            if (mCurrentInventoryUri == null){
                quantityEditText.setText("0");
                currentProducts_in_Stock = 0;
                setTitle(getString(R.string.editor_activity_title_new_product));
                productSaveButton.setText("SAVE");
                invalidateOptionsMenu();
            } else {
                setTitle(getString(R.string.editor_activity_title_edit_product));
                productSaveButton.setText("UPDATE");
                getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        productNameEditText.setOnTouchListener(mTouchListener);
        productDescEditText.setOnTouchListener(mTouchListener);
        productPriceEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);
        addQuantityImageButton.setOnTouchListener(mTouchListener);
        reduceQuantityImageButton.setOnTouchListener(mTouchListener);
        addImageImageView.setOnTouchListener(mTouchListener);
        contactsNumberEditText.setOnTouchListener(mTouchListener);
        contactsEmailEditText.setOnTouchListener(mTouchListener);
    }

    private void saveInventory(){

        String productName = productNameEditText.getText().toString().trim();
        String productDesc = productDescEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productName)){
            productNameEditText.requestFocus();
            Toast.makeText(InventoryDetailsActivity.this,"Product requires a name",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(productPriceEditText.getText().toString().trim())){
            productPriceEditText.requestFocus();
            Toast.makeText(InventoryDetailsActivity.this,"Product requires price",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(quantityEditText.getText().toString().trim())){
            quantityEditText.requestFocus();
            Toast.makeText(InventoryDetailsActivity.this,"Product quantity required",Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(contactsNumberEditText.getText().toString().trim()) && TextUtils.isEmpty(contactsEmailEditText.getText().toString().trim())){
            contactsEmailEditText.requestFocus();
            contactsNumberEditText.requestFocus();
            Toast.makeText(InventoryDetailsActivity.this,"Order phone number or Email is required",Toast.LENGTH_LONG).show();
            return;
        } else {
            if (!TextUtils.isEmpty(contactsEmailEditText.getText().toString().trim())){
                if (!isValidEmail(contactsEmailEditText.getText().toString().trim())){
                    contactsEmailEditText.requestFocus();
                    Toast.makeText(InventoryDetailsActivity.this,"Valid email is required" ,Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

        Double productPrice = Double.valueOf(productPriceEditText.getText().toString().trim());
        int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_DESCRIPTION, productDesc);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IN_STOCK, quantity);

        if (!TextUtils.isEmpty(contactsNumberEditText.getText().toString().trim())){
            values.put(InventoryContract.InventoryEntry.COLUMN_ORDER_PHONE_NUM, contactsNumberEditText.getText().toString().trim());
        }

        if (!TextUtils.isEmpty(contactsEmailEditText.getText().toString().trim())){
            values.put(InventoryContract.InventoryEntry.COLUMN_ORDER_EMAIL, contactsEmailEditText.getText().toString().trim());
        }

        if (dataImage != null){
             values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE, dataImage);
        }

        if (mCurrentInventoryUri == null){
            Uri newUri = null;
            try {
                newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (newUri == null) {
                Toast.makeText(this, "Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product saved successfully!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values,null,null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product updated successfully!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_DESCRIPTION,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_IN_STOCK,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE,
                InventoryContract.InventoryEntry.COLUMN_ORDER_PHONE_NUM,
                InventoryContract.InventoryEntry.COLUMN_ORDER_EMAIL};

        return new CursorLoader(this,
                mCurrentInventoryUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME);
            int descColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_DESCRIPTION);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
            int inStockColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IN_STOCK);
            int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_IMAGE);
            int phoneNumColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ORDER_PHONE_NUM);
            int emailColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ORDER_EMAIL);

            String name = cursor.getString(nameColumnIndex);
            String desc = cursor.getString(descColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int inStock = cursor.getInt(inStockColumnIndex);
            byte[] imgByte = cursor.getBlob(imageColumnIndex);

            if (imgByte != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                retrievePhotoImageView.setVisibility(View.VISIBLE);
                retrievePhotoImageView.setImageBitmap(bmp);
            } else {
                retrievePhotoImageView.setVisibility(View.INVISIBLE);
            }

            if (phoneNumColumnIndex != -1) {
                String phonenum = cursor.getString(phoneNumColumnIndex);
                contactsNumberEditText.setText(phonenum);
            }

            productNameEditText.setText(name);
            productDescEditText.setText(desc);
            productPriceEditText.setText(Double.toString(price));
            quantityEditText.setText(Integer.toString(inStock));
            contactsEmailEditText.setText(email);
            currentProducts_in_Stock = inStock;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productNameEditText.setText("");
        productDescEditText.setText("");
        productPriceEditText.setText("0.0");
        quantityEditText.setText("0");
        currentProducts_in_Stock = 0;
        contactsNumberEditText.setText("");
        contactsEmailEditText.setText("");
    }

    public void deleteDetailsInventory(View view) {
        showDeleteConfirmationDialog();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct(){
        if (mCurrentInventoryUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    public void addQuantityToStock(View view) {
        currentProducts_in_Stock ++;
        quantityEditText.setText(String.valueOf(currentProducts_in_Stock));
    }

    public void reduceQuantityInStock(View view) {
        if(currentProducts_in_Stock > 0){
            currentProducts_in_Stock--;
            quantityEditText.setText(String.valueOf(currentProducts_in_Stock));
        }
    }

    public void saveDetailsInventory(View view) {
        saveInventory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addImageImageView:
                try{
                    if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                    }
                }
                catch (Exception ex){
                    String exc = ex.toString();
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case PICK_IMAGE_GALLERY:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    retrievePhotoImageView.setVisibility(View.VISIBLE);
                    retrievePhotoImageView.setImageBitmap(yourSelectedImage);
                    dataImage = getBitmapAsByteArray(yourSelectedImage);
                }
        }
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public void orderProductInventory(View view) {
        if (!TextUtils.isEmpty(contactsNumberEditText.getText().toString().trim()) && !contactsNumberEditText.getText().toString().trim().equals("0")){
            Uri number = Uri.parse("tel:" + Integer.parseInt(contactsNumberEditText.getText().toString().trim()) + "");
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(callIntent);
        }
        else if (!TextUtils.isEmpty(contactsEmailEditText.getText().toString().trim())){
            String mailto = "mailto:" + contactsEmailEditText.getText().toString().trim() + "" +
                    "&subject=" + Uri.encode("Order") +
                    "&body=" + Uri.encode("I would like to place an order");
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));
            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this,"Exception occured while sending email..",Toast.LENGTH_LONG).show();
            }
        }
    }
}