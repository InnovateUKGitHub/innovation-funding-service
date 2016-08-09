package com.worth.ifs.security;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.security.SecurityRuleUtil.checkProcessRole;
import static com.worth.ifs.user.resource.UserRoleType.*;

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
        Role partnerRole = roleRepository.findOneByName(PARTNER.getName());
        List<ProjectUser> partnerProjectUser = projectUserRepository.findByProjectIdAndUserIdAndRoleId(projectId, userId, partnerRole.getId());
        return !partnerProjectUser.isEmpty();
    }

    protected boolean isSpecificProjectPartnerByApplicationId(long applicationId, long organisationId, long userId) {
        Role partnerRole = roleRepository.findOneByName(PARTNER.getName());
        long projectId = projectRepository.findOneByApplicationId(applicationId).getId();
        ProjectUser partnerProjectUser = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRoleId(projectId, userId, organisationId, partnerRole.getId());
        return partnerProjectUser != null;
    }

    protected boolean partnerBelongsToOrganisation(long projectId, long userId, long organisationId){
        Role partnerRole = roleRepository.findOneByName(PARTNER.getName());
        ProjectUser partnerProjectUser = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRoleId(projectId, userId, organisationId,partnerRole.getId());
        return partnerProjectUser != null;
    }

    protected boolean isLeadPartner(long projectId, long userId) {

        Project project = projectRepository.findOne(projectId);
        Role leadApplicantRole = roleRepository.findOneByName(LEADAPPLICANT.getName());
        ProcessRole leadApplicantProcessRole = processRoleRepository.findOneByApplicationIdAndRoleId(project.getApplication().getId(), leadApplicantRole.getId());
        Organisation leadOrganisation = leadApplicantProcessRole.getOrganisation();

        Role partnerRole = roleRepository.findOneByName(PARTNER.getName());
        ProjectUser partnerProjectUser = projectUserRepository.findOneByProjectIdAndUserIdAndOrganisationIdAndRoleId(projectId, userId, leadOrganisation.getId(), partnerRole.getId());
        return partnerProjectUser != null;
    }
}
