package org.innovateuk.ifs.profile.controller;

import org.hamcrest.Matchers;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.profile.populator.UserProfilePopulator;
import org.innovateuk.ifs.profile.viewmodel.UserProfileViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Title.Mrs;
import static org.innovateuk.ifs.user.resource.Title.Ms;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProfileControllerTest extends BaseControllerMockMVCTest<ProfileController> {

    @Override
    protected ProfileController supplyControllerUnderTest() {
        return new ProfileController();
    }

    @Mock
    private UserProfilePopulator userProfilePopulator;

    @Mock
    private UserService userService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    private UserResource user;

    @Before
    public void setUp() {
        super.setUp();

        user = newUserResource()
                .withTitle(Ms)
                .withFirstName("firstname")
                .withLastName("lastname")
                .withPhoneNumber("1234567890")
                .withEmail("email@provider.com")
                .build();
        setLoggedInUser(user);
    }

    @Test
    public void viewProfile() throws Exception {
        UserProfileViewModel expected = mock(UserProfileViewModel.class);
        when(userProfilePopulator.populate(user)).thenReturn(expected);
        MvcResult result = mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        UserProfileViewModel actual = (UserProfileViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expected, actual);
    }

    @Test
    public void userProfileDetailsAreAddedToModelWhenViewingDetailsForm() throws Exception {
        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("firstName", Matchers.equalTo(user.getFirstName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("lastName", Matchers.equalTo(user.getLastName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("phoneNumber", Matchers.equalTo(user.getPhoneNumber()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("email", Matchers.equalTo(user.getEmail()))));
    }

    @Test
    public void userServiceSaveMethodIsCalledWhenSubmittingValidDetailsForm() throws Exception {

        when(userService.updateDetails(user.getId(), user.getEmail(), "newfirstname", "newlastname",
                "Mrs", "0987654321", false))
                .thenReturn(ServiceResult.serviceSuccess(newUserResource().build()));
        mockMvc.perform(post("/profile/edit")
                .param("title", Mrs.toString())
                .param("firstName", "newfirstname")
                .param("lastName", "newlastname")
                .param("phoneNumber", "0987654321")
                .param("allowMarketingEmails", Boolean.FALSE.toString())

        );

        verify(userService, times(1)).updateDetails(
                eq(user.getId()),
                eq(user.getEmail()),
                eq("newfirstname"),
                eq("newlastname"),
                any(),
                eq("0987654321"),
                eq(false));
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
                isA(String.class),
                isA(Boolean.class));
    }

    @Test
    public void whenSubmittingAValidFormTheUserProfileDetailsViewIsReturned() throws Exception {

        when(userService.updateDetails(eq(user.getId()), eq(user.getEmail()), eq(user.getFirstName()), eq(user.getLastName()), anyString(),
                eq(user.getPhoneNumber()),
                anyBoolean()))
                .thenReturn(ServiceResult.serviceSuccess(newUserResource().build()));
        UserResource newUser = newUserResource().build();
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class), eq(true))).thenReturn(newUser);
        mockMvc.perform(post("/profile/edit")
                .param("title", user.getTitle().name())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("phoneNumber", user.getPhoneNumber())
                .param("allowMarketingEmails", Boolean.FALSE.toString())
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile/user-profile"));
    }

    @Test
    public void whenSubmittingAnInvalidFormTheUserProfileDetailsFormIsReturned() throws Exception {
        mockMvc.perform(post("/profile/edit")
                .param("firstName", "illegalcharacters:!@#$%^&*()")
                .param("lastName", "illegalcharacters:!@#$%^&*()")
                .param("phoneNumber", "illegalcharacters:!@#$%^&*()")
                .param("allowMarketingEmails", Boolean.FALSE.toString())
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile/edit-user-profile"));
    }

    @Test
    public void userServiceResponseErrorsAreAddedTheModel() throws Exception {

        Error error = new Error("objectName", singletonList("fieldName"), BAD_REQUEST);
        when(userService.updateDetails(eq(user.getId()), eq(user.getEmail()), eq(user.getFirstName()), eq(user.getLastName()), anyString(),
                eq(user.getPhoneNumber()), anyBoolean()))
                .thenReturn(ServiceResult.serviceFailure(error));

        mockMvc.perform(post("/profile/edit")
                .param("title", user.getTitle().name())
                .param("firstName", user.getFirstName())
                .param("lastName", user.getLastName())
                .param("phoneNumber", user.getPhoneNumber())
                .param("allowMarketingEmails", Boolean.FALSE.toString())
        )
                .andExpect(model().hasErrors());
    }
}
