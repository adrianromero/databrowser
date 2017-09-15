/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adr.dataclient;

/**
 *
 * @author adrian
 */
public class OutputArea extends SyntaxArea {
    @Override
    protected final Token[] getTokens() {
        return new Token[] {
            new Token("keyword", KEYWORD_PATTERN),
            new Token("brace", BRACE_PATTERN),
            new Token("semicolon", SEMICOLON_PATTERN),
            new Token("string", STRING_PATTERN),
            new Token("comment", COMMENT_PATTERN),
            new Token("failmessage", FAILMESSAGE_PATTERN),
            new Token("successmessage", SUCCESSMESSAGE_PATTERN)
        };  
    }
}
