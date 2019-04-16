package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/{id}")
    public RestResult<ProcessRoleResource> findOne(@PathVariable("id") final long id) {
        return usersRolesService.getProcessRoleById(id).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByUserApplication/{userId}/{applicationId}", "/find-by-user-application/{userId}/{applicationId}"})
    public RestResult<ProcessRoleResource> findByUserApplication(@PathVariable("userId") final long userId,
                                                         @PathVariable("applicationId") final long applicationId) {
        return usersRolesService.getProcessRoleByUserIdAndApplicationId(userId, applicationId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByApplicationId/{applicationId}", "/find-by-application-id/{applicationId}"})
    public RestResult<List<ProcessRoleResource>> findByUserApplication(@PathVariable("applicationId") final long applicationId) {
        return usersRolesService.getProcessRolesByApplicationId(applicationId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findByUserId/{userId}", "/find-by-user-id/{userId}"})
    public RestResult<List<ProcessRoleResource>> findByUser(@PathVariable("userId") final long userId) {
        return usersRolesService.getProcessRolesByUserId(userId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/findAssignable/{applicationId}", "/find-assignable/{applicationId}"})
    public RestResult<List<ProcessRoleResource>> findAssignable(@PathVariable("applicationId") final long applicationId) {
        return usersRolesService.getAssignableProcessRolesByApplicationId(applicationId).toGetResponse();
    }

    @GetMapping("{id}/application")
    public RestResult<ApplicationResource> findByProcessRole(@PathVariable("id") final long id) {
        return applicationService.findByProcessRole(id).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"userHasApplicationForCompetition/{userId}/{competitionId}", "user-has-application-for-competition/{userId}/{competitionId}"})
    public RestResult<Boolean> userHasApplicationForCompetition(@PathVariable("userId") final long userId, @PathVariable("competitionId") final long competitionId) {
        return usersRolesService.userHasApplicationForCompetition(userId, competitionId).toGetResponse();
    }
}
