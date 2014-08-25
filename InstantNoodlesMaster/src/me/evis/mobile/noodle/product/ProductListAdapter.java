package me.evis.mobile.noodle.product;

import java.util.ArrayList;
import java.util.List;

import me.evis.mobile.noodle.R;
import me.evis.mobile.noodle.db.ProductDao;
import me.evis.mobile.util.AdsUtil;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * The position == products.size() implies the additional item "Select None".
 */
public class ProductListAdapter extends BaseAdapter implements ActionBar.OnNavigationListener {
    private Context context;
    private List<Product> products = new ArrayList<Product>();
    
    public ProductListAdapter(Context context, List<Product> products) {
        this.context = context;
        if (products != null) {
            this.products = products;
        }
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < products.size()) {
            return products.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (position >= 0 && position < products.size()) {
            return products.get(position).getId();
        } else {
            return -1;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_spinner_item, parent, false);
        TextView text = (TextView) view.findViewById(R.id.product_spinner_item_name);
        
        if (position < products.size()) {
            final Product product = products.get(position);
            text.setText(product.getName());
        } else {
            text.setText(R.string.app_name);
        }
        
        return view;
    } 
    
    @Override
    public View getDropDownView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if (position >= 0 && position < products.size()) {
            final Product product = products.get(position);
            view = LayoutInflater.from(context).inflate(R.layout.product_spinner_dropdown_item, parent, false);
            
            ((TextView) view.findViewById(
                    R.id.product_spinner_item_name)).setText(product.getName());
            ((TextView) view.findViewById(
                    R.id.product_spinner_item_brand)).setText(product.getBrand());
            
            ((ImageButton) view.findViewById(R.id.product_spinner_item_buy)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdsUtil.buyProduct(v.getContext(), product);
                }
            });
            
            ((ImageButton) view.findViewById(R.id.product_spinner_item_delete)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean deleted = ProductDao.deleteByProductId(v.getContext(), product.getProductId());
                    if (deleted) {
                        products.remove(position);
                    }
                    ProductListAdapter.this.notifyDataSetChanged();
                }
            });
        } else {
            view = LayoutInflater.from(context).inflate(
                    R.layout.product_spinner_dropdown_item_select_none, parent, false);
        }
        
        return view;
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long itemId) {
        ProductDao.markSelectedById(context, itemId);
        return true;
    }    
}