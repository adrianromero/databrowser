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
import com.adr.fonticon.FontAwesome;
import com.adr.fonticon.IconBuilder;
import com.adr.hellocommon.dialog.MessageUtils;
import com.adr.hellocommon.utils.FXMLUtil;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;

/**
 *
 * @author adrian
 */
public class Command {

    @FXML
    private BorderPane root;
    @FXML
    private StackPane headerContainer;
    @FXML
    private StackPane recordContainer;
    @FXML
    private StackPane outputContainer;
    @FXML
    private Label tasks;

    @FXML
    Button actionExecute;
    @FXML
    Button actionQuery;
    @FXML
    Button actionFind;
    @FXML
    Button actionClear;

    private SyntaxArea commandHeader;
    private SyntaxArea commandField;
    private SyntaxArea commandOutput;

    private final ResourceBundle resources = ResourceBundle.getBundle("com/adr/dataclient/fxml/command");
    private final Application app;
    private int running = 0;

    public Command(Application app) {
        this.app = app;
        FXMLUtil.load(this, "/com/adr/dataclient/fxml/command.fxml", "com/adr/dataclient/fxml/command");
    }

    public Parent getNode() {
        return root;
    }

    @FXML
    void initialize() {

        actionExecute.setGraphic(IconBuilder.create(FontAwesome.FA_CLOUD_UPLOAD).build());
        actionQuery.setGraphic(IconBuilder.create(FontAwesome.FA_CLOUD_DOWNLOAD).build());
        actionFind.setGraphic(IconBuilder.create(FontAwesome.FA_SEARCH).build());
        actionClear.setGraphic(IconBuilder.create(FontAwesome.FA_BAN).build());

        commandHeader = new RecordsArea();
        commandHeader.getNode().setMaxHeight(80.0);
        headerContainer.getChildren().add(new VirtualizedScrollPane<>(commandHeader.getNode()));

        commandField = new RecordsArea();
        recordContainer.getChildren().add(new VirtualizedScrollPane<>(commandField.getNode()));

        commandOutput = new OutputArea();
        commandOutput.getNode().setEditable(false);
        outputContainer.getChildren().add(new VirtualizedScrollPane<>(commandOutput.getNode()));

        Record r = new RecordMap(
                new Entry("__ENTITY", "USERNAME"),
                new Entry("ID.KEY", VariantString.NULL),
                new Entry("NAME", "guest"),
                new Entry("DISPLAYNAME", VariantString.NULL),
                new Entry("ROLE_ID", VariantString.NULL));
        try {
            commandField.getNode().replaceText(RecordsSerializer.write(r));
            commandField.getNode().position(0, 0);
        } catch (IOException ex) {
            Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void onClear(ActionEvent event) {
        commandOutput.getNode().clear();
    }

    @FXML
    void onLogin(ActionEvent event) {
        beginTask();
        login("admin", "admin")
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printLoginResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.login"), t)));
    }

    @FXML
    void onLogout(ActionEvent event) {
        commandHeader.getNode().replaceText("");
    }

    @FXML
    void onCurrentUser(ActionEvent event) {
        beginTask();
        current(commandHeader.getNode().getText())
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printCurrentResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.current"), t)));
    }

    @FXML
    void onHasAuthorization(ActionEvent event) {
        beginTask();
        hasAuthorization(commandHeader.getNode().getText(), "cachelo")
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printHasAuthorizationResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.hasauthorization"), t)));
    }

//    @FXML
//    void onLogout(ActionEvent event) {
//        MessageUtils.showInfo(MessageUtils.getRoot(root), "title", "message");
//    }
    @FXML
    void onQuery(ActionEvent event) {
        beginTask();
        query(commandHeader.getNode().getText(), commandField.getNode().getText())
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printQueryResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.query"), t)));
    }

    @FXML
    void onFind(ActionEvent event) {
        beginTask();
        find(commandHeader.getNode().getText(), commandField.getNode().getText())
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printFindResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.find"), t)));
    }

    @FXML
    void onExecute(ActionEvent event) {
        beginTask();
        execute(commandHeader.getNode().getText(), commandField.getNode().getText())
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printExecuteResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.execute"), t)));
    }

    //////////////////////////
    // Print operations
    //////////////////////////
    private void beginTask() {
        running++;
        tasks.setText(String.format("running %s", running));
    }

    private <T, U> void endTask(T t, U u) {
        running--;
        if (running <= 0) {
            tasks.setText("");
        } else {
            tasks.setText(String.format("running %s", running));
        }
    }

    private void printLoginResult(AsyncResult<String> asyncresult) {
        try {
            Record header = new RecordMap(new Entry("Authorization", asyncresult.getResult()));
            commandHeader.getNode().replaceText(RecordsSerializer.write(header));
            commandHeader.getNode().selectRange(0, 0);
            commandOutput.getNode().appendText(String.format(resources.getString("result.login"), asyncresult.getElapsed().elapsed()));
            commandOutput.getNode().requestFollowCaret();
        } catch (IOException ex) {
            Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
            printException(resources.getString("request.login"), ex);
        }
    }

    private void printFindResult(AsyncResult<Record> asyncresult) {
        try {
            if (asyncresult.getResult() == null) {
                commandOutput.getNode().appendText(String.format(resources.getString("result.findsuccess0"), asyncresult.getElapsed().elapsed()));
                commandOutput.getNode().requestFollowCaret();
            } else {
                commandOutput.getNode().appendText(String.format(resources.getString("result.findsuccess1"), asyncresult.getElapsed().elapsed()));
                commandOutput.getNode().appendText(RecordsSerializer.write(asyncresult.getResult()) + "\n");
                commandOutput.getNode().requestFollowCaret();
            }
        } catch (IOException ex) {
            Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
            printException(resources.getString("request.find"), ex);
        }
    }

    private void printCurrentResult(AsyncResult<Record> asyncresult) {
        try {
            if (asyncresult.getResult() == null) {
                commandOutput.getNode().appendText(String.format(resources.getString("result.currentanonymous"), asyncresult.getElapsed().elapsed()));
                commandOutput.getNode().requestFollowCaret();
            } else {
                commandOutput.getNode().appendText(String.format(resources.getString("result.current"), asyncresult.getElapsed().elapsed()));
                commandOutput.getNode().appendText(RecordsSerializer.write(asyncresult.getResult()) + "\n");
                commandOutput.getNode().requestFollowCaret();
            }
        } catch (IOException ex) {
            Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
            printException(resources.getString("request.current"), ex);
        }
    }

    private void printHasAuthorizationResult(AsyncResult<HasAuthorizationResult> asyncresult) {
        if (asyncresult.getResult().hasAuthorization()) {
            commandOutput.getNode().appendText(String.format(resources.getString("result.hasauthorizationyes"), asyncresult.getElapsed().elapsed(), asyncresult.getResult().getResource()));
            commandOutput.getNode().requestFollowCaret();
        } else {
            commandOutput.getNode().appendText(String.format(resources.getString("result.hasauthorizationno"), asyncresult.getElapsed().elapsed(), asyncresult.getResult().getResource()));
            commandOutput.getNode().requestFollowCaret();
        }
    }

    private void printQueryResult(AsyncResult<List<Record>> asyncresult) {
        try {
            commandOutput.getNode().appendText(String.format(resources.getString("result.query"), asyncresult.getElapsed().elapsed(), asyncresult.getResult().size()));
            commandOutput.getNode().appendText(RecordsSerializer.writeList(asyncresult.getResult()) + "\n");
            commandOutput.getNode().requestFollowCaret();
        } catch (IOException ex) {
            Logger.getLogger(Command.class.getName()).log(Level.SEVERE, null, ex);
            printException(resources.getString("request.query"), ex);
        }
    }

    private void printExecuteResult(AsyncResult<Void> asyncresult) {
        commandOutput.getNode().appendText(String.format(resources.getString("result.execute"), asyncresult.getElapsed().elapsed()));
        commandOutput.getNode().requestFollowCaret();
    }

    private void printException(String requestname, Throwable ex) {
        commandOutput.getNode().appendText(String.format(resources.getString("result.exception"), requestname, ex.getMessage().replaceAll("\n", " ")));
        commandOutput.getNode().requestFollowCaret();
    }

    //////////////////////////
    // Asynchronous operations
    //////////////////////////
    //
    private CompletableFuture<AsyncResult<String>> login(String user, String password) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new AsyncResult<String>(ReducerLogin.login(app.getQueryLink(), user, password), e);
            } catch (DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<Record>> current(String headerText) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new AsyncResult<Record>(ReducerLogin.current(app.getQueryLink(), readHeader(headerText)), e);
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<HasAuthorizationResult>> hasAuthorization(String headerText, String resource) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean hasAuthorization = ReducerLogin.hasAuthorization(app.getQueryLink(), readHeader(headerText), resource);
                return new AsyncResult<HasAuthorizationResult>(new HasAuthorizationResult(resource, hasAuthorization), e);
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<List<Record>>> query(String headerText, String filterText) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new AsyncResult<List<Record>>(app.getQueryLink().query(readHeader(headerText), readFilter(filterText)), e);
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<Record>> find(String headerText, String filterText) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new AsyncResult<Record>(app.getQueryLink().find(readHeader(headerText), readFilter(filterText)), e);
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<Void>> execute(String headerText, String listText) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                app.getDataLink().execute(readHeader(headerText), readList(listText));
                return new AsyncResult<Void>(null, e);
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

    private <T, U> BiConsumer<T, U> runLater(BiConsumer<T, U> consumer) {
        return (T t, U u) -> {
            Platform.runLater(() -> {
                consumer.accept(t, u);
            });
        };
    }
}
