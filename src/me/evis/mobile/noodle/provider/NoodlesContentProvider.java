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

public class NoodlesContentProvider extends ContentProvider {

    private NoodlesMetaDbHelper dbHelper;
    private static HashMap<String, String> NOODLES_PROJECTION_MAP;
    private static final String TABLE_NAME = "noodles";
    private static final String AUTHORITY = "me.evis.mobile.noodle.provider.noodlescontentprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TABLE_NAME);
    public static final Uri ID_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/id");
    public static final Uri BRAND_ID_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/brand_id");
    public static final Uri NAME_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/name");
    public static final Uri NET_WEIGHT_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/net_weight");
    public static final Uri NOODLES_WEIGHT_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/noodles_weight");
    public static final Uri STEP_1_ID_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/step_1_id");
    public static final Uri STEP_2_ID_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/step_2_id");
    public static final Uri STEP_3_ID_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/step_3_id");
    public static final Uri STEP_4_ID_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/step_4_id");
    public static final Uri SOAKAGE_TIME_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/soakage_time");
    public static final Uri DESCRIPTION_FIELD_CONTENT_URI = Uri
            .parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
                    + "/description");
    public static final Uri LOGO_FIELD_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/logo");

    public static final String DEFAULT_SORT_ORDER = "ID ASC";

    private static final UriMatcher URL_MATCHER;

    private static final int NOODLES = 1;
    private static final int NOODLES_ID = 2;
    private static final int NOODLES_BRAND_ID = 3;
    private static final int NOODLES_NAME = 4;
    private static final int NOODLES_NET_WEIGHT = 5;
    private static final int NOODLES_NOODLES_WEIGHT = 6;
    private static final int NOODLES_STEP_1_ID = 7;
    private static final int NOODLES_STEP_2_ID = 8;
    private static final int NOODLES_STEP_3_ID = 9;
    private static final int NOODLES_STEP_4_ID = 10;
    private static final int NOODLES_SOAKAGE_TIME = 11;
    private static final int NOODLES_DESCRIPTION = 12;
    private static final int NOODLES_LOGO = 13;

    // Content values keys (using column names)
    public static final String ID = "ID";
    public static final String BRAND_ID = "BRAND_ID";
    public static final String NAME = "NAME";
    public static final String NET_WEIGHT = "NET_WEIGHT";
    public static final String NOODLES_WEIGHT = "NOODLES_WEIGHT";
    public static final String STEP_1_ID = "STEP_1_ID";
    public static final String STEP_2_ID = "STEP_2_ID";
    public static final String STEP_3_ID = "STEP_3_ID";
    public static final String STEP_4_ID = "STEP_4_ID";
    public static final String SOAKAGE_TIME = "SOAKAGE_TIME";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String LOGO = "LOGO";

    public boolean onCreate() {
        dbHelper = new NoodlesMetaDbHelper(getContext(), true);
        return (dbHelper == null) ? false : true;
    }

    public Cursor query(Uri url, String[] projection, String selection,
            String[] selectionArgs, String sort) {
        SQLiteDatabase mDB = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (URL_MATCHER.match(url)) {
        case NOODLES:
            qb.setTables(TABLE_NAME);
            qb.setProjectionMap(NOODLES_PROJECTION_MAP);
            break;
        case NOODLES_ID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("id='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_BRAND_ID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("brand_id='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_NAME:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("name='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_NET_WEIGHT:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("net_weight='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_NOODLES_WEIGHT:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("noodles_weight='" + url.getPathSegments().get(2)
                    + "'");
            break;
        case NOODLES_STEP_1_ID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("step_1_id='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_STEP_2_ID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("step_2_id='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_STEP_3_ID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("step_3_id='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_STEP_4_ID:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("step_4_id='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_SOAKAGE_TIME:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("soakage_time='" + url.getPathSegments().get(2)
                    + "'");
            break;
        case NOODLES_DESCRIPTION:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("description='" + url.getPathSegments().get(2) + "'");
            break;
        case NOODLES_LOGO:
            qb.setTables(TABLE_NAME);
            qb.appendWhere("logo='" + url.getPathSegments().get(2) + "'");
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
        case NOODLES:
            return "vnd.android.cursor.dir/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_ID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_BRAND_ID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_NAME:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_NET_WEIGHT:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_NOODLES_WEIGHT:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_STEP_1_ID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_STEP_2_ID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_STEP_3_ID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_STEP_4_ID:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_SOAKAGE_TIME:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_DESCRIPTION:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";
        case NOODLES_LOGO:
            return "vnd.android.cursor.item/vnd.me.evis.mobile.noodle.provider.noodles";

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

    public int delete(Uri url, String where, String[] whereArgs) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        int count;
        String segment = "";
        switch (URL_MATCHER.match(url)) {
        case NOODLES:
            count = mDB.delete(TABLE_NAME, where, whereArgs);
            break;
        case NOODLES_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_BRAND_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "brand_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_NAME:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "name="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_NET_WEIGHT:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "net_weight="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_NOODLES_WEIGHT:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "noodles_weight="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_STEP_1_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "step_1_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_STEP_2_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "step_2_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_STEP_3_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "step_3_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_STEP_4_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "step_4_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_SOAKAGE_TIME:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "soakage_time="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_DESCRIPTION:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "description="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_LOGO:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.delete(TABLE_NAME,
                    "logo="
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
        case NOODLES:
            count = mDB.update(TABLE_NAME, values, where, whereArgs);
            break;
        case NOODLES_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_BRAND_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "brand_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_NAME:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "name="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_NET_WEIGHT:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "net_weight="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_NOODLES_WEIGHT:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "noodles_weight="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_STEP_1_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "step_1_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_STEP_2_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "step_2_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_STEP_3_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "step_3_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_STEP_4_ID:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "step_4_id="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_SOAKAGE_TIME:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "soakage_time="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_DESCRIPTION:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "description="
                            + segment
                            + (!TextUtils.isEmpty(where) ? " AND (" + where
                                    + ')' : ""), whereArgs);
            break;
        case NOODLES_LOGO:
            segment = "'" + url.getPathSegments().get(2) + "'";
            count = mDB.update(TABLE_NAME, values,
                    "logo="
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
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/id" + "/*",
                NOODLES_ID);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/brand_id"
                + "/*", NOODLES_BRAND_ID);
        URL_MATCHER.addURI(AUTHORITY,
                TABLE_NAME.toLowerCase() + "/name" + "/*", NOODLES_NAME);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/net_weight"
                + "/*", NOODLES_NET_WEIGHT);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase()
                + "/noodles_weight" + "/*", NOODLES_NOODLES_WEIGHT);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/step_1_id"
                + "/*", NOODLES_STEP_1_ID);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/step_2_id"
                + "/*", NOODLES_STEP_2_ID);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/step_3_id"
                + "/*", NOODLES_STEP_3_ID);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/step_4_id"
                + "/*", NOODLES_STEP_4_ID);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase()
                + "/soakage_time" + "/*", NOODLES_SOAKAGE_TIME);
        URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/description"
                + "/*", NOODLES_DESCRIPTION);
        URL_MATCHER.addURI(AUTHORITY,
                TABLE_NAME.toLowerCase() + "/logo" + "/*", NOODLES_LOGO);

        NOODLES_PROJECTION_MAP = new HashMap<String, String>();
        NOODLES_PROJECTION_MAP.put(ID, "id");
        NOODLES_PROJECTION_MAP.put(BRAND_ID, "brand_id");
        NOODLES_PROJECTION_MAP.put(NAME, "name");
        NOODLES_PROJECTION_MAP.put(NET_WEIGHT, "net_weight");
        NOODLES_PROJECTION_MAP.put(NOODLES_WEIGHT, "noodles_weight");
        NOODLES_PROJECTION_MAP.put(STEP_1_ID, "step_1_id");
        NOODLES_PROJECTION_MAP.put(STEP_2_ID, "step_2_id");
        NOODLES_PROJECTION_MAP.put(STEP_3_ID, "step_3_id");
        NOODLES_PROJECTION_MAP.put(STEP_4_ID, "step_4_id");
        NOODLES_PROJECTION_MAP.put(SOAKAGE_TIME, "soakage_time");
        NOODLES_PROJECTION_MAP.put(DESCRIPTION, "description");
        NOODLES_PROJECTION_MAP.put(LOGO, "logo");

    }
}
