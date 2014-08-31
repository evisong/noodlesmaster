package me.evis.mobile.util;

import me.evis.mobile.noodle.R;
import me.evis.mobile.noodle.db.ProductDao;
import me.evis.mobile.noodle.product.Product;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.admob.AdMobExtras;

public class AdsUtil {

    private AdsUtil() {
        // No instance
    }

    public static AdRequest createAdRequest() {
        Bundle bundle = new Bundle();
        bundle.putString("color_bg", "FFFFFF");
        bundle.putString("color_bg_top", "FFFFFF");
        bundle.putString("color_border", "DDDDDD");
        bundle.putString("color_link", "000080");
        bundle.putString("color_text", "0088FF");
        bundle.putString("color_url", "333333");
        AdMobExtras extras = new AdMobExtras(bundle);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("motorola-me860-TA7440B9FQ")
                .addTestDevice("7N2MWW144M050194")
                .addNetworkExtras(extras).build();
        return adRequest;
    }
    
    public static void buyProduct(Context context, long id) {
        Product product = ProductDao.getById(context, id);
        buyProduct(context, product);
    }
    
    public static void buyProduct(Context context, Product product) {
        String buyUrl = null;
        if (product != null) {
            buyUrl = product.getBuyUrl();
        } else {
            buyUrl = context.getString(R.string.amazon_default_url);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(buyUrl));
        context.startActivity(intent);
    }
}
