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

public class ManufacturerContentProvider extends ContentProvider {

    private NoodlesDbHelper dbHelper;
    private static HashMap<String, String> MANUFACTURER_PROJECTION_MAP;
    private static final String TABLE_NAME = "manufacturer";
    private static final String AUTHORITY = "me.evis.mobile.noodle.provider.manufacturercontentprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE_NAME);
    public static final Uri ID_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/id");
    public static final Uri NAME_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/name");
    public static final Uri LOGO_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/logo");

    public static final String DEFAULT_SORT_ORDER = "_id ASC";

    private static final UriMatcher URL_MATCHER;

    private static final int MANUFACTURER = 1;
    private static final int MANUFACTURER_ID = 2;
    private static final int MANUFACTURER_NAME = 3;

    // Content values keys (using column names)
    public static final String _ID = "_id";
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
        case MANUFACTURER:
            qb.setTables(TABLE_NAME);
            qb.setProjectionMap(MANUFACTURER_PROJECTION_MAP);
            break;
        case MANUFACTURER_ID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("_id='" + url.getPathSegments().get(2) + "'");
            break;
        case MANUFACTURER_NAME:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("name LIKE '" + url.getPathSegments().get(2) + "'");
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
        case MANUFACTURER:
            return "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.manufacturer";
        case MANUFACTURER_ID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.manufacturer";
        case MANUFACTURER_NAME:
            return "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.manufacturer";

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
        if (URL_MATCHER.match(url) != MANUFACTURER) {
            throw new IllegalArgumentException("Unknown URL " + url);
        }

        rowID = mDB.insert("manufacturer", "manufacturer", values);
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
        case MANUFACTURER:
            count = mDB.delete(TABLE_NAME, where, whereArgs);
            break;
        case MANUFACTURER_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "_id="
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
        case MANUFACTURER:
            count = mDB.update(TABLE_NAME, values, where, whereArgs);
            break;
        case MANUFACTURER_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "_id="
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
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), MANUFACTURER);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/id" + "/*",
                MANUFACTURER_ID);
        URL_MATCHER.addURI(AUTHORITY,
                TABLE_NAME.toLowerCase() + "/name" + "/*", MANUFACTURER_NAME);

        MANUFACTURER_PROJECTION_MAP = new HashMap<String, String>();
        MANUFACTURER_PROJECTION_MAP.put(_ID, "_id");
        MANUFACTURER_PROJECTION_MAP.put(NAME, "name");
        MANUFACTURER_PROJECTION_MAP.put(LOGO, "logo");

    }
}
