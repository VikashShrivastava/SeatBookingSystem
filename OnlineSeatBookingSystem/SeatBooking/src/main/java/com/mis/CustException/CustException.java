package com.mis.CustException;

public class CustException extends Exception{
    public CustException(String msg)
    {
        super(msg);
        setStackTrace(new StackTraceElement[0]);
    }
}
