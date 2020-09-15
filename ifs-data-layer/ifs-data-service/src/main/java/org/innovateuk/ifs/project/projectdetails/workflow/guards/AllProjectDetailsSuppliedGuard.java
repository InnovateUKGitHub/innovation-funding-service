package org.innovateuk.ifs.project.projectdetails.workflow.guards;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectDetailsEvent;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

/**
 * This asserts that all mandatory Project Details have been included prior to allowing them to be submitted.
 */
@Component
public class AllProjectDetailsSuppliedGuard implements Guard<ProjectDetailsState, ProjectDetailsEvent> {

    @Override
    public boolean evaluate(StateContext<ProjectDetailsState, ProjectDetailsEvent> context) {

        Project project = (Project) context.getMessageHeader("target");

        return validateIsReadyForSubmission(project);
    }

    private boolean validateIsReadyForSubmission(final Project project) {
        return project.getAddress() != null &&
                projectLocationComplete(project) &&
                project.getTargetStartDate() != null;
    }

    private boolean projectLocationComplete(Project project) {
        boolean locationPerPartnerRequired = project.getApplication().getCompetition().isLocationPerPartner();
        return !locationPerPartnerRequired
                || project.getPartnerOrganisations()
                .stream()
                .noneMatch(partnerOrganisation -> {
                    if (partnerOrganisation.getOrganisation().isInternational()) {
                        return StringUtils.isBlank(partnerOrganisation.getInternationalLocation());
                    }
                    return StringUtils.isBlank(partnerOrganisation.getPostcode());
                });
    }
}
