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

/**
 *
 */
public enum JavaSEVersion implements ComboBoxItem {
    NONE(null, ""), SE7("1.7", "Java 7"), SE8("1.8", "Java 8");

    private String code;
    private String label;

    JavaSEVersion(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public static JavaSEVersion valueFor(String code) {
        JavaSEVersion result = null;
        for (JavaSEVersion javaSEVersion : JavaSEVersion.values()) {
            if (javaSEVersion.code != null && javaSEVersion.code.equals(code)) {
                result = javaSEVersion;
            }
        }
        return result;
    }
}
