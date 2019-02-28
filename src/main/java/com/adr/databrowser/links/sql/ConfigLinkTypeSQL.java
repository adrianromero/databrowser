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
package com.adr.databrowser.links.sql;

import com.adr.databrowser.links.ConfigLink;
import com.adr.databrowser.links.ConfigLinkType;
import javafx.scene.Node;

/**
 *
 * @author adrian
 */
public class ConfigLinkTypeSQL implements ConfigLinkType {

    ViewLinkTypeSQL viewlink = new ViewLinkTypeSQL();

    @Override
    public String toString() {
        return "SQL";
    }

    @Override
    public Node getEditNode() {
        return viewlink.getNode();
    }

    @Override
    public ConfigLink create(String name) {
        return new ConfigLinkSQL(this, name);
    }

    public ViewLinkTypeSQL view() {
        return viewlink;
    }
}
