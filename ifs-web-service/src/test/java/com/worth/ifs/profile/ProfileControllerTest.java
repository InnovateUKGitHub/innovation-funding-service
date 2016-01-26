package com.worth.ifs.profile;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.resource.ResourceEnvelopeConstants;
import com.worth.ifs.commons.resource.ResourceError;
import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.registration.RegistrationController;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProfileControllerTest extends BaseUnitTest {
    @InjectMocks
    private ProfileController profileController;

    User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                .setViewResolvers(viewResolver())
                .build();

        user = newUser()
                .withTitle("title")
                .withFirstName("firstname")
                .withLastName("lastname")
                .withPhoneNumber("1234567890")
                .withEmailAddress("email@provider.com")
                .build();
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(user);
    }

    @Test
    public void userProfileDetailsAreAddedToModelWhenViewingDetails() throws Exception {
        mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("title",       Matchers.equalTo(user.getTitle()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("firstName", Matchers.equalTo(user.getFirstName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("lastName", Matchers.equalTo(user.getLastName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("phoneNumber", Matchers.equalTo(user.getPhoneNumber()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("email", Matchers.equalTo(user.getEmail()))));
    }

    @Test
    public void userProfileDetailsAreAddedToModelWhenViewingDetailsForm() throws Exception {
        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("title",       Matchers.equalTo(user.getTitle()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("firstName",   Matchers.equalTo(user.getFirstName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("lastName",    Matchers.equalTo(user.getLastName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("phoneNumber", Matchers.equalTo(user.getPhoneNumber()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("email",       Matchers.equalTo(user.getEmail()))));
    }

    @Test
    public void userServiceSaveMethodIsCalledWhenSubmittingValidDetailsForm() throws Exception {
        ResourceEnvelope<UserResource> envelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.OK.getName(), new ArrayList<>(), newUserResource().build());

        when(userService.updateDetails(user.getEmail(), "newfirstname", "newlastname", "Mrs", "0987654321")).thenReturn(envelope);
        mockMvc.perform(post("/profile/edit")
                        .param("title", "Mrs")
                        .param("firstName", "newfirstname")
                        .param("lastName", "newlastname")
                        .param("phoneNumber", "0987654321")
        );

        verify(userService, times(1)).updateDetails(
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
                isA(String.class),
                isA(String.class),
                isA(String.class),
                isA(String.class),
                isA(String.class));
    }

    @Test
    public void whenSubmittingAValidFormTheUserProfileDetailsViewIsReturned() throws Exception {
        ResourceEnvelope<UserResource> envelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.OK.getName(), new ArrayList<>(), newUserResource().build());

        when(userService.updateDetails(user.getEmail(), user.getFirstName(), user.getLastName(), user.getTitle(), user.getPhoneNumber())).thenReturn(envelope);

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
        List<ResourceError> resourceErrors = new ArrayList<>();
        resourceErrors.add(new ResourceError("errorname","errordescription"));

        ResourceEnvelope<UserResource> envelope = new ResourceEnvelope<>(ResourceEnvelopeConstants.ERROR.getName(), resourceErrors, newUserResource().build());

        when(userService.updateDetails(user.getEmail(), user.getFirstName(), user.getLastName(), user.getTitle(), user.getPhoneNumber())).thenReturn(envelope);

        mockMvc.perform(post("/profile/edit")
                        .param("title", user.getTitle())
                        .param("firstName", user.getFirstName())
                        .param("lastName", user.getLastName())
                        .param("phoneNumber", user.getPhoneNumber())

        )
                .andExpect(model().hasErrors());
    }
}