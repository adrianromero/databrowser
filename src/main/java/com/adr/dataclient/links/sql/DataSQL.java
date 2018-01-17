/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.sql;

import com.adr.data.sql.SQLEngine;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
    private final boolean security;
    private final String secret;
    private final long expires;

    public DataSQL(SQLEngine engine, String driver, String url, String username, String password, boolean security, String secret, long expires) {
        this.engine = engine;
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.security = security;
        this.secret = secret;
        this.expires = expires;
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

    public boolean isSecurity() {
        return security;
    }

    public String getSecret() {
        return secret;
    }

    public long getExpires() {
        return expires;
    }

    public static String genSecret() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            return "d7U6SjuwBG7CcieIAizjYQ==";
        }
    }

    public static long parseExpires(String l) {
        try {
            return Long.parseLong(l);
        } catch (NumberFormatException e) {
            return 500000L;
        }
    }
}
