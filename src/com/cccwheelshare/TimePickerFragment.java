package com.cccwheelshare;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

@SuppressLint({ "ValidFragment", "SimpleDateFormat" })
@SuppressWarnings( "deprecation" )
public class TimePickerFragment extends DialogFragment
                                implements TimePickerDialog.OnTimeSetListener
{
    public EditText FIELD;

    public TimePickerFragment( EditText edit_text )
    {
        this.FIELD = edit_text;
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour   = c.get( Calendar.HOUR_OF_DAY );
        int minute = c.get( Calendar.MINUTE );

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog( getActivity(),
                                     this,
                                     hour,
                                     minute,
                                     DateFormat.is24HourFormat( getActivity() ) );
    }

    @Override
    public void onTimeSet( TimePicker view, int hourOfDay, int minute )
    {
        this.FIELD.setText( new SimpleDateFormat( "HH:mm" )
                            .format( new Date( 0, 0, 0, hourOfDay, minute ) ) );
        // Do something with the time chosen by the user
    }
}

