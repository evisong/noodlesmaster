package me.evis.mobile.noodle.db;

import java.io.IOException;
import java.io.InputStream;

import me.evis.mobile.noodle.R;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DB_NAME = "noodlesmaster.db";
    private static final int DB_VERSION = 1;
//    private static final String ASSET_DB_SQL_PATH = "databases";
//    private static final String DB_INIT_SQL_NAME = "init.sql";
    
    private Context context;
    
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        InputStream is = null;
        try {
//            is = context.getAssets().open(ASSET_DB_SQL_PATH + "/" + DB_INIT_SQL_NAME);
            is = context.getResources().openRawResource(R.raw.database_init);
            String initSql = IOUtils.toString(is);
            db.execSQL(initSql);
        } catch (IOException e) {
            Log.e(TAG, "Error loading init SQL from raw", e);
        } catch (SQLException e) {
            Log.e(TAG, "Error executing init SQL", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing.
    }

}
