/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.sql;

import com.adr.data.Link;
import com.adr.data.route.ReducerIdentity;
import com.adr.data.route.ReducerLink;
import com.adr.data.security.SecureSentences;
import com.adr.data.security.jwt.ReducerJWTAuthorization;
import com.adr.data.security.jwt.ReducerJWTVerify;
import com.adr.data.security.jwt.ReducerJWTCurrentUser;
import com.adr.data.security.jwt.ReducerJWTLogin;
import com.adr.data.sql.SQLCommandLink;
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
    private Link querylink;
    private Link commandlink; 
    
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
            Link localquerylink = new SQLQueryLink(cpds, engine, SecureSentences.QUERIES);   
            Link localcommandlink = new SQLCommandLink(cpds, engine, SecureSentences.COMMANDS);   
            
            byte[] secret = datasql.getSecret().getBytes(StandardCharsets.UTF_8);
            
            querylink =  new ReducerLink(
                    new ReducerJWTVerify(secret),
                    new ReducerJWTLogin(localquerylink, secret, datasql.getExpires()),
                    new ReducerJWTCurrentUser(),
                    new ReducerJWTAuthorization(localquerylink, "QUERY", Collections.emptySet(), Collections.emptySet()),
                    new ReducerIdentity(localquerylink));  
            commandlink = new ReducerLink(
                    new ReducerJWTVerify(secret),
                    new ReducerJWTAuthorization(localquerylink, "EXECUTE", Collections.emptySet(), Collections.emptySet()),
                    new ReducerIdentity(localcommandlink));
        } else {
            querylink = new SQLQueryLink(cpds, engine);
            commandlink = new SQLCommandLink(cpds, engine);
        }
    }

    @Override
    public void destroy() {
        querylink = null;
        commandlink = null;

        cpds.close();
        cpds = null;
        engine = null;
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
