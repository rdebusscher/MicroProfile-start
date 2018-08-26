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
package be.atbash.ee.jessie;

import be.atbash.ee.jessie.core.model.BeansXMLMode;
import be.atbash.ee.jessie.core.model.JavaEEVersion;
import be.atbash.ee.jessie.core.model.JavaSEVersion;
import be.atbash.ee.jessie.core.model.MicroProfileVersion;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Named
public class DataBean {

    private List<SelectItem> javaSEItems;
    private List<SelectItem> mpItems;
    private List<SelectItem> beansxmlItems;

    @PostConstruct
    public void init() {
        defineJavaSEItems();

        defineMPVersions();
        defineBeanxmlItems();
    }

    private void defineBeanxmlItems() {
        beansxmlItems = new ArrayList<>();
        for (BeansXMLMode beansXMLMode : BeansXMLMode.values()) {
            beansxmlItems.add(new SelectItem(beansXMLMode.getMode(), beansXMLMode.getMode()));
        }
    }

    private void defineMPVersions() {
        mpItems = new ArrayList<>();
        for (MicroProfileVersion microProfileVersion : MicroProfileVersion.values()) {
            mpItems.add(new SelectItem(microProfileVersion.getCode(), microProfileVersion.getLabel()));
        }
    }

    private void defineJavaSEItems() {
        javaSEItems = new ArrayList<>();
        for (JavaSEVersion javaSEVersion : JavaSEVersion.values()) {
            javaSEItems.add(new SelectItem(javaSEVersion.getCode(), javaSEVersion.getLabel()));
        }
    }

    public List<SelectItem> getJavaSEItems() {
        return javaSEItems;
    }

    public List<SelectItem> getMpItems() {
        return mpItems;
    }

    public List<SelectItem> getBeansxmlItems() {
        return beansxmlItems;
    }

}
