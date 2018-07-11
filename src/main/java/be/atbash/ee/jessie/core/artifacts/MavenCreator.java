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

import be.atbash.ee.jessie.core.addon.AddonManager;
import be.atbash.ee.jessie.core.exception.TechnicalException;
import be.atbash.ee.jessie.core.model.JavaSEVersion;
import be.atbash.ee.jessie.core.model.JessieModel;
import be.atbash.ee.jessie.core.model.TechnologyStack;
import be.atbash.ee.jessie.spi.JessieAddon;
import be.atbash.ee.jessie.spi.JessieMavenAdapter;
import be.atbash.ee.jessie.spi.MavenHelper;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 *
 */
@ApplicationScoped
public class MavenCreator {

    public static final String SRC_MAIN_JAVA = "src/main/java";
    public static final String SRC_MAIN_RESOURCES = "src/main/resources";
    public static final String SRC_MAIN_WEBAPP = "src/main/webapp";

    public static final String SRC_TEST_JAVA = "src/test/java";
    public static final String SRC_TEST_RESOURCES = "src/test/resources";

    @Inject
    private DirectoryCreator directoryCreator;

    @Inject
    private FileCreator fileCreator;

    @Inject
    private AddonManager addonManager;

    @Inject
    private MavenHelper mavenHelper;

    public void createMavenFiles(JessieModel model) {
        Model pomFile = null;
        switch (model.getSpecification().getModuleStructure()) {

            case SINGLE:
                pomFile = createSingleModule(model);
                break;
            case MULTI:
                throw new IllegalArgumentException("Maven multi module needs to be supported");
                //break;
        }

        applyMavenAdapters(model, pomFile);

        writePOMFile(pomFile, model);

        createDefaultDirectories(model);
    }

    private void writePOMFile(Model pomFile, JessieModel model) {
        String content;
        MavenXpp3Writer pomWriter = new MavenXpp3Writer();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pomWriter.write(out, pomFile);
            out.close();
            content = new String(out.toByteArray());
        } catch (IOException e) {
            throw new TechnicalException(e);
        }

        fileCreator.writeContents(model.getDirectory(), "pom.xml", content);
    }

    private void applyMavenAdapters(JessieModel model, Model pomFile) {
        List<JessieAddon> allAddons = model.getParameter(JessieModel.Parameter.ADDONS);

        for (JessieAddon addon : allAddons) {
            addon.adaptMavenModel(pomFile, model);
        }

        for (JessieMavenAdapter mavenAdapter : addonManager.getMavenAdapters()) {
            mavenAdapter.adaptMavenModel(pomFile, model);
        }
    }

    private void createDefaultDirectories(JessieModel model) {
        String javaDirectory = model.getDirectory() + "/" + SRC_MAIN_JAVA;
        directoryCreator.createDirectory(javaDirectory);
        javaDirectory = model.getDirectory() + "/src/test/java";
        directoryCreator.createDirectory(javaDirectory);
        fileCreator.createEmptyFile(javaDirectory, ".gitkeep");

        String resourcesDirectory = model.getDirectory() + "/src/main/resources";
        directoryCreator.createDirectory(resourcesDirectory);
        fileCreator.createEmptyFile(resourcesDirectory, ".gitkeep");

        String webappDirectory = model.getDirectory() + "/" + SRC_MAIN_WEBAPP;
        directoryCreator.createDirectory(webappDirectory);
        fileCreator.createEmptyFile(webappDirectory, ".gitkeep");
    }

    private Model createSingleModule(JessieModel model) {

        Model pomFile = new Model();
        pomFile.setModelVersion("4.0.0");

        pomFile.setGroupId(model.getMaven().getGroupId());
        pomFile.setArtifactId(model.getMaven().getArtifactId());
        pomFile.setVersion("1.0-SNAPSHOT");

        pomFile.setPackaging("war");

        addDependencies(pomFile, model);

        addJavaSEVersionProperties(pomFile, model);

        if (model.getTechnologyStack() == TechnologyStack.MP) {
            pomFile.addProperty("failOnMissingWebXml", "false");
        }

        Build build = new Build();
        build.setFinalName(model.getMaven().getArtifactId());
        pomFile.setBuild(build);

        return pomFile;
    }

    private void addDependencies(Model pomFile, JessieModel model) {
        switch (model.getTechnologyStack()) {

            case JAVA_EE:
                addJavaEEDependency(pomFile, model);
                break;
            case MP:
                addJavaMPDependencies(pomFile, model);
                break;
            default:
                throw new IllegalArgumentException(String.format("TechnologyStack unknown %s", model.getTechnologyStack()));
        }

    }

    private void addJavaMPDependencies(Model pomFile, JessieModel model) {
        mavenHelper.addDependency(pomFile, "org.eclipse.microprofile", "microprofile",
                model.getSpecification().getMicroProfileVersion().getCode(), "provided", "pom");
    }

    private void addJavaEEDependency(Model pomFile, JessieModel model) {
        switch (model.getSpecification().getJavaEEVersion()) {

            case EE6:
                mavenHelper.addDependency(pomFile, "javax", "javaee-api", "6.0", "provided");
                break;
            case EE7:
                mavenHelper.addDependency(pomFile, "javax", "javaee-api", "7.0", "provided");
                break;
            case EE8:
                mavenHelper.addDependency(pomFile, "javax", "javaee-api", "8.0", "provided");
                break;
        }
    }

    private void addJavaSEVersionProperties(Model pomFile, JessieModel model) {

        JavaSEVersion seVersion = model.getSpecification().getJavaSEVersion();
        pomFile.addProperty("maven.compiler.source", seVersion.getCode());
        pomFile.addProperty("maven.compiler.target", seVersion.getCode());
    }

}
