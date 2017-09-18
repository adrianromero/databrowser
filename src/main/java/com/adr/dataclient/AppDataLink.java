/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.data.DataLink;

/**
 *
 * @author adrian
 */
public class AppDataLink {
    private final String name;
    private final DataLink link;

    public AppDataLink(String name, DataLink link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public DataLink getLink() {
        return link;
    }
    
    @Override
    public String toString() {
        return name;
    }      
}
