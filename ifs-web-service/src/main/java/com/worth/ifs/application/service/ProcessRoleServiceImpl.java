package com.worth.ifs.application.service;

import com.worth.ifs.user.domain.UserApplicationRole;
import com.worth.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessRoleServiceImpl implements ProcessRoleService {
    @Autowired
    UserRestService userRestService;

    @Override
    public UserApplicationRole findUserApplicationRole(Long userId, Long applicationId) {
        return userRestService.findUserApplicationRole(userId, applicationId);
    }
}
