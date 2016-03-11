package com.worth.ifs.user.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * Integration tests for {@link UserController}.
 *
 * Created by dwatson on 02/10/15.
 */
public class UserControllerIntegrationTest extends BaseControllerIntegrationTest<UserController> {

    @Override
    @Autowired
    protected void setControllerUnderTest(UserController controller) {
        this.controller = controller;
    }

    @Test
    public void test_findByEmailAddress() {
        UserResource user= controller.findByEmail("steve.smith@empire.com").getSuccessObject();
        assertEquals("steve.smith@empire.com", user.getEmail());
    }

    @Test
    public void test_findAll() {

        List<User> users = controller.findAll().getSuccessObject();
        assertEquals(10, users.size());

        //
        // Assert that we've got the users we were expecting
        //
        List<String> emailAddresses = users.stream().map(User::getEmail).collect(toList());
        List<String> expectedUsers = asList("steve.smith@empire.com", "jessica.doe@ludlow.co.uk", "paul.plum@gmail.com", "competitions@innovateuk.gov.uk", "finance@innovateuk.gov.uk", "pete.tom@egg.com", "felix.wilson@gmail.com", "ewan+1@hiveit.co.uk", "ewan+2@hiveit.co.uk", "ewan+12@hiveit.co.uk");
        assertTrue(emailAddresses.containsAll(expectedUsers));
    }
}
