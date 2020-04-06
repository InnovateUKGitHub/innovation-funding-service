package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.SYSTEM_REGISTRATION_USER;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

@PermissionRules
@Component
public class ApplicationPermissionRules extends BasePermissionRules {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MonitoringOfficerRepository projectMonitoringOfficerRepository;

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "The consortium can see the participation percentage for their applications")
    public boolean consortiumCanSeeTheResearchParticipantPercentage(final ApplicationResource applicationResource, UserResource user) {
        return isMemberOfProjectTeam(applicationResource.getId(), user);
    }

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "The assessor can see the participation percentage for applications they assess")
    public boolean assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(final ApplicationResource applicationResource, UserResource user) {
        return isAssessorForApplication(applicationResource, user);
    }

    private boolean isAssessorForApplication(ApplicationResource applicationResource, UserResource user) {
        return isAssessor(applicationResource.getId(), user) || isPanelAssessor(applicationResource.getId(), user) || isInterviewAssessor(applicationResource.getId(), user);
    }

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "The internal users can see the participation percentage for applications they assess")
    public boolean internalUsersCanSeeTheResearchParticipantPercentageInApplications(final ApplicationResource applicationResource, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "Stakeholders can see the participation percentage for applications they are assigned to")
    public boolean stakeholdersCanSeeTheResearchParticipantPercentageInApplications(final ApplicationResource applicationResource, UserResource user) {
        return userIsStakeholderInCompetition(applicationResource.getCompetition(), user.getId());
    }

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "Monitoring officers can see the participation percentage for applications they are assigned to")
    public boolean monitoringOfficersCanSeeTheResearchParticipantPercentageInApplications(final ApplicationResource applicationResource, UserResource user) {
        return monitoringOfficerCanViewApplication(applicationResource.getId(), user.getId());
    }

    @PermissionRule(value = "READ_FINANCE_DETAILS",
            description = "The consortium can see the application finance details",
            additionalComments = "This rule secures ApplicationResource which can contain more information than this rule should allow. Consider a new cut down object based on ApplicationResource")
    public boolean leadApplicantCanSeeTheApplicationFinanceDetails(final ApplicationResource applicationResource, final UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user);
    }

    @PermissionRule(value = "READ_FINANCE_TOTALS",
            description = "The consortium can see the application finance totals",
            additionalComments = "This rule secures ApplicationResource which can contain more information than this rule should allow. Consider a new cut down object based on ApplicationResource")
    public boolean consortiumCanSeeTheApplicationFinanceTotals(final ApplicationResource applicationResource, final UserResource user) {
        return isMemberOfProjectTeam(applicationResource.getId(), user);
    }

    @PermissionRule(value = "READ_FINANCE_TOTALS",
            description = "The assessor can see the application finance totals in the applications they assess",
            additionalComments = "This rule secures ApplicationResource which can contain more information than this rule should allow. Consider a new cut down object based on ApplicationResource")
    public boolean assessorCanSeeTheApplicationFinancesTotals(final ApplicationResource applicationResource, final UserResource user) {
        return isAssessor(applicationResource.getId(), user) || isPanelAssessor(applicationResource.getId(), user) || isInterviewAssessor(applicationResource.getId(), user);
    }

    @PermissionRule(value = "READ_FINANCE_TOTALS",
            description = "Internal users can view the finance totals.",
            additionalComments = "This rule secures ApplicationResource which can contain more information than this rule should allow. Consider a new cut down object based on ApplicationResource")
    public boolean internalUserCanSeeApplicationFinancesTotals(final ApplicationResource applicationResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_FINANCE_TOTALS",
            description = "Monitoring officers can view the finance totals.")
    public boolean monitoringOfficersCanSeeApplicationFinancesTotals(final ApplicationResource applicationResource, final UserResource user) {
        return monitoringOfficerCanViewApplication(applicationResource.getId(), user.getId());
    }

    @PermissionRule(value = "READ_FINANCE_TOTALS",
            description = "Stakeholders can view the finance totals.",
            additionalComments = "This rule secures ApplicationResource which can contain more information than this rule should allow. Consider a new cut down object based on ApplicationResource")
    public boolean stakeholdersCanSeeApplicationFinancesTotals(final ApplicationResource applicationResource, final UserResource user) {
        return userIsStakeholderInCompetition(applicationResource.getCompetition(), user.getId());
    }

    @PermissionRule(value = "APPLICATION_SUBMITTED_NOTIFICATION", description = "A lead applicant can send the notification of a submitted application")
    public boolean aLeadApplicantCanSendApplicationSubmittedNotification(final ApplicationResource applicationResource, final UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user);
    }

    @PermissionRule(value = "READ", description = "Internal users (other than innovation lead) can see all application resources")
    public boolean internalUsersCanViewApplications(final ApplicationResource application, final UserResource user) {
        return !isInnovationLead(user) && isInternal(user);
    }

    @PermissionRule(value = "READ", description = "A user can see an application resource which they are connected to")
    public boolean usersConnectedToTheApplicationCanView(ApplicationResource application, UserResource user) {
        return userIsConnectedToApplicationResource(application, user);
    }

    @PermissionRule(value = "READ", description = "Innovation leads can see application resources for competitions assigned to them.")
    public boolean innovationLeadAssignedToCompetitionCanViewApplications(final ApplicationResource application, final UserResource user) {
        return application != null && application.getCompetition() != null && userIsInnovationLeadOnCompetition(application.getCompetition(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Stakeholders can see application resources for competitions assigned to them.")
    public boolean stakeholderAssignedToCompetitionCanViewApplications(final ApplicationResource application, final UserResource user) {
        return application != null && application.getCompetition() != null && userIsStakeholderInCompetition(application.getCompetition(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Monitoring officers can see application resources for projects assigned to them.")
    public boolean monitoringOfficerAssignedToProjectCanViewApplications(final ApplicationResource application, final UserResource user) {
        return application != null && application.getCompetition() != null && projectMonitoringOfficerRepository.existsByProjectApplicationIdAndUserId(application.getId(), user.getId());
    }

    @PermissionRule(value = "READ", description = "Project Partners can see applications that are linked to their Projects")
    public boolean projectPartnerCanViewApplicationsLinkedToTheirProjects(final ApplicationResource application, final UserResource user) {
        Project linkedProject = projectRepository.findOneByApplicationId(application.getId());
        if (linkedProject == null) {
            return false;
        }
        return isPartner(linkedProject.getId(), user.getId());
    }

    @PermissionRule(value = "UPDATE", description = "A user can update their own application if they are a lead applicant or collaborator of the application")
    public boolean applicantCanUpdateApplicationResource(ApplicationResource application, UserResource user) {
        Set<Role> allApplicantRoles = EnumSet.of(Role.LEADAPPLICANT, Role.COLLABORATOR);
        List<ProcessRole> applicantProcessRoles = processRoleRepository.findByUserIdAndRoleInAndApplicationId(user.getId(), allApplicantRoles, application.getId());
        return !applicantProcessRoles.isEmpty();
    }

    @PermissionRule(value = "READ_AVAILABLE_INNOVATION_AREAS", description = "A user can view the Innovation Areas that are available to their applications")
    public boolean usersConnectedToTheApplicationCanViewInnovationAreas(ApplicationResource applicationResource, final UserResource user) {
        return usersConnectedToTheApplicationCanView(applicationResource, user);
    }

    @PermissionRule(value = "UPDATE_INNOVATION_AREA", description = "A lead applicant can update their application's Innovation Area")
    public boolean leadApplicantCanUpdateApplicationResource(ApplicationResource applicationResource, UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user);
    }

    @PermissionRule(value = "UPDATE_RESEARCH_CATEGORY", description = "A lead applicant can update their application's Research Category")
    public boolean leadApplicantCanUpdateResearchCategory(ApplicationResource applicationResource, UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user);
    }

    @PermissionRule(value = "UPDATE_APPLICATION_STATE", description = "A lead applicant can update the state of their own application")
    public boolean leadApplicantCanUpdateApplicationState(final ApplicationResource applicationResource, final UserResource user) {
        return isLeadApplicant(applicationResource.getId(), user);
    }

    @PermissionRule(value = "UPDATE_APPLICATION_STATE", description = "A comp admin can update the state of an application")
    public boolean compAdminCanUpdateApplicationState(final ApplicationResource applicationResource, final UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(value = "UPDATE_APPLICATION_STATE", description = "A project finance user can update the state of an application")
    public boolean projectFinanceCanUpdateApplicationState(final ApplicationResource applicationResource, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "UPLOAD_ASSESSOR_FEEDBACK",
            description = "An Internal user can upload Assessor Feedback documentation for an Application whilst " +
                    "the Application's Competition is in Funders' Panel or Assessor Feedback state",
            particularBusinessState = "Application's Competition Status = 'Funders Panel' or 'Assessor Feedback'")
    public boolean internalUserCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(ApplicationResource application, UserResource user) {
        return isInternal(user) && application.isInEditableAssessorFeedbackCompetitionState();
    }

    @PermissionRule(
            value = "REMOVE_ASSESSOR_FEEDBACK",
            description = "A Comp Admin user can remove Assessor Feedback documentation so long as the Feedback has not yet been published",
            particularBusinessState = "Application's Competition Status != 'Project Setup' or beyond")
    public boolean compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(ApplicationResource application, UserResource user) {
        return isCompAdmin(user) && !application.isInPublishedAssessorFeedbackCompetitionState();
    }

    @PermissionRule(
            value = "REMOVE_ASSESSOR_FEEDBACK",
            description = "A Project Finance user can remove Assessor Feedback documentation so long as the Feedback has not yet been published",
            particularBusinessState = "Application's Competition Status != 'Project Setup' or beyond")
    public boolean projectFinanceUserCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(ApplicationResource application, UserResource user) {
        return isProjectFinanceUser(user) && !application.isInPublishedAssessorFeedbackCompetitionState();
    }

    @PermissionRule(
            value = "DOWNLOAD_ASSESSOR_FEEDBACK",
            description = "An Internal user can see and download Assessor Feedback at any time for any Application")
    public boolean internalUserCanSeeAndDownloadAllAssessorFeedbackAtAnyTime(ApplicationResource application, UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(
            value = "DOWNLOAD_ASSESSOR_FEEDBACK",
            description = "A member of the Application Team can see and download Assessor Feedback attached to their Application when it has been published",
            particularBusinessState = "Application's Competition Status = 'Project Setup' or beyond")
    public boolean applicationTeamCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(ApplicationResource application, UserResource user) {
        return application.isInPublishedAssessorFeedbackCompetitionState() && isMemberOfProjectTeam(application.getId(), user);
    }

    boolean userIsConnectedToApplicationResource(ApplicationResource application, UserResource user) {
        return processRoleRepository.existsByUserIdAndApplicationId(user.getId(), application.getId());
    }

    @PermissionRule(value = "CREATE",
            description = "Any logged in user with global roles or user with system registrar role can create an application but only for open competitions",
            particularBusinessState = "Competition is in Open state")
    public boolean userCanCreateNewApplication(CompetitionResource competition, UserResource user) {
        return competition.isOpen() && (user.hasRole(APPLICANT) || user.hasRole(SYSTEM_REGISTRATION_USER));
    }

    @PermissionRule(value = "MARK_AS_INELIGIBLE", description = "Application can be marked as ineligible by internal admin user and innovation lead only until ", particularBusinessState = "competition is in assessment state")
    public boolean markAsInelgibileAllowedBeforeAssesment(ApplicationResource application, UserResource user){
        Competition competition = competitionRepository.findById(application.getCompetition()).orElse(null);
        return (isInternalAdmin(user) || isInnovationLead(user)) && !isCompetitionBeyondAssessment(competition);
    }

    @PermissionRule(value = "CHECK_COLLABORATIVE_FUNDING_CRITERIA_MET", description = "The consortium can check collaborative funding criteria is met")
    public boolean consortiumCanCheckCollaborativeFundingCriteriaIsMet(final ApplicationResource applicationResource,
                                                                       final UserResource user) {
        return isMemberOfProjectTeam(applicationResource.getId(), user);
    }

    private boolean isCompetitionBeyondAssessment(final Competition competition) {
        return EnumSet.of(FUNDERS_PANEL, ASSESSOR_FEEDBACK, PROJECT_SETUP).contains(competition.getCompetitionStatus());
    }
}

