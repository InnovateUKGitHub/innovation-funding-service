package com.worth.ifs.user.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This RestController exposes CRUD operations
 * to manage {@link ProcessRole} related data.
 */
@RestController
@RequestMapping("/processrole")
public class ProcessRoleController {

    @Autowired
    private UsersRolesService usersRolesService;

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping("/{id}")
    public RestResult<ProcessRoleResource> findOne(@PathVariable("id") final Long id) {
        return usersRolesService.getProcessRoleById(id).toGetResponse();
    }

    @RequestMapping("/findByUserApplication/{userId}/{applicationId}")
    public RestResult<ProcessRoleResource> findByUserApplication(@PathVariable("userId") final Long userId,
                                                         @PathVariable("applicationId") final Long applicationId) {
        return usersRolesService.getProcessRoleByUserIdAndApplicationId(userId, applicationId).toGetResponse();
    }

    @RequestMapping("/findByApplicationId/{applicationId}")
    public RestResult<List<ProcessRoleResource>> findByUserApplication(@PathVariable("applicationId") final Long applicationId) {

        return usersRolesService.getProcessRolesByApplicationId(applicationId).toGetResponse();
    }

    @RequestMapping("/findByUserId/{userId}")
    public RestResult<List<ProcessRoleResource>> findByUser(@PathVariable("userId") final Long userId) {
        return usersRolesService.getProcessRolesByUserId(userId).toGetResponse();
    }

    @RequestMapping("/findAssignable/{applicationId}")
    public RestResult<List<ProcessRoleResource>> findAssignable(@PathVariable("applicationId") final Long applicationId) {
        return usersRolesService.getAssignableProcessRolesByApplicationId(applicationId).toGetResponse();
    }

    @RequestMapping("{id}/application")
    public RestResult<ApplicationResource> findByProcessRole(@PathVariable("id") final Long id) {
        return applicationService.findByProcessRole(id).toGetResponse();
    }
}
