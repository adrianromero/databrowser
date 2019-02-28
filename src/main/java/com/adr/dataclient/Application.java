/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.data.Link;
import com.adr.data.mem.MemCommandLink;
import com.adr.data.mem.MemQueryLink;
import com.adr.data.mem.Storage;
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
    
    public ObservableList<AppLink> getCommandQueryLinks() {
        return applinks;
    }

    public void constructLinks(List<ConfigLink> links) {
        applinks.clear();
        for (ConfigLink l: links) {
            AppLink appl = l.createAppLink();
            appl.create();
            applinks.add(appl);
        }
        
        AppLink mem = new TestAppLink();
        mem.create();
        applinks.add(mem);
    }

    public void destroyLinks() {
        for (AppLink l: applinks) {
            l.destroy();
        }
        applinks.clear();
    }
        
    private static class TestAppLink implements AppLink {
            
        private Link commandlink = null;
        private Link querylink = null;

        @Override
        public void create() {
            Storage storage = new Storage();
            commandlink = new MemCommandLink(storage);
            querylink = new MemQueryLink(storage);
        }

        @Override
        public void destroy() {
            commandlink = null;
            querylink = null;
        }

        @Override
        public Link getCommandLink() {
            return commandlink;
        }

        @Override
        public Link getQueryLink() {
            return querylink;
        }
        
        @Override
        public String toString() {
            return "Memory";
        }
    }
}
