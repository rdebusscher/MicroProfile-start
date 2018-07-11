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

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@ApplicationScoped
public class JSFCreator extends AbstractCreator {

    public void createJSFFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, String> variables = model.getVariables();

        String webInfDirectory = model.getDirectory() + "/" + MavenCreator.SRC_MAIN_WEBAPP + "/WEB-INF";
        directoryCreator.createDirectory(webInfDirectory);

        String webXMLContents = thymeleafEngine.processFile("web.xml", alternatives, variables);
        fileCreator.writeContents(webInfDirectory, "web.xml", webXMLContents);

        String webDirectory = model.getDirectory() + "/" + MavenCreator.SRC_MAIN_WEBAPP;

        String indexContents = thymeleafEngine.processFile("index.xhtml", alternatives, variables);
        fileCreator.writeContents(webDirectory, "index.xhtml", indexContents);

    }
}
