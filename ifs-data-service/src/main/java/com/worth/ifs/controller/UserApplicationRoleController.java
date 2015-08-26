package com.worth.ifs.controller;

import com.worth.ifs.domain.UserApplicationRole;
import com.worth.ifs.repository.UserApplicationRoleRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userapplicationrole")
public class UserApplicationRoleController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    UserApplicationRoleRepository userApplicationRoleRepository;

    @RequestMapping("/findByUserApplication/{userId}/{applicationId}")
    public UserApplicationRole findByUserApplication(@PathVariable("userId") final Long userId,
                                                     @PathVariable("applicationId") final Long applicationId) {
        return userApplicationRoleRepository.findByUserIdAndApplicationId(userId, applicationId);
    }

}
