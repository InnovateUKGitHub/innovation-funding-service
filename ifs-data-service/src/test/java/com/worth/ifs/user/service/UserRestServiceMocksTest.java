package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.userListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.userResourceListType;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;


public class UserRestServiceMocksTest extends BaseRestServiceUnitTest<UserRestServiceImpl> {

    private static final String usersUrl = "/user";
    private static final String processRolesUrl = "/processroles";

    @Override
    protected UserRestServiceImpl registerRestServiceUnderTest() {
        UserRestServiceImpl userRestService = new UserRestServiceImpl();
        userRestService.setUserRestUrl(usersUrl);
        userRestService.setProcessRoleRestUrl(processRolesUrl);
        return userRestService;
    }

    @Test
    public void test_findAll() {

        User user1 = new User();
        user1.setPassword("user1");
        User user2 = new User();
        user2.setPassword("user2");

        List<User> userList = asList(user1, user2);
        setupGetWithRestResultExpectations(usersUrl + "/findAll/", userListType(), userList, OK);

        List<User> users = service.findAll().getSuccessObject();
        assertEquals(2, users.size());
        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
    }

    @Test
    public void findExistingUserByEmailShouldReturnUserResource() {
        UserResource userResource = newUserResource().withEmail("testemail@email.com").build();

        List<UserResource> userResourceList = singletonList(userResource);
        setupGetWithRestResultExpectations(usersUrl + "/findByEmail/" + userResource.getEmail() + "/", userResourceListType(), userResourceList, OK);

        List<UserResource> users = service.findUserByEmail(userResource.getEmail()).getSuccessObject();
        assertEquals(1, users.size());
        assertEquals(userResource, users.get(0));
    }

    @Test
    public void findingNonExistingUserByEmailShouldReturnEmptyList() {
        String email = "email@test.test";

        setupGetWithRestResultExpectations(usersUrl + "/findByEmail/" + email + "/", userResourceListType(), emptyList(), OK);

        List<UserResource> users = service.findUserByEmail(email).getSuccessObject();
        assertTrue(users.isEmpty());
    }

    @Test
    public void searchingByEmptyUserEmailShouldReturnNull() {
        String email = "";
        List<UserResource> users = service.findUserByEmail(email).getSuccessObject();
        assertEquals(0, users.size());
    }

    @Test
    public void createLeadApplicantForOrganisation() {
        UserResource userResource = newUserResource()
                .with(id(null))
                .withEmail("testemail@test.test")
                .withTitle("testTitle")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPassword("testPassword")
                .withPhoneNumber("1234567890")
                .build();

        Long organisationId = 1L;

        ResourceEnvelope<UserResource> userResourceEnvelope = new ResourceEnvelope<>("OK", new ArrayList<>(), userResource);
        setupPostWithRestResultExpectations(usersUrl + "/createLeadApplicantForOrganisation/" + organisationId, new ParameterizedTypeReference<ResourceEnvelope<UserResource>>() {}, userResource, userResourceEnvelope, OK);

        ResourceEnvelope<UserResource> receivedResourceEnvelope = service.createLeadApplicantForOrganisation(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                organisationId
        ).getSuccessObject();

        assertEquals(userResourceEnvelope.getEntity(), receivedResourceEnvelope.getEntity());
    }
}
