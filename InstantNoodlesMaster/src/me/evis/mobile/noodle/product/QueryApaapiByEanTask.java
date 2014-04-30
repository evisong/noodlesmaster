package me.evis.mobile.noodle.product;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import me.evis.mobile.noodle.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.amazon.advertising.api.sample.SignedRequestsHelper;

public class QueryApaapiByEanTask extends AsyncTask<String, Void, HttpResponse> {
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
    protected HttpResponse doInBackground(String... params) {
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
        serviceParams.put("ResponseGroup", "Large");

        requestUrl = helper.sign(serviceParams);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Signed Request is \"" + requestUrl + "\"");
        }
        
        HttpClient client = AndroidHttpClient.newInstance("Android", context);
        HttpGet request = new HttpGet(requestUrl);
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (ClientProtocolException e) {
            Log.e(TAG, "Http protocol error", e);
        } catch (IOException e) {
            Log.e(TAG, "Error when requesting URL: \"" + requestUrl + "\"", e);
        }
        
        return response;
//        title = fetchTitle(requestUrl); 
//        if (title == null || title.equals("")) {
//            return new StringResult(false, "Failed to get the product title");
//        }
//        return new StringResult(true, title);
    }
    
    @Override
    protected void onPostExecute(HttpResponse response) {
        super.onPostExecute(response);
        
        if (response == null) {
            failure("Error response");
            return;
        }
        
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            failure("HTTP " + response.getStatusLine().getStatusCode() 
                    + ": " + response.getStatusLine().getReasonPhrase());
            return;
        }
        
        Document doc = null;
        try {
            InputStream is = response.getEntity().getContent();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(is);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing HTTP response", e);
            failure("Error parsing HTTP response");
            return;
        }
        
        NodeList errors = doc.getElementsByTagName("Error");
        if (errors.getLength() > 0) {
            failure(errors.item(0).getTextContent());
            return;
        }
        
        NodeList titleNodes = doc.getElementsByTagName("Title");
        if (titleNodes.getLength() > 0) {
            success(titleNodes.item(0).getTextContent());
        }
    }
    
    private void success(String result) {
        if (onSuccessListener != null) {
            onSuccessListener.onSuccess(result);
        }
    }
    
    private void failure(String result) {
        if (onFailureListener != null) {
            onFailureListener.onFailure(result);
        }
    }

    
    public interface OnSuccessListener {
        public void onSuccess(String productName);
    }
    
    public interface OnFailureListener {
        public void onFailure(String failure);
    }
}
