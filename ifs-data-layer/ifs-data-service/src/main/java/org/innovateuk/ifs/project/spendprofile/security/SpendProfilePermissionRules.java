package org.innovateuk.ifs.project.spendprofile.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * Permissions for access to Spend Profile and its status
 */
@PermissionRules
@Component
public class SpendProfilePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "VIEW_SPEND_PROFILE_STATUS", description = "Internal admin team (comp admin and project finance) users can get the approved status of a Spend Profile for any Project")
    public boolean internalAdminTeamCanViewCompetitionStatus(ProjectResource project, UserResource user){
        return isInternalAdmin(user);
    }

    @PermissionRule(value = "VIEW_SPEND_PROFILE_STATUS", description = "Support users can get the approved status of a Spend Profile for any Project")
    public boolean supportCanViewCompetitionStatus(ProjectResource project, UserResource user){
        return isSupport(user);
    }

    @PermissionRule(value = "VIEW_SPEND_PROFILE_STATUS", description = "Innovation lead users can get the approved status of a Spend Profile for any Project")
    public boolean assignedInnovationLeadCanViewSPStatus(ProjectResource project, UserResource user){
        Application application = applicationRepository.findById(project.getApplication()).get();
        return userIsInnovationLeadOnCompetition(application.getCompetition().getId(), user.getId());
    }

    @PermissionRule(value = "VIEW_SPEND_PROFILE_STATUS", description = "Stakeholders can get the approved status of a Spend Profile for any Project")
    public boolean assignedStakeholderCanViewSPStatus(ProjectResource project, UserResource user){
        Application application = applicationRepository.findById(project.getApplication()).get();
        return userIsStakeholderInCompetition(application.getCompetition().getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Partners can view their own Spend Profile data")
    public boolean partnersCanViewTheirOwnSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Project Finance Users can view their own Spend Profile data")
    public boolean projectFinanceUserCanViewAnySpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Lead partner view Spend Profile data")
    public boolean leadPartnerCanViewAnySpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isLeadPartner(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Monitoring officer can view Spend Profile data for the projects they are assigned to")
    public boolean monitoringOfficerCanViewProjectsSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isMonitoringOfficer(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE_CSV",
            description = "Partners can view their own Spend Profile data")
    public boolean partnersCanViewTheirOwnSpendProfileCsv(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE_CSV",
            description = "All internal admin users can view Spend Profile data of any applicant")
    public boolean internalAdminUsersCanSeeSpendProfileCsv(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isInternalAdmin(user);
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE_CSV",
            description = "Support users can view Spend Profile data of any applicant")
    public boolean supportUsersCanSeeSpendProfileCsv(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isSupport(user);
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE_CSV",
            description = "Innovation lead users can view Spend Profile data for project on competition assigned to them")
    public boolean innovationLeadUsersCanSeeSpendProfileCsv(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        Project project = projectRepository.findById(projectOrganisationCompositeId.getProjectId()).get();
        return userIsInnovationLeadOnCompetition(project.getApplication().getCompetition().getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE_CSV",
            description = "Stakeholders can view Spend Profile data for project on competition assigned to them")
    public boolean stakeholdersCanSeeSpendProfileCsv(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        Project project = projectRepository.findById(projectOrganisationCompositeId.getProjectId()).get();
        return userIsStakeholderInCompetition(project.getApplication().getCompetition().getId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE_CSV",
            description = "Lead partner can view Spend Profile data")
    public boolean leadPartnerCanViewAnySpendProfileCsv(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isLeadPartner(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(
            value = "EDIT_SPEND_PROFILE",
            description = "Partners can edit their own Spend Profile data")
    public boolean partnersCanEditTheirOwnSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId()) &&
                isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(value = "MARK_SPEND_PROFILE_COMPLETE", description = "Any partner belonging to organisation can mark its spend profile as complete")
    public boolean partnersCanMarkSpendProfileAsComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId()) &&
                isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(value = "MARK_SPEND_PROFILE_INCOMPLETE", description = "Any lead partner can mark partners spend profiles as incomplete")
    public boolean leadPartnerCanMarkSpendProfileIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isLeadPartner(projectOrganisationCompositeId.getProjectId(), user.getId()) &&
                isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(value = "COMPLETE_SPEND_PROFILE_REVIEW", description = "Only a Project Manager can complete the project's spend profiles review")
    public boolean projectManagerCanCompleteSpendProfile(ProjectCompositeId projectCompositeId, UserResource user) {
        return isProjectManager(projectCompositeId.id(), user.getId()) &&
                isProjectActive(projectCompositeId.id());
    }
}
