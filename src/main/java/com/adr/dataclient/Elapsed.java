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
public class Elapsed {
    private final long initial;
    public Elapsed() {
        initial = System.currentTimeMillis();
    }
    public long elapsed() {
        return System.currentTimeMillis() - initial;
    }
}
