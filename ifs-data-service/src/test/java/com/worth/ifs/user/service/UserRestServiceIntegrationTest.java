package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceIntegrationTest;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link UserRestServiceImpl}.
 */
public class UserRestServiceIntegrationTest extends BaseRestServiceIntegrationTest<UserRestService> {

    public static final String EMAIL = "steve.smith@empire.com";

    @Override
    @Autowired
    protected void setRestService(UserRestService service) {
        this.service = service;
    }

    @Test
    public void test_retrieveUserByToken() {
        User user = service.retrieveUserByToken("123abc").getSuccessObject();
        assertNotNull(user);
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void test_retrieveUserByEmailAndPassword() {
        User user = service.retrieveUserByEmailAndPassword(EMAIL, "test").getSuccessObject();
        assertNotNull(user);
        assertEquals(EMAIL, user.getEmail());
    }
}
