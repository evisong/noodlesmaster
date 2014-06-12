package me.evis.mobile.noodle.share;

import me.evis.mobile.noodle.R;
import me.evis.mobile.util.DateTimeUtil;
import android.content.Context;
import android.content.Intent;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.exception.WeiboShareException;

public class WeiboShareProvider {
    private static final String WEIBO_CALLBACK_ACTION = "com.sina.weibo.sdk.action.ACTION_SDK_REQ_ACTIVITY";
    
    public static boolean isWeiboCallbackIntent(Intent intent) {
        return WEIBO_CALLBACK_ACTION.equals(intent.getAction());
    }
    
    public static String getShareText(Context context, String productName, int totalSecs) {
        if (productName != null && productName.length() > 0) {
            productName = " \"" + productName + "\" ";
        } else {
            productName = "";
        }
        String time = DateTimeUtil.getLocalizedTimeString(context, totalSecs);
        return context.getString(R.string.share_weibo_content, productName, time);
    }
    
    /**
     * @throws WeiboShareException, {@link IllegalStateException}
     * @param mWeiboShareAPI
     * @param textObj
     * @param imageObj
     */
    public static void share(IWeiboShareAPI mWeiboShareAPI, TextObject textObj, ImageObject imageObj) {
        // Check Weibo app; prompt to download Weibo if not installed.
        if (mWeiboShareAPI.checkEnvironment(true)) {
            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
                if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                    WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                    weiboMessage.textObject = textObj;
                    weiboMessage.imageObject = imageObj;
                    
                    SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
                    request.transaction = String.valueOf(System.currentTimeMillis());
                    request.multiMessage = weiboMessage;
                    mWeiboShareAPI.sendRequest(request);
                    
                } else {
                    WeiboMessage weiboMessage = new WeiboMessage();
                    weiboMessage.mediaObject = textObj;
                    
                    SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
                    request.transaction = String.valueOf(System.currentTimeMillis());
                    request.message = weiboMessage;
                    mWeiboShareAPI.sendRequest(request);
                }
                    
            } else {
                throw new IllegalStateException("Weibo API version not supported.");
            }
        }
    }
}
