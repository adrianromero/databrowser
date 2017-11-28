package com.adr.dataclient.links.sql;

import com.adr.data.sql.SQLEngine;
import com.adr.dataclient.links.ViewLinkType;
import com.adr.hellocommon.utils.FXMLUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Created by adrian on 27/09/17.
 */
class ViewLinkTypeSQL implements ViewLinkType {

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

    public ViewLinkTypeSQL() {
        FXMLUtil.load(this, "/com/adr/dataclient/fxml/linksql.fxml", "com/adr/dataclient/fxml/linksql");
        engine.setItems(FXCollections.observableArrayList(SQLEngine.GENERIC, SQLEngine.H2, SQLEngine.MYSQL, SQLEngine.POSTGRESQL));
    }
    
    public DataSQL getDataSQL() {
        return new DataSQL(engine.getValue(), driver.getText(), url.getText(), username.getText(), password.getText());
    }

    public void setDataSQL(DataSQL datasql) {
        engine.setValue(datasql.getEngine());
        driver.setText(datasql.getDriver());
        url.setText(datasql.getUrl());
        username.setText(datasql.getUsername());
        password.setText(datasql.getPassword());
    }

    @Override
    public Node getNode() {
        return root;
    }
}
