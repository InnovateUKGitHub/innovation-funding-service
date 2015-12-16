package com.worth.ifs.registration;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;

import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RegistrationControllerTest extends BaseUnitTest {
    @InjectMocks
    private RegistrationController registrationController;

    @Before
    public void setUp(){
        super.setup();
        setupUserRoles();

        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setViewResolvers(viewResolver())
                .build();
    }

    @Test
    public void onGetRequestRegistrationViewIsReturned() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(get("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
        ;
    }

    @Test
    public void emptyFormInputsShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "")
                        .param("password", "")
                        .param("retypedPassword", "")
                        .param("title", "")
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("phoneNumber", "")
                        .param("termsAndConditions", "")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "title"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "firstName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "lastName"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "phoneNumber"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"))
        ;
    }

    @Test
    public void invalidEmailFormatShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "invalid email format")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "email"))
        ;
    }

    @Test
    public void incorrectPasswordSizeShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "12345")
                        .param("retypedPassword", "123456789012345678901234567890123")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "password"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
        ;
    }

    @Test
    public void unmatchedPasswordAndRetypePasswordShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "12345678")
                        .param("retypedPassword", "123456789")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "retypedPassword"))
        ;
    }

    @Test
    public void uncheckedTermsAndConditionsCheckboxShouldReturnError() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();
        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration-register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "termsAndConditions"))
        ;
    }

    @Test
    public void validFormInputShouldInitiateCreateUserServiceCall() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();

        UserResource userResource = new UserResource();
        userResource.setPassword("testtest");
        userResource.setFirstName("firstName");
        userResource.setLastName("lastName");
        userResource.setTitle("Mr");
        userResource.setPhoneNumber("0123456789");
        userResource.setEmail("test@test.test");
        userResource.setToken("testToken123abc");
        userResource.setId(1L);


        ResourceEnvelope<UserResource> envelope = new ResourceEnvelope<>("OK", new ArrayList<>(), userResource);


        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);
        when(userService.createUserForOrganisation(userResource.getFirstName(),
                userResource.getLastName(),
                userResource.getPassword(),
                userResource.getEmail(),
                userResource.getTitle(),
                userResource.getPhoneNumber(),
                1L,
                "applicant")).thenReturn(envelope);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", userResource.getEmail())
                        .param("password", userResource.getPassword())
                        .param("retypedPassword", userResource.getPassword())
                        .param("title", userResource.getTitle())
                        .param("firstName", userResource.getFirstName())
                        .param("lastName", userResource.getLastName())
                        .param("phoneNumber", userResource.getPhoneNumber())
                        .param("termsAndConditions", "1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().value("userId", "1"))
                .andExpect(view().name("redirect:/application/create/initialize-application/"))
        ;
        verify(tokenAuthenticationService).addAuthentication(Matchers.isA(HttpServletResponse.class), Matchers.isA(UserResource.class));
    }

    @Test
    public void correctOrganisationNameIsAddedToModel() throws Exception {
        Organisation organisation = newOrganisation().withId(4L).withName("uniqueOrganisationName").build();

        when(organisationService.getOrganisationById(4L)).thenReturn(organisation);
        mockMvc.perform(post("/registration/register?organisationId=4")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(model().attribute("organisationName", "uniqueOrganisationName"));
    }
}