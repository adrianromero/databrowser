/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.dataclient.links.AppLink;
import com.adr.dataclient.links.ConfigLink;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 *
 * @author adrian
 */
public class Application {
     
    private final ObservableList<AppLink> applinks;
    
    public Application() {       
        applinks = FXCollections.observableArrayList();
    }
    
    public ObservableList<AppLink> getDataQueryLinks() {
        return applinks;
    }

    public void constructLinks(List<ConfigLink> links) {
        applinks.clear();
        for (ConfigLink l: links) {
            AppLink appl = l.createAppLink();
            appl.create();
            applinks.add(appl);
        }
    }

    public void destroyLinks() {
        for (AppLink l: applinks) {
            l.destroy();
        }
        applinks.clear();
    }
}
