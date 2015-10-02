package com.worth.ifs.user.controller;

import com.worth.ifs.BaseControllerMocksTest;
import com.worth.ifs.user.domain.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



public class UserControllerTest extends BaseControllerMocksTest<UserController> {

    @Override
    protected UserController supplyControllerUnderTest() {
        return new UserController();
    }

    @Test
    public void userControllerShouldReturnAllUsers() throws Exception {
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "testToken123abc", "test/image/url/1", null);
        User testUser2 = new User(2L, "testUser2", "email2@email.nl", "password", "testToken456def", "test/image/url/2", null);
        User testUser3 = new User(3L, "testUser3", "email3@email.nl", "password", "testToken789ghi", "test/image/url/3", null);

        List<User> users = new ArrayList<User>();
        users.add(testUser1);
        users.add(testUser2);
        users.add(testUser3);

        when(userRepositoryMock.findAll()).thenReturn(users);
        mockMvc.perform(get("/user/findAll/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id",        is((Number)testUser1.getId().intValue())))
                .andExpect(jsonPath("[0]name",      is(testUser1.getName())))
                .andExpect(jsonPath("[0]imageUrl",  is(testUser1.getImageUrl())))
                .andExpect(jsonPath("[0]token",     is(testUser1.getToken())))
                .andExpect(jsonPath("[1]id",        is((Number)testUser2.getId().intValue())))
                .andExpect(jsonPath("[1]name",      is(testUser2.getName())))
                .andExpect(jsonPath("[1]imageUrl",  is(testUser2.getImageUrl())))
                .andExpect(jsonPath("[1]token",     is(testUser2.getToken())))
                .andExpect(jsonPath("[2]id",        is((Number)testUser3.getId().intValue())))
                .andExpect(jsonPath("[2]name",      is(testUser3.getName())))
                .andExpect(jsonPath("[2]imageUrl",  is(testUser3.getImageUrl())))
                .andExpect(jsonPath("[2]token",     is(testUser3.getToken())));
    }

    @Test
    public void userControllerShouldReturnUserById() throws Exception {
        User testUser1 = new User(1L, "testUser1",  "email1@email.nl", "password", "testToken123abc", "test/image/url/1", null);

        when(userRepositoryMock.findOne(testUser1.getId())).thenReturn(testUser1);
        mockMvc.perform(get("/user/id/"+testUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id",        is((Number)testUser1.getId().intValue())))
                .andExpect(jsonPath("name", is(testUser1.getName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("token", is(testUser1.getToken())));
    }

    @Test
    public void userControllerShouldReturnUserByToken() throws Exception {
        User testUser1 = new User(1L, "testUser1",  "email1@email.nl", "password", "testToken123abc", "test/image/url/1", null);

        List<User> userList = Arrays.asList(testUser1);

        when(userRepositoryMock.findByToken(testUser1.getToken())).thenReturn(userList);

        mockMvc.perform(get("/user/token/"+testUser1.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id",        is((Number)testUser1.getId().intValue())))
                .andExpect(jsonPath("name", is(testUser1.getName())))
                .andExpect(jsonPath("imageUrl", is(testUser1.getImageUrl())))
                .andExpect(jsonPath("token", is(testUser1.getToken())));

    }
}