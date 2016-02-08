package com.worth.ifs.application.service;

import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link ProcessRole} related data,
 * through the RestService {@link UserRestService}.
 */
// TODO DW - INFUND-1555 - return RestResults from this Service
@Service
public class ProcessRoleServiceImpl implements ProcessRoleService {
    @Autowired
    UserRestService userRestService;

    @Override
    public ProcessRole findProcessRole(Long userId, Long applicationId) {
        return userRestService.findProcessRole(userId, applicationId).getSuccessObject();
    }

    @Override
    public List<ProcessRole> findProcessRolesByApplicationId(Long applicationId) {
        return userRestService.findProcessRole(applicationId).getSuccessObject();
    }

    @Override
    public List<ProcessRole> findAssignableProcessRoles(Long applicationId) {
        return userRestService.findAssignableProcessRoles(applicationId).getSuccessObject();
    }

    @Override
    public ProcessRole getById(Long id){
        return userRestService.findProcessRoleById(id).getSuccessObject();
    }
}
