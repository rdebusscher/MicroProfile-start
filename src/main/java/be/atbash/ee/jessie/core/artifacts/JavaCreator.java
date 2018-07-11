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
import be.atbash.ee.jessie.core.model.ViewType;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@ApplicationScoped
public class JavaCreator extends AbstractCreator {

    public void createJavaFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        if (model.getTechnologyStack() == TechnologyStack.JAVA_EE) {
            if (model.getSpecification().getViews().contains(ViewType.JSF)) {
                String rootJava = MavenCreator.SRC_MAIN_JAVA + "/" + directoryCreator.createPathForGroupAndArtifact(model.getMaven());
                String viewDirectory = model.getDirectory() + "/" + rootJava + "/view";
                directoryCreator.createDirectory(viewDirectory);

                String javaFile = thymeleafEngine.processFile("HelloBean.java", alternatives, variables);
                fileCreator.writeContents(viewDirectory, "HelloBean.java", javaFile);

            }
        }

        if (model.getTechnologyStack() == TechnologyStack.MP) {
            String rootJava = MavenCreator.SRC_MAIN_JAVA + "/" + directoryCreator.createPathForGroupAndArtifact(model.getMaven());
            String viewDirectory = model.getDirectory() + "/" + rootJava;
            directoryCreator.createDirectory(viewDirectory);

            String artifactId = variables.get("artifact");
            String javaFile = thymeleafEngine.processFile("RestApplication.java", alternatives, variables);
            fileCreator.writeContents(viewDirectory, artifactId + "RestApplication.java", javaFile);

            javaFile = thymeleafEngine.processFile("HelloController.java", alternatives, variables);
            fileCreator.writeContents(viewDirectory, "HelloController.java", javaFile);

        }
    }
}
