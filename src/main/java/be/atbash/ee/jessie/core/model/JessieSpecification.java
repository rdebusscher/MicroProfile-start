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

import be.atbash.ee.jessie.core.model.deserializer.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 *
 */
public class JessieSpecification {

    @JsonProperty(value = "javaEE")
    @JsonDeserialize(using = JavaEEVersionDeserializer.class)
    private JavaEEVersion javaEEVersion;

    @JsonProperty(value = "MP")
    @JsonDeserialize(using = MicroProfileVersionDeserializer.class)
    private MicroProfileVersion microProfileVersion;

    @JsonProperty(value = "javaSE")
    @JsonDeserialize(using = JavaSEVersionDeserializer.class)
    private JavaSEVersion javaSEVersion;

    @JsonProperty(value = "structure")
    @JsonDeserialize(using = ModuleDeserializer.class)
    private ModuleStructure moduleStructure;

    @JsonDeserialize(using = ViewTypeDeserializer.class)
    private List<ViewType> views;

    public JavaEEVersion getJavaEEVersion() {
        return javaEEVersion;
    }

    public void setJavaEEVersion(JavaEEVersion javaEEVersion) {
        this.javaEEVersion = javaEEVersion;
    }

    public MicroProfileVersion getMicroProfileVersion() {
        return microProfileVersion;
    }

    public void setMicroProfileVersion(MicroProfileVersion microProfileVersion) {
        this.microProfileVersion = microProfileVersion;
    }

    public JavaSEVersion getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(JavaSEVersion javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    public ModuleStructure getModuleStructure() {
        return moduleStructure;
    }

    public void setModuleStructure(ModuleStructure moduleStructure) {
        this.moduleStructure = moduleStructure;
    }

    public List<ViewType> getViews() {
        return views;
    }

    public void setViews(List<ViewType> views) {
        this.views = views;
    }

}
