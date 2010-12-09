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

package me.evis.mobile.noodle;

import me.evis.mobile.noodle.provider.BrandContentProvider;
import me.evis.mobile.noodle.provider.NoodlesContentProvider;
import me.evis.mobile.util.AssetUtil;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class BrandList extends ListActivity {
    /**
     * The columns we are interested in from the database
     */
    private static final String[] PROJECTION = new String[] {
        BrandContentProvider._ID, // 0
        BrandContentProvider.NAME, // 1
        BrandContentProvider.LOGO, // 2
    };
    
    private static final String LOGO_PATH = "logos/";
    
    private static final int REQUEST_CODE_BROWSE_NOODLES = 2010100903;
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brandlist);

		// adds listener to list view
		ListView listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				startActivityForResult(new Intent(Intent.ACTION_VIEW,
						ContentUris.withAppendedId(NoodlesContentProvider.BRAND_ID_FIELD_CONTENT_URI, id)), 
						REQUEST_CODE_BROWSE_NOODLES);
			}
		});

		// Perform a managed query. The Activity will handle closing and re-querying the cursor
        // when needed.
        Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null,
                BrandContentProvider.DEFAULT_SORT_ORDER);

        // Used to map notes entries from the database to views
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, 
        		R.layout.brandlist_row, cursor,
                new String[] { BrandContentProvider.NAME, BrandContentProvider.LOGO }, 
                new int[] { R.id.ItemName, R.id.ItemLogo });
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				// NoodlesContentProvider.LOGO
				if (view instanceof ImageView && columnIndex == 2) {
					String logo = cursor.getString(columnIndex);
					AssetUtil.setAssetImage((ImageView) view, LOGO_PATH, logo);
					return true;
				}
				return false;
			}
		});
        setListAdapter(adapter);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_BROWSE_NOODLES && resultCode == RESULT_OK) {
			setResult(resultCode, data);
			finish();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}