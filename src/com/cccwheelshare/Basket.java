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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Basket extends ListActivity{
    
    private ProgressDialog m_ProgressDialog = null; 
    private ArrayList<Item> m_items = Globals.Basket;
    private BasketAdapter m_adapter;
    private double total_cost;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basket);
        m_items = Globals.Basket;
        this.m_adapter = new BasketAdapter(this, R.layout.menu_list_item, m_items);
        setListAdapter(this.m_adapter);
        
        
        
    }
    
    public void checkout(final View v){
        startActivityForResult( new Intent( this, Ride_Payment.class ), 0 );
        
    }

    private class BasketAdapter extends ArrayAdapter<Item> {

        private ArrayList<Item> items;

        public BasketAdapter(Context context, int textViewResourceId, ArrayList<Item> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.menu_list_item, null);
                }
                Item o = items.get(position);
                if (o != null) {
                        TextView tt = (TextView) v.findViewById(R.id.item_name);
                        TextView pp = (TextView) v.findViewById( R.id.price );
                        if (tt != null) {
                              tt.setText(o.getName());                            
                              }
                        if (pp != null){
                            pp.setText("$" + Double.toString( o.getCost() ));
                        }
                        total_cost = total_cost + o.getCost();
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


