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

import be.atbash.ee.jessie.core.model.JessieModel;
import be.atbash.ee.jessie.core.model.JessieSpecification;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@ApplicationScoped
public class TemplateModelValues {

    @Inject
    private TemplateModelLoader templateModelLoader;

    public void applyTemplateValues(JessieModel model) {
        if (model.getSpecification() == null) {
            model.setSpecification(new JessieSpecification());
        }

        Set<String> templates = new HashSet<>();

        String templateName = model.getTemplate();

        // FIXME Use StringUtils
        while (templateName != null && !templates.contains(templateName)) {
            templates.add(templateName);

            JessieModel templateModel = templateModelLoader.loadTemplateValues(templateName);
            assignDefaults(model, templateModel);

            templateName = templateModel.getTemplate();
        }

    }

    private void assignDefaults(JessieModel model, JessieModel templateModel) {
        JessieSpecification modelSpecification = model.getSpecification();
        JessieSpecification templateSpecification = templateModel.getSpecification();

        if (modelSpecification.getModuleStructure() == null) {
            modelSpecification.setModuleStructure(templateSpecification.getModuleStructure());
        }
        if (modelSpecification.getJavaSEVersion() == null) {
            modelSpecification.setJavaSEVersion(templateSpecification.getJavaSEVersion());
        }
        if (modelSpecification.getJavaEEVersion() == null) {
            modelSpecification.setJavaEEVersion(templateSpecification.getJavaEEVersion());
        }
        if (modelSpecification.getViews() == null || modelSpecification.getViews().isEmpty()) {
            modelSpecification.setViews(templateSpecification.getViews());
        }

        model.getAddons().addAll(templateModel.getAddons());
        model.getOptions().putAll(templateModel.getOptions());

    }
}
