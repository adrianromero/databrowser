//     Data Browser is a JavaFX application for Data
//     Copyright (C) 2019 Adrián Romero Corchado.
//
//     This file is part of Data Browser
//
//     Licensed under the Apache License, Version 2.0 (the "License");
//     you may not use this file except in compliance with the License.
//     You may obtain a copy of the License at
//
//         http://www.apache.org/licenses/LICENSE-2.0
//
//     Unless required by applicable law or agreed to in writing, software
//     distributed under the License is distributed on an "AS IS" BASIS,
//     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//     See the License for the specific language governing permissions and
//     limitations under the License.
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
