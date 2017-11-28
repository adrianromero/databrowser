/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.dataclient.links.AppDataLink;
import com.adr.dataclient.links.AppLink;
import com.adr.dataclient.links.AppQueryLink;
import com.adr.dataclient.links.ConfigLink;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 *
 * @author adrian
 */
public class Application {
    
    private final List<AppLink> applinks;          
    private final ObservableList<AppQueryLink> appquerylinks;
    private final ObservableList<AppDataLink> appdatalinks;
    
    public Application() {       
        applinks = new ArrayList<AppLink>();
        appquerylinks = FXCollections.observableArrayList();
        appdatalinks = FXCollections.observableArrayList();
    }
    
    public ObservableList<AppQueryLink> getQueryLinks() {
        return appquerylinks;
    }

    public ObservableList<AppDataLink> getDataLinks() {
        return appdatalinks;
    }

    public void constructLinks(List<ConfigLink> links) {
        applinks.clear();
        for (ConfigLink l: links) {
            applinks.add(l.createAppLink());
        }
        
        // Now the observables...
        appquerylinks.clear();
        appdatalinks.clear(); 
        
        for (AppLink l: applinks) {
            l.create();
            l.publish(appdatalinks, appquerylinks);
        }
    }

    public void destroyLinks() {

        for (AppLink l: applinks) {
            l.destroy();
        }
        applinks.clear();
        
        // Now the observables...
        appquerylinks.clear();
        appdatalinks.clear(); 
    }

}
