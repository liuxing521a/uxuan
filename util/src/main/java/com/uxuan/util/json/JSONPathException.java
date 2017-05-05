package com.uxuan.util.json;

public class JSONPathException extends JSONException {

    private static final long serialVersionUID = 1L;

    public JSONPathException(String message){
        super(message);
    }
    
    public JSONPathException(String message, Throwable cause){
        super(message, cause);
    }
}
