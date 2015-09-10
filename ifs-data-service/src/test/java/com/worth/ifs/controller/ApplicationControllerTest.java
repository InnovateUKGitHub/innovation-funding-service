package com.worth.ifs.controller;

import com.worth.ifs.domain.*;
import com.worth.ifs.repository.ApplicationRepository;
import com.worth.ifs.repository.UserApplicationRoleRepository;
import com.worth.ifs.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationControllerTest {
    @InjectMocks
    private ApplicationController applicationController;

    @Mock
    ApplicationRepository applicationRepositoryMock;
    @Mock
    UserApplicationRoleRepository userAppRoleRepositoryMock;
    @Mock
    UserRepository userRepositoryMock;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController)
                .build();
    }

    @Test
     public void applicationControllerShouldReturnApplicationById() throws Exception {
        Application testApplication1 = new Application(null, "testApplication1Name", null, null, 1L);
        Application testApplication2 = new Application(null, "testApplication2Name", null, null, 2L);

        when(applicationRepositoryMock.findById(1L)).thenReturn(testApplication1);
        when(applicationRepositoryMock.findById(2L)).thenReturn(testApplication2);

        mockMvc.perform(get("/application/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("testApplication1Name")))
                .andExpect(jsonPath("id", is(1)));
        mockMvc.perform(get("/application/id/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is("testApplication2Name")))
                .andExpect(jsonPath("id", is(2)));
    }

    @Test
    public void applicationControllerShouldReturnApplicationByUserId() throws Exception {
        User testUser2 = new User(2L, "testUser2",  "email2@email.nl", "password", "test/image/url/2", "testToken456def", null);
        User testUser1 = new User(1L, "testUser1",  "email1@email.nl", "password", "test/image/url/1", "testToken123abc", null);

        Application testApplication1 = new Application(null, "testApplication1Name", null, null, 1L);
        Application testApplication2 = new Application(null, "testApplication2Name", null, null, 2L);
        Application testApplication3 = new Application(null, "testApplication3Name", null, null, 3L);

        Organisation organisation1 = new Organisation(1L, "test organisation 1");
        Organisation organisation2 = new Organisation(2L, "test organisation 2");

        UserApplicationRole testUserApplicationRole1 = new UserApplicationRole(0L, testUser1, testApplication1, new Role(), organisation1);
        UserApplicationRole testUserApplicationRole2 = new UserApplicationRole(1L, testUser1, testApplication2, new Role(), organisation1);
        UserApplicationRole testUserApplicationRole3 = new UserApplicationRole(2L, testUser2, testApplication2, new Role(), organisation2);
        UserApplicationRole testUserApplicationRole4 = new UserApplicationRole(3L, testUser2, testApplication3, new Role(), organisation2);

        when(userRepositoryMock.findById(1L)).thenReturn(new ArrayList<User>() {{
            add(testUser1);
        }});

        when(userRepositoryMock.findById(2L)).thenReturn(new ArrayList<User>() {{
            add(testUser2);
        }});

        when(userAppRoleRepositoryMock.findByUser(testUser1)).thenReturn(new ArrayList<UserApplicationRole>() {{
            add(testUserApplicationRole1);
            add(testUserApplicationRole2);
        }});

        when(userAppRoleRepositoryMock.findByUser(testUser2)).thenReturn(new ArrayList<UserApplicationRole>() {{
            add(testUserApplicationRole3);
            add(testUserApplicationRole4);
        }});

        mockMvc.perform(get("/application/findByUser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]name", is("testApplication1Name")))
                .andExpect(jsonPath("[0]id", is(1)))
                .andExpect(jsonPath("[1]name", is("testApplication2Name")))
                .andExpect(jsonPath("[1]id", is(2)));
        mockMvc.perform(get("/application/findByUser/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]name", is("testApplication2Name")))
                .andExpect(jsonPath("[0]id", is(2)))
                .andExpect(jsonPath("[1]name", is("testApplication3Name")))
                .andExpect(jsonPath("[1]id", is(3)));
    }

    @Test
    public void applicationControllerShouldReturnAllApplications() throws Exception {

        List<Application> applications = new ArrayList<Application>();
        applications.add(new Application(null, "testApplication1Name", null, null, 1L));
        applications.add(new Application(null, "testApplication2Name", null, null, 2L));
        applications.add(new Application(null, "testApplication3Name", null, null, 3L));

        when(applicationRepositoryMock.findAll()).thenReturn(applications);
        mockMvc.perform(get("/application/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]name", is("testApplication1Name")))
                .andExpect(jsonPath("[0]id", is(1)))
                .andExpect(jsonPath("[1]name", is("testApplication2Name")))
                .andExpect(jsonPath("[1]id", is(2)))
                .andExpect(jsonPath("[2]name", is("testApplication3Name")))
                .andExpect(jsonPath("[2]id", is(3)));
    }
}