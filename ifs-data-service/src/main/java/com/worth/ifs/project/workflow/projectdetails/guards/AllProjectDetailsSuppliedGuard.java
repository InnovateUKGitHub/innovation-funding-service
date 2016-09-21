package com.worth.ifs.project.workflow.projectdetails.guards;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.ProjectDetailsOutcomes;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.user.domain.Organisation;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.worth.ifs.util.CollectionFunctions.*;

/**
 * This asserts that all mandatory Project Details have been included prior to allowing them to be submitted.
 */
@Component
public class AllProjectDetailsSuppliedGuard implements Guard<ProjectDetailsState, ProjectDetailsOutcomes> {

    @Override
    public boolean evaluate(StateContext<ProjectDetailsState, ProjectDetailsOutcomes> context) {

        Project project = (Project) context.getMessageHeader("target");

        return validateIsReadyForSubmission(project);
    }

    private boolean validateIsReadyForSubmission(final Project project) {
        return project.getAddress() != null &&
                getExistingProjectManager(project).isPresent() &&
                project.getTargetStartDate() != null
                //&& allFinanceContactsSet(project)
                ;
    }

    // This function is unused, should I delete this. I am tempted to keep this - just in case
    private boolean allFinanceContactsSet(Project project) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        Set<Organisation> partnerOrganisations = new HashSet<>(simpleMap(projectUsers, ProjectUser::getOrganisation));
        List<ProjectUser> financeRoles = simpleFilter(projectUsers, ProjectUser::isFinanceContact);
        Set<Organisation> financeRoleOrgs = new HashSet<>(simpleMap(financeRoles, ProjectUser::getOrganisation));
        return financeRoleOrgs.containsAll(partnerOrganisations);
    }

    private Optional<ProjectUser> getExistingProjectManager(Project project) {
        List<ProjectUser> projectUsers = project.getProjectUsers();
        List<ProjectUser> projectManagers = simpleFilter(projectUsers, pu -> pu.getRole().isProjectManager());
        return getOnlyElementOrEmpty(projectManagers);
    }
}
