package org.innovateuk.ifs.profile.controller;

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

    @GetMapping("/id/{userId}/getProfileSkills")
    public RestResult<ProfileSkillsResource> getProfileSkills(@PathVariable("userId") long userId) {
        return profileService.getProfileSkills(userId).toGetResponse();
    }

    @PutMapping("/id/{userId}/updateProfileSkills")
    public RestResult<Void> updateProfileSkills(@PathVariable("userId") long id,
                                                @Valid @RequestBody ProfileSkillsEditResource profileSkills) {
        return profileService.updateProfileSkills(id, profileSkills).toPutResponse();
    }

    @GetMapping("/id/{userId}/getProfileAgreement")
    public RestResult<ProfileAgreementResource> getProfileAgreement(@PathVariable("userId") long userId) {
        return profileService.getProfileAgreement(userId).toGetResponse();
    }

    @PutMapping("/id/{userId}/updateProfileAgreement")
    public RestResult<Void> updateProfileAgreement(@PathVariable("userId") long userId) {
        return profileService.updateProfileAgreement(userId).toPutResponse();
    }

    @GetMapping("/id/{userId}/getUserProfile")
    public RestResult<UserProfileResource> getUserProfile(@PathVariable("userId") Long userId) {
        return profileService.getUserProfile(userId).toGetResponse();
    }

    @PutMapping("/id/{userId}/updateUserProfile")
    public RestResult<Void> updateUserProfile(@PathVariable("userId") Long userId,
                                              @RequestBody UserProfileResource profileDetails) {
        return profileService.updateUserProfile(userId, profileDetails).toPutResponse();
    }

    @GetMapping("/id/{userId}/profileStatus")
    public RestResult<UserProfileStatusResource> getUserProfileStatus(@PathVariable("userId") Long userId) {
        return profileService.getUserProfileStatus(userId).toGetResponse();
    }
}
