package com.worth.ifs.application.security;

import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.security.SecurityRuleUtil;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.user.domain.UserRoleType.*;
import static com.worth.ifs.util.CollectionFunctions.onlyElement;

@PermissionRules
@Component
public class ApplicationRules {
    private static final Log LOG = LogFactory.getLog(ApplicationRules.class);

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ProcessRoleRepository processRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    @PermissionRule(value = "READ_PARTICIPATION_PERCENTAGE", description = "The consortium can see the participation percentage for their applications")
    public boolean consortiumCanSeeTheParticipantPercentage(final ApplicationResource applicationResource, UserResource user) {
        final boolean isLeadApplicant = checkRole(user, applicationResource.getId(), LEADAPPLICANT);
        final boolean isCollaborator = checkRole(user, applicationResource.getId(), COLLABORATOR);
        return isLeadApplicant || isCollaborator;
    }

    @PermissionRule(value = "READ_PARTICIPATION_PERCENTAGE", description = "The assessor can see the participation percentage for applications they assess")
    public boolean assessorCanSeeTheParticipantPercentageInApplicationsTheyAssess(final ApplicationResource applicationResource, UserResource user) {
        final boolean isLeadApplicant = checkRole(user, applicationResource.getId(), LEADAPPLICANT);
        final boolean isCollaborator = checkRole(user, applicationResource.getId(), COLLABORATOR);
        return isLeadApplicant || isCollaborator;
    }

    @PermissionRule(value = "READ_PARTICIPATION_PERCENTAGE", description = "The assessor can see the participation percentage for applications they assess")
    public boolean compAdminCanSeeTheParticipantPercentageInApplications(final ApplicationResource applicationResource, UserResource user) {
        return SecurityRuleUtil.isCompAdmin(user);
    }

    @PermissionRule(value = "UPDATE", description = "A user can update their own application if they are a lead applicant or collaborator of the application")
    public boolean applicantCanUpdateApplicationResource(ApplicationResource application, UserResource user) {
        List<Role> allApplicantRoles = roleRepository.findByNameIn(Arrays.asList(APPLICANT.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName()));
        List<ProcessRole> applicantProcessRoles = processRoleRepository.findByUserIdAndRoleInAndApplicationId(user.getId(), allApplicantRoles, application.getId());
        return !applicantProcessRoles.isEmpty();
    }

    boolean userIsConnectedToApplicationResource(ApplicationResource application, UserResource user) {
        ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(), application.getId());
        return processRole != null;
    }

    boolean userIsLeadApplicantOnApplicationResource(ApplicationResource application, UserResource user) {
        Role role = onlyElement(roleRepository.findByName(UserRoleType.LEADAPPLICANT.getName()));
        return !processRoleRepository.findByUserIdAndRoleAndApplicationId(user.getId(), role, application.getId()).isEmpty();
    }

    boolean applicationExists(ApplicationResource applicationResource) {
        Long id = applicationResource.getId();
        return id != null && applicationRepository.exists(id);
    }

    private boolean checkRole(UserResource user, Long applicationId, UserRoleType userRoleType) {
        final ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(), applicationId);
        return processRole != null && processRole.getRole().getName().equals(userRoleType.getName());
    }
}
