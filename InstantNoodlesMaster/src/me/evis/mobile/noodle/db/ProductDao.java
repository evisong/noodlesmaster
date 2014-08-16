package me.evis.mobile.noodle.db;

import java.util.ArrayList;
import java.util.List;

import me.evis.mobile.noodle.product.Product;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProductDao {
    protected static final String TABLE = "user_product";
    
    public static Product getById(Context context, long id) {
        Product record = null;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE, null, "_id = ?", new String[] {String.valueOf(id)}, null, null, null, "1");
        if (cursor.moveToFirst()) {
            record = fromCursor(context, cursor);
        }
        
        cursor.close();
        db.close();
        return record;
    }
    
    public static Product getByProductId(Context context, String productId) {
        Product record = null;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE, null, "productId = ?", new String[] {productId}, null, null, null, "1");
        if (cursor.moveToFirst()) {
            record = fromCursor(context, cursor);
        }
        
        cursor.close();
        db.close();
        return record;
    }
    
    public static Product getLatest(Context context) {
        Product record = null;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE, null, null, null, null, null, "lastSelectedOn DESC", "1");
        if (cursor.moveToNext()) {
            record = fromCursor(context, cursor);
        }
        
        cursor.close();
        db.close();
        return record;
    }
    
    public static List<Product> listLatest10(Context context) {
        List<Product> records = new ArrayList<Product>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE, null, null, null, null, null, "lastSelectedOn DESC", "10");
        while (cursor.moveToNext()) {
            records.add(fromCursor(context, cursor));
        }
        
        cursor.close();
        db.close();
        return records;
    }
    
    public static long insert(Context context, Product record) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        long id = db.insert(TABLE, null, getValues(record, true));
        
        db.close();
        return id;
    }
    
    
    public static boolean update(Context context, Product record) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        int num = db.update(TABLE, getValues(record, false), "productId = ?", new String[] {record.getProductId()});
        
        db.close();
        return 1 == num;
    }
    
    public static boolean insertOrUpdate(Context context, Product record) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        boolean result = false;
        boolean isCreate = true;
        
        Cursor cursor = db.query(TABLE, null, "productId = ?", new String[] {record.getProductId()}, null, null, null, "1");
        if (cursor.moveToNext()) {
            isCreate = false;
        }
        cursor.close();
        
        if (isCreate) {
            long id = db.insert(TABLE, null, getValues(record, true));
            if (id >= 0) {
                result = true;
            }
        } else {
            int num = db.update(TABLE, getValues(record, false), "productId = ?", new String[] {record.getProductId()});
            if (num > 0) {
                result = true;
            }
        }
        
        db.close();
        return result;
    }
    
    public static boolean markSelectedByProductId(Context context, String productId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put("lastSelectedOn", System.currentTimeMillis());
        int num = db.update(TABLE, values, "productId = ?", new String[] {productId});
        
        db.close();
        return 1 == num;
    }
    
    
    public static boolean markSelectedById(Context context, long id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put("lastSelectedOn", System.currentTimeMillis());
        int num = db.update(TABLE, values, "_id = ?", new String[] {String.valueOf(id)});
        
        db.close();
        return 1 == num;
    }
    
    public static boolean deleteByProductId(Context context, String productId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        int num = db.delete(TABLE, "productId = ?", new String[] {productId});
        
        db.close();
        return 1 == num;
    }
    
    private static Product fromCursor(Context context, Cursor cursor) {
        Product record = new Product();
        
        record.setId(cursor.getLong(cursor.getColumnIndex("_id")));
        record.setProductId(cursor.getString(cursor.getColumnIndex("productId")));
        record.setName(cursor.getString(cursor.getColumnIndex("name")));
        record.setBrand(cursor.getString(cursor.getColumnIndex("brand")));
        record.setManufacturer(cursor.getString(cursor.getColumnIndex("manufacturer")));
        record.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        record.setImageUrl(cursor.getString(cursor.getColumnIndex("imageUrl")));
        record.setBuyUrl(cursor.getString(cursor.getColumnIndex("buyUrl")));
        
        return record;
    }
    
    private static ContentValues getValues(Product record, boolean isNew) {
        ContentValues values = new ContentValues();
        
        values.put("productId", record.getProductId());
        values.put("name", record.getName());
        values.put("brand", record.getBrand());
        values.put("manufacturer", record.getManufacturer());
        values.put("description", record.getDescription());
        values.put("imageUrl", record.getImageUrl());
        values.put("buyUrl", record.getBuyUrl());
        values.put("lastSelectedOn", System.currentTimeMillis());
        if (isNew) {
            values.put("createdOn", System.currentTimeMillis());
        }
        
        return values;
    }
}
