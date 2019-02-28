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
package com.adr.dataclient.links;

import java.util.Properties;

/**
 * Created by adrian on 20/09/17.
 */
public abstract class ConfigLink {

    private String name;

    public ConfigLink(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public abstract void readFromView();
    public abstract void writeToView();
    
    public abstract void readFromProperties(Properties p, String prefix);
    public abstract void writeToProperties(Properties p, String prefix);

    public abstract ConfigLinkType getLinkType();
    public abstract AppLink createAppLink();

    @Override
    public String toString() {
        return name;
    }
}
