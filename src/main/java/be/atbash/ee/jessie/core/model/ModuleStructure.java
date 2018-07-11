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
public enum ModuleStructure implements ComboBoxItem {

    NONE(null, ""), SINGLE("single", "Single"), MULTI("multi", "Multi");

    private String code;
    private String label;

    ModuleStructure(String code, String label) {
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

    public static ModuleStructure valueFor(String code) {
        ModuleStructure result = null;
        String value = null;
        if (code != null) {
            value = code.toLowerCase();
        }
        for (ModuleStructure moduleStructure : ModuleStructure.values()) {
            if (moduleStructure.code != null && moduleStructure.code.equals(value)) {
                result = moduleStructure;
            }
        }
        return result;
    }

}
