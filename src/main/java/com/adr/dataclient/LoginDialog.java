/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.hellocommon.utils.FXMLUtil;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 *
 * @author adrian
 */
public class LoginDialog {
    @FXML    
    private ResourceBundle resources;  
    @FXML
    private GridPane root;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    
    public LoginDialog() {
        FXMLUtil.load(this, "/com/adr/dataclient/fxml/logindialog.fxml", "com/adr/dataclient/fxml/logindialog");
    }    
    
    public void showDialog(Node owner, BiConsumer<String, String> actionok) {
        username.setText("");
        password.setText("");
        MessageUtils.showConfirm(MessageUtils.getRoot(owner), resources.getString("label.title"), root, (ActionEvent e) -> {
            actionok.accept(username.getText(), password.getText());              
        });   
    }    
}
