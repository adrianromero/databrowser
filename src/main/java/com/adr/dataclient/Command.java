/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author adrian
 */
public class Command {
    
    @FXML private BorderPane root;
    
    public Command() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/adr/dataclient/fxml/command.fxml"));
        loader.setController(this);    
        try {
            loader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }        
    }    
    
    public Parent getNode() {
        return root;
    }    
}
