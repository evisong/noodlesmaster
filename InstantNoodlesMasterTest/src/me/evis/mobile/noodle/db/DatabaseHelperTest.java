package me.evis.mobile.noodle.db;

import junit.framework.Assert;
import me.evis.mobile.noodle.product.ObjectResult;
import me.evis.mobile.noodle.product.Product;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class DatabaseHelperTest extends AndroidTestCase {
    private DatabaseHelper dbHelper = null;
    
    @Override
    protected void setUp() throws Exception {
        dbHelper = new DatabaseHelper(getContext());
        super.setUp();
    }

	public void testOnCreate() {
	    SQLiteDatabase db =  dbHelper.getWritableDatabase();
	    dbHelper.onCreate(db);
	    Product latest = ProductDao.getLatest(getContext());
	    Assert.assertNotNull(latest);
	}
}
