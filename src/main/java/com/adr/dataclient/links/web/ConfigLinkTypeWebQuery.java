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

import com.adr.dataclient.links.ConfigLink;
import com.adr.dataclient.links.ConfigLinkType;
import javafx.scene.Node;

/**
 *
 * @author adrian
 */
public class ConfigLinkTypeWebQuery implements ConfigLinkType {

    ViewLinkTypeWeb viewlink = new ViewLinkTypeWeb();

    @Override
    public String toString() {
        return "Web";
    }

    @Override
    public Node getEditNode() {
        return viewlink.getNode();
    }

    @Override
    public ConfigLink create(String name) {
        return new ConfigLinkWebQuery(this, name);
    }

    public ViewLinkTypeWeb view() {
        return viewlink;
    }
}
