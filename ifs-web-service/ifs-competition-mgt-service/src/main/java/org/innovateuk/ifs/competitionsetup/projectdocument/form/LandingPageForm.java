package org.innovateuk.ifs.competitionsetup.projectdocument.form;

import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.List;

/**
 * Form to capture the details of the new project document
 */
public class LandingPageForm extends BaseBindingResultTarget {

    private List<ProjectDocumentResource> projectDocumentResources;

    public LandingPageForm(List<ProjectDocumentResource> projectDocumentResources) {
        this.projectDocumentResources = projectDocumentResources;
    }

    public LandingPageForm() {
    }

    public List<ProjectDocumentResource> getProjectDocumentResources() {
        return projectDocumentResources;
    }

    public void setProjectDocumentResources(List<ProjectDocumentResource> projectDocumentResources) {
        this.projectDocumentResources = projectDocumentResources;
    }
}


