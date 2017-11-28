/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.sql;

import com.adr.dataclient.links.ConfigLink;
import com.adr.dataclient.links.ConfigLinkType;
import javafx.scene.Node;

/**
 *
 * @author adrian
 */
public class ConfigLinkTypeSQL implements ConfigLinkType {

    ViewLinkTypeSQL viewlink = new ViewLinkTypeSQL();

    @Override
    public String toString() {
        return "SQL";
    }

    @Override
    public Node getEditNode() {
        return viewlink.getNode();
    }

    @Override
    public ConfigLink create(String name) {
        return new ConfigLinkSQL(this, name);
    }

    public ViewLinkTypeSQL view() {
        return viewlink;
    }
}
