package com.cccwheelshare;

import java.util.Date;

public class Transaction
{
    private String  method;
    private int     toID,
                    fromID;
    private Date    sentTime;

    public Transaction()
    {
        this.method   = "";
        this.toID     = 0;
        this.fromID   = 0;
        this.sentTime = new Date();
    }

    public Transaction( String method,
                        int toID,
                        int fromID,
                        Date sent )
    {
        this.method   = method;
        this.toID     = toID;
        this.fromID   = fromID;
        this.sentTime = sent;
    }

    public int  TOID()                    { return this.toID; }
    public void TOID(final int toID)      { this.toID = toID; }
    public int  FROMID()                  { return this.fromID; }
    public void FROMID(final int fromID)  { this.fromID = fromID; }
    public Date SENT()                    { return this.sentTime; }
    public void SENT(final Date sentTime) { this.sentTime = sentTime; }

    public String print()
    {
        return this.method;
    }

}
