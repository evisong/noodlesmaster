package me.evis.mobile.util;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class AssetUtil {

	private AssetUtil() {
		// No instance
	}
	
	public static boolean setAssetImage(ImageView imageView, String filename) {
		return setAssetImage(imageView, null, filename);
	}
	
	public static boolean setAssetImage(ImageView imageView, String path, String filename) {
		if (filename == null || "".equals(filename) || "NULL".equalsIgnoreCase(filename)) {
			return false;
		}
		String _path = (path == null) ? "" : path;
		
        InputStream is = null;
        try {
            is = imageView.getContext().getAssets().open(_path + filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        imageView.setImageBitmap(bitmap);
        
        return true;
	}
}
