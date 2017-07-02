package org.innovateuk.ifs.security;

import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;

/**
 * Base class to contain useful shorthand methods for the Permission rule subclasses
 */
public abstract class RootPermissionRules {

    @Autowired
    protected ProcessRoleRepository processRoleRepository;

    @Autowired
    protected RoleRepository roleRepository;

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

}
