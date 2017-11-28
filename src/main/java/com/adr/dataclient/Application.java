/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.dataclient.links.AppLink;
import com.adr.dataclient.links.ConfigLink;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import com.adr.dataclient.links.AppDataQueryLink;

/**
 *
 * @author adrian
 */
public class Application {
    
    private final List<AppLink> applinks;          
    private final ObservableList<AppDataQueryLink> appdataquerylinks;
    
    public Application() {       
        applinks = new ArrayList<AppLink>();
        appdataquerylinks = FXCollections.observableArrayList();
    }
    
    public ObservableList<AppDataQueryLink> getDataQueryLinks() {
        return appdataquerylinks;
    }

    public void constructLinks(List<ConfigLink> links) {
        applinks.clear();
        for (ConfigLink l: links) {
            applinks.add(l.createAppLink());
        }
        
        // Now the observables...
        appdataquerylinks.clear();
        
        for (AppLink l: applinks) {
            l.create();
            appdataquerylinks.add(l.get());
        }
    }

    public void destroyLinks() {

        for (AppLink l: applinks) {
            l.destroy();
        }
        applinks.clear();
        
        // Now the observables...
        appdataquerylinks.clear();
    }
}
