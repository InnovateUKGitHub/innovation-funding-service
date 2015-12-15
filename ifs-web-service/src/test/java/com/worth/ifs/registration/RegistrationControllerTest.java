package com.worth.ifs.registration;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
    public void validFormInputShouldInitiateCreateUserServiceCall() throws Exception {
        Organisation organisation = newOrganisation().withId(1L).withName("Organisation 1").build();

        UserDto userDto = new UserDto();
        userDto.setPassword("testtest");
        userDto.setFirstName("firstName");
        userDto.setLastName("lastName");
        userDto.setTitle("Mr");
        userDto.setPhoneNumber("0123456789");
        userDto.setEmail("test@test.test");


        when(organisationService.getOrganisationById(1L)).thenReturn(organisation);
        when(userService.createUserForOrganisation(userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPassword(),
                userDto.getEmail(),
                userDto.getTitle(),
                userDto.getPhoneNumber(),
                1L,
                "applicant")).thenReturn(userDto);

        mockMvc.perform(post("/registration/register?organisationId=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", userDto.getEmail())
                        .param("password", userDto.getPassword())
                        .param("retypedPassword", userDto.getPassword())
                        .param("title", userDto.getTitle())
                        .param("firstName", userDto.getFirstName())
                        .param("lastName", userDto.getLastName())
                        .param("phoneNumber", userDto.getPhoneNumber())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
        ;
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