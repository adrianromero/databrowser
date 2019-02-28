//     Data Browser is a JavaFX application for Data
//     Copyright (C) 2019 Adrián Romero Corchado.
//
//     This file is part of Data Browser
//
//     Licensed under the Apache License, Version 2.0 (the "License");
//     you may not use this file except in compliance with the License.
//     You may obtain a copy of the License at
//
//         http://www.apache.org/licenses/LICENSE-2.0
//
//     Unless required by applicable law or agreed to in writing, software
//     distributed under the License is distributed on an "AS IS" BASIS,
//     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//     See the License for the specific language governing permissions and
//     limitations under the License.
package com.adr.databrowser;

import com.adr.data.Link;
import com.adr.data.mem.MemCommandLink;
import com.adr.data.mem.MemQueryLink;
import com.adr.data.mem.Storage;
import com.adr.databrowser.links.AppLink;
import com.adr.databrowser.links.ConfigLink;
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
