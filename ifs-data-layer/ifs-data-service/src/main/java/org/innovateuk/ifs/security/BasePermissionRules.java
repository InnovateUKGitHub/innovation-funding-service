package org.innovateuk.ifs.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.competition.mapper.ExternalFinanceRepository;
import org.innovateuk.ifs.competition.repository.InnovationLeadRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectParticipantRepository;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.*;

/**
 * Base class to contain useful shorthand methods for the Permission rule subclasses
 */
public abstract class BasePermissionRules extends RootPermissionRules {

    @Autowired
    protected ProjectUserRepository projectUserRepository;

    @Autowired
    protected ApplicationRepository applicationRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    protected AssessmentRepository assessmentRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected InterviewRepository interviewRepository;

    @Autowired
    protected SupporterAssignmentRepository supporterAssignmentRepository;

    @Autowired
    private InnovationLeadRepository innovationLeadRepository;

    @Autowired
    private StakeholderRepository stakeholderRepository;

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @Autowired
    private MonitoringOfficerRepository monitoringOfficerRepository;

    @Autowired
    private ExternalFinanceRepository externalFinanceRepository;

    @Autowired
    private ProjectParticipantRepository projectParticipantRepository;

    protected boolean isPartner(long projectId, long userId) {
        List<ProjectUser> partnerProjectUser = projectUserRepository.findByProjectIdAndUserIdAndRoleIsIn(projectId, userId, PROJECT_USER_ROLES.stream().collect(Collectors.toList()));
        return !partnerProjectUser.isEmpty();
    }

    protected boolean isMonitoringOfficer(long projectId, long userId) {
        return monitoringOfficerRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    protected boolean isMonitoringOfficerForProjectLinkedToApplication(long applicationId, long userId) {
        Project project = projectRepository.findOneByApplicationId(applicationId);
        return project != null && isMonitoringOfficer(project.getId(), userId);
    }

    protected boolean isSpecificProjectPartnerByProjectId(long projectId, long organisationId, long userId) {
        ProjectUser partnerProjectUser = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, userId, organisationId, PROJECT_PARTNER);
        ProjectUser managerProjectUser = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, userId, organisationId, PROJECT_MANAGER);
        return partnerProjectUser != null || managerProjectUser != null;
    }

    protected boolean partnerBelongsToOrganisation(long projectId, long userId, long organisationId){
        ProjectUser partnerProjectUser = projectUserRepository.findFirstByProjectIdAndUserIdAndOrganisationIdAndRoleIn(projectId, userId, organisationId, PROJECT_USER_ROLES.stream().collect(Collectors.toList()));
        return partnerProjectUser != null;
    }

    protected boolean isSameProjectOrganisation(long projectId, long firstUserId, long secondUserId) {
        List<ProjectUser> projectUserOneRoles = projectUserRepository.findByProjectIdAndUserId(projectId, firstUserId);
        List<ProjectUser> projectUserTwoRoles = projectUserRepository.findByProjectIdAndUserId(projectId, secondUserId);
        return !projectUserOneRoles.isEmpty() && !projectUserTwoRoles.isEmpty() &&
                projectUserOneRoles.get(0).getOrganisation().equals(projectUserTwoRoles.get(0).getOrganisation());
    }

    protected boolean isLeadPartner(long projectId, long userId) {

        Project project = projectRepository.findById(projectId).get();
        ProcessRole leadApplicantProcessRole = processRoleRepository.findOneByApplicationIdAndRole(project.getApplication().getId(), ProcessRoleType.LEADAPPLICANT);
        Organisation leadOrganisation = organisationRepository.findById(leadApplicantProcessRole.getOrganisationId()).get();

        ProjectUser partnerProjectUser = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, userId, leadOrganisation.getId(), PROJECT_PARTNER);
        return partnerProjectUser != null;
    }

    protected boolean isProjectManager(long projectId, long userId) {
        List<ProjectUser> projectManagerUsers = projectUserRepository.findByProjectIdAndUserIdAndRole(projectId, userId, PROJECT_MANAGER);
        return projectManagerUsers != null && !projectManagerUsers.isEmpty();
    }

    protected boolean isFinanceContact(long projectId, long userId) {
        return !projectUserRepository.findByProjectIdAndUserIdAndRole(projectId, userId, PROJECT_FINANCE_CONTACT).isEmpty();
    }

    protected boolean userIsInnovationLeadOnCompetition(long competitionId, long loggedInUserId) {
        List<InnovationLead> competitionParticipants = innovationLeadRepository.findInnovationsLeads(competitionId);
        return competitionParticipants.stream().anyMatch(cp -> cp.getUser().getId().equals(loggedInUserId));
    }

    protected boolean userIsStakeholderInCompetition(long competitionId, long loggedInUserId) {
        return stakeholderRepository.existsByCompetitionIdAndUserId(competitionId, loggedInUserId);
    }

    protected boolean userIsExternalFinanceInCompetition(long competitionId, long loggedInUserId) {
        return externalFinanceRepository.existsByCompetitionIdAndUserId(competitionId, loggedInUserId);
    }

    protected boolean userIsStakeholderOnCompetitionForProject(long projectId, long loggedInUserId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if(!project.isPresent()) {
            return false;
        }
        Application application = project.get().getApplication();
        return userIsStakeholderInCompetition(application.getCompetition().getId(), loggedInUserId);
    }

    protected boolean userIsExternalFinanceOnCompetitionForProject(long projectId, long loggedInUserId) {
        Optional<Project> project = projectRepository.findById(projectId);
        if(!project.isPresent()) {
            return false;
        }
        Application application = project.get().getApplication();
        return userIsExternalFinanceInCompetition(application.getCompetition().getId(), loggedInUserId);
    }

    protected boolean userIsMonitoringOfficerInCompetition(long competitionId, long loggedInUserId) {
        return monitoringOfficerRepository.existsByProjectApplicationCompetitionIdAndUserId(competitionId, loggedInUserId);
    }

    protected boolean isProjectActive(long projectId) {
        ProjectProcess projectProcess = projectProcessRepository.findOneByTargetId(projectId);
        return projectProcess.getProcessState().isActive();
    }

    protected boolean isSupporterForApplication(long applicationId, long loggedInUserId) {
        return supporterAssignmentRepository.existsByParticipantIdAndTargetId(loggedInUserId, applicationId);
    }

    protected boolean isSupporterForCompetition(long competitionId, long loggedInUserId) {
        return supporterAssignmentRepository.existsByParticipantIdAndCompetitionId(loggedInUserId, competitionId);
    }

    protected boolean isProjectPartnerForApplication(ApplicationResource applicationResource, UserResource user) {
        Project linkedProject = projectRepository.findOneByApplicationId(applicationResource.getId());
        if (linkedProject == null) {
            return false;
        }

        return isPartner(linkedProject.getId(), user.getId());
    }

    public boolean checkHasAnyProjectParticipantRole(final UserResource user,
                                                     final long projectId,
                                                     final ProjectParticipantRole... roles) {
        return projectParticipantRepository.findByProjectId(projectId)
                .stream()
                .filter(participant -> user.getId().equals(participant.getUser().getId()))
                .filter(participant-> asList(roles).contains(participant.getRole()))
                .findFirst()
                .isPresent();
    }

    public boolean checkHasAnyProjectParticipantRole(final UserResource user,
                                                     final long projectId,
                                                     final long organisationId,
                                                     final ProjectParticipantRole... roles) {
        return projectUserRepository.findByProjectId(projectId)
                .stream()
                .filter(projectUser -> user.getId().equals(projectUser.getUser().getId()))
                .filter(projectUser-> asList(roles).contains(projectUser.getRole()))
                .filter(projectUser-> projectUser.getOrganisation().getId().equals(organisationId))
                .findFirst()
                .isPresent();
    }

}
