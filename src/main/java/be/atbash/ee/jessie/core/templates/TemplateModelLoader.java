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
package be.atbash.ee.jessie.core.templates;

import be.atbash.ee.jessie.core.exception.JessieUnexpectedException;
import be.atbash.ee.jessie.core.exception.TechnicalException;
import be.atbash.ee.jessie.core.file.ModelReader;
import be.atbash.ee.jessie.core.file.YAMLReader;
import be.atbash.ee.jessie.core.model.JessieModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@ApplicationScoped
public class TemplateModelLoader {

    @Inject
    private ModelReader modelReader;

    @Inject
    private YAMLReader yamlReader;

    private List<String> templates = new ArrayList<>();

    @PostConstruct
    public void init() {

        URL templatesRegistryFile = this.getClass().getClassLoader().getResource("templates/templates.yaml");
        if (templatesRegistryFile == null) {
            throw new JessieUnexpectedException("JES001- templates.yaml file not found");
        }

        InputStream resource = this.getClass().getClassLoader().getResourceAsStream("templates/templates.yaml");
        List<String> templateFiles = yamlReader.readYAML(resource, List.class);
        // FIXME mechanism to define custom templates.
        try {
            resource.close();
        } catch (IOException e) {
            throw new TechnicalException(e);
        }

        if (templateFiles == null || templateFiles.isEmpty()) {
            throw new JessieUnexpectedException("JES002- the yaml files for the templates aren't found");
        }
        for (String templateFile : templateFiles) {
            templates.add(templateFile.substring(0, templateFile.length() - 5));
        }

    }

    public JessieModel loadTemplateValues(String templateName) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("templates/" + templateName + ".yaml");
        return modelReader.readModel(stream, templateName);
    }

    public boolean isValidTemplate(String templateName) {
        return templates.contains(templateName);
    }
}
