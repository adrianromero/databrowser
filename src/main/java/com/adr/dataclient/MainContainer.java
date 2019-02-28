//     Data Browser is a JavaFX application for Data
//     Copyright (C) 2019 Adrián Romero Corchado.
//
//     This file is part of Data Browser
//
//     Licensed under the Apache License, Version 2.0 (the "License");
//     you may not use this file except in compliance with the License.
//     You may obtain a copy of the License at
//
//         http://www.apache.org/licenses/LICENSE-2.0
//
//     Unless required by applicable law or agreed to in writing, software
//     distributed under the License is distributed on an "AS IS" BASIS,
//     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//     See the License for the specific language governing permissions and
//     limitations under the License.
package com.adr.dataclient;

import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.hellocommon.utils.FXMLUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 *
 * @author adrian
 */
public class MainContainer {

    private final static Logger LOG = Logger.getLogger(MainContainer.class.getName());
    
    @FXML private StackPane root;
    @FXML private StackPane appcontainer;
    @FXML private Button connect;
    @FXML private Button disconnect;

    private final Application app;
    private Command commandcontroller;
    private Links linkscontroller;

    
    public MainContainer() {
        this.app = new Application();       
        FXMLUtil.load(this, "/com/adr/dataclient/fxml/maincontainer.fxml");   
    }
    
    @FXML
    public void initialize() {
        MessageUtils.setDialogRoot(root, true);
        MessageUtils.useDefaultCSS();
        
        root.getStylesheets().add(getClass().getResource("/com/adr/dataclient/styles/main.css").toExternalForm());
//        root.getStylesheets().add(getClass().getResource("/com/adr/dataclient/styles/dark.css").toExternalForm());

        commandcontroller = new Command(app);
        linkscontroller = new Links();

        appcontainer.getChildren().addAll(commandcontroller.getNode(), linkscontroller.getNode());

        disconnect.setVisible(false);
        commandcontroller.getNode().setVisible(false);
    }

    @FXML
    void actionConnect(ActionEvent event) {
        
        try {
            app.constructLinks(linkscontroller.getConfigLinks());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Connection error", ex);
            app.destroyLinks();
            MessageUtils.showWarning(root, "chungy", "messge", e ->{});   
            return;
        }
        
        disconnect.setVisible(true);
        commandcontroller.getNode().setVisible(true);
        commandcontroller.start();
        
        connect.setVisible(false);
        linkscontroller.getNode().setVisible(false);
    }
    
    @FXML
    void actionDisconnect(ActionEvent event) {
        disconnect.setVisible(false);
        commandcontroller.getNode().setVisible(false);
        connect.setVisible(true);
        linkscontroller.getNode().setVisible(true);
        
        app.destroyLinks();
    }

    public Parent getNode() {
        return root;
    }
}
