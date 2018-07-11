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
package be.atbash.ee.jessie.core.file;

import be.atbash.ee.jessie.core.exception.TechnicalException;
import be.atbash.ee.jessie.core.model.JessieModel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
@ApplicationScoped
public class ModelReader {

    @Inject
    private YAMLReader yamlReader;

    public JessieModel readModel(InputStream in, String modelName) {

        JessieModel result = yamlReader.readYAML(in, JessieModel.class);

        result.addParameter(JessieModel.Parameter.FILENAME, modelName);

        return result;
    }

    public JessieModel readModel(String fileName) {
        JessieModel result;

        File file = determineFileName(fileName);

        try {
            FileInputStream stream = new FileInputStream(file);
            result = yamlReader.readYAML(stream, JessieModel.class);

            result.addParameter(JessieModel.Parameter.FILENAME, file.getName());
            stream.close();
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
        return result;
    }

    private File determineFileName(String fileName) {
        File result = new File(fileName);
        if (!result.exists()) {
            // If file not exists, try with the .yaml extension.
            if (!fileName.endsWith(".yaml")) {
                result = new File(fileName + ".yaml");
            }
        }
        return result;
    }

}
