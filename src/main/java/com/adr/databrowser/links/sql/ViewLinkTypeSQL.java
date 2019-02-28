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
package com.adr.databrowser.links.sql;

import com.adr.data.sql.SQLEngine;
import com.adr.hellocommon.utils.FXMLUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Created by adrian on 27/09/17.
 */
class ViewLinkTypeSQL {

    @FXML
    GridPane root;

    @FXML
    private ChoiceBox<SQLEngine> engine;
    @FXML
    private TextField driver;
    @FXML
    private TextField url;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private CheckBox security;
    @FXML
    private Label labelsecret;
    @FXML
    private Label labelexpires;
    @FXML
    private TextField secret;
    @FXML
    private TextField expires;

    public ViewLinkTypeSQL() {
        FXMLUtil.load(this, "/com/adr/databrowser/fxml/linksql.fxml", "com/adr/databrowser/fxml/linksql");
        engine.setItems(FXCollections.observableArrayList(SQLEngine.GENERIC, SQLEngine.H2, SQLEngine.MYSQL, SQLEngine.POSTGRESQL));
    }

    @FXML
    void actionSecurity(ActionEvent event) {
        if (security.isSelected()) {
            secret.setDisable(false);
            secret.setText(DataSQL.genSecret());
            labelsecret.setDisable(false);
            expires.setDisable(false);
            expires.setText(Long.toString(500000L));
            labelexpires.setDisable(false);
        } else {
            secret.setDisable(true);
            secret.setText("");
            labelsecret.setDisable(true);
            expires.setDisable(true);
            expires.setText("");
            labelexpires.setDisable(true);
        }
    }
        
    public DataSQL getDataSQL() {
        return new DataSQL(
                engine.getValue(), 
                driver.getText(), 
                url.getText(), 
                username.getText(), 
                password.getText(), 
                security.isSelected(), 
                secret.getText(), 
                DataSQL.parseExpires(expires.getText()));
    }

    public void setDataSQL(DataSQL datasql) {
        engine.setValue(datasql.getEngine());
        driver.setText(datasql.getDriver());
        url.setText(datasql.getUrl());
        username.setText(datasql.getUsername());
        password.setText(datasql.getPassword());
        security.setSelected(datasql.isSecurity());
        secret.setText(datasql.getSecret());
        secret.setDisable(!datasql.isSecurity());
        expires.setText(Long.toString(datasql.getExpires()));
        expires.setDisable(!datasql.isSecurity());
    }
    
    public Node getNode() {
        return root;
    }
}
