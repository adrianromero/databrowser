/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import java.io.IOException;

import com.adr.data.DataException;
import com.adr.data.record.Entry;
import com.adr.data.record.Record;
import com.adr.data.record.RecordMap;
import com.adr.data.recordparser.RecordsSerializer;
import com.adr.data.security.ReducerLogin;
import com.adr.data.var.VariantString;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.hellocommon.utils.FXMLUtil;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

/**
 *
 * @author adrian
 */
public class Command {

    @FXML
    private BorderPane root;
    @FXML
    private BorderPane recordscontainer;
    @FXML
    private BorderPane outputcontainer;
    @FXML
    private Label tasks;

    private SytntaxArea commandHeader;
    private SytntaxArea commandField;
    private SytntaxArea commandOutput;
    
    private final Application app;
    private int running = 0;

    public Command(Application app) {
        this.app = app;
        FXMLUtil.load(this, "/com/adr/dataclient/fxml/command.fxml");
    }

    public Parent getNode() {
        return root;
    }

    @FXML
    void initialize() {
        
        commandHeader = new SytntaxArea();
        commandHeader.getNode().setMaxHeight(80.0);
        StackPane pHeader1 = new StackPane(new VirtualizedScrollPane<>(commandHeader.getNode()));
        pHeader1.getStyleClass().add(0, "scroll-pane");        
        StackPane pHeader2 = new StackPane(pHeader1);
        pHeader2.setPadding(new Insets(5, 5, 0, 5));  
        recordscontainer.setTop(pHeader2);
        
        commandField = new SytntaxArea();
        StackPane pField1 = new StackPane(new VirtualizedScrollPane<>(commandField.getNode()));
        pField1.getStyleClass().add(0, "scroll-pane");
        StackPane pField2 = new StackPane(pField1);
        pField2.setPadding(new Insets(5));  
        recordscontainer.setCenter(pField2);
        
        commandOutput = new SytntaxArea();
        commandOutput.getNode().setEditable(false);
        outputcontainer.setCenter(new VirtualizedScrollPane<>(commandOutput.getNode()));
        
       
        Record r = new RecordMap(
                new Entry("__ENTITY", "USERNAME"),
                new Entry("ID.KEY", VariantString.NULL),
                new Entry("NAME", "guest"),
                new Entry("DISPLAYNAME", VariantString.NULL));
        try {
            commandField.getNode().replaceText(RecordsSerializer.write(r));
            commandField.getNode().position(0, 0);            
        } catch (IOException ex) {
            Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
        }

//        scrollOutput.vvalueProperty().bind(commandOutput.heightProperty());
    }

    @FXML
    void onLogin(ActionEvent event) {
        beginTask();
        login("admin", "admin")
                .whenComplete(runLater(this::endTask))                
                .thenApply(runLater(this::printLoginResult))
                .exceptionally(runLater(this::printException));        
    }

    @FXML
    void onLogout(ActionEvent event) {
        MessageUtils.showInfo(MessageUtils.getRoot(root), "title", "message");
    }

    @FXML
    void onQuery(ActionEvent event) {
        beginTask();
        query(commandHeader.getNode().getText(), commandField.getNode().getText())
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printQueryResult))
                .exceptionally(runLater(this::printException));
    }

    @FXML
    void onFind(ActionEvent event) {
        find(commandHeader.getNode().getText(), commandField.getNode().getText())
                .thenApply(runLater(this::printFindResult))
                .exceptionally(runLater(this::printException));
    }

    @FXML
    void onExecute(ActionEvent event) {
        execute(commandHeader.getNode().getText(), commandField.getNode().getText())
                .thenApply(runLater(this::printExecuteResult))
                .exceptionally(runLater(this::printException));
    }

    //////////////////////////
    // Print operations
    //////////////////////////
    
    private void beginTask() {
        running ++;
        tasks.setText(String.format("running %s", running));
    }
    
    private <T, U> void endTask(T t, U u) {
        running --;
        if (running <= 0) {
            tasks.setText("");
        } else {
           tasks.setText(String.format("running %s", running));
        }
    }
    
    private void printLoginResult(String authorization) {
        try {        
            Record header = new RecordMap(new Entry("Authorization", authorization));  
            commandHeader.getNode().replaceText(RecordsSerializer.write(header));
            commandHeader.getNode().selectRange(0, 0); 
            commandOutput.getNode().appendText("Success. Login request.\n");
            commandOutput.getNode().requestFollowCaret();
        } catch (IOException ex) {
            Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
            printException(ex);
        }        
    }
    
    private void printFindResult(Record result) {
        try {
            if (result == null) {
                commandOutput.getNode().appendText("Fail. Find request. No record returned.\n");
                commandOutput.getNode().requestFollowCaret();
            } else {
                commandOutput.getNode().appendText("Success. Find request. Record returned.\n");
                commandOutput.getNode().appendText(RecordsSerializer.write(result) + "\n");
                commandOutput.getNode().requestFollowCaret();
            }
        } catch (IOException ex) {
            Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
            printException(ex);
        }
    }

    private void printQueryResult(List<Record> result) {
        try {
            commandOutput.getNode().appendText("Success. Query request. Rows returned: " + Integer.toString(result.size()) + ".\n");
            commandOutput.getNode().appendText(RecordsSerializer.writeList(result) + "\n");
            commandOutput.getNode().requestFollowCaret();
        } catch (IOException ex) {
            Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
            printException(ex);
        }
    }
    
    private void printExecuteResult(Void v) {
        commandOutput.getNode().appendText("Success. Execute request.\n");
        commandOutput.getNode().requestFollowCaret();
    }

    private void printException(Throwable ex) {
        commandOutput.getNode().appendText("Fail. " + ex.getMessage().replaceAll("\n", " ") + "\n");
        commandOutput.getNode().requestFollowCaret();
    }

    //////////////////////////
    // Asynchronous operations
    //////////////////////////
    //
    private CompletableFuture<String> login(String user, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
            }
            try {
                return ReducerLogin.login(app.getQueryLink(), user, password);
            } catch (DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }
    
    private CompletableFuture<List<Record>> query(String headerText, String filterText) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
            }
            try {
                return app.getQueryLink().query(readHeader(headerText), readFilter(filterText));
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<Record> find(String headerText, String filterText) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
            }
            try {
                return app.getQueryLink().find(readHeader(headerText), readFilter(filterText));
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<Void> execute(String headerText, String listText) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
            }
            try {
                app.getDataLink().execute(readHeader(headerText), readList(listText));
                return null;
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }    

    private Record readHeader(String headerText) throws IOException {
        if (headerText.trim().isEmpty()) {
            return Record.EMPTY;
        } else {
            return RecordsSerializer.read(headerText);
        }
    }

    private Record readFilter(String filterText) throws IOException {
        return RecordsSerializer.read(filterText);
    }

    private List<Record> readList(String listText) throws IOException {
        return RecordsSerializer.readList(listText);
    }

    private <T> Function<T, ? extends Void> runLater(Consumer<T> consumer) {
        return (T t) -> {
            Platform.runLater(() -> {
                consumer.accept(t);
            });
            return null;
        };
    }

    private <T, U> BiConsumer<T, U> runLater(BiConsumer<T,U> consumer) {
        return (T t, U u) -> {
            Platform.runLater(() -> {
                consumer.accept(t, u);
            });
        };
    }
}
