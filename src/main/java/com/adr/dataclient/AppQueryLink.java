/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.data.QueryLink;

/**
 *
 * @author adrian
 */
public class AppQueryLink {
    private final String name;
    private final QueryLink link;

    public AppQueryLink(String name, QueryLink link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public QueryLink getLink() {
        return link;
    }

    @Override
    public String toString() {
        return name;
    }  
}
