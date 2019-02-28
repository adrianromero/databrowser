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
package com.adr.dataclient.links.web;

import com.adr.dataclient.links.AppLink;
import com.adr.dataclient.links.ConfigLink;
import com.adr.dataclient.links.ConfigLinkType;
import java.util.Properties;

/**
 *
 * @author adrian
 */
public class ConfigLinkWebQuery extends ConfigLink {

    private final ConfigLinkTypeWebQuery type;

    private DataURLs urls;

    public ConfigLinkWebQuery(ConfigLinkTypeWebQuery type, String name) {
        super(name);
        this.type = type;
        this.urls = new DataURLs("http://localhost:4567/query", "http://localhost:4567/execute");
    }

    @Override
    public void readFromView() {
        urls = type.view().getURLs();
    }

    @Override
    public void writeToView() {
        type.view().setURLs(urls);
    }

    @Override
    public void readFromProperties(Properties p, String prefix) {
        urls = new DataURLs(p.getProperty(prefix + ".urlquery"), p.getProperty(prefix + ".urlexecute"));
    }

    @Override
    public void writeToProperties(Properties p, String prefix) {
        p.setProperty(prefix + ".urlquery", urls.getUrlquery());
        p.setProperty(prefix + ".urlexecute", urls.getUrldata());
    }

    @Override
    public ConfigLinkType getLinkType() {
        return type;
    }

    @Override
    public AppLink createAppLink() {
        return new AppLinkWebQuery(getName(), urls);
    }
}
