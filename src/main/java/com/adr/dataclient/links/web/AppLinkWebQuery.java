/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.web;

import com.adr.data.Link;
import com.adr.data.http.WebLink;
import com.adr.dataclient.links.AppLink;
import java.util.logging.Logger;
import okhttp3.OkHttpClient;

/**
 *
 * @author adrian
 */
public class AppLinkWebQuery implements AppLink {

    private final static Logger LOG = Logger.getLogger(AppLinkWebQuery.class.getName());
    
    private final String name;
    private final DataURLs urls;
    
    private Link querylink;
    private Link commandlink;
    
    public AppLinkWebQuery(String name, DataURLs urls) {
        this.name = name;
        this.urls = urls;
    }

    @Override
    public void create() {
        OkHttpClient client = new OkHttpClient.Builder().build();      
        querylink = urls.getUrlquery().isEmpty() ? null : new WebLink(urls.getUrlquery(), client);
        commandlink = urls.getUrldata().isEmpty() ? null : new WebLink(urls.getUrldata(), client);
    }

    @Override
    public void destroy() {
        querylink = null;
        commandlink = null;
    }

    @Override
    public Link getQueryLink() {
        return querylink;
    }
    
    @Override
    public Link getCommandLink() {
        return commandlink;
    }

    @Override
    public String toString() {
        return name;
    }   
}
