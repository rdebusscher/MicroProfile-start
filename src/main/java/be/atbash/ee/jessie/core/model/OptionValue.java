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

import be.atbash.ee.jessie.core.model.serializer.OptionValueSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize(using = OptionValueSerializer.class)
public class OptionValue {

    private List<String> values;

    public OptionValue() {
        values = new ArrayList<>();
    }

    public OptionValue(String value) {
        this();
        values.add(value);
    }

    public OptionValue(List<String> values) {
        this.values = values;
    }

    public void addValue(String value) {
        values.add(value);
    }

    public String getSingleValue() {
        if (values.size() > 1) {
            throw new OptionValueSingleValueException("OptionalValue has multiple values and thus we cannot return the single value for it.");
        }
        return values.get(0);
    }

    public List<String> getValues() {
        // FIXME Immutable
        return values;
    }

    public boolean isMultipleValues() {
        return values.size() > 1;
    }
}
