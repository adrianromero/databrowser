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
