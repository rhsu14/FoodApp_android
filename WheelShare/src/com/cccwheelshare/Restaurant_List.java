package com.cccwheelshare;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Restaurant_List extends ListActivity{
    
    //private ProgressDialog m_ProgressDialog = null; 
    private ArrayList<Restaurant> m_orders = null;
    private OrderAdapter m_adapter;
    private Runnable viewOrders;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list);
        m_orders = new ArrayList<Restaurant>();
        this.m_adapter = new OrderAdapter(this, R.layout.restaurant_list_item, m_orders);
        setListAdapter(this.m_adapter);
        
        
        viewOrders = new Runnable(){
            @Override
            public void run() {
                getOrders();
            }
        };
        Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
        thread.start();
        //m_ProgressDialog = ProgressDialog.show(Restaurant_List.this,    
         //     "Please wait...", "Retrieving data ...", true);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0){
        startActivityForResult( new Intent( this, Menu_List.class ), 0 );
        }
        if (position == 1){
            startActivityForResult( new Intent( this, User_Register.class ), 0 );
            }
    }
    
    
    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            if(m_orders != null && m_orders.size() > 0){
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_orders.size();i++)
                m_adapter.add(m_orders.get(i));
            }
            //m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
        }
    };
    
   
    private void getOrders(){
          try{
              m_orders = new ArrayList<Restaurant>();
              Restaurant o1 = new Restaurant();
              o1.setRestaurantName("Sam's Mediterranean");
              Restaurant o2 = new Restaurant();
              o2.setRestaurantName("3rd and U Cafe");
              Restaurant o3 = new Restaurant();
              o3.setRestaurantName( "Pho King IV" );
              m_orders.add(o1);
              m_orders.add(o2);
              m_orders.add(o3);
              Log.i("ARRAY", ""+ m_orders.size());
            } catch (Exception e) { 
              Log.e("BACKGROUND_PROC", e.getMessage());
            }
            runOnUiThread(returnRes);
        }
    private class OrderAdapter extends ArrayAdapter<Restaurant> {

        private ArrayList<Restaurant> items;

        public OrderAdapter(Context context, int textViewResourceId, ArrayList<Restaurant> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.restaurant_list_item, null);
                }
                Restaurant o = items.get(position);
                if (o != null) {
                        TextView tt = (TextView) v.findViewById(R.id.restaurant_name);
                        if (tt != null) {
                              tt.setText(o.getRestaurantName());                            }
                }
                return v;
        }
    }
    
    public boolean onCreateOptionsMenu( Menu menu )
    {
        //getMenuInflater().inflate( R.menu.user_login, menu );
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      // action with ID action_refresh was selected
      case R.id.restaurants:
          startActivityForResult( new Intent( this, Restaurant_List.class ), 0 );
        break;
      // action with ID action_settings was selected
      case R.id.check_out:
          startActivityForResult( new Intent( this, Basket.class ), 0 );
        break;
      default:
        break;
      }

      return true;
    } 
    
    
}
