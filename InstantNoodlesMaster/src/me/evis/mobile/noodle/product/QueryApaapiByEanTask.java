package me.evis.mobile.noodle.product;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import me.evis.mobile.noodle.R;
import me.evis.mobile.util.DeviceUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.amazon.advertising.api.sample.SignedRequestsHelper;

public class QueryApaapiByEanTask extends AsyncTask<String, Void, StringResult> {
    private static final String TAG = "QueryApaapiByEanTask";
    
    private Context context;
    private OnSuccessListener onSuccessListener;
    private OnFailureListener onFailureListener;
    
    public QueryApaapiByEanTask(Context context, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        this.context = context;
        this.onSuccessListener = onSuccessListener;
        this.onFailureListener = onFailureListener;
    }
    
    @Override
    protected StringResult doInBackground(String... params) {
        if (!DeviceUtil.isOnline(context)) {
            return new StringResult(false, "No network available");
        }
        
        if (context == null) {
            throw new IllegalStateException("Context in null");
        }
        if (params.length < 1) {
            throw new IllegalStateException("No params");
        }
        
        String ean = params[0];
        SignedRequestsHelper helper;
        String endpoint = context.getString(R.string.amazon_aws_endpoint);
        String accessKey = context.getString(R.string.amazon_aws_access_key_id);
        String secretKey = context.getString(R.string.amazon_aws_secret_key);
        String associateTag = context.getString(R.string.amazon_associate_tag);
        
        try {
            helper = SignedRequestsHelper.getInstance(endpoint, accessKey, secretKey);
        } catch (Exception e) {
            Log.e(TAG, "Error creating signer", e);
            return null;
        }
        
        String requestUrl = null;

        Map<String, String> serviceParams = new HashMap<String, String>();
        serviceParams.put("Service", "AWSECommerceService");
        serviceParams.put("Version", "2011-08-01");
        serviceParams.put("AssociateTag", associateTag);
        serviceParams.put("Operation", "ItemLookup");
        serviceParams.put("SearchIndex", "All");
        serviceParams.put("IdType", "EAN");
        serviceParams.put("ItemId", ean);
        serviceParams.put("ResponseGroup", "ItemAttributes,EditorialReview,Images");

        requestUrl = helper.sign(serviceParams);
        Log.d(TAG, "Signed Request is \"" + requestUrl + "\"");
        
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android", context);
        HttpGet request = new HttpGet(requestUrl);
        HttpResponse response = null;
        StringResult result = null;
        
        try {
            response = client.execute(request);
            
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                result = new StringResult(false, "HTTP " + response.getStatusLine().getStatusCode() 
                        + ": " + response.getStatusLine().getReasonPhrase());
            } else {
                Document doc = null;
                InputStream is = response.getEntity().getContent();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(is);
                
                NodeList errors = doc.getElementsByTagName("Error");
                if (errors.getLength() > 0) {
                    result = new StringResult(false, errors.item(0).getTextContent());
                } else {               
                    NodeList titleNodes = doc.getElementsByTagName("Title");
                    if (titleNodes.getLength() > 0) {
                        result = new StringResult(true, titleNodes.item(0).getTextContent());
                    }
                }
            }
            
        } catch (ClientProtocolException e) {
            Log.e(TAG, "Http protocol error", e);
            result = new StringResult(false, "Error response");
            
        } catch (IOException e) {
            Log.e(TAG, "Error when requesting URL: \"" + requestUrl + "\" or parsing HTTP response", e);
            result = new StringResult(false, "Error requesting or parsing HTTP response");
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing HTTP response", e);
            result = new StringResult(false, "Error parsing HTTP response");
            
        } finally {
            // Close the connection
            if (client != null && client.getConnectionManager() != null) {
                client.close();
                client = null;
            }
        }
        
        return result;
    }
    
    @Override
    protected void onPostExecute(StringResult result) {
        super.onPostExecute(result);
        
        if (result.isSuccess() && onSuccessListener != null) {
            onSuccessListener.onSuccess(result.getResult());
        }
        
        if (!result.isSuccess() && onFailureListener != null) {
            onFailureListener.onFailure(result.getResult());
        }
    }
    
    public interface OnSuccessListener {
        public void onSuccess(String productName);
    }
    
    public interface OnFailureListener {
        public void onFailure(String failure);
    }
}
