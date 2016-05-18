package com.worth.ifs.application.security;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.security.SecurityRuleUtil;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.competition.resource.CompetitionResource.Status.*;
import static com.worth.ifs.security.SecurityRuleUtil.checkRole;
import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@PermissionRules
@Component
public class ApplicationPermissionRules {

    public static final List<CompetitionResource.Status> ASSESSOR_FEEDBACK_PUBLISHED_STATES = singletonList(PROJECT_SETUP);
    public static final List<CompetitionResource.Status> EDITABLE_ASSESSOR_FEEDBACK_STATES = asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK);

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "The consortium can see the participation percentage for their applications")
    public boolean consortiumCanSeeTheResearchParticipantPercentage(final ApplicationResource applicationResource, UserResource user) {
        final boolean isLeadApplicant = checkRole(user, applicationResource.getId(), LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkRole(user, applicationResource.getId(), COLLABORATOR, processRoleRepository);
        return isLeadApplicant || isCollaborator;
    }

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "The assessor can see the participation percentage for applications they assess")
    public boolean assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(final ApplicationResource applicationResource, UserResource user) {
        final boolean isAssessor = checkRole(user, applicationResource.getId(), ASSESSOR, processRoleRepository);
        return isAssessor;
    }

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "The assessor can see the participation percentage for applications they assess")
    public boolean compAdminCanSeeTheResearchParticipantPercentageInApplications(final ApplicationResource applicationResource, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(value = "READ_FINANCE_TOTALS",
            description = "The consortium can see the application finance totals",
            additionalComments = "This rule secures ApplicationResource which can contain more information than this rule should allow. Consider a new cut down object based on ApplicationResource")
    public boolean consortiumCanSeeTheApplicationFinanceTotals(final ApplicationResource applicationResource, final UserResource user) {
        final boolean isLeadApplicant = checkRole(user, applicationResource.getId(), LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkRole(user, applicationResource.getId(), COLLABORATOR, processRoleRepository);
        return isLeadApplicant || isCollaborator;
    }


    @PermissionRule(value = "APPLICATION_SUBMITTED_NOTIFICATION", description = "A lead applicant can send the notification of a submitted application")
    public boolean aLeadApplicantCanSendApplicationSubmittedNotification(final ApplicationResource applicationResource, final UserResource user) {
        final boolean isLeadApplicant = checkRole(user, applicationResource.getId(), LEADAPPLICANT, processRoleRepository);
        return isLeadApplicant;
    }

    @PermissionRule(value = "READ_FINANCE_TOTALS",
            description = "A comp admin can see application finances for organisations",
            additionalComments = "This rule secures ApplicationResource which can contain more information than this rule should allow. Consider a new cut down object based on ApplicationResource")
    public boolean compAdminCanSeeApplicationFinancesTotals(final ApplicationResource applicationResource, final UserResource user) {
        return SecurityRuleUtil.isCompAdmin(user);
    }

    @PermissionRule(value = "READ", description = "A user can see an application resource which they are connected to")
    public boolean usersConnectedToTheApplicationCanView(ApplicationResource application, UserResource user) {
        boolean isConnectedToApplication = userIsConnectedToApplicationResource(application, user);
        return  isConnectedToApplication;
    }

    @PermissionRule(value = "READ", description = "Comp admins can see application resources")
    public boolean compAdminsCanViewApplications(final ApplicationResource application, final UserResource user){
        return isCompAdmin(user);
    }

    @PermissionRule(value = "UPDATE", description = "A user can update their own application if they are a lead applicant or collaborator of the application")
    public boolean applicantCanUpdateApplicationResource(ApplicationResource application, UserResource user) {
        List<Role> allApplicantRoles = roleRepository.findByNameIn(asList(LEADAPPLICANT.getName(), COLLABORATOR.getName()));
        List<ProcessRole> applicantProcessRoles = processRoleRepository.findByUserIdAndRoleInAndApplicationId(user.getId(), allApplicantRoles, application.getId());
        return !applicantProcessRoles.isEmpty();
    }

    @PermissionRule(
            value = "UPLOAD_ASSESSOR_FEEDBACK",
            description = "A Comp Admin user can upload Assessor Feedback documentation for an Application whilst " +
                          "the Application's Competition is in Funders' Panel or Assessor Feedback state",
            particularBusinessState = "Application's Competition Status = 'Funders Panel' or 'Assessor Feedback'")
    public boolean compAdminCanUploadAssessorFeedbackToApplicationInFundersPanelOrAssessorFeedbackState(ApplicationResource application, UserResource user) {

        if (!isCompAdmin(user)) {
            return false;
        }

        Long competitionId = application.getCompetition();
        Competition competition = competitionRepository.findOne(competitionId);
        return EDITABLE_ASSESSOR_FEEDBACK_STATES.contains(competition.getCompetitionStatus());
    }

    @PermissionRule(
            value = "REMOVE_ASSESSOR_FEEDBACK",
            description = "A Comp Admin user can remove Assessor Feedback documentation so long as the Feedback has not yet been published",
            particularBusinessState = "Application's Competition Status != 'Project Setup' or beyond")
    public boolean compAdminCanRemoveAssessorFeedbackThatHasNotYetBeenPublished(ApplicationResource application, UserResource user) {

        if (!isCompAdmin(user)) {
            return false;
        }

        Long competitionId = application.getCompetition();
        Competition competition = competitionRepository.findOne(competitionId);
        return !ASSESSOR_FEEDBACK_PUBLISHED_STATES.contains(competition.getCompetitionStatus());
    }

    @PermissionRule(
            value = "DOWNLOAD_ASSESSOR_FEEDBACK",
            description = "A Comp Admin user can see and download Assessor Feedback at any time for any Application")
    public boolean compAdminCanSeeAndDownloadAllAssessorFeedback(ApplicationResource application, UserResource user) {
        return isCompAdmin(user);
    }

    @PermissionRule(
            value = "DOWNLOAD_ASSESSOR_FEEDBACK",
            description = "A Lead Applicant can see and download Assessor Feedback attached to their Application when it has been published",
            particularBusinessState = "Application's Competition Status = 'Project Setup' or beyond")
    public boolean leadApplicantCanSeeAndDownloadPublishedAssessorFeedbackForTheirApplications(ApplicationResource application, UserResource user) {

        boolean isLeadApplicantForApplication = checkRole(user, application.getId(), UserRoleType.LEADAPPLICANT, processRoleRepository);

        if (isLeadApplicantForApplication) {
            Long competitionId = application.getCompetition();
            Competition competition = competitionRepository.findOne(competitionId);
            return ASSESSOR_FEEDBACK_PUBLISHED_STATES.contains(competition.getCompetitionStatus());
        }

        return false;
    }

    boolean userIsConnectedToApplicationResource(ApplicationResource application, UserResource user) {
        ProcessRole processRole =  processRoleRepository.findByUserIdAndApplicationId(user.getId(), application.getId());
        return processRole != null;
    }
}

