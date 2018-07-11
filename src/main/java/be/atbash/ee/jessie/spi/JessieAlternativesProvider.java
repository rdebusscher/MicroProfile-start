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
package be.atbash.ee.jessie.spi;

import be.atbash.ee.jessie.core.model.JessieModel;

import java.util.Set;

/**
 *
 */
public interface JessieAlternativesProvider {

    /**
     * Returns the alternative names for file template resolution.
     * If no alternatives, must return empty Set.
     *
     * @return
     */
    Set<String> alternativesNames(JessieModel model);
}
