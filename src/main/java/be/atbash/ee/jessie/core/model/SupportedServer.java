/*
 * Copyright 2017-2018 Rudy De Busscher (https://www.atbash.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.ee.jessie.core.model;

public enum SupportedServer {
    WILDFLY_SWARM("wildfly-swarm"), LIBERTY("liberty"), KUMULUZEE("kumuluzEE"), PAYARA_MICRO("payara-micro");

    private String name;

    SupportedServer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SupportedServer valueFor(String data) {
        SupportedServer result = null;
        for (SupportedServer supportedServer : SupportedServer.values()) {
            if (supportedServer.name.equals(data)) {
                result = supportedServer;
            }
        }
        return result;
    }
}
