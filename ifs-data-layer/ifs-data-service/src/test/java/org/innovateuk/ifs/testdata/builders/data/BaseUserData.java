package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.user.resource.UserResource;

/**
 * Running data context for generating Users
 */
public class BaseUserData {

    private UserResource user;

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }
}
