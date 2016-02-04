package com.worth.ifs.user.controller;

import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.RestResultBuilder;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestHandler;

/**
 * This RestController exposes CRUD operations
 * to manage {@link ProcessRole} related data.
 */
@RestController
@ExposesResourceFor(ProcessRoleResource.class)
@RequestMapping("/processrole")
public class ProcessRoleController {

    @Autowired
    private UsersRolesService usersRolesService;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    ApplicationMapper applicationMapper;

    @RequestMapping("/{id}")
    public RestResult<ProcessRole> findOne(@PathVariable("id") final Long id) {

        return newRestHandler(ProcessRole.class).perform(() -> usersRolesService.getProcessRoleById(id));
    }

    @RequestMapping("/findByUserApplication/{userId}/{applicationId}")
    public RestResult<ProcessRole> findByUserApplication(@PathVariable("userId") final Long userId,
                                                     @PathVariable("applicationId") final Long applicationId) {
        return newRestHandler(ProcessRole.class).perform(() -> usersRolesService.getProcessRoleByUserIdAndApplicationId(userId, applicationId));
    }

    @RequestMapping("/findByApplicationId/{applicationId}")
    public RestResult<List<ProcessRole>> findByUserApplication(@PathVariable("applicationId") final Long applicationId) {

        RestResultBuilder<List<ProcessRole>, List<ProcessRole>> handler = newRestHandler();
        return handler.perform(() -> usersRolesService.getProcessRolesByApplicationId(applicationId));
    }

    @RequestMapping("/findByUserId/{userId}")
    public RestResult<List<ProcessRole>> findByUser(@PathVariable("userId") final Long userId) {

        RestResultBuilder<List<ProcessRole>, List<ProcessRole>> handler = newRestHandler();
        return handler.perform(() -> usersRolesService.getProcessRolesByUserId(userId));
    }

    @RequestMapping("/findAssignable/{applicationId}")
    public RestResult<List<ProcessRole>> findAssignable(@PathVariable("applicationId") final Long applicationId) {

        RestResultBuilder<List<ProcessRole>, List<ProcessRole>> handler = newRestHandler();
        return handler.perform(() -> usersRolesService.getAssignableProcessRolesByApplicationId(applicationId));
    }

    @RequestMapping("{id}/application")
    public ApplicationResource findByProcessRole(@PathVariable("id") final Long id){
        ProcessRole processRole = processRoleRepository.findOne(id);
        if (processRole != null && processRole.getApplication() != null){
               return applicationMapper.mapApplicationToResource(processRole.getApplication());
        }
        return null;
    }
}
