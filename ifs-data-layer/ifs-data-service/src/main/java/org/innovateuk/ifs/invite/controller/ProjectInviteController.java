package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.transactional.ProjectInviteService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.innovateuk.ifs.invite.resource.ProjectInviteConstants.*;

/**
 * Project Invite controller to handle RESTful service related to Project Invite
 */

@RestController
@ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
@RequestMapping({PROJECT_INVITE_BASE_URL, CAMEL_PROJECT_INVITE_BASE_URL})
public class ProjectInviteController {

    @Autowired
    private ProjectInviteService projectInviteService;

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({PROJECT_INVITE_SAVE, CAMEL_PROJECT_INVITE_SAVE})
    public RestResult<Void> saveProjectInvites(@RequestBody @Valid ProjectUserInviteResource projectUserInviteResource) {

        return projectInviteService.saveProjectInvite(projectUserInviteResource).toPostResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({GET_INVITE_BY_HASH + "{hash}", CAMEL_GET_INVITE_BY_HASH + "{hash}"})
    public RestResult<ProjectUserInviteResource> getProjectInviteByHash(@PathVariable("hash") String hash) {
        return projectInviteService.getInviteByHash(hash).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({GET_PROJECT_INVITE_LIST + "{projectId}", CAMEL_GET_PROJECT_INVITE_LIST + "{projectId}"})
    public RestResult<List<ProjectUserInviteResource>> getInvitesByProject(@PathVariable("projectId") Long projectId) {
        return projectInviteService.getInvitesByProject(projectId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PutMapping(value = {ACCEPT_INVITE  + "{hash}/{userId}", CAMEL_ACCEPT_INVITE  + "{hash}/{userId}"})
    public RestResult<Void> acceptInvite( @PathVariable("hash") String hash, @PathVariable("userId") Long userId) {
        return projectInviteService.acceptProjectInvite(hash, userId).toPostResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({CHECK_EXISTING_USER_URL + "{hash}", CAMEL_CHECK_EXISTING_USER_URL + "{hash}"})
    public RestResult<Boolean> checkExistingUser( @PathVariable("hash") String hash) {
        return projectInviteService.checkUserExistsForInvite(hash).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({GET_USER_BY_HASH_MAPPING + "{hash}", CAMEL_GET_USER_BY_HASH_MAPPING + "{hash}"})
    public RestResult<UserResource> getUser( @PathVariable("hash") String hash) {
        return projectInviteService.getUserByInviteHash(hash).toGetResponse();
    }
}
