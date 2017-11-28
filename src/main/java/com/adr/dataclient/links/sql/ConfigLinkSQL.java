/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.sql;

import com.adr.data.sql.SQLEngine;
import com.adr.dataclient.links.AppLink;
import com.adr.dataclient.links.ConfigLink;
import com.adr.dataclient.links.ConfigLinkType;
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
        this.datasql = new DataSQL(SQLEngine.POSTGRESQL, "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/hellodb", "tad", "tad");
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
                p.getProperty(prefix + ".password"));
    }

    @Override
    public void writeToProperties(Properties p, String prefix) {
        p.setProperty(prefix + ".engine", datasql.getEngine().toString());
        p.setProperty(prefix + ".driver", datasql.getDriver());
        p.setProperty(prefix + ".url", datasql.getUrl());
        p.setProperty(prefix + ".username", datasql.getUsername());
        p.setProperty(prefix + ".password", datasql.getPassword());
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
