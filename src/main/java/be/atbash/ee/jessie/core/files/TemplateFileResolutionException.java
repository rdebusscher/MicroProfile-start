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
package be.atbash.ee.jessie.core.files;

import be.atbash.ee.jessie.core.exception.JessieException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */

public class TemplateFileResolutionException extends JessieException {

    public TemplateFileResolutionException(String file, Set<String> alternatives) {
        super(createMessage(file, alternatives));
    }

    private static String createMessage(String file, Set<String> alternatives) {
        String alternativeInfo = alternatives.stream().collect(Collectors.joining(", "));
        return String.format("No Template file found for '%s' with alternatives '%s'", file, alternativeInfo);
    }
}
