/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient.links.web;

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
        FXMLUtil.load(this, "/com/adr/dataclient/fxml/linkweb.fxml", "com/adr/dataclient/fxml/linkweb");
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
