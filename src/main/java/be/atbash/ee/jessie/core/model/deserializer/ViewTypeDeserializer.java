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

import be.atbash.ee.jessie.core.model.ViewType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class ViewTypeDeserializer extends JsonDeserializer<List<ViewType>> {
    @Override
    public List<ViewType> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        Set<ViewType> result = new HashSet<>();

        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

        JsonNode node = mapper.readTree(jsonParser);

        for (JsonNode viewNode : node) {
            String propertyValue = viewNode.asText();
            ViewType viewType = ViewType.valueFor(propertyValue);
            if (viewType == null) {
                throw new PropertyValueNotSupportedException("view", propertyValue);
            }
            result.add(viewType);
        }
        return new ArrayList<>(result);
    }
}
