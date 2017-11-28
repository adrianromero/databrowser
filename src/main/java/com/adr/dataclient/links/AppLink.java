/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links;

/**
 *
 * @author adrian
 */
public interface AppLink {
    public void create();
    public void destroy(); 
    public AppDataQueryLink get();
}
