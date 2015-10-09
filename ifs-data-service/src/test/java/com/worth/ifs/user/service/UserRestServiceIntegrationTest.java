package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceIntegrationTest;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Tests for {@link UserRestServiceImpl}.
 *
 * Created by dwatson on 02/10/15.
 */
public class UserRestServiceIntegrationTest extends BaseRestServiceIntegrationTest<UserRestService> {

    @Override
    @Autowired
    protected void setRestService(UserRestService service) {
        this.service = service;
    }

    @Test
    public void test_retrieveUserByEmailAndPassword() {
        User user = service.retrieveUserByEmailAndPassword("steve.smith@empire.com", "test");
        assertNotNull(user);
        assertEquals("steve.smith@empire.com", user.getEmail());
    }
}
