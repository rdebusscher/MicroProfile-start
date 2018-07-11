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

import be.atbash.ee.jessie.core.exception.TechnicalException;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;

/**
 *
 */
@ApplicationScoped
public class JessieModelInitializer {
    public void defineTechnologyStack(JessieModel model) {
        if (model.getSpecification() != null && model.getSpecification().getMicroProfileVersion() != null) {
            model.setTechnologyStack(TechnologyStack.MP);
        } else {
            model.setTechnologyStack(TechnologyStack.JAVA_EE);
        }
    }

    public void defineDefaults(JessieModel model, boolean localExecution) {
        checkDirectory(model, localExecution);

        defineTemplate(model);
    }

    private void checkDirectory(JessieModel model, boolean localExecution) {
        if (model.getDirectory() == null) {
            String modelFileName = model.getParameter(JessieModel.Parameter.FILENAME);
            model.setDirectory(getDirectoryFromModelFileName(modelFileName));
        }

        if (localExecution) {
            File file = new File(model.getDirectory());
            try {
                model.setDirectory(file.getCanonicalPath());
            } catch (IOException e) {
                throw new TechnicalException(e);
            }
        }
    }

    private String getDirectoryFromModelFileName(String modelFileName) {
        String result = modelFileName;
        int length = modelFileName.length();
        if (modelFileName.endsWith(".yaml") && length > 4) {
            result = "./" + modelFileName.substring(0, length - 5);
        }
        return result;
    }

    private void defineTemplate(JessieModel model) {
        // FIXME use Atbash String Utils
        if (model.getTemplate() == null) {

            switch (model.getTechnologyStack()) {

                case JAVA_EE:
                    model.setTemplate("default");
                    break;
                case MP:
                    model.setTemplate("defaultMP");
                    break;
                default:
                    throw new IllegalArgumentException(String.format("TechnologyStack unknown %s", model.getTechnologyStack()));
            }
        }
    }
}
