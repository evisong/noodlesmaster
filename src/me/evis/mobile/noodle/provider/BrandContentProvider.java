/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *																			  *																			*
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2010, Motorola Mobility, Inc. All rights reserved.           *
 * 																			  *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */
package me.evis.mobile.noodle.provider;

import java.util.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.text.*;

import me.evis.mobile.noodle.*;

public class BrandContentProvider extends ContentProvider {

    private NoodlesDbHelper dbHelper;
    private static HashMap<String, String> BRAND_PROJECTION_MAP;
    private static final String TABLE_NAME = "brand";
    private static final String AUTHORITY = "me.evis.mobile.noodle.provider.brandcontentprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE_NAME);
    public static final Uri _ID_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase());
    public static final Uri UUID_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/uuid");
    public static final Uri MANUFACTURER_UUID_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/manufacturer_uuid");
    public static final Uri PARENT_BRAND_UUID_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/parent_brand_uuid");
    public static final Uri NAME_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/name");

    public static final String DEFAULT_SORT_ORDER = "_id ASC";

    private static final UriMatcher URL_MATCHER;

    private static final int BRAND = 1;
    private static final int BRAND__ID = 2;
    private static final int BRAND_UUID = 3;
    private static final int BRAND_MANUFACTURER_UUID = 4;
    private static final int BRAND_PARENT_BRAND_UUID = 5;
    private static final int BRAND_NAME = 6;

    // Content values keys (using column names)
    public static final String _ID = "_id";
    public static final String UUID = "uuid";
    public static final String MANUFACTURER_UUID = "manufacturer_uuid";
    public static final String PARENT_BRAND_UUID = "parent_brand_uuid";
    public static final String NAME = "name";
    public static final String LOGO = "logo";

    public boolean onCreate() {
        dbHelper = new NoodlesDbHelper(getContext(), true);
        return (dbHelper == null) ? false : true;
    }

    public Cursor query(Uri url, String[] projection, String selection,
            String[] selectionArgs, String sort) {
        SQLiteDatabase mDB = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (URL_MATCHER.match(url)) {
        case BRAND:
            qb.setTables(TABLE_NAME);
            qb.setProjectionMap(BRAND_PROJECTION_MAP);
            break;
        case BRAND__ID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("_id=" + url.getPathSegments().get(1));
            break;
        case BRAND_UUID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("uuid='" + url.getPathSegments().get(2) + "'");
            break;
        case BRAND_MANUFACTURER_UUID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("manufacturer_uuid='" + url.getPathSegments().get(2)
                    + "'");
            break;
        case BRAND_PARENT_BRAND_UUID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("parent_brand_uuid='" + url.getPathSegments().get(2)
                    + "'");
            break;
        case BRAND_NAME:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("name LIKE '%" + url.getPathSegments().get(2) + "%'");
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

    public String getType(Uri url) {
        switch (URL_MATCHER.match(url)) {
        case BRAND:
            return "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.brand";
        case BRAND__ID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.brand";
        case BRAND_UUID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.brand";
        case BRAND_MANUFACTURER_UUID:
            return "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.brand";
        case BRAND_PARENT_BRAND_UUID:
            return "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.brand";
        case BRAND_NAME:
            return "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.brand";

        default:
            throw new IllegalArgumentException("Unknown URL " + url);
        }
    }

    public Uri insert(Uri url, ContentValues initialValues) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        long rowID;
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        if (URL_MATCHER.match(url) != BRAND) {
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        rowID = mDB.insert("brand", "brand", values);
        if (rowID > 0) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        throw new SQLException("Failed to insert row into " + url);
    }

    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        int count;
        String segment = "";
        switch (URL_MATCHER.match(url)) {
        case BRAND:
            count = mDB.delete(TABLE_NAME, where, whereArgs);
            break;
        case BRAND__ID:
            segment = url.getPathSegments().get(1);
            count = mDB.delete(TABLE_NAME,
                    "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BRAND_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BRAND_MANUFACTURER_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "manufacturer_uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BRAND_PARENT_BRAND_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "parent_brand_uuid="
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

    public int update(Uri url, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        int count;
        String segment = "";
        switch (URL_MATCHER.match(url)) {
        case BRAND:
            count = mDB.update(TABLE_NAME, values, where, whereArgs);
            break;
        case BRAND__ID:
            segment = url.getPathSegments().get(1);
            count = mDB.update(TABLE_NAME, values,
                    "_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BRAND_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BRAND_MANUFACTURER_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "manufacturer_uuid="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case BRAND_PARENT_BRAND_UUID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "parent_brand_uuid="
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
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), BRAND);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/#",
                BRAND__ID);
        URL_MATCHER.addURI(AUTHORITY,
                TABLE_NAME.toLowerCase() + "/uuid" + "/*", BRAND_UUID);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase()
                + "/manufacturer_uuid" + "/*", BRAND_MANUFACTURER_UUID);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase()
                + "/parent_brand_uuid" + "/*", BRAND_PARENT_BRAND_UUID);
        URL_MATCHER.addURI(AUTHORITY,
                TABLE_NAME.toLowerCase() + "/name" + "/*", BRAND_NAME);

        BRAND_PROJECTION_MAP = new HashMap<String, String>();
        BRAND_PROJECTION_MAP.put(_ID, "_id");
        BRAND_PROJECTION_MAP.put(UUID, "uuid");
        BRAND_PROJECTION_MAP.put(MANUFACTURER_UUID, "manufacturer_uuid");
        BRAND_PROJECTION_MAP.put(PARENT_BRAND_UUID, "parent_brand_uuid");
        BRAND_PROJECTION_MAP.put(NAME, "name");
        BRAND_PROJECTION_MAP.put(LOGO, "logo");

    }
}
