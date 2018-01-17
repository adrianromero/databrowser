/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.web;

import com.adr.dataclient.links.ConfigLink;
import com.adr.dataclient.links.ConfigLinkType;
import javafx.scene.Node;

/**
 *
 * @author adrian
 */
public class ConfigLinkTypeWebQuery implements ConfigLinkType {

    ViewLinkTypeWeb viewlink = new ViewLinkTypeWeb();

    @Override
    public String toString() {
        return "Web";
    }

    @Override
    public Node getEditNode() {
        return viewlink.getNode();
    }

    @Override
    public ConfigLink create(String name) {
        return new ConfigLinkWebQuery(this, name);
    }

    public ViewLinkTypeWeb view() {
        return viewlink;
    }
}
