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
public enum ViewType implements ComboBoxItem {

    JSF("jsf", "JSF"), REST("rest", "JAX-RS");

    private String code;
    private String label;

    ViewType(String code, String label) {
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

    public static ViewType valueFor(String code) {
        ViewType result = null;
        for (ViewType viewType : ViewType.values()) {
            if (viewType.code.equalsIgnoreCase(code)) {
                result = viewType;
            }
        }
        return result;
    }

}
