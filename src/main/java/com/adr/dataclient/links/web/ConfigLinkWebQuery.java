/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.web;

import com.adr.dataclient.links.AppLink;
import com.adr.dataclient.links.ConfigLink;
import com.adr.dataclient.links.ConfigLinkType;
import java.util.Properties;

/**
 *
 * @author adrian
 */
public class ConfigLinkWebQuery extends ConfigLink {

    private final ConfigLinkTypeWebQuery type;

    private DataURLs urls;

    public ConfigLinkWebQuery(ConfigLinkTypeWebQuery type, String name) {
        super(name);
        this.type = type;
        this.urls = new DataURLs("http://localhost:4567/query", "http://localhost:4567/execute");
    }

    @Override
    public void readFromView() {
        urls = type.view().getURLs();
    }

    @Override
    public void writeToView() {
        type.view().setURLs(urls);
    }

    @Override
    public void readFromProperties(Properties p, String prefix) {
        urls = new DataURLs(p.getProperty(prefix + ".urlquery"), p.getProperty(prefix + ".urlexecute"));
    }

    @Override
    public void writeToProperties(Properties p, String prefix) {
        p.setProperty(prefix + ".urlquery", urls.getUrlquery());
        p.setProperty(prefix + ".urlexecute", urls.getUrldata());
    }

    @Override
    public ConfigLinkType getLinkType() {
        return type;
    }

    @Override
    public AppLink createAppLink() {
        return new AppLinkWebQuery(getName(), urls);
    }
}
