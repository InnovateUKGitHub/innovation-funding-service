package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserResourceEnvelope;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;


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

        User[] userList = new User[]{user1, user2};
        ResponseEntity<User[]> responseEntity = new ResponseEntity<>(userList, HttpStatus.OK);
        when(mockRestTemplate.exchange(dataServicesUrl + usersUrl + "/findAll/", HttpMethod.GET, httpEntityForRestCall(), User[].class)).thenReturn(responseEntity);

        List<User> users = service.findAll();
        assertEquals(2, users.size());
        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
    }

    @Test
    public void findExistingUserByEmailShouldReturnUserResource() {
        UserResource userResource = newUserResource().withEmail("testemail@email.com").build();

        UserResource[] userResourceList = new UserResource[]{userResource};
        ResponseEntity<UserResource[]> responseEntity = new ResponseEntity<>(userResourceList, HttpStatus.OK);
        when(mockRestTemplate.exchange(dataServicesUrl + usersUrl + "/findByEmail/" + userResource.getEmail() + "/", HttpMethod.GET, httpEntityForRestCall(), UserResource[].class)).thenReturn(responseEntity);

        List<UserResource> users = service.findUserByEmail(userResource.getEmail());
        assertEquals(1, users.size());
        assertEquals(userResource, users.get(0));
    }

    @Test
    public void findingNonExistingUserByEmailShouldReturnEmptyList() {
        String email = "email@test.test";

        UserResource[] userResourceList = new UserResource[]{};
        ResponseEntity<UserResource[]> responseEntity = new ResponseEntity<>(userResourceList, HttpStatus.OK);
        when(mockRestTemplate.exchange(dataServicesUrl + usersUrl + "/findByEmail/" + email + "/", HttpMethod.GET, httpEntityForRestCall(), UserResource[].class)).thenReturn(responseEntity);

        List<UserResource> users = service.findUserByEmail(email);
        assertTrue(users.isEmpty());
    }

    @Test
    public void searchingByEmptyUserEmailShouldReturnNull() {
        String email = "";

        UserResource[] userResourceList = new UserResource[]{};
        ResponseEntity<UserResource[]> responseEntity = new ResponseEntity<>(userResourceList, HttpStatus.OK);
        when(mockRestTemplate.exchange(dataServicesUrl + usersUrl + "/findByEmail/" + email + "/", HttpMethod.GET, httpEntityForRestCall(), UserResource[].class)).thenReturn(responseEntity);

        List<UserResource> users = service.findUserByEmail(email);
        assertTrue(users == null);
    }

    @Test
    public void createLeadApplicantForOrganisation() {
        UserResource userResource = newUserResource().withId(1L)
                .withEmail("testemail@test.test")
                .withTitle("testTitle")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPhoneNumber("1234567890")
                .build();

        Long organisationId = 1L;

        ResourceEnvelope<UserResource> userResourceEnvelope = new ResourceEnvelope<>("OK", new ArrayList<>(), userResource);
        UserResourceEnvelope resourceEnvelope = new UserResourceEnvelope(userResourceEnvelope);

        ResponseEntity<UserResourceEnvelope> responseEntity = new ResponseEntity<>(resourceEnvelope, HttpStatus.OK);
        when(mockRestTemplate.postForEntity(eq(dataServicesUrl + usersUrl + "/createLeadApplicantForOrganisation/" + organisationId), isA(HttpEntity.class), eq(UserResourceEnvelope.class))).thenReturn(responseEntity);

        ResourceEnvelope<UserResource> receivedResourceEnvelope = service.createLeadApplicantForOrganisation(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                organisationId
        );

        assertEquals(userResourceEnvelope.getEntity(), receivedResourceEnvelope.getEntity());
    }
}
