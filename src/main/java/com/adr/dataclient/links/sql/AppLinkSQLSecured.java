/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.sql;

import com.adr.data.DataLink;
import com.adr.data.QueryLink;
import com.adr.data.route.ReducerDataIdentity;
import com.adr.data.route.ReducerDataLink;
import com.adr.data.route.ReducerQueryIdentity;
import com.adr.data.route.ReducerQueryLink;
import com.adr.data.security.SecureCommands;
import com.adr.data.security.jwt.ReducerDataJWTAuthorization;
import com.adr.data.security.jwt.ReducerDataJWTVerify;
import com.adr.data.security.jwt.ReducerJWTCurrentUser;
import com.adr.data.security.jwt.ReducerJWTLogin;
import com.adr.data.security.jwt.ReducerQueryJWTAuthorization;
import com.adr.data.security.jwt.ReducerQueryJWTVerify;
import com.adr.data.sql.SQLDataLink;
import com.adr.data.sql.SQLEngine;
import com.adr.data.sql.SQLQueryLink;
import com.adr.dataclient.links.AppDataLink;
import com.adr.dataclient.links.AppLink;
import com.adr.dataclient.links.AppQueryLink;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;

/**
 *
 * @author adrian
 */
class AppLinkSQLSecured implements AppLink, AppQueryLink, AppDataLink {

    private final static Logger LOG = Logger.getLogger(AppLinkSQLSecured.class.getName());
    
    private final String name;
    private final DataSQL datasql;
    private HikariDataSource cpds;
    private SQLEngine engine;
    private QueryLink querylink;
    private DataLink datalink;
    
    private byte[] secret = "mysecret".getBytes(StandardCharsets.UTF_8);
    private long expires = 500000L;
    
    public AppLinkSQLSecured(String name, DataSQL datasql) {
        this.name = name;
        this.datasql = datasql;
    }

    private QueryLink createQueryLink() {
        QueryLink localquerylink = new SQLQueryLink(cpds, engine, SecureCommands.QUERIES);   
        return new ReducerQueryLink(
                new ReducerQueryJWTVerify(secret),
                new ReducerJWTLogin(localquerylink, secret, expires),
                new ReducerJWTCurrentUser(),
                new ReducerQueryJWTAuthorization(localquerylink, Collections.emptySet(), Collections.emptySet()),
                new ReducerQueryIdentity(localquerylink));        
    }

    private DataLink createDataLink() {
        QueryLink localquerylink = new SQLQueryLink(cpds, engine, SecureCommands.QUERIES);
        DataLink localdatalink = new SQLDataLink(cpds, engine, SecureCommands.COMMANDS);        
        return new ReducerDataLink(
                new ReducerDataJWTVerify(secret),
                new ReducerDataJWTAuthorization(localquerylink, Collections.emptySet(), Collections.emptySet()),
                new ReducerDataIdentity(localdatalink));
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
    public void publish(ObservableList<AppDataLink> appdatalinks, ObservableList<AppQueryLink> appquerylinks) {
        appdatalinks.add(this);
        appquerylinks.add(this);
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
