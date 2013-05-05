/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *                                                                            *                                                                         *
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2010, Motorola Mobility, Inc. All rights reserved.           *
 *                                                                            *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */
package me.evis.mobile.noodle.provider;

import java.util.HashMap;

import me.evis.mobile.noodle.NoodlesDbHelper;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class NoodlesContentProvider extends ContentProvider {

    private NoodlesDbHelper dbHelper;
    private static HashMap<String, String> NOODLES_PROJECTION_MAP;
    protected static final String TABLE_NAME = "noodles";
    private static final String AUTHORITY = "me.evis.mobile.noodle.provider.noodlescontentprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE_NAME);
    public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase());
    public static final Uri UUID_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/uuid");
    public static final Uri BRAND_UUID_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/brand_uuid");
    public static final Uri NAME_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/name");
    public static final Uri SOAKAGE_TIME_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/soakage_time");
    public static final Uri CODE_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + BarcodeContentProvider.TABLE_NAME.toLowerCase() + "/code");
    
    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of noodles.
     */
    public static final String CONTENT_TYPE = 
        "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.noodles";

    /**
     * The MIME type of a {@link #CONTENT_URI} sub-directory of a single noodle.
     */
    public static final String CONTENT_ITEM_TYPE = 
        "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";

    public static final String DEFAULT_SORT_ORDER = "_id ASC";

    private static final UriMatcher URL_MATCHER;

    private static final int NOODLES = 1;
    private static final int NOODLES__ID = 2;
    private static final int NOODLES_UUID = 3;
    private static final int NOODLES_BRAND_UUID = 4;
    private static final int NOODLES_NAME = 5;
    private static final int NOODLES_SOAKAGE_TIME = 6;
    private static final int BARCODE_CODE = 7;

    // Content values keys (using column names)
    public static final String _ID = "_id";
    public static final String UUID = "uuid";
    public static final String BRAND_UUID = "brand_uuid";
    public static final String NAME = "name";
    public static final String NET_WEIGHT = "net_weight";
    public static final String NOODLES_WEIGHT = "noodles_weight";
    public static final String STEP_1_UUID = "step_1_uuid";
    public static final String STEP_2_UUID = "step_2_uuid";
    public static final String STEP_3_UUID = "step_3_uuid";
    public static final String STEP_4_UUID = "step_4_uuid";
    public static final String SOAKAGE_TIME = "soakage_time";
    public static final String DESCRIPTION = "description";
    public static final String LOGO = "logo";
    
    private static final String JOINED_TABLE_NAMES = TABLE_NAME
        + " LEFT JOIN " + BrandContentProvider.TABLE_NAME + " ON " + TABLE_NAME + "." + BRAND_UUID + "=" + BrandContentProvider.TABLE_NAME + "." + BrandContentProvider.UUID
        + " LEFT JOIN " + ManufacturerContentProvider.TABLE_NAME + " ON " + BrandContentProvider.TABLE_NAME + "." + BrandContentProvider.MANUFACTURER_UUID + "=" + ManufacturerContentProvider.TABLE_NAME + "." + ManufacturerContentProvider.UUID
        + " LEFT JOIN " + StepContentProvider.TABLE_NAME + " step1 ON " + TABLE_NAME + "." + STEP_1_UUID + "=" + "step1." + StepContentProvider.UUID
        + " LEFT JOIN " + StepContentProvider.TABLE_NAME + " step2 ON " + TABLE_NAME + "." + STEP_2_UUID + "=" + "step2." + StepContentProvider.UUID
        + " LEFT JOIN " + StepContentProvider.TABLE_NAME + " step3 ON " + TABLE_NAME + "." + STEP_3_UUID + "=" + "step3." + StepContentProvider.UUID
        + " LEFT JOIN " + StepContentProvider.TABLE_NAME + " step4 ON " + TABLE_NAME + "." + STEP_4_UUID + "=" + "step4." + StepContentProvider.UUID;

    private static final String JOINED_WITH_BARCODE_TABLE_NAMES = TABLE_NAME
        + " JOIN " + BarcodeContentProvider.TABLE_NAME + " ON " + TABLE_NAME + "." + UUID + "=" + BarcodeContentProvider.TABLE_NAME + "." + BarcodeContentProvider.NOODLES_UUID
        + " LEFT JOIN " + BrandContentProvider.TABLE_NAME + " ON " + TABLE_NAME + "." + BRAND_UUID + "=" + BrandContentProvider.TABLE_NAME + "." + BrandContentProvider.UUID
        + " LEFT JOIN " + ManufacturerContentProvider.TABLE_NAME + " ON " + BrandContentProvider.TABLE_NAME + "." + BrandContentProvider.MANUFACTURER_UUID + "=" + ManufacturerContentProvider.TABLE_NAME + "." + ManufacturerContentProvider.UUID
        + " LEFT JOIN " + StepContentProvider.TABLE_NAME + " step1 ON " + TABLE_NAME + "." + STEP_1_UUID + "=" + "step1." + StepContentProvider.UUID
        + " LEFT JOIN " + StepContentProvider.TABLE_NAME + " step2 ON " + TABLE_NAME + "." + STEP_2_UUID + "=" + "step2." + StepContentProvider.UUID
        + " LEFT JOIN " + StepContentProvider.TABLE_NAME + " step3 ON " + TABLE_NAME + "." + STEP_3_UUID + "=" + "step3." + StepContentProvider.UUID
        + " LEFT JOIN " + StepContentProvider.TABLE_NAME + " step4 ON " + TABLE_NAME + "." + STEP_4_UUID + "=" + "step4." + StepContentProvider.UUID;
    
    @Override
    public boolean onCreate() {
        dbHelper = new NoodlesDbHelper(getContext(), true);
        return (dbHelper == null) ? false : true;
    }

    @Override
    public Cursor query(Uri url, String[] projection, String selection,
            String[] selectionArgs, String sort) {
        SQLiteDatabase mDB = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (URL_MATCHER.match(url)) {
        case NOODLES:
            qb.setTables(TABLE_NAME);
            qb.setProjectionMap(NOODLES_PROJECTION_MAP);
            break;
        case NOODLES__ID:
            qb.setTables(JOINED_TABLE_NAMES);
            qb.appendWhere(TABLE_NAME + "._id=" + url.getPathSegments().get(1));
            break;
        case NOODLES_UUID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere(TABLE_NAME + ".uuid='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_BRAND_UUID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("brand_uuid='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_NAME:
            qb.setTables(TABLE_NAME);
            // A fuzzy inquiry.
            qb.appendWhere("name LIKE '%" + url.getPathSegments().get(2) + "%'");
            break;
        case NOODLES_SOAKAGE_TIME:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("soakage_time='" + url.getPathSegments().get(2)
                    + "'");
            break;
        case BARCODE_CODE:
            qb.setTables(JOINED_WITH_BARCODE_TABLE_NAMES);
            qb.appendWhere(BarcodeContentProvider.TABLE_NAME + "." + BarcodeContentProvider.CODE + "='" + url.getPathSegments().get(2) + "'");
            break;
            
        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
        String orderBy = "";
        if (TextUtils.isEmpty(sort)) {
            orderBy = DEFAULT_SORT_ORDER;
        } else {
            orderBy = sort;
        }
        Cursor c = qb.query(mDB, projection, selection, selectionArgs, null,
                null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), url);
        return c;
    }

    @Override
    public String getType(Uri url) {
        switch (URL_MATCHER.match(url)) {
        case NOODLES:
            return CONTENT_TYPE;
        case NOODLES__ID:
            return CONTENT_ITEM_TYPE;
        case NOODLES_UUID:
            return CONTENT_ITEM_TYPE;
        case NOODLES_BRAND_UUID:
            return CONTENT_TYPE;
        case NOODLES_NAME:
            // A fuzzy inquiry.
            return CONTENT_TYPE;
        case NOODLES_SOAKAGE_TIME:
            return CONTENT_TYPE;
        case BARCODE_CODE:
            return CONTENT_ITEM_TYPE;
           
        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
    }

    @Override
    public Uri insert(Uri url, ContentValues initialValues) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        long rowID;
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        if (URL_MATCHER.match(url) != NOODLES) {
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        rowID = mDB.insert("noodles", "noodles", values);
        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        throw new SQLException("Failed to insert row into " + url);
    }

    @Override
    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        int count;
        String segment = "";
        switch (URL_MATCHER.match(url)) {
        case NOODLES:
            count = mDB.delete(TABLE_NAME, where, whereArgs);
            break;
        case NOODLES__ID:
            segment = url.getPathSegments().get(1);
            count = mDB.delete(TABLE_NAME,
                    "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_BRAND_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "brand_uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    @Override
    public int update(Uri url, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        int count;
        String segment = "";
        switch (URL_MATCHER.match(url)) {
        case NOODLES:
            count = mDB.update(TABLE_NAME, values, where, whereArgs);
            break;
        case NOODLES__ID:
            segment = url.getPathSegments().get(1);
            count = mDB.update(TABLE_NAME, values,
                    "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_BRAND_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "brand_uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
        getContext().getContentResolver().notifyChange(url, null);
        return count;
    }

    static {
        URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), NOODLES);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/#",
                NOODLES__ID);
        URL_MATCHER.addURI(AUTHORITY,
                TABLE_NAME.toLowerCase() + "/uuid" + "/*", NOODLES_UUID);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/brand_uuid"
                + "/*", NOODLES_BRAND_UUID);
        URL_MATCHER.addURI(AUTHORITY,
                TABLE_NAME.toLowerCase() + "/name" + "/*", NOODLES_NAME);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase()
                + "/soakage_time" + "/*", NOODLES_SOAKAGE_TIME);
        URL_MATCHER.addURI(AUTHORITY,
                BarcodeContentProvider.TABLE_NAME.toLowerCase() + "/code" + "/*", BARCODE_CODE);

        NOODLES_PROJECTION_MAP = new HashMap<String, String>();
        NOODLES_PROJECTION_MAP.put(_ID, "_id");
        NOODLES_PROJECTION_MAP.put(UUID, "uuid");
        NOODLES_PROJECTION_MAP.put(BRAND_UUID, "brand_uuid");
        NOODLES_PROJECTION_MAP.put(NAME, "name");
        NOODLES_PROJECTION_MAP.put(NET_WEIGHT, "net_weight");
        NOODLES_PROJECTION_MAP.put(NOODLES_WEIGHT, "noodles_weight");
        NOODLES_PROJECTION_MAP.put(STEP_1_UUID, "step_1_uuid");
        NOODLES_PROJECTION_MAP.put(STEP_2_UUID, "step_2_uuid");
        NOODLES_PROJECTION_MAP.put(STEP_3_UUID, "step_3_uuid");
        NOODLES_PROJECTION_MAP.put(STEP_4_UUID, "step_4_uuid");
        NOODLES_PROJECTION_MAP.put(SOAKAGE_TIME, "soakage_time");
        NOODLES_PROJECTION_MAP.put(DESCRIPTION, "description");
        NOODLES_PROJECTION_MAP.put(LOGO, "logo");

    }
}