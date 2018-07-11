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
package be.atbash.ee.jessie.core.addon;

import be.atbash.ee.jessie.spi.JessieAddon;
import be.atbash.ee.jessie.spi.JessieAlternativesProvider;
import be.atbash.ee.jessie.spi.JessieMavenAdapter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
@ApplicationScoped
public class AddonManager {

    @Inject
    private Instance<JessieAddon> addons;

    @Inject
    private Instance<JessieAlternativesProvider> alternativeProviders;

    @Inject
    private Instance<JessieMavenAdapter> mavenAdapters;

    public List<JessieAddon> getAddons(String addonName) {
        List<JessieAddon> result = new ArrayList<>();

        for (JessieAddon addon : addons) {
            if (addonName.equalsIgnoreCase(addon.addonName())) {
                // There are not that many addons that a break (or convertion to while) improves performance.
                result.add(addon);
            }
        }

        return result;
    }

    public List<JessieAlternativesProvider> getAlternativeProviders() {

        Iterator<JessieAlternativesProvider> alternativesIterator = alternativeProviders.iterator();
        return getProviders(alternativesIterator);
    }

    public List<JessieMavenAdapter> getMavenAdapters() {

        Iterator<JessieMavenAdapter> mavenAdapterIterator = mavenAdapters.iterator();
        return getProviders(mavenAdapterIterator);
    }

    private <T> List<T> getProviders(Iterator<T> alternativesIterator) {
        List<T> result = new ArrayList<>();
        while (alternativesIterator.hasNext()) {
            T provider = alternativesIterator.next();

            //exclude the addons, they are already selected by the addon SPI feature.
            if (!JessieAddon.class.isAssignableFrom(provider.getClass())) {
                result.add(provider);
            }
        }
        return result;
    }
}
