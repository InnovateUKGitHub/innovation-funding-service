package com.worth.ifs.application.security;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.util.BooleanFunctions.and;
import static com.worth.ifs.util.CollectionFunctions.onlyElement;

@PermissionRules
@Component
public class ApplicationRules {

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ProcessRoleRepository processRoleRepository;

    @Autowired
    RoleRepository roleRepository;

    @PermissionRule(value = "READ", description = "A user can see an applicationResource which they are connected to and if the application exists")
    public boolean applicantCanSeeConnectedApplicationResource(ApplicationResource application, User user) {
        return !(applicationExists(application) && !userIsConnectedToApplicationResource(application, user));
    }

    @PermissionRule(value="UPDATE", description="A user can only update an application is they are the lead applicant")
    public boolean onlyLeadApplicantCanChangeApplicationResource(ApplicationResource application, User user){
        return and(userIsConnectedToApplicationResource(application, user), userIsLeadApplicantOnApplicationResource(application, user), application.isOpen());
    }

    boolean userIsConnectedToApplicationResource(ApplicationResource application, User user){
        List<ProcessRole> processRole = processRoleRepository.findByUserAndApplicationId(user, application.getId());
        return !processRole.isEmpty();
    }

    boolean userIsLeadApplicantOnApplicationResource(ApplicationResource application, User user){
        Role role = onlyElement(roleRepository.findByName(UserRoleType.LEADAPPLICANT.getName()));
        return !processRoleRepository.findByUserIdAndRoleAndApplicationId(user.getId(), role, application.getId()).isEmpty();
    }

    boolean applicationExists(ApplicationResource applicationResource){
        Long id = applicationResource.getId();
        return id!=null && applicationRepository.exists(id);
    }
}
