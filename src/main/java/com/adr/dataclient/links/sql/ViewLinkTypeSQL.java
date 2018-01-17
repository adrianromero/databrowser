package com.adr.dataclient.links.sql;

import com.adr.data.sql.SQLEngine;
import com.adr.hellocommon.utils.FXMLUtil;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
        FXMLUtil.load(this, "/com/adr/dataclient/fxml/linksql.fxml", "com/adr/dataclient/fxml/linksql");
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
        expires.setText(Long.toString(datasql.getExpires()));
    }
    
    public Node getNode() {
        return root;
    }
}
