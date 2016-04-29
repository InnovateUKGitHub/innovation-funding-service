package com.worth.ifs.profile;

import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.address.builder.AddressResourceBuilder;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;

public class ProfileControllerTest extends BaseUnitTest {
    @InjectMocks
    private ProfileController profileController;

    private UserResource user;
    
    private OrganisationResource organisation;
    
    private OrganisationAddressResource address;

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
                .withOrganisations(Arrays.asList(6L))
                .build();
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(user);
        
        address = new OrganisationAddressResource();
        address.setAddressType(AddressType.REGISTERED);
        address.setAddress(AddressResourceBuilder
        		.newAddressResource()
        		.withAddressLine1("line1")
        		.withAddressLine2("line2")
        		.withAddressLine3("line3")
        		.withTown("town")
        		.withCounty("county")
        		.withPostcode("postcode")
        		.build());
        
        organisation = newOrganisationResource()
        		.withName("orgname")
        		.withCompanyHouseNumber("companyhousenumber")
        		.withAddress(Arrays.asList(address))
        		.build();
        when(organisationService.getOrganisationById(6L)).thenReturn(organisation);
    }

    @Test
    public void userProfileDetailsAreAddedToModelWhenViewingDetails() throws Exception {
        mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("title", Matchers.equalTo(user.getTitle()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("firstName", Matchers.equalTo(user.getFirstName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("lastName", Matchers.equalTo(user.getLastName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("phoneNumber", Matchers.equalTo(user.getPhoneNumber()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("email", Matchers.equalTo(user.getEmail()))))

                .andExpect(model().attribute("organisationDetailsForm", Matchers.hasProperty("name", Matchers.equalTo(organisation.getName()))))
        		.andExpect(model().attribute("organisationDetailsForm", Matchers.hasProperty("companyHouseNumber", Matchers.equalTo(organisation.getCompanyHouseNumber()))))
		        .andExpect(model().attribute("organisationDetailsForm", Matchers.hasProperty("addressLine1", Matchers.equalTo(address.getAddress().getAddressLine1()))))
		        .andExpect(model().attribute("organisationDetailsForm", Matchers.hasProperty("addressLine2", Matchers.equalTo(address.getAddress().getAddressLine2()))))
		        .andExpect(model().attribute("organisationDetailsForm", Matchers.hasProperty("addressLine3", Matchers.equalTo(address.getAddress().getAddressLine3()))))
		        .andExpect(model().attribute("organisationDetailsForm", Matchers.hasProperty("town", Matchers.equalTo(address.getAddress().getTown()))))
		        .andExpect(model().attribute("organisationDetailsForm", Matchers.hasProperty("county", Matchers.equalTo(address.getAddress().getCounty()))))
		        .andExpect(model().attribute("organisationDetailsForm", Matchers.hasProperty("postcode", Matchers.equalTo(address.getAddress().getPostcode()))));

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