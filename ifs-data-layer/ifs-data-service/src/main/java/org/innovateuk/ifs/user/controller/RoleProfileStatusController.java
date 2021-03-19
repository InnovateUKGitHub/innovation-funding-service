package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.transactional.RoleProfileStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class RoleProfileStatusController {

    public static final Sort DEFAULT_SORT = new Sort(
            new Sort.Order(Sort.Direction.ASC, "user.firstName"),
            new Sort.Order(Sort.Direction.ASC, "user.lastName")
    );

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "40";

    @Autowired
    private RoleProfileStatusService roleProfileStatusService;

    @GetMapping("/{userId}/role-profile-status/{profileRole}")
    public RestResult<RoleProfileStatusResource> findByUserIdAndProfileRole(@PathVariable long userId, @PathVariable final ProfileRole profileRole) {
        return roleProfileStatusService.findByUserIdAndProfileRole(userId, profileRole).toGetResponse();
    }

    @GetMapping("/{userId}/role-profile-status")
    public RestResult<List<RoleProfileStatusResource>> findByUserId(@PathVariable long userId) {
        return roleProfileStatusService.findByUserId(userId).toGetResponse();
    }

    @PutMapping("/{userId}/role-profile-status")
    public RestResult<Void> updateUserStatus(@PathVariable long userId, @RequestBody RoleProfileStatusResource roleProfileStatusResource) {
        return roleProfileStatusService.updateUserStatus(userId, roleProfileStatusResource).toPutResponse();
    }

    @GetMapping("/role-profile-status/{roleProfileState}/{profileRole}")
    public RestResult<UserPageResource> getByRoleProfileStatus(@PathVariable RoleProfileState roleProfileState,
                                                               @PathVariable ProfileRole profileRole,
                                                               @RequestParam(required = false) String filter,
                                                               @RequestParam(defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return roleProfileStatusService.findByRoleProfile(roleProfileState, profileRole, filter, PageRequest.of(page, size, DEFAULT_SORT))
                .toGetResponse();
    }
}