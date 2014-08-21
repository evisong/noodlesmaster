package me.evis.mobile.noodle.product;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import me.evis.mobile.noodle.R;
import me.evis.mobile.util.DeviceUtil;
import me.evis.mobile.util.XmlUtil;

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

public class QueryProductByEanTask extends AsyncTask<String, Void, ObjectResult<Product>> {
    private static final String TAG = "QueryApaapiByEanTask";
    
    private Context context;
    private OnSuccessListener onSuccessListener;
    private OnFailureListener onFailureListener;
    
    public QueryProductByEanTask(Context context, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener) {
        this.context = context;
        this.onSuccessListener = onSuccessListener;
        this.onFailureListener = onFailureListener;
    }
    
    @Override
    protected ObjectResult<Product> doInBackground(String... params) {
        if (!DeviceUtil.isOnline(context)) {
            return new ObjectResult<Product>(false, context.getString(R.string.error_no_network));
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
        // Temporarily removed product description to shrink the response size
        //serviceParams.put("ResponseGroup", "ItemAttributes,EditorialReview,Images");
        serviceParams.put("ResponseGroup", "ItemAttributes");

        requestUrl = helper.sign(serviceParams);
        Log.d(TAG, "Signed Request is \"" + requestUrl + "\"");
        
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android", context);
        HttpGet request = new HttpGet(requestUrl);
        HttpResponse response = null;
        ObjectResult<Product> result = null;
        
        try {
            response = client.execute(request);
            
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                String resultStr = "HTTP " + response.getStatusLine().getStatusCode() 
                        + ": " + response.getStatusLine().getReasonPhrase();
                Log.d(TAG, resultStr);
                
                // Handle specific errors
                switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_SERVICE_UNAVAILABLE:
                    resultStr = context.getString(R.string.error_service_error);
                    break;

                default:
                    break;
                }
                
                result = new ObjectResult<Product>(false, resultStr);
                
            } else {
                Document doc = null;
                InputStream is = response.getEntity().getContent();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(is);
                
                NodeList errors = doc.getElementsByTagName("Error");
                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                
                if (errors.getLength() > 0) {
                    String resultStr = "Service Error: " + errors.item(0).getTextContent();
                    Log.d(TAG, resultStr);
                    
                    // Handle specific errors
                    String code = XmlUtil.getXPathItemText(doc, xpath, "//Error/Code/text()");
                    if ("AWS.InvalidParameterValue".equals(code)) {
                        resultStr = context.getString(R.string.error_barcode_not_found);
                    } else if ("AWS.InternalError".equals(code)) {
                        resultStr = context.getString(R.string.error_service_error);
                    } else if ("AWS.InvalidAccount".equals(code)) {
                        resultStr = context.getString(R.string.error_need_upgrade);
                    } else if ("RequestThrottled".equals(code)) {
                        resultStr = context.getString(R.string.error_service_error);
                    }
                    
                    result = new ObjectResult<Product>(false, resultStr);
                    
                } else {
                    NodeList itemNodes = doc.getElementsByTagName("Item");
                    if (itemNodes.getLength() > 0) {
                        Product product = new Product();
                        
                        product.setProductId(XmlUtil.getXPathItemText(doc, xpath, "//Item/ASIN/text()"));
                        product.setName(XmlUtil.getXPathItemText(doc, xpath, "//Item/ItemAttributes/Title/text()"));
                        product.setManufacturer(XmlUtil.getXPathItemText(doc, xpath, "//Item/ItemAttributes/Manufacturer/text()"));
                        product.setImageUrl(XmlUtil.getXPathItemText(doc, xpath, "//Item/MediumImage/URL/text()"));
                        product.setBuyUrl(XmlUtil.getXPathItemText(doc, xpath, "//Item/DetailPageURL/text()"));
                        String brandOrAuthor = XmlUtil.getXPathItemText(doc, xpath, "//Item/ItemAttributes/Author/text()");
                        if (brandOrAuthor == null) {
                            brandOrAuthor = XmlUtil.getXPathItemText(doc, xpath, "//Item/ItemAttributes/Brand/text()");
                        }
                        product.setBrand(brandOrAuthor);
                        // Temporarily removed product description to shrink the response size
                        //product.setDescription(XmlUtil.getXPathItemText(doc, xpath, "//Item/EditorialReviews/EditorialReview/Content/text()"));
                        result = new ObjectResult<Product>(true, null, product);
                    } else {
                        Log.d(TAG, "Items are empty");
                        result = new ObjectResult<Product>(false, "Items are empty");
                    }
                }
            }
            
        } catch (ClientProtocolException e) {
            Log.e(TAG, "Http protocol error", e);
            result = new ObjectResult<Product>(false, "Error response");
            
        } catch (IOException e) {
            Log.e(TAG, "Error when requesting URL: \"" + requestUrl + "\" or parsing HTTP response", e);
            result = new ObjectResult<Product>(false, "Error requesting or parsing HTTP response");
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing HTTP response", e);
            result = new ObjectResult<Product>(false, "Error parsing HTTP response");
            
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
    protected void onPostExecute(ObjectResult<Product> result) {
        super.onPostExecute(result);
        
        if (result.isSuccess() && onSuccessListener != null) {
            onSuccessListener.onSuccess(result.getResult());
        }
        
        if (!result.isSuccess() && onFailureListener != null) {
            onFailureListener.onFailure(result.getMessage());
        }
    }
    
    public interface OnSuccessListener {
        public void onSuccess(Product product);
    }
    
    public interface OnFailureListener {
        public void onFailure(String failure);
    }
}
