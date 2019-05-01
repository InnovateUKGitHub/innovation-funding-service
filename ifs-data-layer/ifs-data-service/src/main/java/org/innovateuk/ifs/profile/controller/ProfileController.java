package org.innovateuk.ifs.profile.controller;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.profile.transactional.ProfileService;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This RestController exposes CRUD operations to both the
 * {org.innovateuk.ifs.user.service.UserRestServiceImpl} and other REST-API users
 * to manage {@link org.innovateuk.ifs.profile.domain.Profile} related data.
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/id/{userId}/getProfileSkills", "/id/{userId}/get-profile-skills"})
    public RestResult<ProfileSkillsResource> getProfileSkills(@PathVariable("userId") long userId) {
        return profileService.getProfileSkills(userId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PutMapping({"/id/{userId}/updateProfileSkills", "/id/{userId}/update-profile-skills"})
    public RestResult<Void> updateProfileSkills(@PathVariable("userId") long id,
                                                @Valid @RequestBody ProfileSkillsEditResource profileSkills) {
        return profileService.updateProfileSkills(id, profileSkills).toPutResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/id/{userId}/getProfileAgreement", "/id/{userId}/get-profile-agreement"})
    public RestResult<ProfileAgreementResource> getProfileAgreement(@PathVariable("userId") long userId) {
        return profileService.getProfileAgreement(userId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PutMapping({"/id/{userId}/updateProfileAgreement", "/id/{userId}/update-profile-agreement"})
    public RestResult<Void> updateProfileAgreement(@PathVariable("userId") long userId) {
        return profileService.updateProfileAgreement(userId).toPutResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/id/{userId}/getUserProfile", "/id/{userId}/get-user-profile"})
    public RestResult<UserProfileResource> getUserProfile(@PathVariable("userId") Long userId) {
        return profileService.getUserProfile(userId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PutMapping({"/id/{userId}/updateUserProfile", "/id/{userId}/update-user-profile"})
    public RestResult<Void> updateUserProfile(@PathVariable("userId") Long userId,
                                              @RequestBody UserProfileResource profileDetails) {
        return profileService.updateUserProfile(userId, profileDetails).toPutResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/id/{userId}/profileStatus", "/id/{userId}/profile-status"})
    public RestResult<UserProfileStatusResource> getUserProfileStatus(@PathVariable("userId") Long userId) {
        return profileService.getUserProfileStatus(userId).toGetResponse();
    }
}
