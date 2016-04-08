package com.worth.ifs.profile;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProfileControllerTest extends BaseUnitTest {
    @InjectMocks
    private ProfileController profileController;

    UserResource user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                .setViewResolvers(viewResolver())
                .build();

        user = newUserResource()
                .withTitle("title")
                .withFirstName("firstname")
                .withLastName("lastname")
                .withPhoneNumber("1234567890")
                .withEmail("email@provider.com")
                .build();
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(user);
    }

    @Test
    public void userProfileDetailsAreAddedToModelWhenViewingDetails() throws Exception {
        mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("title", Matchers.equalTo(user.getTitle()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("firstName", Matchers.equalTo(user.getFirstName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("lastName", Matchers.equalTo(user.getLastName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("phoneNumber", Matchers.equalTo(user.getPhoneNumber()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("email", Matchers.equalTo(user.getEmail()))));
    }

    @Test
    public void userProfileDetailsAreAddedToModelWhenViewingDetailsForm() throws Exception {
        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("title", Matchers.equalTo(user.getTitle()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("firstName", Matchers.equalTo(user.getFirstName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("lastName", Matchers.equalTo(user.getLastName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("phoneNumber", Matchers.equalTo(user.getPhoneNumber()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("email", Matchers.equalTo(user.getEmail()))));
    }

    @Test
    public void userServiceSaveMethodIsCalledWhenSubmittingValidDetailsForm() throws Exception {

        when(userService.updateDetails(user.getId(), user.getEmail(), "newfirstname", "newlastname", "Mrs", "0987654321")).thenReturn(restSuccess(newUserResource().build()));
        mockMvc.perform(post("/profile/edit")
                        .param("title", "Mrs")
                        .param("firstName", "newfirstname")
                        .param("lastName", "newlastname")
                        .param("phoneNumber", "0987654321")
        );

        verify(userService, times(1)).updateDetails(
                user.getId(),
                user.getEmail(),
                "newfirstname",
                "newlastname",
                "Mrs",
                "0987654321");
    }

    @Test
    public void userServiceSaveMethodIsNotCalledWhenSubmittingInvalidDetailsForm() throws Exception {
        mockMvc.perform(post("/profile/edit")
                        .param("title", "illegalcharacters:!@#$%^&*()")
                        .param("firstName", "illegalcharacters:!@#$%^&*()")
                        .param("lastName", "illegalcharacters:!@#$%^&*()")
                        .param("phoneNumber", "illegalcharacters:!@#$%^&*()")
        );

        verify(userService, times(0)).updateDetails(
                isA(Long.class),
                isA(String.class),
                isA(String.class),
                isA(String.class),
                isA(String.class),
                isA(String.class));
    }

    @Test
    public void whenSubmittingAValidFormTheUserProfileDetailsViewIsReturned() throws Exception {

        when(userService.updateDetails(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getTitle(), user.getPhoneNumber())).thenReturn(restSuccess(newUserResource().build()));

        mockMvc.perform(post("/profile/edit")
                        .param("title", user.getTitle())
                        .param("firstName", user.getFirstName())
                        .param("lastName", user.getLastName())
                        .param("phoneNumber", user.getPhoneNumber())

        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile/user-profile"));
    }

    @Test
    public void whenSubmittingAnInvalidFormTheUserProfileDetailsFormIsReturned() throws Exception {
        mockMvc.perform(post("/profile/edit")
                        .param("title", "illegalcharacters:!@#$%^&*()")
                        .param("firstName", "illegalcharacters:!@#$%^&*()")
                        .param("lastName", "illegalcharacters:!@#$%^&*()")
                        .param("phoneNumber", "illegalcharacters:!@#$%^&*()")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile/edit-user-profile"));
    }

    @Test
    public void userServiceResponseErrorsAreAddedTheModel() throws Exception {

        Error error = new Error("errorname", "errordescription", BAD_REQUEST);
        when(userService.updateDetails(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getTitle(), user.getPhoneNumber())).thenReturn(restFailure(error));

        mockMvc.perform(post("/profile/edit")
                        .param("title", user.getTitle())
                        .param("firstName", user.getFirstName())
                        .param("lastName", user.getLastName())
                        .param("phoneNumber", user.getPhoneNumber())

        )
                .andExpect(model().hasErrors());
    }
}