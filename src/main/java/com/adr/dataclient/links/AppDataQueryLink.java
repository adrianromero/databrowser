/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links;

import com.adr.data.DataLink;
import com.adr.data.QueryLink;

/**
 *
 * @author adrian
 */
public interface AppDataQueryLink {
    DataLink getDataLink();    
    QueryLink getQueryLink();
}
