package me.evis.mobile.noodle.product;

import junit.framework.Assert;
import me.evis.mobile.noodle.R;
import android.test.AndroidTestCase;

public class QueryProductByEanTaskTest extends AndroidTestCase {
    private QueryProductByEanTask task = null;
    
    @Override
    protected void setUp() throws Exception {
        task = new QueryProductByEanTask(getContext(), null, null);
        super.setUp();
    }

	public void testDoInBackgroundNotFound() {
	    ObjectResult<Product> error = task.doInBackground("dummyvalue");
	    Assert.assertEquals(getContext().getString(R.string.error_barcode_not_found), error.getMessage());
	}
	
	public void testDoInBackgroundFound() {
	    ObjectResult<Product> product = task.doInBackground("6901463960099");
	    Assert.assertNotNull(product.getResult().getProductId());
	}
}
