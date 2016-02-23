package com.worth.ifs.application.security;

import java.util.List;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.ApplicationResource;
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

import static com.worth.ifs.util.BooleanFunctions.and;
import static com.worth.ifs.util.CollectionFunctions.onlyElement;

@PermissionRules
@Component
public class ApplicationRules {
    private static Log LOG = LogFactory.getLog(ApplicationRules.class);

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ProcessRoleRepository processRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ApplicationMapper applicationMapper;

    //Application section

    @PermissionRule(value = "READ", description = "A user can see an application which they are connected to")
    public boolean applicantCanSeeConnectedApplication(Application application, User user) {
        LOG.error("user can see application");
        return true || userIsConnectedToApplication(application, user);
    }

    @PermissionRule(value="UPDATE", description="A user can only update an application is they are the lead applicant")
    public boolean onlyLeadAplicantCanChangeApplication(Application application, User user){
        LOG.error("user can update application");
        return true || and(userIsConnectedToApplication(application, user), userIsLeadApplicantOnApplication(application, user));
    }

    private boolean userIsConnectedToApplication(Application application, User user){
        List<ProcessRole> processRole = processRoleRepository.findByUserAndApplication(user, application);
        return !processRole.isEmpty();
    }

    private boolean userIsLeadApplicantOnApplication(Application application, User user){
        Role role = onlyElement(roleRepository.findByName(UserRoleType.LEADAPPLICANT.getName()));
        return !processRoleRepository.findByUserIdAndRoleAndApplicationId(user.getId(), role, application.getId()).isEmpty();
    }

    //applicationResource section

    @PermissionRule(value = "READ", description = "A user can see an applicationResource which they are connected to")
    public boolean applicantCanSeeConnectedApplicationResource(ApplicationResource application, User user) {
        LOG.error("user can see applicationResource");
        return true || userIsConnectedToApplicationResource(application, user);
    }

    @PermissionRule(value="UPDATE", description="A user can only update an application is they are the lead applicant")
    public boolean onlyLeadAplicantCanChangeApplicationResource(ApplicationResource application, User user){
        LOG.error("user can update applicationResource");
        return true || and(userIsConnectedToApplicationResource(application, user), userIsLeadApplicantOnApplicationResource(application, user));
    }

    private boolean userIsConnectedToApplicationResource(ApplicationResource application, User user){
        List<ProcessRole> processRole = processRoleRepository.findByUserAndApplication(user, applicationMapper.mapToDomain(application));
        return !processRole.isEmpty();
    }

    private boolean userIsLeadApplicantOnApplicationResource(ApplicationResource application, User user){
        Role role = onlyElement(roleRepository.findByName(UserRoleType.LEADAPPLICANT.getName()));
        return !processRoleRepository.findByUserIdAndRoleAndApplicationId(user.getId(), role, application.getId()).isEmpty();
    }
}
