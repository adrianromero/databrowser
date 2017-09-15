/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.hellocommon.utils.FXMLUtil;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;

/**
 *
 * @author adrian
 */
public class MainContainer {
    
    @FXML private StackPane root;
    @FXML private Tab command;
    @FXML private Tab datalinks;
    
    private final Application app;
    private Command commandcontroller;
    
    public MainContainer() {
        this.app = new Application();       
        FXMLUtil.load(this, "/com/adr/dataclient/fxml/maincontainer.fxml");   
    }
    
    @FXML
    public void initialize() {
        root.getProperties().put("DialogRoot", true);
        
        commandcontroller = new Command(app);       
        command.setContent(commandcontroller.getNode());     
        
        SytntaxArea area = new SytntaxArea();
        datalinks.setContent(area.getNode());
    }
    
    public Parent getNode() {
        return root;
    }
}
