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

import me.evis.mobile.noodle.provider.NoodlesContentProvider;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class NoodlesList extends ListActivity {
    /**
     * The columns we are interested in from the database
     */
    private static final String[] PROJECTION = new String[] {
        NoodlesContentProvider.ID, // 0
        NoodlesContentProvider.NAME, // 1
        NoodlesContentProvider.LOGO, // 2
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noodleslist);

        // adds listener to list view
        ListView listView = getListView();
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Toast.makeText(arg1.getContext(),
                        getString(R.string.selected) + " " + arg2,
                        Toast.LENGTH_SHORT).show();
            }
        });
        
        // Perform a managed query. The Activity will handle closing and re-querying the cursor
        // when needed.
        Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null,
                NoodlesContentProvider.DEFAULT_SORT_ORDER);

        // Used to map notes entries from the database to views
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.noodleslist_row, cursor,
                new String[] { NoodlesContentProvider.NAME, NoodlesContentProvider.LOGO }, new int[] { R.id.text01, R.id.img01 });
        setListAdapter(adapter);
    }
}