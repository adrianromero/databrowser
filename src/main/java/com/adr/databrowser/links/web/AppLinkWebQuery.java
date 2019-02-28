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
package com.adr.databrowser.links.web;

import com.adr.data.Link;
import com.adr.data.http.WebLink;
import com.adr.databrowser.links.AppLink;
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
