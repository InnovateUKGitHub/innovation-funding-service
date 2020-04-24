package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

/**
 * ApplicationFinancePermissionRules are applying rules for seeing / updating the application
 */
@Component
@PermissionRules
public class ApplicationFinancePermissionRules extends BasePermissionRules {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @PermissionRule(value = "READ", description = "The consortium can see the application finances of their own organisation")
    public boolean consortiumCanSeeTheApplicationFinancesForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplication(applicationFinanceResource, user);
    }

    @PermissionRule(value = "READ", description = "The projectUsers can see the application finances of their own organisation")
    public boolean projectUsersCanSeeTheApplicationFinancesForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAProjectUserForApplication(applicationFinanceResource, user);
    }

    @PermissionRule(value = "READ", description = "An assessor can see the application finances for organisations in the applications they assess")
    public boolean assessorCanSeeTheApplicationFinanceForOrganisationsInApplicationsTheyAssess(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAssessor(applicationFinanceResource.getApplication(), user) || isInterviewAssessor(applicationFinanceResource.getApplication(), user);
    }

    @PermissionRule(value = "READ", description = "An internal user can see application finances for organisations")
    public boolean internalUserCanSeeApplicationFinancesForOrganisations(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ", description = "Monitoring officers can see application finances for organisations")
    public boolean monitoringOfficersCanSeeApplicationFinancesForOrganisations(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return monitoringOfficerCanViewApplication(applicationFinanceResource.getApplication(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Stakeholders can see application finances for organisations on applications they are assigned to")
    public boolean stakeholdersCanSeeApplicationFinancesForOrganisations(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        Application application = applicationRepository.findById(applicationFinanceResource.getApplication()).get();
        return userIsStakeholderInCompetition(application.getCompetition().getId(), user.getId());
    }

    @PermissionRule(value = "ADD_COST", description = "The consortium can add a cost to the application finances of their own organisation or if lead applicant")
    public boolean consortiumCanAddACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationOrIsLeadApplicant(applicationFinanceResource, user);
    }

    @PermissionRule(value = "ADD_COST", description = "Internal users can add a cost to the application finances")
    public boolean internalUserCanAddACostToApplicationFinance(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "ADD_COST", description = "Stakeholders can add a cost to the application finances they are assigned to")
    public boolean stakeholdersCanAddACostToApplicationFinance(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        Application application = applicationRepository.findById(applicationFinanceResource.getApplication()).get();
        return userIsStakeholderInCompetition(application.getCompetition().getId(), user.getId());
    }

    @PermissionRule(value = "ADD_COST", description = "An assessor can add a cost to the application finances")
    public boolean assessorCanAddACostToApplicationFinance(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAssessor(applicationFinanceResource.getApplication(), user) && hasDetailedView(applicationFinanceResource.getApplication());
    }

    @PermissionRule(value = "UPDATE_COST", description = "The consortium can update a cost to the application finances of their own organisation or if lead applicant")
    public boolean consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationOrIsLeadApplicant(applicationFinanceResource, user);
    }

    @PermissionRule(value = "READ_FILE_ENTRY", description = "The consortium can get file entry resource for finance section of a collaborator")
    public boolean consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplication(applicationFinanceResource.getApplication(), user);
    }

    @PermissionRule(value = "READ_FILE_ENTRY", description = "An internal user can get file entry resource for finance section of a collaborator")
    public boolean internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_FILE_ENTRY", description = "An internal user can get file entry resource for finance section of a collaborator")
    public boolean assessorUserCanGetFileEntryResourceForFinanceIdOfACollaborator(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAssessor(applicationFinanceResource.getApplication(), user);
    }

    @PermissionRule(value = "CREATE_FILE_ENTRY", description = "A consortium member can create a file entry for the finance section for their organisation")
    public boolean consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationAndOrganisation(applicationFinanceResource, user);
    }

    @PermissionRule(value = "UPDATE_FILE_ENTRY", description = "A consortium member can update a file entry for the finance section for their organisation")
    public boolean consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationAndOrganisation(applicationFinanceResource, user);
    }

    @PermissionRule(value = "DELETE_FILE_ENTRY", description = "A consortium member can delete a file entry for the finance section for their organisation")
    public boolean consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationAndOrganisation(applicationFinanceResource, user);
    }

    private boolean isAConsortiumMemberOnApplicationAndOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        final boolean isLeadApplicant = checkProcessRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), COLLABORATOR, processRoleRepository);
        return isLeadApplicant || isCollaborator;
    }

    private boolean isAConsortiumMemberOnApplication(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        final boolean isLeadApplicant = checkProcessRole(user, applicationFinanceResource.getApplication(), LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationFinanceResource.getApplication(), COLLABORATOR, processRoleRepository);

        return isLeadApplicant || isCollaborator;
    }

    private boolean isAProjectUserForApplication(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        Optional<Project> project = projectRepository.findByApplicationId(applicationFinanceResource.getApplication());

        if (project.isPresent()) {

            return project.get().getProjectUsers().stream()
                    .map(ProjectUser::getUser)
                    .map(User::getId)
                    .filter(id -> id.equals(user.getId()))
                    .findAny()
                    .isPresent();
        }

        return false;
    }

    private boolean isAConsortiumMemberOnApplication(final Long applicationId, final UserResource user) {
        final boolean isLeadApplicant = checkProcessRole(user, applicationId, LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationId, COLLABORATOR, processRoleRepository);

        return isLeadApplicant || isCollaborator;
    }

    private boolean isAConsortiumMemberOnApplicationOrIsLeadApplicant(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        final boolean isLeadApplicant = checkProcessRole(user, applicationFinanceResource.getApplication(), LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), COLLABORATOR, processRoleRepository);

        return isLeadApplicant || isCollaborator;
    }

    private boolean hasDetailedView(long applicationId) {
        Optional<Application> application = applicationRepository.findById(applicationId);
        if (application.isPresent()) {
            Competition competition = competitionRepository.findById(application.get().getCompetition().getId()).get();
            return competition.getAssessorFinanceView().equals(AssessorFinanceView.DETAILED);
        }
        return false;
    }
}
