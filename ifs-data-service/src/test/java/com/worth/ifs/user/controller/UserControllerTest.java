package com.worth.ifs.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.commons.error.CommonFailureKeys.USERS_DUPLICATE_EMAIL_ADDRESS;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
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
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "testToken123abc", "test/image/url/1", null);
        User testUser2 = new User(2L, "testUser2", "email2@email.nl", "password", "testToken456def", "test/image/url/2", null);
        User testUser3 = new User(3L, "testUser3", "email3@email.nl", "password", "testToken789ghi", "test/image/url/3", null);

        List<User> users = new ArrayList<>();
        users.add(testUser1);
        users.add(testUser2);
        users.add(testUser3);

        when(userServiceMock.findAll()).thenReturn(serviceSuccess(users));
        mockMvc.perform(get("/user/findAll/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("[0]name", is(testUser1.getName())))
                .andExpect(jsonPath("[0]imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("[0]token", is(testUser1.getToken())))
                .andExpect(jsonPath("[1]id", is((Number) testUser2.getId().intValue())))
                .andExpect(jsonPath("[1]name", is(testUser2.getName())))
                .andExpect(jsonPath("[1]imageUrl", is(testUser2.getImageUrl())))
                .andExpect(jsonPath("[1]token", is(testUser2.getToken())))
                .andExpect(jsonPath("[2]id", is((Number) testUser3.getId().intValue())))
                .andExpect(jsonPath("[2]name", is(testUser3.getName())))
                .andExpect(jsonPath("[2]imageUrl", is(testUser3.getImageUrl())))
                .andExpect(jsonPath("[2]token", is(testUser3.getToken())))
                .andDo(document("user/get-all-users"));
    }

    @Test
    public void userControllerShouldReturnUserById() throws Exception {
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "testToken123abc", "test/image/url/1", null);

        when(userServiceMock.getUserById(testUser1.getId())).thenReturn(serviceSuccess(testUser1));
        mockMvc.perform(get("/user/id/" + testUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("name", is(testUser1.getName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("token", is(testUser1.getToken())))
                .andDo(document("user/get-user"));
    }

    @Test
    public void userControllerShouldReturnUserByToken() throws Exception {
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "testToken123abc", "test/image/url/1", null);

        when(userServiceMock.getUserByToken(testUser1.getToken())).thenReturn(serviceSuccess(testUser1));

        mockMvc.perform(get("/user/token/" + testUser1.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is((Number) testUser1.getId().intValue())))
                .andExpect(jsonPath("name", is(testUser1.getName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("token", is(testUser1.getToken())))
                .andDo(document("user/get-user-by-token"));

    }

    @Test
    public void userControllerReturnUserResourceOfCreatedUserAfterUserCreation() throws Exception {

        UserResource userResource = newUserResource().withEmail("testemail@email.email")
                .withName("testFirstName testLastName")
                .withFirstName("testFirstName")
                .withLastName("testLastName")
                .withPhoneNumber("1234567890")
                .withPassword("testPassword")
                .withTitle("Mr")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String applicationJsonString = mapper.writeValueAsString(userResource);

        Long organisationId = 1L;

        when(userServiceMock.createUser(organisationId, userResource)).thenReturn(serviceSuccess(userResource));

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/" + organisationId, "json")
                .contentType(APPLICATION_JSON)
                .content(applicationJsonString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", notNullValue()))
                .andExpect(jsonPath("$.lastName", notNullValue()))
                .andExpect(jsonPath("$.phoneNumber", notNullValue()))
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.password", notNullValue()))
                .andExpect(jsonPath("$.email", notNullValue()))
                .andExpect(jsonPath("$.name", is(userResource.getFirstName() + " " + userResource.getLastName()))
                );
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

        when(userServiceMock.findByEmail(user.getEmail())).thenReturn(serviceSuccess(singletonList(new UserResource(user))));

        mockMvc.perform(get("/user/findByEmail/" + user.getEmail() + "/", "json")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].email", is(user.getEmail())))
                .andExpect(jsonPath("[1].email").doesNotExist()
                );
    }

    @Test
    public void userControllerShouldReturnEmptyListWhenNoUserIsFoundByEmail() throws Exception {

        String email = "testemail@email.com";

        when(userServiceMock.findByEmail(email)).thenReturn(serviceSuccess(emptyList()));

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

        when(userServiceMock.createUser(organisationId, userResource)).thenReturn(serviceFailure(new Error(USERS_DUPLICATE_EMAIL_ADDRESS)));

        mockMvc.perform(post("/user/createLeadApplicantForOrganisation/" + organisationId, "json")
                .contentType(APPLICATION_JSON)
                .content(applicationJsonString))
                .andExpect(status().isConflict());
    }
}