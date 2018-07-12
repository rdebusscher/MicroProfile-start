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
package be.atbash.ee.jessie.core;

import be.atbash.ee.jessie.core.model.JessieModel;
import be.atbash.ee.jessie.core.model.TechnologyStack;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@ApplicationScoped
public class TemplateVariableProvider {

    public Map<String, String> determineVariables(JessieModel model) {
        Map<String, String> result = new HashMap<>();

        result.put("java_package", model.getMaven().getGroupId() + '.' + model.getMaven().getArtifactId());
        result.put("maven_artifactid", model.getMaven().getArtifactId());

        String artifactId = model.getMaven().getArtifactId().replaceAll("\\.", "");
        result.put("artifact", StringUtils.capitalize(artifactId));

        if (model.getTechnologyStack() == TechnologyStack.MP) {
            result.put("mp_version", model.getSpecification().getMicroProfileVersion().getCode());
        }
        return result;

    }
}
