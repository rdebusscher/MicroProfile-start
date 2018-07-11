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
package be.atbash.ee.jessie.core.validation;

import be.atbash.ee.jessie.core.exception.JessieConfigurationException;
import be.atbash.ee.jessie.core.model.JessieModel;
import be.atbash.ee.jessie.core.templates.TemplateModelLoader;
import be.atbash.ee.jessie.spi.JessieAddon;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

/**
 *
 */
@ApplicationScoped
public class ModelValidation {

    private ValidatorFactory validatorFactory;

    @Inject
    private TemplateModelLoader templateModelLoader;

    @PostConstruct
    public void init() {
        validatorFactory = Validation.buildDefaultValidatorFactory();

    }

    public void validate(Object objectToValidate) {
        // toDO What was the idea here??
        // This is the first phase of validation
        Set<ConstraintViolation<Object>> violations = validatorFactory.getValidator().validate(objectToValidate);

        if (!violations.isEmpty()) {
            throw new ModelValidationException(violations);
        }
    }

    public void validate(JessieModel model) {
        if (!templateModelLoader.isValidTemplate(model.getTemplate())) {
            String message = String.format("JES-102- specified template '%s' is not found", model.getTemplate());
            throw new JessieConfigurationException(message);
        }
        // TODO More validations
    }

    public void validateByAddons(JessieModel model) {
        List<JessieAddon> addons = model.getParameter(JessieModel.Parameter.ADDONS);
        for (JessieAddon addon : addons) {
            addon.validate(model);
        }
    }
}
