package com.worth.ifs.finance.security;

import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static com.worth.ifs.user.domain.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;

/*
    ApplicationFinanceResoureRules are taking care of the permissioning for reading the finances.
 */
@Component
@PermissionRules
public class ApplicationFinanceResourceRules {
    private static final Log LOG = LogFactory.getLog(ApplicationFinanceResourceRules.class);

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProcessRoleRepository processRoleRepository;

    @PermissionRule(value = "READ", description = "An applicant can only see their finances of their own organisation")
    public boolean applicationCanSeeTheirOwnOrganisationFinances(ApplicationFinanceResourceId applicationFinanceResourceId, User user) {
        boolean isLeadApplicant = checkRole(user, applicationFinanceResourceId.getApplicationId(), applicationFinanceResourceId.getOrganisationId(), LEADAPPLICANT);
        boolean isCollaborator = checkRole(user, applicationFinanceResourceId.getApplicationId(), applicationFinanceResourceId.getOrganisationId(), COLLABORATOR);
        boolean isAssessor = checkRole(user, applicationFinanceResourceId.getApplicationId(), applicationFinanceResourceId.getOrganisationId(), ASSESSOR);

        return isLeadApplicant || isCollaborator || isAssessor;
    }

    private boolean checkRole(User user, Long applicationId, Long organisationId, UserRoleType userRoleType) {
        List<Role> roles = roleRepository.findByName(userRoleType.getName());

        if (roles.isEmpty()) {
            LOG.error("Could not find a Lead Applicant role");
            return false;
        }
        Role role = roles.get(0);
        ProcessRole processRole = processRoleRepository.findByUserIdAndRoleAndApplicationIdAndOrganisationId(user.getId(), role.getId(), applicationId, organisationId);
        return (processRole!=null);
    }

}
