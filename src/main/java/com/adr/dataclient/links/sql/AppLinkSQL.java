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
import com.adr.dataclient.links.AppLink;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adrian
 */
class AppLinkSQL implements AppLink {

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

        if (datasql.isSecurity()) {
            QueryLink localquerylink = new SQLQueryLink(cpds, engine, SecureCommands.QUERIES);   
            DataLink localdatalink = new SQLDataLink(cpds, engine, SecureCommands.COMMANDS);   
            
            byte[] secret = datasql.getSecret().getBytes(StandardCharsets.UTF_8);
            
            querylink =  new ReducerQueryLink(
                    new ReducerQueryJWTVerify(secret),
                    new ReducerJWTLogin(localquerylink, secret, datasql.getExpires()),
                    new ReducerJWTCurrentUser(),
                    new ReducerQueryJWTAuthorization(localquerylink, Collections.emptySet(), Collections.emptySet()),
                    new ReducerQueryIdentity(localquerylink));  
            datalink = new ReducerDataLink(
                    new ReducerDataJWTVerify(secret),
                    new ReducerDataJWTAuthorization(localquerylink, Collections.emptySet(), Collections.emptySet()),
                    new ReducerDataIdentity(localdatalink));
        } else {
            querylink = new SQLQueryLink(cpds, engine);
            datalink = new SQLDataLink(cpds, engine);
        }
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
