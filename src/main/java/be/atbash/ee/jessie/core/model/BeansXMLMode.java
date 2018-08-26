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

public enum BeansXMLMode {
    IMPLICIT("implicit"), ANNOTATED("annotated"), ALL("all");

    private String mode;

    BeansXMLMode(String mode) {

        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static BeansXMLMode getValue(String mode) {
        BeansXMLMode result = null;
        for (BeansXMLMode beansXMLMode : BeansXMLMode.values()) {
            if (beansXMLMode.getMode().equalsIgnoreCase(mode)) {
                result = beansXMLMode;
            }
        }
        return result;
    }

    public static class OptionName {
        public static String name = BeansXMLMode.class.getSimpleName();
    }
}
