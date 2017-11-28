package com.adr.dataclient.links;

import javafx.scene.Node;

/**
 * Created by adrian on 29/09/17.
 */
public interface ConfigLinkType {
    Node getEditNode();
    ConfigLink create(String name);
}
