package com.worth.ifs.controller;

import com.worth.ifs.domain.Application;
import com.worth.ifs.domain.User;
import com.worth.ifs.repository.UserRepository;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    UserRepository userRepositoryMock;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .build();
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
                .andExpect(jsonPath("[0]id",        is((int)testUser1.getId())))
                .andExpect(jsonPath("[0]name",      is(testUser1.getName())))
                .andExpect(jsonPath("[0]imageUrl",  is(testUser1.getImageUrl())))
                .andExpect(jsonPath("[0]token",     is(testUser1.getToken())))
                .andExpect(jsonPath("[1]id",        is((int)testUser2.getId())))
                .andExpect(jsonPath("[1]name",      is(testUser2.getName())))
                .andExpect(jsonPath("[1]imageUrl",  is(testUser2.getImageUrl())))
                .andExpect(jsonPath("[1]token",     is(testUser2.getToken())))
                .andExpect(jsonPath("[2]id",        is((int)testUser3.getId())))
                .andExpect(jsonPath("[2]name",      is(testUser3.getName())))
                .andExpect(jsonPath("[2]imageUrl",  is(testUser3.getImageUrl())))
                .andExpect(jsonPath("[2]token",     is(testUser3.getToken())));
    }

    @Test
    public void userControllerShouldReturnUserById() throws Exception {
        User testUser1 = new User(1L, "testUser1",  "email1@email.nl", "password", "testToken123abc", "test/image/url/1", null);

        List<User> userList = new ArrayList<User>();
        userList.add(testUser1);

        when(userRepositoryMock.findById(testUser1.getId())).thenReturn(userList);
        mockMvc.perform(get("/user/id/"+testUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id",        is((int)testUser1.getId())))
                .andExpect(jsonPath("name",      is(testUser1.getName())))
                .andExpect(jsonPath("imageUrl",  is(testUser1.getImageUrl())))
                .andExpect(jsonPath("token",     is(testUser1.getToken())));
    }

    @Test
    public void userControllerShouldReturnUserByToken() throws Exception {
        User testUser1 = new User(1L, "testUser1",  "email1@email.nl", "password", "testToken123abc", "test/image/url/1", null);

        List<User> userList = Arrays.asList(testUser1);

        when(userRepositoryMock.findByToken(testUser1.getToken())).thenReturn(userList);

        mockMvc.perform(get("/user/token/"+testUser1.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id",        is((int)testUser1.getId())))
                .andExpect(jsonPath("name",      is(testUser1.getName())))
                .andExpect(jsonPath("imageUrl",  is(testUser1.getImageUrl())))
                .andExpect(jsonPath("token",    is(testUser1.getToken())));

    }
}