package org.innovateuk.ifs.management.cofunders.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;

import java.util.List;
import java.util.Map;

public class ReviewCofunderViewModel {

    private final Map<CofunderState, List<CofunderAssignmentResource>> assignments;
    private final long applicationId;
    private final String applicationName;
    private final long competitionId;

    public ReviewCofunderViewModel(Map<CofunderState, List<CofunderAssignmentResource>> assignments, ApplicationResource applicationResource) {
        this.assignments = assignments;
        this.applicationId = applicationResource.getId();
        this.applicationName = applicationResource.getName();
        this.competitionId = applicationResource.getCompetition();
    }

    public Map<CofunderState, List<CofunderAssignmentResource>> getAssignments() {
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
        return this.assignments.containsKey(CofunderState.ACCEPTED);
    }

    public boolean isPending() {
        return this.assignments.containsKey(CofunderState.CREATED);
    }

    public boolean isDeclined() {
        return this.assignments.containsKey(CofunderState.REJECTED);
    }

    public int getAcceptedCount() {
        return isAccepted() ? this.assignments.get(CofunderState.ACCEPTED).size() : 0;
    }

    public int getPendingCount() {
        return isPending() ? this.assignments.get(CofunderState.CREATED).size() : 0;
    }

    public int getDeclinedCount() {
        return isDeclined() ? this.assignments.get(CofunderState.REJECTED).size() : 0;
    }
}

