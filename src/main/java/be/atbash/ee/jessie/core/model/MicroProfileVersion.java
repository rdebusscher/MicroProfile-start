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

import java.util.HashSet;
import java.util.Set;

public enum MicroProfileVersion {

    NONE(null, ""), MP12("1.2", "MP 1.2"), MP13("1.3", "MP 1.3");

    private String code;
    private String label;
    private Set<String> alternatives;

    MicroProfileVersion(String code, String label) {
        this.code = code;
        this.label = label;
        alternatives = new HashSet<>();

    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public Set<String> getAlternatives() {
        return alternatives;
    }

    public static MicroProfileVersion valueFor(String code) {
        MicroProfileVersion result = null;
        for (MicroProfileVersion microProfileVersion : MicroProfileVersion.values()) {
            if (microProfileVersion.code != null && microProfileVersion.code.equals(code)) {
                result = microProfileVersion;
            }
        }
        return result;
    }
}
