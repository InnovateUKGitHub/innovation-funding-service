package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.user.resource.UserResource;


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

    // TODO we can remove the setters once POJOTest is removed
    public void setUser(UserResource user) {
        this.user = user;
    }

    public void setProfile(ProfileResource profile) {
        this.profile = profile;
    }
}
