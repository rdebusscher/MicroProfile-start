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
package be.atbash.ee.jessie.view;

import be.atbash.ee.jessie.ZipFileCreator;
import be.atbash.ee.jessie.addon.microprofile.servers.model.MicroprofileSpec;
import be.atbash.ee.jessie.addon.microprofile.servers.model.SupportedServer;
import be.atbash.ee.jessie.core.artifacts.Creator;
import be.atbash.ee.jessie.core.model.*;
import be.atbash.util.StringUtils;
import org.apache.deltaspike.core.api.scope.ViewAccessScoped;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ViewAccessScoped
@Named
public class JessieDataBean implements Serializable {

    @Inject
    private ModelManager modelManager;

    @Inject
    private Creator creator;

    @Inject
    private ZipFileCreator zipFileCreator;

    private String activeScreen;
    private JessieMaven mavenData;
    private String javaSEVersion = "1.8";
    ;
    private String mpVersion;
    private String supportedServer;

    private List<SelectItem> supportedServerItems;
    private List<String> selectedSpecs = new ArrayList<>();
    private List<SelectItem> specs;
    private String selectedSpecDescription;

    private boolean hasErrors;

    @PostConstruct
    public void init() {
        mavenData = new JessieMaven();
    }

    public void activePage(String activeScreen) {
        this.activeScreen = activeScreen;
        if ("build".equals(activeScreen)) {
            validateMainValues();
        }
        if ("options".equals(activeScreen)) {
            MicroProfileVersion version = MicroProfileVersion.valueFor(mpVersion);

            defineSupportedServerItems(version);
            selectedSpecs.clear();
            defineExampleSpecs(version);
        }
    }

    private void defineExampleSpecs(MicroProfileVersion version) {
        specs = new ArrayList<>();

        for (MicroprofileSpec microprofileSpec : MicroprofileSpec.values()) {
            if (microprofileSpec.getMpVersions().contains(version)) {
                specs.add(new SelectItem(microprofileSpec.getCode(), microprofileSpec.getLabel()));
                selectedSpecs.add(microprofileSpec.getCode());
            }
        }
    }

    private void defineSupportedServerItems(MicroProfileVersion version) {

        supportedServerItems = new ArrayList<>();
        for (SupportedServer supportedServer : SupportedServer.values()) {
            if (supportedServer.getMpVersions().contains(version)) {
                supportedServerItems.add(new SelectItem(supportedServer.getName(), supportedServer.getName()));
            }
        }
        randomizeSupportedServers();
    }

    private void randomizeSupportedServers() {
        Random rnd = new Random();
        Map<Integer, SelectItem> data = supportedServerItems
                .stream().collect(Collectors.toMap(s -> rnd.nextInt(500),
                        Function.identity()));

        supportedServerItems = new ArrayList<>(data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new))
                .values());

    }

    public String selectedMenuItemStyleClass(String menuItem) {
        return activeScreen.equals(menuItem) ? "activePage" : "";
    }

    public void validateMainValues() {
        hasErrors = false;
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (mavenData.getGroupId() == null || mavenData.getGroupId().trim().isEmpty()) {
            addErrorMessage(facesContext, "groupId is required");
        }

        if (mavenData.getArtifactId() == null || mavenData.getArtifactId().trim().isEmpty()) {
            addErrorMessage(facesContext, "artifactId is required");
        }

        if (javaSEVersion == null || javaSEVersion.trim().isEmpty()) {
            addErrorMessage(facesContext, "Java SE version is required");
        }

        if ("options".equals(activeScreen) && supportedServer.trim().isEmpty()) {
            addErrorMessage(facesContext, "Supported server selection is required");
        }
    }

    private void addErrorMessage(FacesContext facesContext, String message) {
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        hasErrors = true;
    }

    public void generateProject() {
        if (StringUtils.isEmpty(supportedServer)) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            addErrorMessage(facesContext, "Supported server selection is required");
            FacesContext.getCurrentInstance().getViewRoot().setViewId("/options.xhtml");
            FacesContext.getCurrentInstance().renderResponse();
            return;
        }

        JessieModel model = new JessieModel();
        model.setDirectory(mavenData.getArtifactId());
        JessieMaven mavenModel = new JessieMaven();
        mavenModel.setGroupId(mavenData.getGroupId());
        mavenModel.setArtifactId(mavenData.getArtifactId());
        model.setMaven(mavenModel);

        JessieSpecification specifications = new JessieSpecification();

        specifications.setJavaSEVersion(JavaSEVersion.valueFor(javaSEVersion));
        specifications.setModuleStructure(ModuleStructure.SINGLE);

        specifications.setMicroProfileVersion(MicroProfileVersion.valueFor(mpVersion));

        model.getOptions().put("mp.server", new OptionValue(supportedServer));
        model.getOptions().put("mp.specs", new OptionValue(selectedSpecs));


        model.setSpecification(specifications);

        modelManager.prepareModel(model, false);
        creator.createArtifacts(model);

        download(zipFileCreator.createArchive());

    }

    private void download(byte[] archive) {
        String fileName = mavenData.getArtifactId() + ".zip";
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset();
        ec.setResponseContentType("application/zip");
        ec.setResponseContentLength(archive.length);
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try {
            OutputStream outputStream = ec.getResponseOutputStream();

            outputStream.write(archive);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace(); // FIXME
        }

        fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
    }

    public String getActiveScreen() {
        return activeScreen;
    }

    public JessieMaven getMavenData() {
        return mavenData;
    }

    public String getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(String javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    public String getMpVersion() {
        return mpVersion;
    }

    public void setMpVersion(String mpVersion) {
        this.mpVersion = mpVersion;
    }

    public List<SelectItem> getSupportedServerItems() {
        return supportedServerItems;
    }

    public List<SelectItem> getSpecs() {
        return specs;
    }

    public List<String> getSelectedSpecs() {
        return selectedSpecs;
    }

    public void setSelectedSpecs(List<String> selectedSpecs) {
        this.selectedSpecs = selectedSpecs;

        selectedSpecDescription = selectedSpecs.stream()
                .map(s -> MicroprofileSpec.valueFor(s).getLabel())
                .collect(Collectors.joining(", "));

    }

    public String getSelectedSpecDescription() {
        return selectedSpecDescription;
    }

    public String getSupportedServer() {
        return supportedServer;
    }

    public void setSupportedServer(String supportedServer) {
        this.supportedServer = supportedServer;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }
}
