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
package com.adr.databrowser.links.web;

import com.adr.hellocommon.utils.FXMLUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 *
 * @author adrian
 */
public class ViewLinkTypeWeb {

    @FXML
    GridPane root;

    @FXML
    private Label labelquery;
    @FXML
    private CheckBox isquery;
    @FXML
    private TextField urlquery;
    @FXML
    private Label labeldata;
    @FXML
    private CheckBox isdata;
    @FXML
    private TextField urldata;

    public ViewLinkTypeWeb() {
        FXMLUtil.load(this, "/com/adr/databrowser/fxml/linkweb.fxml", "com/adr/databrowser/fxml/linkweb");
    }
    
    public DataURLs getURLs() {
        return new DataURLs(urlquery.getText(), urldata.getText());
    }

    public void setURLs(DataURLs urls) {
        urlquery.setText(urls.getUrlquery());
        isquery.setSelected(!urls.getUrlquery().isEmpty());
        urldata.setText(urls.getUrldata());
        isdata.setSelected(!urls.getUrldata().isEmpty());
    }

    public Node getNode() {
        return root;
    }
    
    @FXML
    void actionData(ActionEvent event) {      
        if (isdata.isSelected()) {
            urldata.setDisable(false);
            urldata.setText("http://localhost:4567/execute");
            labeldata.setDisable(false);
        } else {
            urldata.setDisable(true);
            urldata.setText("");
            labeldata.setDisable(true);
        }
    }

    @FXML
    void actionQuery(ActionEvent event) {
        if (isquery.isSelected()) {
            urlquery.setDisable(false);
            urlquery.setText("http://localhost:4567/query");
            labelquery.setDisable(false);
        } else {
            urlquery.setDisable(true);
            urlquery.setText("");
            labelquery.setDisable(true);
        }
    }    
}
