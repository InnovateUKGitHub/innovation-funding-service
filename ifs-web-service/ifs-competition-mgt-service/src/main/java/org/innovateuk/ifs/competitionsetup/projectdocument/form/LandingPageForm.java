package org.innovateuk.ifs.competitionsetup.projectdocument.form;

import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * Form to capture the details of the new project document
 */
public class LandingPageForm extends BaseBindingResultTarget {

    private Set<Long> enabledIds;

    public Set<Long> getEnabledIds() {
        return enabledIds;
    }

    public void setEnabledIds(Set<Long> enabledIds) {
        this.enabledIds = enabledIds;
    }

    public LandingPageForm() {
    }
}


