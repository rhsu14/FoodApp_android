package com.cccwheelshare;

import java.io.IOException;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class Ride_Payment extends Activity
{
    private static EditText CARD_CSV;
    private static EditText CARD_MONTH;
    private static EditText CARD_NAME;
    private static EditText CARD_NUMBER;
    private static EditText CARD_YEAR;
    private static String   status;
    private static TextView STATUS;
    
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.ride_payment );

        Globals.setAppFont( (ViewGroup) findViewById( R.id.Ride_Payment ),
                            Globals.Font.on ? Typeface.createFromAsset( getAssets(), Globals.Font.mangal )
                                            : Typeface.DEFAULT );

        CARD_CSV    = (EditText) findViewById( R.id.CARD_CSV_Field );
        CARD_MONTH  = (EditText) findViewById( R.id.CARD_MONTH_Field );
        CARD_NAME   = (EditText) findViewById( R.id.CARD_NAME_Field );
        CARD_NUMBER = (EditText) findViewById( R.id.CARD_NUMBER_Field );
        CARD_YEAR   = (EditText) findViewById( R.id.CARD_YEAR_Field );
        STATUS      = (TextView) findViewById( R.id.STATUS );
        
        CARD_NUMBER.setText( Globals.CREDITCARD );
    }
    
    @SuppressWarnings( "deprecation" )
    public void verify( View v ) throws ClientProtocolException, IOException, JSONException
    {
        status = Globals.Empty;
        
        final String card_number = CARD_NUMBER.getText().toString().trim();
        Resources r = this.getResources();
        if( !Luhn( card_number ) )
        {
            status = "\n"+r.getString( R.string.Ride_Payment_Bad_Number_Invalid )+"\n"+status;
        }
        
        final String card_name = CARD_NAME.getText().toString().trim();
        
        if( card_name.equals( Globals.Empty ) )
        {
            status += "\n"+r.getString( R.string.Ride_Payment_Bad_Name_Missing );
        }
        
        final String Card_Month = CARD_MONTH.getText().toString().trim();
        Integer card_month = null; 
        
        if( Card_Month.equals( Globals.Empty ) )
        {
            status += "\n"+"Missing card expiration month.";
        }
        else
        {
            card_month = Integer.parseInt( Card_Month );
            
            if( card_month < 1 || card_month > 12 )
            {
                status += "\n"+r.getString( R.string.Ride_Payment_Bad_Month_Invalid );
            }
        }
        
        final String Card_Year = CARD_YEAR.getText().toString().trim();
        Integer card_year = null; 
        
        if( Card_Year.equals( Globals.Empty ) )
        {
            status += "\n"+r.getString( R.string.Ride_Payment_Bad_Year_Missing );
        }
        else if( Card_Year.length() != 2 )
        {
            status += "\n"+r.getString( R.string.Ride_Payment_Bad_Year_Length );
        }
        else
        {
            Integer this_year   = new Date().getYear(),
                    this_decade = this_year - this_year % 100;

            card_year = this_decade+Integer.parseInt( Card_Year );
            
            if( card_year < this_year || card_year > this_year+20 )
            {
                status += "\n"+r.getString( R.string.Ride_Payment_Bad_Year_Invalid );
            }
        }
        
        final String Card_Csv = CARD_CSV.getText().toString().trim();
        Integer card_csv = null; 
        
        if( Card_Csv.equals( Globals.Empty ) )
        {
            status += "\n"+"Missing csv code.";
        }
        else if( Card_Csv.length() < 3 || Card_Csv.length() > 4 )
        {
            status += "\n"+"Invalid csv code length.";
        }
        else
        {
            card_csv = Integer.parseInt( Card_Csv );
        }
        
        if( status.equals( Globals.Empty ) &&
            !card_year.equals( null ) &&
            !card_month.equals( null ) &&
            !card_csv.equals( null ) )
        {
            STATUS.setText( "Passed" );
            proceed( Ride_Booking.class );
            finish();
        }
        else
        {
            STATUS.setText( "Verification failed:"+"\n"+status );
        }
    }
    
    protected void onNewIntent( Intent intent )
    {
        Bundle bundle = getIntent().getExtras();
        
        if( bundle != null )
        {
            try
            {
                JSONObject data = new JSONObject( bundle.getString( Globals.JSON.data ) );
                STATUS.setText( Globals.Empty+data.getBoolean( Globals.JSON.success ) );
            }
            catch( JSONException e ) {}
        }
    }
    
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        if( requestCode == 1 )
        {
           if( resultCode == RESULT_OK )
           {      
               Boolean success = data.getBooleanExtra( Globals.JSON.success, false );
               setResult( RESULT_OK, new Intent().putExtra( Globals.JSON.success, success ) );
           }
           else if( resultCode == RESULT_CANCELED )
           {   
               STATUS.setText( "Request Canceled" ); 
           }
        }
    }
    
    private Boolean Luhn( final String digits )
    {
        final int size = digits.length();

        if( size >= 13 && size <= 19 )
        {
            int sum = 0;
            
            for( int i = 0;
                 i < size;
                 ++i )
            {
                int value = Integer.parseInt( digits.substring( i, i+1 ) );
                
                if( i % 2 == size % 2 )
                {
                    value *= 2;
                    
                    if( value > 9 )
                    {
                        value %= 10;
                        ++value;
                    }
                }
                
                sum += value;
            }
            
            return sum % 10 == 0;
        }
        else
        {
            status += "\n"+"Incorrect length.";
        }
        
        return false;
    }
    
    private void proceed( Class<?> page )
    {
        startActivityForResult( new Intent( this, page ), 0 );
    }
    
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.ride_payment, menu );
        return true;
    }
}
