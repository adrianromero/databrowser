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
public abstract class SyntaxArea {

    public static final String[] KEYWORDS = new String[]{
        "null", "NULL", "true", "TRUE", "false", "FALSE",
        "INT", "LONG", "STRING", "DOUBLE", "DECIMAL", "BOOLEAN", "INSTANT", "LOCALDATETIME", "LOCALDATE", "LOCALTIME", "BYTES", "OBJECT", "VOID"
    };
    public static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    public static final String BRACE_PATTERN = "\\(|\\)";
    public static final String SEMICOLON_PATTERN = "\\:";
    public static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    public static final String COMMENT_PATTERN = "#[^\n]*";
    public static final String FAILMESSAGE_PATTERN = "(^|\n)Fail. [^\n]*\n[^\n]*";
    public static final String SUCCESSMESSAGE_PATTERN = "(^|\n)Success. [^\n]*";

    private final CodeArea codeArea;
    private Token[] tokens;
    private Pattern pattern;

    public SyntaxArea() {

        pattern = null;
        codeArea = new CodeArea();
//        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(100))
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
        codeArea.getStylesheets().add(SyntaxArea.class.getResource("/com/adr/dataclient/styles/recordshighlight.css").toExternalForm());
        codeArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
//        codeArea.setWrapText(true);
//        codeArea.setEditable(false);
//        codeArea.replaceText(sampleCode);          
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
        String text = codeArea.getText();
        if (text != null && !text.isEmpty()) {
         codeArea.setStyleSpans(0, highlighting);
        }
    }

    protected abstract Token[] getTokens();

    public CodeArea getNode() {
        return codeArea;
    }

    public static class Token {

        private final String name;
        private final String regex;

        public Token(String name, String regex) {
            this.name = name;
            this.regex = regex;
        }

        public String getName() {
            return name;
        }

        public String getRegex() {
            return regex;
        }
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        generatePattern();

        Matcher matcher = pattern.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass = getStyleClass(matcher);
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private String getStyleClass(Matcher matcher) {
        for (Token t : tokens) {
            if (matcher.group(t.getName()) != null) {
                return t.getName();
            }
        }
        throw new RuntimeException("Group name not found in tokens.");
    }

    private void generatePattern() {
        if (tokens == null) {
            tokens = getTokens();
            StringBuilder patternBuilder = new StringBuilder();
            for (Token t : tokens) {
                if (patternBuilder.length() > 0) {
                    patternBuilder.append('|');
                }
                patternBuilder.append("(?<");
                patternBuilder.append(t.getName());
                patternBuilder.append(">");
                patternBuilder.append(t.getRegex());
                patternBuilder.append(")");
            }
            pattern = Pattern.compile(patternBuilder.toString());
        }
    }
}
