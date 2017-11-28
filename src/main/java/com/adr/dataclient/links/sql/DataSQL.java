/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.sql;

import com.adr.data.sql.SQLEngine;

/**
 *
 * @author adrian
 */
public class DataSQL {
    private final SQLEngine engine;
    private final String driver;
    private final String url;
    private final String password;
    private final String username;

    public DataSQL(SQLEngine engine, String driver, String url, String username, String password) {
        this.engine = engine;
        this.driver = driver;      
        this.url = url;
        this.username = username;        
        this.password = password;
    }

    public SQLEngine getEngine() {
        return engine;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
