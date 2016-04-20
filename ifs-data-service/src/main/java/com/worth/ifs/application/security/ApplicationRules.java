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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;
import static com.worth.ifs.user.domain.UserRoleType.*;

@PermissionRules
@Component
public class ApplicationRules {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "The consortium can see the participation percentage for their applications")
    public boolean consortiumCanSeeTheResearchParticipantPercentage(final ApplicationResource applicationResource, UserResource user) {
        final boolean isLeadApplicant = checkRole(user, applicationResource.getId(), LEADAPPLICANT);
        final boolean isCollaborator = checkRole(user, applicationResource.getId(), COLLABORATOR);
        return isLeadApplicant || isCollaborator;
    }

    @PermissionRule(value = "READ_RESEARCH_PARTICIPATION_PERCENTAGE", description = "The assessor can see the participation percentage for applications they assess")
    public boolean assessorCanSeeTheResearchParticipantPercentageInApplicationsTheyAssess(final ApplicationResource applicationResource, UserResource user) {
        final boolean isAssessor = checkRole(user, applicationResource.getId(), ASSESSOR);
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
        final boolean isLeadApplicant = checkRole(user, applicationResource.getId(), LEADAPPLICANT);
        final boolean isCollaborator = checkRole(user, applicationResource.getId(), COLLABORATOR);
        return isLeadApplicant || isCollaborator;
    }

    @PermissionRule(value = "READ_FINANCE_TOTALS",
            description = "A comp admin can see application finances for organisations",
            additionalComments = "This rule secures ApplicationResource which can contain more information than this rule should allow. Consider a new cut down object based on ApplicationResource")
    public boolean compAdminCanSeeApplicationFinancesTotals(final ApplicationResource applicationResource, final UserResource user) {
        return SecurityRuleUtil.isCompAdmin(user);
    }


    @PermissionRule(value = "READ", description = "A user can see an applicationResource which they are connected to and if the application exists")
    public boolean applicantCanSeeConnectedApplicationResource(ApplicationResource application, UserResource user) {
        return isCompAdmin(user) || !(applicationExists(application) && !userIsConnectedToApplicationResource(application, user));
    }

    @PermissionRule(value = "UPDATE", description = "A user can update their own application if they are a lead applicant or collaborator of the application")
    public boolean applicantCanUpdateApplicationResource(ApplicationResource application, UserResource user) {
        List<Role> allApplicantRoles = roleRepository.findByNameIn(Arrays.asList(LEADAPPLICANT.getName(), COLLABORATOR.getName()));
        List<ProcessRole> applicantProcessRoles = processRoleRepository.findByUserIdAndRoleInAndApplicationId(user.getId(), allApplicantRoles, application.getId());
        return !applicantProcessRoles.isEmpty();
    }

    boolean userIsConnectedToApplicationResource(ApplicationResource application, UserResource user) {
        ProcessRole processRole = processRoleRepository.findByUserIdAndApplicationId(user.getId(), application.getId());
        return processRole != null;
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

