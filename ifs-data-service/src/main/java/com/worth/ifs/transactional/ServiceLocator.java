package com.worth.ifs.transactional;

import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A Service Locator with access to all services, for use within non Spring-managed objects to look up sets of services
 * without having to pass multiple services to the target object's methods.
 *
 * Created by dwatson on 05/10/15.
 */
@Component
public class ServiceLocator {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    public RoleRepository getRoleRepository() {
        return roleRepository;
    }

    public ProcessRoleRepository getProcessRoleRepository() {
        return processRoleRepository;
    }
}
