/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 *
 * @author adrian
 */
public class SytntaxArea {

    private static final String[] KEYWORDS = new String[]{
        "null", "NULL", "true", "TRUE", "false", "FALSE",
        "INT",
        "LONG",
        "STRING",
        "DOUBLE",
        "DECIMAL",
        "BOOLEAN",
        "INSTANT",
        "LOCALDATETIME",
        "LOCALDATE",
        "LOCALTIME",
        "BYTES",
        "OBJECT",
        "VOID"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String SEMICOLON_PATTERN = "\\:";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "#[^\n]*";
    private static final String FAILMESSAGE_PATTERN = "(^|\n)Fail. [^\n]*";
    private static final String SUCCESSMESSAGE_PATTERN = "(^|\n)Success. [^\n]*";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            + "|(?<FAILMESSAGE>" + FAILMESSAGE_PATTERN + ")"
            + "|(?<SUCCESSMESSAGE>" + SUCCESSMESSAGE_PATTERN + ")"
    );

    private final CodeArea codeArea;

    public SytntaxArea() {

        codeArea = new CodeArea();
//        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.richChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
        codeArea.getStylesheets().add(SytntaxArea.class.getResource("/com/adr/dataclient/styles/recordshighlight.css").toExternalForm());
        codeArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//        codeArea.setWrapText(true);
//        codeArea.setEditable(false);
//        codeArea.replaceText(0, 0, sampleCode);          
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        ForkJoinPool.commonPool().execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass
                    = matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("BRACE") != null ? "brace"
                    : matcher.group("SEMICOLON") != null ? "semicolon"
                    : matcher.group("STRING") != null ? "string"
                    : matcher.group("COMMENT") != null ? "comment"
                    : matcher.group("FAILMESSAGE") != null ? "failmessage"
                    : matcher.group("SUCCESSMESSAGE") != null ? "successmessage"
                    : null;
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public CodeArea getNode() {
        return codeArea;
    }
}
