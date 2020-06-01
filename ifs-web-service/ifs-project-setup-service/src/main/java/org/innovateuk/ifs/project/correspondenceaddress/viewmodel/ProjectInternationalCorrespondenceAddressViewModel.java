package org.innovateuk.ifs.project.correspondenceaddress.viewmodel;

import org.innovateuk.ifs.address.resource.Countries;
import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

public class ProjectInternationalCorrespondenceAddressViewModel implements BasicProjectDetailsViewModel {

    private final Long projectId;
    private String projectName;
    private boolean collaborativeProject;
    private List<String> countries = Countries.COUNTRIES;


    public ProjectInternationalCorrespondenceAddressViewModel(ProjectResource projectResource) {
        this.projectId = projectResource.getId();
        this.projectName = projectResource.getName();
        this.collaborativeProject = projectResource.isCollaborativeProject();

    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public List<String> getCountries() {
        return countries;
    }
}
