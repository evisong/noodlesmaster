package me.evis.mobile.noodle;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

public class ProductDetailDialog extends AlertDialog {

    public ProductDetailDialog(Context context) {
        super(context);
    }

    public ProductDetailDialog(Context context, int theme) {
        super(context, theme);
    }

    public ProductDetailDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
