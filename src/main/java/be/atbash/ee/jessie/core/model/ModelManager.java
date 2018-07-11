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

import be.atbash.ee.jessie.core.AlternativesProvider;
import be.atbash.ee.jessie.core.TemplateVariableProvider;
import be.atbash.ee.jessie.core.addon.AddonManager;
import be.atbash.ee.jessie.core.templates.TemplateModelValues;
import be.atbash.ee.jessie.core.validation.ModelValidation;
import be.atbash.ee.jessie.spi.JessieAddon;
import be.atbash.ee.jessie.spi.JessieAlternativesProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@ApplicationScoped
public class ModelManager {

    @Inject
    private JessieModelInitializer modelInitializer;

    @Inject
    private ModelValidation modelValidation;

    @Inject
    private TemplateModelValues templateModelValues;

    @Inject
    private AlternativesProvider alternativesProvider;

    @Inject
    private TemplateVariableProvider templateVariableProvider;

    @Inject
    private AddonManager addonManager;

    /**
     * @param model
     * @param localExecution Is the generation run on local machine and thus need real directories.
     */
    public void prepareModel(JessieModel model, boolean localExecution) {
        modelInitializer.defineTechnologyStack(model);
        modelInitializer.defineDefaults(model, localExecution);

        modelValidation.validate(model);

        templateModelValues.applyTemplateValues(model);

        List<JessieAddon> allAddons = determineAddons(model);
        model.addParameter(JessieModel.Parameter.ADDONS, allAddons);

        setAddonOptions(allAddons, model.getOptions());

        modelValidation.validateByAddons(model);

        Set<String> alternatives = determineAlternatives(model, allAddons);
        model.addParameter(JessieModel.Parameter.ALTERNATIVES, alternatives);

        Map<String, String> variables = templateVariableProvider.determineVariables(model);
        model.addVariables(variables);
    }

    private Set<String> determineAlternatives(JessieModel model, List<JessieAddon> allAddons) {
        Set<String> alternatives = alternativesProvider.determineAlternatives(model);

        for (JessieAddon addon : allAddons) {
            alternatives.addAll(addon.alternativesNames(model));
        }

        List<JessieAlternativesProvider> alternativeProviders = addonManager.getAlternativeProviders();
        for (JessieAlternativesProvider alternativeProvider : alternativeProviders) {
            alternatives.addAll(alternativeProvider.alternativesNames(model));
        }
        return alternatives;
    }

    private List<JessieAddon> determineAddons(JessieModel model) {
        List<String> addonList = model.getAddons();
        List<JessieAddon> allAddons = getAddons(addonList);

        addDependentAddons(allAddons);

        orderAddons(allAddons);

        addAddonDefaultOptions(model, allAddons);

        allAddons.removeIf(addon -> isAddonDisabled(addon.addonName(), model.getOptions()));
        // Keep the addons for the expanded config writeout
        model.setAddons(
                allAddons.stream()
                        .map(JessieAddon::addonName)
                        .collect(Collectors.toList()));

        return allAddons;
    }

    private void addAddonDefaultOptions(JessieModel model, List<JessieAddon> addons) {
        for (JessieAddon addon : addons) {
            Map<String, String> additionalOptions = addon.getConditionalConfiguration(model, addons);
            for (Map.Entry<String, String> optionEntry : additionalOptions.entrySet()) {
                addDefaultOption(model.getOptions(), optionEntry.getKey(), optionEntry.getValue());
            }

        }
    }

    private void addDefaultOption(Map<String, OptionValue> options, String key, String value) {
        OptionValue optionValue;
        if (!options.containsKey(key)) {
            optionValue = new OptionValue(value);
        } else {

            optionValue = options.get(key);
            optionValue.addValue(value);
        }
        options.put(key, optionValue);
    }

    private void orderAddons(List<JessieAddon> allAddons) {
        allAddons.sort(Comparator.comparing(JessieAddon::priority));
    }

    private void addDependentAddons(List<JessieAddon> allAddons) {

        Set<String> dependents = allAddons.stream()
                .map(JessieAddon::getDependentAddons)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<String> addons = allAddons.stream().map(JessieAddon::addonName).collect(Collectors.toList());

        dependents.removeAll(addons);

        if (!dependents.isEmpty()) {
            allAddons.addAll(getAddons(dependents));
            addDependentAddons(allAddons);
        }

    }

    private List<JessieAddon> getAddons(Collection<String> addonList) {
        List<JessieAddon> allAddons = new ArrayList<>();
        for (String name : addonList) {
            if (name != null) {
                List<JessieAddon> addons = addonManager.getAddons(name);
                if (addons.isEmpty()) {
                    // FIXME Log warning
                }
                allAddons.addAll(addons);
            } else {
                // FIXME Log warning
            }
        }
        return allAddons;
    }

    private void setAddonOptions(List<JessieAddon> allAddons, Map<String, OptionValue> options) {

        for (JessieAddon addon : allAddons) {
            Map<String, OptionValue> addonOptions = new HashMap<>();
            int beginIndex = addon.addonName().length() + 1; // +1 for the .
            for (Map.Entry<String, OptionValue> optionEntry : options.entrySet()) {

                if (optionEntry.getKey().startsWith(addon.addonName() + '.')) {
                    addonOptions.put(optionEntry.getKey().substring(beginIndex), optionEntry.getValue());
                }
            }

            addon.addonOptions(addonOptions);
        }

    }

    private boolean isAddonDisabled(String addonName, Map<String, OptionValue> options) {
        boolean result = false;
        String optionName = addonName + ".disable";
        if (options.containsKey(optionName)) {
            Boolean addonDisabled = Boolean.valueOf(options.get(optionName).getSingleValue());
            if (addonDisabled) {
                result = true;
            }
        }
        return result;
    }

}
