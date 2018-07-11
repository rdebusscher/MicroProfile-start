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
package be.atbash.ee.jessie.core.model.deserializer;

import be.atbash.ee.jessie.core.model.MicroProfileVersion;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 *
 */
public class MicroProfileVersionDeserializer extends JsonDeserializer<MicroProfileVersion> {
    @Override
    public MicroProfileVersion deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String propertyValue = jsonParser.getText();
        MicroProfileVersion result = MicroProfileVersion.valueFor(propertyValue);
        if (result == null) {
            throw new PropertyValueNotSupportedException("mp", propertyValue);
        }
        return result;
    }
}
