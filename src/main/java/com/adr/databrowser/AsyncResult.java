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
package com.adr.databrowser;

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
