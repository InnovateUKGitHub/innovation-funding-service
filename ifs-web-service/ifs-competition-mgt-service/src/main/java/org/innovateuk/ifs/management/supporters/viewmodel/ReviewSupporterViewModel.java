package org.innovateuk.ifs.management.supporters.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;

import java.util.List;
import java.util.Map;

public class ReviewSupporterViewModel {

    private final Map<SupporterState, List<SupporterAssignmentResource>> assignments;
    private final long applicationId;
    private final String applicationName;
    private final long competitionId;

    public ReviewSupporterViewModel(Map<SupporterState, List<SupporterAssignmentResource>> assignments, ApplicationResource applicationResource) {
        this.assignments = assignments;
        this.applicationId = applicationResource.getId();
        this.applicationName = applicationResource.getName();
        this.competitionId = applicationResource.getCompetition();
    }

    public Map<SupporterState, List<SupporterAssignmentResource>> getAssignments() {
        return assignments;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    /*View model logic*/
    public boolean isAccepted() {
        return this.assignments.containsKey(SupporterState.ACCEPTED);
    }

    public boolean isPending() {
        return this.assignments.containsKey(SupporterState.CREATED);
    }

    public boolean isDeclined() {
        return this.assignments.containsKey(SupporterState.REJECTED);
    }

    public int getAcceptedCount() {
        return isAccepted() ? this.assignments.get(SupporterState.ACCEPTED).size() : 0;
    }

    public int getPendingCount() {
        return isPending() ? this.assignments.get(SupporterState.CREATED).size() : 0;
    }

    public int getDeclinedCount() {
        return isDeclined() ? this.assignments.get(SupporterState.REJECTED).size() : 0;
    }
}

