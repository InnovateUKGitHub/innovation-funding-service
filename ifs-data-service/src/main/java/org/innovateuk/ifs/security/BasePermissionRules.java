package org.innovateuk.ifs.security;

import java.util.List;

import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.repository.ProjectUserRepository;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;

import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;

/**
 * Base class to contain useful shorthand methods for the Permission rule subclasses
 */
public abstract class BasePermissionRules {

    @Autowired
    protected ProcessRoleRepository processRoleRepository;

    @Autowired
    protected ProjectUserRepository projectUserRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AssessmentRepository assessmentRepository;

    protected boolean isMemberOfProjectTeam(long applicationId, UserResource user) {
        return isLeadApplicant(applicationId, user) || isCollaborator(applicationId, user);
    }

    protected boolean isCollaborator(long applicationId, UserResource user) {
        return checkProcessRole(user, applicationId, COLLABORATOR, processRoleRepository);
    }

    protected boolean isLeadApplicant(long applicationId, UserResource user) {
        return checkProcessRole(user, applicationId, LEADAPPLICANT, processRoleRepository);
    }

    protected boolean isAssessor(long applicationId, UserResource user) {
        return checkProcessRole(user, applicationId, ASSESSOR, processRoleRepository);
    }

    protected boolean isPartner(long projectId, long userId) {
        List<ProjectUser> partnerProjectUser = projectUserRepository.findByProjectIdAndUserIdAndRole(projectId, userId, PROJECT_PARTNER);
        return !partnerProjectUser.isEmpty();
    }

    protected boolean isSpecificProjectPartnerByProjectId(long projectId, long organisationId, long userId) {
        ProjectUser partnerProjectUser = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, userId, organisationId, PROJECT_PARTNER);
        ProjectUser managerProjectUser = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, userId, organisationId, PROJECT_MANAGER);
        return partnerProjectUser != null || managerProjectUser != null;
    }

    protected boolean partnerBelongsToOrganisation(long projectId, long userId, long organisationId){
        ProjectUser partnerProjectUser = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRole(projectId, userId, organisationId, PROJECT_PARTNER);
        return partnerProjectUser != null;
    }

    protected boolean isLeadPartner(long projectId, long userId) {

        Project project = projectRepository.findOne(projectId);
        Role leadApplicantRole = roleRepository.findOneByName(LEADAPPLICANT.getName());
        ProcessRole leadApplicantProcessRole = processRoleRepository.findOneByApplicationIdAndRoleId(project.getApplication().getId(), leadApplicantRole.getId());
        Organisation leadOrganisation = organisationRepository.findOne(leadApplicantProcessRole.getOrganisationId());

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
}
