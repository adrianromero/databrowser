/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import java.io.IOException;

import com.adr.data.DataException;
import com.adr.data.DataLink;
import com.adr.data.QueryLink;
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
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.fxmisc.flowless.VirtualizedScrollPane;

/**
 *
 * @author adrian
 */
public class Command {

    @FXML
    private ResourceBundle resources;
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
    private ChoiceBox<AppDataLink> appdatalinks;
    @FXML
    private ChoiceBox<AppQueryLink> appquerylinks;

    @FXML
    Button actionExecute;
    @FXML
    Button actionQuery;
    @FXML
    Button actionFind;
    @FXML
    MenuButton menuSecurity;
    @FXML
    Button actionClear;

    private SyntaxArea commandHeader;
    private SyntaxArea commandField;
    private SyntaxArea commandOutput;

    private RotateTransition tasksrt;

    private final Application app;
    private int running = 0;

    private LoginDialog login = new LoginDialog();
    private AuthorizationDialog authorization = new AuthorizationDialog();

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

        Shape s = IconBuilder.create(FontAwesome.FA_CIRCLE_O_NOTCH).build();
        s.setCacheHint(CacheHint.ROTATE);
        tasksrt = new RotateTransition(Duration.millis(1000), s);
        tasksrt.setFromAngle(0);
        tasksrt.setToAngle(360);
        tasksrt.setCycleCount(Animation.INDEFINITE);
        tasksrt.setInterpolator(Interpolator.LINEAR);
        tasks.setGraphic(s);

        commandHeader = new RecordsArea();
        commandHeader.getNode().setMaxHeight(80.0);
        headerContainer.getChildren().add(new VirtualizedScrollPane<>(commandHeader.getNode()));

        commandField = new RecordsArea();
        recordContainer.getChildren().add(new VirtualizedScrollPane<>(commandField.getNode()));

        commandOutput = new OutputArea();
        commandOutput.getNode().setEditable(false);
        outputContainer.getChildren().add(new VirtualizedScrollPane<>(commandOutput.getNode()));

        appdatalinks.setItems(app.getDataLinks());
        // appdatalinks.getSelectionModel().selectFirst();
        actionExecute.disableProperty().bind(appdatalinks.valueProperty().isNull());

        appquerylinks.setItems(app.getQueryLinks());
        // appquerylinks.getSelectionModel().selectFirst();   
        actionQuery.disableProperty().bind(appquerylinks.valueProperty().isNull());
        actionFind.disableProperty().bind(appquerylinks.valueProperty().isNull());
        menuSecurity.disableProperty().bind(appquerylinks.valueProperty().isNull());

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

        login.showDialog(root, (String user, String password) -> {
            beginTask();
            login(appquerylinks.getValue().getLink(), user, password)
                    .whenComplete(runLater(this::endTask))
                    .thenApply(runLater(this::printLoginResult))
                    .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.login"), t)));

        });
    }

    @FXML
    void onCurrentUser(ActionEvent event) {
        beginTask();
        current(appquerylinks.getValue().getLink(), commandHeader.getNode().getText())
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printCurrentResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.current"), t)));
    }

    @FXML
    void onHasAuthorization(ActionEvent event) {
        authorization.showDialog(root, (String resource) -> {         
            beginTask();
            hasAuthorization(appquerylinks.getValue().getLink(), commandHeader.getNode().getText(), resource)
                    .whenComplete(runLater(this::endTask))
                    .thenApply(runLater(this::printHasAuthorizationResult))
                    .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.hasauthorization"), t)));
        });
    }

    @FXML
    void onQuery(ActionEvent event) {
        beginTask();
        query(appquerylinks.getValue().getLink(), commandHeader.getNode().getText(), commandField.getNode().getText())
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printQueryResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.query"), t)));
    }

    @FXML
    void onFind(ActionEvent event) {
        beginTask();
        find(appquerylinks.getValue().getLink(), commandHeader.getNode().getText(), commandField.getNode().getText())
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printFindResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.find"), t)));
    }

    @FXML
    void onExecute(ActionEvent event) {
        beginTask();
        execute(appdatalinks.getValue().getLink(), commandHeader.getNode().getText(), commandField.getNode().getText())
                .whenComplete(runLater(this::endTask))
                .thenApply(runLater(this::printExecuteResult))
                .exceptionally(runLater((Throwable t) -> printException(resources.getString("request.execute"), t)));
    }

    //////////////////////////
    // Print operations
    //////////////////////////
    private void beginTask() {
        running++;

        if (running > 1) {
            tasks.setText(String.format(resources.getString("label.tasks"), running));
        } else if (running > 0) {
            tasks.setVisible(true);
            tasksrt.play();
        }
    }

    private <T, U> void endTask(T t, U u) {
        running--;

        if (running > 1) {
            tasks.setText(String.format(resources.getString("label.tasks"), running));
        } else if (running > 0) {
            tasks.setText("");
        } else if (running == 0) {
            tasksrt.stop();
            tasks.setVisible(false);
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
            if (asyncresult.getResult().size() > 0) {
                commandOutput.getNode().appendText(RecordsSerializer.writeList(asyncresult.getResult()) + "\n");
            }
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
    private CompletableFuture<AsyncResult<String>> login(QueryLink link, String user, String password) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new AsyncResult<String>(ReducerLogin.login(link, user, password), e);
            } catch (DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<Record>> current(QueryLink link, String headerText) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new AsyncResult<Record>(ReducerLogin.current(link, readHeader(headerText)), e);
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<HasAuthorizationResult>> hasAuthorization(QueryLink link, String headerText, String resource) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                boolean hasAuthorization = ReducerLogin.hasAuthorization(link, readHeader(headerText), resource);
                return new AsyncResult<HasAuthorizationResult>(new HasAuthorizationResult(resource, hasAuthorization), e);
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<List<Record>>> query(QueryLink link, String headerText, String filterText) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException exi) {

                }

                return new AsyncResult<List<Record>>(link.query(readHeader(headerText), readFilter(filterText)), e);
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<Record>> find(QueryLink link, String headerText, String filterText) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new AsyncResult<Record>(link.find(readHeader(headerText), readFilter(filterText)), e);
            } catch (IOException | DataException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    private CompletableFuture<AsyncResult<Void>> execute(DataLink link, String headerText, String listText) {
        Elapsed e = new Elapsed();
        return CompletableFuture.supplyAsync(() -> {
            try {
                link.execute(readHeader(headerText), readList(listText));
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
