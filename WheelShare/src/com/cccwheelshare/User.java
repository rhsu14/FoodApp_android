package com.cccwheelshare;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

public class User
{
    private int     id;
    private String  email,
                    username,
                    password,
                    name;
    private Vector<Item> basket = new Vector<Item>(100,1);
    
    public User()
    {
        this.id       = 0;
        this.email    = "";
        this.username = "";
        this.name     = "";
        this.password = "";
    }

    public User( final String  email,
                 final String  username,
                 final String  name,
                 final String  password )
    {
        this.id       = 0;
        this.email    = email;
        this.username = username;
        this.name     = name;
        this.password = password;
    }

    public User( JSONObject builder )
    {
        try
        {
            this.id       = builder.getInt( "userId" );
            this.email    = builder.getString( "email" );
            this.password = builder.getString( "password" );
            this.username = builder.getString( "username" );
            this.name     = builder.getString( "name" );
        }
        catch( JSONException e )
        {
            e.printStackTrace();
        }
    }

    public int     ID()                            { return this.id; }
    public void    ID(final int id)                { this.id = id; }
    public String  EMAIL()                         { return this.email; }
    public void    EMAIL(final String email)       { this.email = email; }
    public String  USERNAME()                      { return this.username; }
    public void    USERNAME(final String username) { this.username = username; }
    public String  PASSWORD()                      { return this.password; }
    public void    PASSWORD(final String password) { this.password = password; }
    public String  NAME()                          { return this.name; }
    public void    NAME(final String name)         { this.name = name; }
    
    
    public void    add_to_basket(Item i){
        basket.add(i);
    }
    
    public JSONObject tojson()
    {
        JSONObject builder = new JSONObject();
        
        try
        {
            builder.put( "id", id );
            builder.put( "email", email );
            builder.put( "username", username );
            builder.put( "name", name );
            builder.put( "password", password );
            
        }
        catch( JSONException e )
        {
            e.printStackTrace();
        }
        
        return builder;
    }
}