package com.worth.ifs.form.security;

import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.security.ApplicationRules;
import com.worth.ifs.form.resource.FormInputResponseResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.security.SecurityRuleUtil;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.user.resource.UserRoleType.*;

@PermissionRules
@Component
public class FormInputResponsePermissionRules {
    private static final Log LOG = LogFactory.getLog(ApplicationRules.class);

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ProcessRoleRepository processRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    @PermissionRule(value = "READ", description = "The consortium can see the input responses of their organisation and application")
    public boolean consortiumCanSeeTheInputResponsesForTheirOrganisationAndApplication(final FormInputResponseResource response, final UserResource user) {
        final boolean isLeadApplicant = checkRole(user, response, LEADAPPLICANT);
        final boolean isCollaborator = checkRole(user, response, COLLABORATOR);
        return isLeadApplicant || isCollaborator;
    }

    @PermissionRule(value = "READ", description = "The assessor can see the input responses of in applications for the applications they assess")
    public boolean assessorCanSeeTheInputResponsesInApplicationsForOrganisationsTheyAssess(final FormInputResponseResource response, final UserResource user) {
        final boolean isAssessor = checkRole(user, response, ASSESSOR);
        return isAssessor;
    }

    @PermissionRule(value = "READ", description = "A comp admin can see form input responses for applications")
    public boolean compAdminCanSeeFormInputResponsesForApplications(final FormInputResponseResource response, final UserResource user) {
        return SecurityRuleUtil.isCompAdmin(user);
    }

    private boolean checkRole(UserResource user, FormInputResponseResource response, UserRoleType userRoleType) {
        final List<Role> roles = roleRepository.findByName(userRoleType.getName());
        final Role role = roles.get(0);
        final Long organisationId = processRoleRepository.findOne(response.getUpdatedBy()).getOrganisation().getId();
        final Long applicationId = response.getApplication();
        final ProcessRole processRole = processRoleRepository.findByUserIdAndRoleIdAndApplicationIdAndOrganisationId(user.getId(), role.getId(), applicationId, organisationId);
        return processRole != null;
    }

}
