package com.cccwheelshare;

import org.json.JSONException;
import org.json.JSONObject;

public class Item
{
    private int     id;
    private double  cost;
    private String  name;

    public Item()
    {
        this.id       = 0;
        this.cost     = 0;
        this.name     = "";
    }

    public Item( final String  name,
                 final float  cost,
                 final int id)
    {
        this.id       = id;
        this.name     = name;
        this.cost     = cost;
    }

    public Item( JSONObject builder )
    {
        try
        {
            this.id       = builder.getInt( "userId" );
            this.cost     = builder.getDouble( "cost");
            this.name     = builder.getString( "name" );
        }
        catch( JSONException e )
        {
            e.printStackTrace();
        }
    }
    
    public void setName(String i){
        this.name = i;
    }
    
    public String getName(){
        return this.name;
    }
    
    public void setID(int i){
        this.id = i;
    }
    
    public int getID(){
        return this.id;
    }
    
    public double getCost(){
        return this.cost;
    }
    
    public void setCost(double d){
        this.cost = d;
    }

    public int     ID()                            { return this.id; }
    public void    ID(final int id)                { this.id = id; }
    public String  NAME()                          { return this.name; }
    public void    NAME(final String name)         { this.name = name; }
    
    public JSONObject tojson()
    {
        JSONObject builder = new JSONObject();
        
        try
        {
            builder.put( "id", id );
            builder.put( "name", name );
            builder.put( "cost", cost );
        }
        catch( JSONException e )
        {
            e.printStackTrace();
        }
        
        return builder;
    }
}