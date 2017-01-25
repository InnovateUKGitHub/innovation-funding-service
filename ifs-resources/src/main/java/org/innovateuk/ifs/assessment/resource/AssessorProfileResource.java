package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.user.resource.UserResource;


/**
 * DTO to encapsulate an Assessors profile view.
 */
public class AssessorProfileResource {

    private UserResource user;
    private ProfileResource profile;

    public AssessorProfileResource() {
        // default constructor
    }

    public AssessorProfileResource(UserResource user, ProfileResource profile) {
        this.user = user;
        this.profile = profile;
    }

    public UserResource getUser() {
        return user;
    }

    public ProfileResource getProfile() {
        return profile;
    }
}
