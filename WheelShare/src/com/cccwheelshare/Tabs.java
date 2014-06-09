package com.cccwheelshare;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class Tabs extends TabActivity
{
    private TabHost tabHost;

    private Object[][]
        tabInfo =
        {
            { "Search",   Restaurant_List.class },
            { "Create",   Ride_Create.class },
            { "Payment",  Ride_Payment.class },
        };

    public TabSpec createTab( TabHost tabHost, String tabName, Class<?> tabClass )
    {
        return tabHost
            .newTabSpec( tabName )
            .setIndicator( tabName )
            .setContent( new Intent( this, tabClass ) );
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.tabs );

        this.tabHost = getTabHost();

        for( Object tab[] : tabInfo )
        {
            this.tabHost.addTab(
                createTab( this.tabHost,
                           (String) tab[0],
                           (Class<?>) tab[1] )
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.tabs, menu );
        return true;
    }
}