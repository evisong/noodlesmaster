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
    
    private Context context;
    
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(R.raw.database_init);
            String initSql = IOUtils.toString(is);
            // Split the SQL into separate statements as execSQL only accepts 1 single statement,
            // according to http://stackoverflow.com/questions/3805938/executing-multiple-statements-with-sqlitedatabase-execsql
            String[] sqlStatements = initSql.split(";");
            for (String sqlStatement : sqlStatements) {
                db.execSQL(sqlStatement);
            }
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
