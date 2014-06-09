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

public class Menu_List extends ListActivity{
    
    private ProgressDialog m_ProgressDialog = null; 
    private ArrayList<Item> m_items = null;
    private OrderAdapter m_adapter;
    private Runnable viewOrders;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_list);
        m_items = new ArrayList<Item>();
        this.m_adapter = new OrderAdapter(this, R.layout.menu_list_item, m_items);
        setListAdapter(this.m_adapter);
        
        registerForContextMenu(getListView());
        
        viewOrders = new Runnable(){
            @Override
            public void run() {
                getOrders();
            }
        };
        Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(Menu_List.this,    
              "Please wait...", "Retrieving data ...", true);
    }
    private Runnable returnRes = new Runnable() {

        @Override
        public void run() {
            if(m_items != null && m_items.size() > 0){
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_items.size();i++)
                m_adapter.add(m_items.get(i));
            }
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
        }
    };
    private void getOrders(){
          try{
              m_items = new ArrayList<Item>();
              Item o1 = new Item();
              o1.setName("Gyro");
              o1.setID(123);
              o1.setCost(199.99);
              Item o2 = new Item();
              o2.setName("Pho");
              o2.setID( 345 );
              o2.setCost( 55.95 );
              Item o3 = new Item();
              o3.setName( "Deliciousness" );
              o3.setID( 788 );
              o3.setCost( 100.15 );
              m_items.add(o1);
              m_items.add(o2);
              m_items.add(o3);
              Thread.sleep(5000);
              Log.i("ARRAY", ""+ m_items.size());
            } catch (Exception e) { 
              Log.e("BACKGROUND_PROC", e.getMessage());
            }
            runOnUiThread(returnRes);
        }
    private class OrderAdapter extends ArrayAdapter<Item> {

        private ArrayList<Item> items;

        public OrderAdapter(Context context, int textViewResourceId, ArrayList<Item> items) {
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
                }
                return v;
        }
        
        
}
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        //String[] names = getResources().getStringArray(R.array.names);
        
        
        switch(item.getItemId()) {
        case R.id.order:
            Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.order) + 
                    " context menu option for " + m_items.get(info.position).getName(),
                    Toast.LENGTH_SHORT).show();
            Globals.Basket.add(m_items.get(info.position));
            return true;
        case R.id.cancel:
            return true;
        case R.id.checkout:
            Globals.Basket.add(m_items.get(info.position));
            startActivityForResult( new Intent( this, Basket.class ), 0 );
            return true;
        default:
            return super.onContextItemSelected(item);
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


/*package com.sai.menu;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ShowContextMenu extends ListActivity {
    

    /** Called when the activity is first created. 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names)));

        registerForContextMenu(getListView());
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }
    
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        String[] names = getResources().getStringArray(R.array.names);
        switch(item.getItemId()) {
        case R.id.edit:
            Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.edit) + 
                    " context menu option for " + names[(int)info.id],
                    Toast.LENGTH_SHORT).show();
            return true;
        case R.id.save:
            Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.save) + 
                    " context menu option for " + names[(int)info.id],
                    Toast.LENGTH_SHORT).show();
            return true;
        case R.id.delete:
            Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.delete) + 
                    " context menu option for " + names[(int)info.id],
                    Toast.LENGTH_SHORT).show();
            return true;
        case R.id.view:
            Toast.makeText(this, "You have chosen the " + getResources().getString(R.string.view) + 
                    " context menu option for " + names[(int)info.id],
                    Toast.LENGTH_SHORT).show();
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }
    
        
}*/