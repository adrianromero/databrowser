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
public class HasAuthorizationResult {
    private final String resource;
    private final boolean hasAuthorization;

    public HasAuthorizationResult(String resource, boolean hasAuthorization) {
        this.resource = resource;
        this.hasAuthorization = hasAuthorization;
    }

    public String getResource() {
        return resource;
    }

    public boolean hasAuthorization() {
        return hasAuthorization;
    }
}
