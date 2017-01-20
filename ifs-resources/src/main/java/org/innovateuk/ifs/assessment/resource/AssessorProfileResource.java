package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.user.resource.UserProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;


// we're not going to have a mapper for this
public class AssessorProfileResource {

    private UserResource user;
    private ProfileResource profile; // TODO the naming of this resource is confusing, too many users, to many profiles

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
