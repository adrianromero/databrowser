/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.web;

import com.adr.data.DataLink;
import com.adr.data.QueryLink;
import com.adr.data.http.WebDataLink;
import com.adr.data.http.WebQueryLink;
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
    
    private QueryLink querylink;
    private DataLink datalink;
    
    public AppLinkWebQuery(String name, DataURLs urls) {
        this.name = name;
        this.urls = urls;
    }

    @Override
    public void create() {
        OkHttpClient client = new OkHttpClient.Builder().build();      
        querylink = urls.getUrlquery().isEmpty() ? null : new WebQueryLink(urls.getUrlquery(), client);
        datalink = urls.getUrldata().isEmpty() ? null : new WebDataLink(urls.getUrldata(), client);
    }

    @Override
    public void destroy() {
        querylink = null;
        datalink = null;
    }

    @Override
    public QueryLink getQueryLink() {
        return querylink;
    }
    
    @Override
    public DataLink getDataLink() {
        return datalink;
    }

    @Override
    public String toString() {
        return name;
    }   
}
