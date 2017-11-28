/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.hellocommon.utils.FXMLUtil;
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
        
        app.constructLinks(linkscontroller.getConfigLinks());
        
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
