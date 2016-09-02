package com.worth.ifs.profile;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.address.resource.AddressTypeResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static com.worth.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.address.builder.AddressTypeResourceBuilder.newAddressTypeResource;
import static com.worth.ifs.address.resource.OrganisationAddressType.OPERATING;
import static com.worth.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProfileControllerTest extends BaseUnitTest {
    @InjectMocks
    private ProfileController profileController;

    private UserResource user;
    
    private OrganisationResource organisation;
    
    @SuppressWarnings("unchecked")
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = setupMockMvc(profileController, () -> loggedInUser, env, messageSource);

        user = newUserResource()
                .withTitle("title")
                .withFirstName("firstname")
                .withLastName("lastname")
                .withPhoneNumber("1234567890")
                .withEmail("email@provider.com")
                .withOrganisations(singletonList(6L))
                .build();
        when(userAuthenticationService.getAuthenticatedUser(isA(HttpServletRequest.class))).thenReturn(user);
    }
    
	@SuppressWarnings("unchecked")
	private void setupOrganisation(OrganisationAddressResource...addressResources) {
        organisation = newOrganisationResource()
        		.withName("orgname")
        		.withCompanyHouseNumber("companyhousenumber")
        		.withAddress(Arrays.asList(addressResources))
        		.build();
        when(organisationService.getOrganisationById(6L)).thenReturn(organisation);
	}

   private OrganisationAddressResource organisationAddress(OrganisationAddressType addressType) {
       AddressTypeResource addressTypeResource = newAddressTypeResource().withId((long)addressType.getOrdinal()).withName(addressType.name()).build();
    	return newOrganisationAddressResource()
        		.withAddressType(addressTypeResource)
        		.withAddress(newAddressResource()
	        		.withAddressLine1("line1" + addressType.name())
	        		.withAddressLine2("line2" + addressType.name())
	        		.withAddressLine3("line3" + addressType.name())
	        		.withTown("town" + addressType.name())
	        		.withCounty("county" + addressType.name())
	        		.withPostcode("postcode" + addressType.name())
	        		.build())
        		.build();
    }
   
    @Test
    public void userProfileDetailsAndOrganisationDetailsAreAddedToModelWhenViewingDetails() throws Exception {
    	
    	OrganisationAddressResource operatingOrgAddress = organisationAddress(OPERATING);
    	setupOrganisation(operatingOrgAddress);
    	
        ResultActions result = mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("title", Matchers.equalTo(user.getTitle()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("firstName", Matchers.equalTo(user.getFirstName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("lastName", Matchers.equalTo(user.getLastName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("phoneNumber", Matchers.equalTo(user.getPhoneNumber()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("email", Matchers.equalTo(user.getEmail()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("organisationName", Matchers.equalTo(organisation.getName()))))
                .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("companyHouseNumber", Matchers.equalTo(organisation.getCompanyHouseNumber()))));

        verifyOrganisationAddress(result, operatingOrgAddress);
    }
    
    @Test
    public void operationAddressForOrganisationIsFavouredWhenRegisteredAddressIsAlsoPresent() throws Exception {

    	OrganisationAddressResource registeredOrgAddress = organisationAddress(REGISTERED);
    	OrganisationAddressResource operatingOrgAddress = organisationAddress(OPERATING);
    	setupOrganisation(registeredOrgAddress, operatingOrgAddress);
    	
        ResultActions result = mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful());
        
        verifyOrganisationAddress(result, operatingOrgAddress);
    }

	@Test
    public void registeredAddressIsUsedWhenOperatingAddressIsAbsent() throws Exception {

    	OrganisationAddressResource registeredOrgAddress = organisationAddress(REGISTERED);
    	setupOrganisation(registeredOrgAddress);
    	
        ResultActions result = mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful());
        

        verifyOrganisationAddress(result, registeredOrgAddress);
    }
	
	@Test
    public void nullValuesUsedWhenNoAddressPresent() throws Exception {

    	setupOrganisation();
    	
        mockMvc.perform(get("/profile/view"))
                .andExpect(status().is2xxSuccessful())
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("addressLine1", Matchers.isEmptyOrNullString())))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("addressLine2", Matchers.isEmptyOrNullString())))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("addressLine3", Matchers.isEmptyOrNullString())))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("town", Matchers.isEmptyOrNullString())))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("county", Matchers.isEmptyOrNullString())))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("postcode", Matchers.isEmptyOrNullString())));
    }
	
    private void verifyOrganisationAddress(ResultActions result, OrganisationAddressResource registeredOrgAddress) throws Exception {
   	 result
	    	 	.andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("addressLine1", Matchers.equalTo(registeredOrgAddress.getAddress().getAddressLine1()))))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("addressLine2", Matchers.equalTo(registeredOrgAddress.getAddress().getAddressLine2()))))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("addressLine3", Matchers.equalTo(registeredOrgAddress.getAddress().getAddressLine3()))))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("town", Matchers.equalTo(registeredOrgAddress.getAddress().getTown()))))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("county", Matchers.equalTo(registeredOrgAddress.getAddress().getCounty()))))
		        .andExpect(model().attribute("userDetailsForm", Matchers.hasProperty("postcode", Matchers.equalTo(registeredOrgAddress.getAddress().getPostcode()))));
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

        Error error = new Error("objectName", singletonList("fieldName"), BAD_REQUEST);
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
