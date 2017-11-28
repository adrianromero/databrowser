package com.adr.dataclient.links;

import java.util.Properties;

/**
 * Created by adrian on 20/09/17.
 */
public abstract class ConfigLink {

    private String name;

    public ConfigLink(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public abstract void readFromView();
    public abstract void writeToView();
    
    public abstract void readFromProperties(Properties p, String prefix);
    public abstract void writeToProperties(Properties p, String prefix);

    public abstract ConfigLinkType getLinkType();
    public abstract AppLink createAppLink();

    @Override
    public String toString() {
        return name;
    }
}
