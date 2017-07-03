package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;

/**
 * Holder of model attributes for the applications shown in the 'Manage applications' page
 */
public class ManageAssessorsRowViewModel {
    private final long id;
    private final String name;
    private final String skillAreas;
    private final long total;
    private final long assigned;
    private final long accepted;
    private final long submitted;

    public ManageAssessorsRowViewModel(AssessorCountSummaryResource assessorCountSummaryResource) {
        this.id = assessorCountSummaryResource.getId();
        this.name = assessorCountSummaryResource.getName();
        this.skillAreas = assessorCountSummaryResource.getSkillAreas();
        this.total = assessorCountSummaryResource.getTotalAssigned();
        this.assigned = assessorCountSummaryResource.getAssigned();
        this.accepted = assessorCountSummaryResource.getAccepted();
        this.submitted = assessorCountSummaryResource.getSubmitted();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public long getTotal() {
        return total;
    }

    public long getAssigned() {
        return assigned;
    }

    public long getAccepted() {
        return accepted;
    }

    public long getSubmitted() {
        return submitted;
    }
}