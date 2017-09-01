package com.adr.dataclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        
        MainContainer mainc = new MainContainer();
        Scene scene = new Scene(mainc.getNode());
 
        

        stage.setTitle("Data Client");
        stage.setScene(scene);        
        // stage.getIcons().add(new Image("/images/mimamememu.png"));
        stage.show();        
    }
}
