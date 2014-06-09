package com.cccwheelshare;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

@SuppressLint({ "ValidFragment", "SimpleDateFormat" })
@SuppressWarnings( "deprecation" )
public class DatePickerFragment extends DialogFragment
                                implements DatePickerDialog.OnDateSetListener
{
    public EditText FIELD;
    
    public DatePickerFragment( EditText edit_text )
    {
        this.FIELD = edit_text;
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year  = c.get( Calendar.YEAR );
        int month = c.get( Calendar.MONTH );
        int day   = c.get( Calendar.DAY_OF_MONTH );

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog( getActivity(),
                                     this,
                                     year,
                                     month,
                                     day );
    }
    
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        this.FIELD.setText( new SimpleDateFormat( "MM/dd/yyyy" )
                            .format( new Date( year - 1900, month, day ) ) );
    }
}

