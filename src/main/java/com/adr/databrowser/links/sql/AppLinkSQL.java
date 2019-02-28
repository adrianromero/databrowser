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
import com.adr.databrowser.links.AppLink;
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
