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
package com.adr.databrowser;

import com.adr.databrowser.links.ConfigLink;
import com.adr.databrowser.links.ConfigLinkType;
import com.adr.databrowser.links.sql.ConfigLinkTypeSQL;
import com.adr.databrowser.links.web.ConfigLinkTypeWebQuery;
import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.utils.FXMLUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * Created by adrian on 19/09/17.
 */
public class Links {

    private final ConfigLinkType[] configLinkTypeList = new ConfigLinkType[]{
        new ConfigLinkTypeSQL(),
        new ConfigLinkTypeWebQuery()
    };

    @FXML
    private ResourceBundle resources;
    @FXML
    private BorderPane root;
    @FXML
    private Button addLink;
    @FXML
    private Button removeLink;
    @FXML
    private Button upLink;
    @FXML
    private Button downLink;
    @FXML
    private VBox views;
    @FXML
    private TextField configname;
    @FXML
    private ChoiceBox<ConfigLinkType> configtype;

    @FXML
    private ListView<ConfigLink> linksList;

    private boolean updating = false;
    private Node currentEditNode = null;

    public Links() {
        FXMLUtil.load(this, "/com/adr/databrowser/fxml/links.fxml", "com/adr/databrowser/fxml/links");
    }

    public Parent getNode() {
        return root;
    }

    public ObservableList<ConfigLink> getConfigLinks() {
        ConfigLink item = linksList.getSelectionModel().getSelectedItem();
        if (item != null) {
            item.readFromView();
        }
        saveProperties();
        return linksList.getItems();
    }

    @FXML
    public void initialize() {

        addLink.setGraphic(IconBuilder.create(FontAwesome.FA_PLUS, 18.0).styleClass("icon-fill").build());
        removeLink.setGraphic(IconBuilder.create(FontAwesome.FA_MINUS, 18.0).styleClass("icon-fill").build());
        upLink.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_UP, 18.0).styleClass("icon-fill").build());
        downLink.setGraphic(IconBuilder.create(FontAwesome.FA_CHEVRON_DOWN, 18.0).styleClass("icon-fill").build());

        configtype.setItems(FXCollections.observableArrayList(configLinkTypeList));
        configtype.getSelectionModel().clearSelection();
        configtype.valueProperty().addListener((ObservableValue<? extends ConfigLinkType> ov, ConfigLinkType old_val, ConfigLinkType new_val) -> {
            updateListItem();
        });
        linksList.setCellFactory((ListView<ConfigLink> list) -> new ListCell<ConfigLink>() {
            @Override
            public void updateItem(ConfigLink item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // setGraphic(item.getGraphic());
                    String label = item.getName();
                    setText((label == null || label.isEmpty()) ? resources.getString("label.empty") : label);
                }
            }
        });
        configname.textProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            updateListItem();
        });
        linksList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ConfigLink> ov, ConfigLink old_val, ConfigLink new_val) -> {
            if (old_val != null) {
                old_val.readFromView();
            }
            updateButtonsStatus();
            updateView();
        });
        
        
        updating = true;
        loadProperties();        
        linksList.getSelectionModel().selectFirst();
        updating = false;
        
        // linkslist.
        updateButtonsStatus();
        updateView();
    }
    
    private void loadProperties() {
        File f = new File(System.getProperty("user.home"), ".databrowser-links.properties");
        Properties p = new Properties();
        try {       
            p.load(new FileInputStream(f));
            
            int i = 0;
            String type;
            List<ConfigLink> links = new ArrayList<>();
            while(!"_".equals(type = p.getProperty("link." + Integer.toString(++i) + ".classType", "_"))) {
                ConfigLinkType linktype = getLinkTypeByName(type);
                ConfigLink l = linktype.create(p.getProperty("link." + Integer.toString(i) + ".name"));
                l.readFromProperties(p, "link." + Integer.toString(i));
                links.add(l);
            }
            linksList.setItems(FXCollections.observableArrayList(links));
        } catch (IOException ex) {
            Logger.getLogger(Links.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private ConfigLinkType getLinkTypeByName(String type) throws IOException {
        for (ConfigLinkType linktype: configLinkTypeList) {
            if (type.equals(linktype.getClass().getName())) {
                return linktype;
            }
        }
        throw new IOException("Config Link Type not found: " + type);
    }
    
    private void saveProperties() {
        Properties p = new Properties();
        List<ConfigLink> links = linksList.getItems();
        int i = 0;
        for (ConfigLink l : links) {
            p.setProperty("link."  + Integer.toString(++i) + ".classType", l.getLinkType().getClass().getName());
            p.setProperty("link." + Integer.toString(i) + ".name", l.getName());
            l.writeToProperties(p, "link." + Integer.toString(i)); 
        }
        
        File f = new File(System.getProperty("user.home"), ".databrowser-links.properties");
        try {
            p.store(new FileOutputStream(f), "Links Properties");
        } catch (IOException ex) {
            Logger.getLogger(Links.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateView() {
        if (!updating) {
            updating = true;

            ConfigLink item = linksList.getSelectionModel().getSelectedItem();
            
            if (item == null) {
                configname.setText(null);
                configtype.setValue(null);
                if (currentEditNode != null) {
                    views.getChildren().remove(currentEditNode);
                    currentEditNode = null;
                }
                views.setDisable(true);
            } else {
                // ConfigLinkType selectedtype = configtype.getValue();
                configname.setText(item.getName());
                configtype.setValue(item.getLinkType());
                if (item.getLinkType().getEditNode() != currentEditNode) {
                    if (currentEditNode != null) {
                        views.getChildren().remove(currentEditNode);
                    }
                    currentEditNode = item.getLinkType().getEditNode();
                    views.getChildren().add(currentEditNode);
                }
                item.writeToView();
                views.setDisable(false);
            }

            updating = false;
        }
    }

    private void updateListItem() {
        if (!updating) {
            updating = true;

            int index = linksList.getSelectionModel().getSelectedIndex();
            ConfigLink old_item = linksList.getSelectionModel().getSelectedItem();
            ConfigLink new_item;
            ConfigLinkType selectedtype = configtype.getValue();

            if (old_item.getLinkType() == selectedtype) {
                new_item = old_item;
                new_item.setName(configname.getText());
            } else {
                // Create a new TopicInfo, we cannot reuse current one
                new_item = selectedtype.create(configname.getText());
                if (new_item.getLinkType().getEditNode() != currentEditNode) {
                    if (currentEditNode != null) {
                        views.getChildren().remove(currentEditNode);
                    }
                    currentEditNode = new_item.getLinkType().getEditNode();
                    views.getChildren().add(currentEditNode);
                }
                new_item.writeToView(); // Initialization
            }

            linksList.getItems().set(index, new_item);
            linksList.getSelectionModel().select(new_item);

            updating = false;
        }
    }

    private void updateButtonsStatus() {

        ConfigLink item = linksList.getSelectionModel().getSelectedItem();
        int index = linksList.getSelectionModel().getSelectedIndex();
        if (item == null) {
            removeLink.setDisable(true);
            upLink.setDisable(true);
            downLink.setDisable(true);
        } else {
            removeLink.setDisable(false);
            upLink.setDisable(index <= 0);
            downLink.setDisable(index >= linksList.getItems().size() - 1);
        }
    }

    @FXML
    void onAddLink(ActionEvent event) {
        ConfigLink t = configtype.getItems().get(0).create("<New>");
        linksList.getItems().add(t);
        linksList.getSelectionModel().select(t);
    }

    @FXML
    void onRemoveLink(ActionEvent event) {
        ConfigLink t = linksList.getSelectionModel().getSelectedItem();
        linksList.getItems().remove(t);
    }

    @FXML
    void onUpLink(ActionEvent event) {
        ConfigLink topic = linksList.getSelectionModel().getSelectedItem();
        int index = linksList.getSelectionModel().getSelectedIndex();
        linksList.getItems().remove(index);
        linksList.getItems().add(index - 1, topic);
        linksList.getSelectionModel().select(index - 1);
    }

    @FXML
    void onDownLink(ActionEvent event) {
        ConfigLink topic = linksList.getSelectionModel().getSelectedItem();
        int index = linksList.getSelectionModel().getSelectedIndex();
        linksList.getItems().remove(index);
        linksList.getItems().add(index + 1, topic);
        linksList.getSelectionModel().select(index + 1);
    }
}
