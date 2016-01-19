package com.worth.ifs.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.transactional.ServiceResult.failure;
import static com.worth.ifs.transactional.ServiceResult.success;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.transactional.RegistrationServiceImpl.ServiceFailures.DUPLICATE_EMAIL_ADDRESS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserControllerTest extends BaseControllerMockMVCTest<UserController> {

    @Override
    protected UserController supplyControllerUnderTest() {
        return new UserController();
    }

    @Test
    public void userControllerShouldReturnAllUsers() throws Exception {
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "test/image/url/1", null, "my-uid");
        User testUser2 = new User(2L, "testUser2", "email2@email.nl", "password", "test/image/url/2", null, "my-uid2");
        User testUser3 = new User(3L, "testUser3", "email3@email.nl", "password", "test/image/url/3", null, "my-uid3");

        List<User> users = new ArrayList<>();
        users.add(testUser1);
        users.add(testUser2);
        users.add(testUser3);

        when(userRepositoryMock.findAll()).thenReturn(users);
        mockMvc.perform(get("/user/findAll/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("[0]name", is(testUser1.getName())))
                .andExpect(jsonPath("[0]imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("[0]uid", is(testUser1.getUid())))
                .andExpect(jsonPath("[1]id", is((Number) testUser2.getId().intValue())))
                .andExpect(jsonPath("[1]name", is(testUser2.getName())))
                .andExpect(jsonPath("[1]imageUrl", is(testUser2.getImageUrl())))
                .andExpect(jsonPath("[1]uid", is(testUser2.getUid())))
                .andExpect(jsonPath("[2]id", is((Number) testUser3.getId().intValue())))
                .andExpect(jsonPath("[2]name", is(testUser3.getName())))
                .andExpect(jsonPath("[2]imageUrl", is(testUser3.getImageUrl())))
                .andExpect(jsonPath("[2]uid", is(testUser3.getUid())))
                .andDo(document("user/get-all-users"));
    }

    @Test
    public void userControllerShouldReturnUserById() throws Exception {
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "test/image/url/1", null, "my-uid");

        when(userRepositoryMock.findOne(testUser1.getId())).thenReturn(testUser1);
        mockMvc.perform(get("/user/id/" + testUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("name", is(testUser1.getName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("uid", is(testUser1.getUid())))
                .andDo(document("user/get-user"));
    }

    @Test
    public void userControllerShouldReturnUserByUid() throws Exception {
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "test/image/url/1", null, "my-uid");

        when(userRepositoryMock.findOneByUid(testUser1.getUid())).thenReturn(testUser1);

        mockMvc.perform(get("/user/uid/" + testUser1.getUid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("name", is(testUser1.getName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("uid", is(testUser1.getUid())))
                .andDo(document("user/get-user-by-token"));

    }

    @Test
    public void userControllerReturnUserResourceOfCreatedUserAfterUserCreation() throws Exception {

        UserResource userResource = newUserResource().withEmail("testemail@email.email")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPhoneNumber("1234567890")
                .withPassword("testPassword")
                .withTitle("Mr")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String applicationJsonString = mapper.writeValueAsString(userResource);

        Long organisationId = 1L;
        Long roleId = 1L;

        User user = new User();
        user.setEmail("testemail@email.email");
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setPhoneNumber("testPhoneNumber");
        user.setPassword("testPassword");
        user.setName("testFirstName testLastName");
        user.setTitle("Mr");

        when(registrationServiceMock.createUserLeadApplicantForOrganisation(organisationId, userResource)).thenReturn(success(user));

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/" + organisationId, "json")
                .contentType(APPLICATION_JSON)
                .content(applicationJsonString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(ResourceEnvelopeConstants.OK.getName())))
                .andExpect(jsonPath("$.entity.firstName", notNullValue()))
                .andExpect(jsonPath("$.entity.lastName", notNullValue()))
                .andExpect(jsonPath("$.entity.phoneNumber", notNullValue()))
                .andExpect(jsonPath("$.entity.title", notNullValue()))
                .andExpect(jsonPath("$.entity.password", notNullValue()))
                .andExpect(jsonPath("$.entity.email", notNullValue()))
                .andExpect(jsonPath("$.entity.name", is(user.getFirstName() + " " + user.getLastName()))
                );

        verify(registrationServiceMock).createUserLeadApplicantForOrganisation(organisationId, userResource);
    }

    @Test
    public void userControllerShouldReturnListOfSingleUserWhenFoundByEmail() throws Exception {
        User user = new User();
        user.setEmail("testemail@email.email");
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setPhoneNumber("testPhoneNumber");
        user.setPassword("testPassword");
        user.setName("testFirstName testLastName");
        user.setTitle("Mr");

        List<User> users = new ArrayList<>();
        users.add(user);

        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(users);

        mockMvc.perform(get("/user/findByEmail/" + user.getEmail() + "/", "json")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].email", is(user.getEmail())))
                .andExpect(jsonPath("[1].email").doesNotExist()
                );
    }

    @Test
    public void userControllerShouldReturnEmptyListWhenNoUserIsFoundByEmail() throws Exception {
        List<User> users = new ArrayList<>();

        String email = "testemail@email.com";

        when(userRepositoryMock.findByEmail(email)).thenReturn(users);

        mockMvc.perform(get("/user/findByEmail/" + email + "/", "json")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());


    }

    @Test
    public void userControllerShouldReturnErrorWhenEmailAddressIsTaken() throws Exception {

        UserResource userResource = newUserResource().withEmail("testemail@email.email")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPhoneNumber("1234567890")
                .withPassword("testPassword")
                .withTitle("Mr")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String applicationJsonString = mapper.writeValueAsString(userResource);

        Long organisationId = 1L;

        User user = new User();

        List<User> users = new ArrayList<User>();
        users.add(user);

        when(registrationServiceMock.createUserLeadApplicantForOrganisation(organisationId, userResource)).thenReturn(failure(DUPLICATE_EMAIL_ADDRESS));

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/" + organisationId, "json")
                .contentType(APPLICATION_JSON)
                .content(applicationJsonString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(ResourceEnvelopeConstants.ERROR.getName()))
                );
    }
}