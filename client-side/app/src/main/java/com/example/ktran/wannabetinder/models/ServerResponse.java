package com.example.ktran.wannabetinder.models;

/**
 * Created by ktran on 11/26/17.
 */

public class ServerResponse {
    private String message;
    private boolean success;
    private String token;


    public String getMessage() {
        return message;
    }
    public String getjsonToken(){return token;}
    public boolean getSuccess() { return success;}
}