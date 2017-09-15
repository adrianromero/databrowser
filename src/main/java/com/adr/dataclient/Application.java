/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.data.DataLink;
import com.adr.data.QueryLink;
import com.adr.dataclient.links.SampleQueryLink;

/**
 *
 * @author adrian
 */
public class Application {
    
    SampleQueryLink linkprovider = new SampleQueryLink();
    
    
    public Application() {
        linkprovider.create();
    }
    
    public QueryLink getQueryLink() {
        return linkprovider.getQueryLink();
    }
    
    public DataLink getDataLink() {
        return linkprovider.getDataLink();
    }   
}
