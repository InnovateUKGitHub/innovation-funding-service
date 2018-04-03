package org.innovateuk.ifs.user.domain;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

public class UserTest {
    User user;

    Long id;
    String imageUrl;
    String email;
    String uid;
    private String firstName;
    private String lastName;


    @Before
    public void setUp() throws Exception {
        id = 0L;
        firstName = "firstName";
        lastName = "lastName";
        email = "test@innovateuk.org";
        imageUrl = "/image/url/test";
        uid = "uid";

        user = new User(id, firstName, lastName, email, imageUrl, uid);
    }

    @Test
    public void userShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(user.getFirstName(), firstName);
        Assert.assertEquals(user.getLastName(), lastName);
        Assert.assertEquals(user.getName(), firstName + " " + lastName);
        Assert.assertEquals(user.getId(), id);
        Assert.assertEquals(user.getImageUrl(), imageUrl);
        Assert.assertEquals(user.getUid(), uid);
    }

    @Test
    public void testInternalUserMethod() {

        Set<UserRoleType> expectedInternalRoles = UserRoleType.internalUserRoleTypes();

        stream(UserRoleType.values()).forEach(type -> {

            User userWithRole = newUser().withRoles(singleton(Role.getByName(type.getName()))).build();
            assertEquals(expectedInternalRoles.contains(type), userWithRole.isInternalUser());
        });
    }
}
