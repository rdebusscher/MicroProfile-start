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
package be.atbash.ee.jessie.core.artifacts;

import be.atbash.ee.jessie.core.model.JessieModel;
import be.atbash.ee.jessie.core.model.TechnologyStack;
import be.atbash.ee.jessie.spi.JessieAddon;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 *
 */
@ApplicationScoped
public class Creator {

    @Inject
    private MavenCreator mavenCreator;

    @Inject
    private CDICreator cdiCreator;

    @Inject
    private JSFCreator jsfCreator;

    @Inject
    private JavaCreator javaCreator;

    public void createArtifacts(JessieModel model) {

        mavenCreator.createMavenFiles(model);

        if (model.getTechnologyStack() == TechnologyStack.JAVA_EE) {
            jsfCreator.createJSFFiles(model);
        }
        cdiCreator.createCDIFilesForWeb(model);

        javaCreator.createJavaFiles(model);

        List<JessieAddon> addons = model.getParameter(JessieModel.Parameter.ADDONS);
        for (JessieAddon addon : addons) {
            addon.createFiles(model);
        }
    }
}
