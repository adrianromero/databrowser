/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.web;

/**
 *
 * @author adrian
 */
public class DataURLs {
    
    private final String urlquery;
    private final String urldata;

    public DataURLs(String urlquery, String urldata) {
        this.urlquery = urlquery;
        this.urldata = urldata;
    }

    public String getUrlquery() {
        return urlquery;
    }

    public String getUrldata() {
        return urldata;
    }  
}
