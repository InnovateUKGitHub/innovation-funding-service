package com.worth.ifs;

import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
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
