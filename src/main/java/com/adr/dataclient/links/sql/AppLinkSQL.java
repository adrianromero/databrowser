/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.sql;

import com.adr.data.DataLink;
import com.adr.data.QueryLink;
import com.adr.data.sql.SQLDataLink;
import com.adr.data.sql.SQLEngine;
import com.adr.data.sql.SQLQueryLink;
import com.adr.dataclient.links.AppLink;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import com.adr.dataclient.links.AppDataQueryLink;

/**
 *
 * @author adrian
 */
class AppLinkSQL implements AppLink, AppDataQueryLink {

    private final static Logger LOG = Logger.getLogger(AppLinkSQL.class.getName());
    
    private final String name;
    private final DataSQL datasql;
    private HikariDataSource cpds;
    private SQLEngine engine;
    private QueryLink querylink;
    private DataLink datalink;
    
    public AppLinkSQL(String name, DataSQL datasql) {
        this.name = name;
        this.datasql = datasql;
    }

    private QueryLink createQueryLink() {
        return new SQLQueryLink(cpds, engine);   
    }

    private DataLink createDataLink() {
        return new SQLDataLink(cpds, engine);
    }

    @Override
    public void create() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(datasql.getDriver());
        config.setJdbcUrl(datasql.getUrl());
        config.setUsername(datasql.getUsername());
        config.setPassword(datasql.getPassword());
        cpds = new HikariDataSource(config);
        engine = datasql.getEngine();
        LOG.log(Level.INFO, "Database engine = {0}", engine.toString());

        querylink = createQueryLink();
        datalink = createDataLink();
    }

    @Override
    public void destroy() {
        querylink = null;
        datalink = null;

        cpds.close();
        cpds = null;
        engine = null;
    }

    @Override
    public AppDataQueryLink get() {
        return this;
     }    

    @Override
    public QueryLink getQueryLink() {
        return querylink;
    }

    @Override
    public DataLink getDataLink() {
        return datalink;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
