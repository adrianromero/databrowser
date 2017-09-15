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
public class AsyncResult<T> {
    private final T result;
    private final Elapsed elapsed;

    public AsyncResult(T result, Elapsed elapsed) {
        this.result = result;
        this.elapsed = elapsed;
    }

    public T getResult() {
        return result;
    }

    public Elapsed getElapsed() {
        return elapsed;
    }
    
}
