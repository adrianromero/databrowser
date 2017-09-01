/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author adrian
 */
public class MainContainer {
    
    @FXML private AnchorPane root;
    @FXML private Tab command;
    
    private Command commandcontroller;
    
    public MainContainer() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adr/dataclient/fxml/maincontainer.fxml"));
        loader.setController(this);    
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }        
    }
    
    @FXML
    public void initialize() {
        
        commandcontroller = new Command();
        command.setContent(commandcontroller.getNode());        
    }
    
    public Parent getNode() {
        return root;
    }
}
