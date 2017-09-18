/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.dataclient.links.SampleQueryLink;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author adrian
 */
public class Application {
    
    SampleQueryLink linkprovider = new SampleQueryLink();
    private final ObservableList<AppQueryLink> appquerylinks;
    private final ObservableList<AppDataLink> appdatalinks;
    
    public Application() {
        linkprovider.create();
        
        appquerylinks = FXCollections.observableArrayList(
                null,
                new AppQueryLink("hellodb", linkprovider.getQueryLink()), 
                new AppQueryLink("hellodb copy", linkprovider.getQueryLink()),
                new AppQueryLink("null", null));
        appdatalinks = FXCollections.observableArrayList(
                null,
                new AppDataLink("hellodb", linkprovider.getDataLink()),
                new AppDataLink("hellodb copy", linkprovider.getDataLink()),
                new AppDataLink("null", null)); 
    }
    
    public ObservableList<AppQueryLink> getQueryLinks() {
        return appquerylinks;
    }

    public ObservableList<AppDataLink> getDataLinks() {
        return appdatalinks;
    }
}
