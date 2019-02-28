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

import com.adr.data.sql.SQLEngine;
import com.adr.databrowser.links.AppLink;
import com.adr.databrowser.links.ConfigLink;
import com.adr.databrowser.links.ConfigLinkType;
import java.util.Properties;

/**
 * @author adrian
 */
class ConfigLinkSQL extends ConfigLink {

    private final ConfigLinkTypeSQL type;

    private DataSQL datasql;

    public ConfigLinkSQL(ConfigLinkTypeSQL type, String name) {
        super(name);
        this.type = type;
        this.datasql = new DataSQL(SQLEngine.POSTGRESQL, 
                "org.postgresql.Driver", 
                "jdbc:postgresql://localhost:5432/hellodb", 
                "tad", 
                "tad", 
                true, 
                DataSQL.genSecret(), 
                500000L);
    }

    @Override
    public void readFromView() {
        datasql = type.view().getDataSQL();
    }

    @Override
    public void writeToView() {
        type.view().setDataSQL(datasql);
    }

    @Override
    public void readFromProperties(Properties p, String prefix) {
        datasql = new DataSQL(
                SQLEngine.valueOf(p.getProperty(prefix + ".engine")),
                p.getProperty(prefix + ".driver"),
                p.getProperty(prefix + ".url"),
                p.getProperty(prefix + ".username"),
                p.getProperty(prefix + ".password"),
                Boolean.parseBoolean(p.getProperty(prefix + ".security")),
                p.getProperty(prefix + ".secret"),
                DataSQL.parseExpires(p.getProperty(prefix + ".expires")));
    }

    @Override
    public void writeToProperties(Properties p, String prefix) {
        p.setProperty(prefix + ".engine", datasql.getEngine().toString());
        p.setProperty(prefix + ".driver", datasql.getDriver());
        p.setProperty(prefix + ".url", datasql.getUrl());
        p.setProperty(prefix + ".username", datasql.getUsername());
        p.setProperty(prefix + ".password", datasql.getPassword());
        p.setProperty(prefix + ".security", Boolean.toString(datasql.isSecurity()));
        p.setProperty(prefix + ".secret", datasql.getSecret());
        p.setProperty(prefix + ".expires", Long.toString(datasql.getExpires()));
    }

    @Override
    public ConfigLinkType getLinkType() {
        return type;
    }

    @Override
    public AppLink createAppLink() {
        return new AppLinkSQL(getName(), datasql);
    }

}
