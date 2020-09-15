package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.profile.form.AssessorProfileEditDetailsForm;
import org.innovateuk.ifs.assessment.profile.populator.AssessorProfileEditDetailsModelPopulator;
import org.innovateuk.ifs.populator.AssessorProfileDetailsModelPopulator;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.UserProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorProfileDetailsEditControllerTest extends BaseControllerMockMVCTest<AssessorProfileDetailsEditController> {
    @Spy
    @InjectMocks
    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileEditDetailsModelPopulator assessorProfileEditDetailsModelPopulator;

    @Mock
    private ProfileRestService profileRestService;

    @Mock
    private Validator validator;

    @Override
    protected AssessorProfileDetailsEditController supplyControllerUnderTest() {
        return new AssessorProfileDetailsEditController();
    }

    @Test
    public void getEditDetails() throws Exception {
        UserResource user = buildTestUser();
        setLoggedInUser(user);
        UserProfileResource userProfile = buildTestUserProfile();

        when(profileRestService.getUserProfile(user.getId())).thenReturn(restSuccess(userProfile));

        MvcResult result = mockMvc.perform(get("/profile/details/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(view().name("profile/details-edit"))
                .andReturn();

        AssessorProfileEditDetailsForm form = (AssessorProfileEditDetailsForm) result.getModelAndView().getModel().get("form");

        assertEquals(userProfile.getFirstName(), form.getFirstName());
        assertEquals(userProfile.getLastName(), form.getLastName());
        assertEquals(userProfile.getEmail(), user.getEmail());
        assertEquals(userProfile.getPhoneNumber(), form.getPhoneNumber());
        assertEquals(userProfile.getAddress(), form.getAddressForm());
    }

    @Test
    public void submitDetails_sameDetails() throws Exception {
        UserResource user = buildTestUser();
        setLoggedInUser(user);

        UserProfileResource profileDetails = buildTestUserProfile();

        when(profileRestService.updateUserProfile(user.getId(), profileDetails)).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(post("/profile/details/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("firstName", profileDetails.getFirstName())
                .param("lastName", profileDetails.getLastName())
                .param("phoneNumber", profileDetails.getPhoneNumber())
                .param("addressForm.addressLine1", profileDetails.getAddress().getAddressLine1())
                .param("addressForm.town", profileDetails.getAddress().getTown())
                .param("addressForm.postcode", profileDetails.getAddress().getPostcode()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"))
                .andReturn();

        AssessorProfileEditDetailsForm form = (AssessorProfileEditDetailsForm) result.getModelAndView().getModel().get("form");
        assertEquals(profileDetails.getFirstName(), form.getFirstName());
        assertEquals(profileDetails.getLastName(), form.getLastName());
        assertEquals(profileDetails.getPhoneNumber(), form.getPhoneNumber());
        assertEquals(profileDetails.getAddress().getPostcode(), form.getAddressForm().getPostcode());

        verify(profileRestService).updateUserProfile(user.getId(), profileDetails);
    }

    @Test
    public void submitDetails_changeDetails() throws Exception {
        String firstName = "Felicia";
        String lastName = "Wilkinson";
        String phoneNumber = "87654321";
        String addressLine1 = "notAddress1";
        String town = "notTown";
        String postcode = "notPost";

        UserResource user = buildTestUser();
        setLoggedInUser(user);

        UserProfileResource profileDetails = newUserProfileResource()
                .withFirstName(firstName)
                .withLastName(lastName)
                .withPhoneNumber(phoneNumber)
                .withEmail(user.getEmail())
                .withAddress(newAddressResource()
                        .withAddressLine1(addressLine1)
                        .withAddressLine2()
                        .withAddressLine3()
                        .withTown(town)
                        .withCounty()
                        .withPostcode(postcode)
                        .build())
                .build();

        when(profileRestService.updateUserProfile(user.getId(), profileDetails)).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(post("/profile/details/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("phoneNumber", phoneNumber)
                .param("addressForm.addressLine1", addressLine1)
                .param("addressForm.town", town)
                .param("addressForm.postcode", postcode))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"))
                .andReturn();

        AssessorProfileEditDetailsForm form = (AssessorProfileEditDetailsForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getFirstName(), firstName);
        assertEquals(form.getLastName(), lastName);
        assertEquals(form.getPhoneNumber(), phoneNumber);
        assertEquals(form.getAddressForm().getAddressLine1(), addressLine1);
        assertEquals(form.getAddressForm().getTown(), town);
        assertEquals(form.getAddressForm().getPostcode(), postcode);

        verify(profileRestService).updateUserProfile(user.getId(), profileDetails);
    }

    @Test
    public void submitDetails_partialRequest() throws Exception {
        UserResource user = buildTestUser();
        setLoggedInUser(user);

        UserProfileResource profileDetails = buildTestUserProfile();

        MvcResult result = mockMvc.perform(post("/profile/details/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("firstName", "")
                .param("lastName", "")
                .param("phoneNumber", profileDetails.getPhoneNumber())
                .param("addressForm.addressLine1", profileDetails.getAddress().getAddressLine1())
                .param("addressForm.town", profileDetails.getAddress().getTown())
                .param("addressForm.postcode", profileDetails.getAddress().getPostcode()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "firstName"))
                .andExpect(model().attributeHasFieldErrors("form", "lastName"))
                .andExpect(view().name("profile/details-edit"))
                .andReturn();

        AssessorProfileEditDetailsForm form = (AssessorProfileEditDetailsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(4, bindingResult.getFieldErrorCount());

        assertEquals(2, bindingResult.getFieldErrorCount("firstName"));
        assertEquals(2, bindingResult.getFieldErrorCount("lastName"));

        assertTrue(bindingResult.getFieldErrors("firstName").stream()
                .anyMatch(error -> error.getDefaultMessage().equalsIgnoreCase("Your first name should have at least {2} characters.")));
        assertTrue(bindingResult.getFieldErrors("firstName").stream()
                .anyMatch(error -> error.getDefaultMessage().equalsIgnoreCase("Please enter a first name.")));
        assertTrue(bindingResult.getFieldErrors("lastName").stream()
                .anyMatch(error -> error.getDefaultMessage().equalsIgnoreCase("Your last name should have at least {2} characters.")));
        assertTrue(bindingResult.getFieldErrors("lastName").stream()
                .anyMatch(error -> error.getDefaultMessage().equalsIgnoreCase("Please enter a last name.")));
    }

    @Test
    public void submitDetails_invalidNames() throws Exception {
        UserResource user = buildTestUser();
        setLoggedInUser(user);

        UserProfileResource profileDetails = buildTestUserProfile();

        MvcResult result = mockMvc.perform(post("/profile/details/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("firstName", "abc£$%123")
                .param("lastName", "xyz&*(789")
                .param("phoneNumber", profileDetails.getPhoneNumber())
                .param("addressForm.addressLine1", profileDetails.getAddress().getAddressLine1())
                .param("addressForm.town", profileDetails.getAddress().getTown())
                .param("addressForm.postcode", profileDetails.getAddress().getPostcode()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "firstName"))
                .andExpect(model().attributeHasFieldErrors("form", "lastName"))
                .andExpect(view().name("profile/details-edit"))
                .andReturn();

        AssessorProfileEditDetailsForm form = (AssessorProfileEditDetailsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());

        assertEquals(1, bindingResult.getFieldErrorCount("firstName"));
        assertEquals(1, bindingResult.getFieldErrorCount("lastName"));

        assertTrue(bindingResult.getFieldErrors("firstName").stream()
                .anyMatch(error -> error.getDefaultMessage().equalsIgnoreCase("Invalid first name.")));
        assertTrue(bindingResult.getFieldErrors("lastName").stream()
                .anyMatch(error -> error.getDefaultMessage().equalsIgnoreCase("Invalid last name.")));
    }

    @Test
    public void submitDetails_emptyRequest() throws Exception {
        UserResource user = buildTestUser();
        setLoggedInUser(user);

        MvcResult result = mockMvc.perform(post("/profile/details/edit")
                .contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "firstName"))
                .andExpect(model().attributeHasFieldErrors("form", "lastName"))
                .andExpect(model().attributeHasFieldErrors("form", "phoneNumber"))
                .andExpect(model().attributeHasFieldErrors("form", "addressForm.addressLine1"))
                .andExpect(model().attributeHasFieldErrors("form", "addressForm.town"))
                .andExpect(model().attributeHasFieldErrors("form", "addressForm.postcode"))
                .andExpect(view().name("profile/details-edit"))
                .andReturn();

        AssessorProfileEditDetailsForm form = (AssessorProfileEditDetailsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(6, bindingResult.getFieldErrorCount());
        assertEquals("Please enter a first name.", bindingResult.getFieldError("firstName").getDefaultMessage());
        assertEquals("Please enter a last name.", bindingResult.getFieldError("lastName").getDefaultMessage());
        assertEquals("Please enter a phone number.", bindingResult.getFieldError("phoneNumber").getDefaultMessage());
        assertEquals("The first line of the address cannot be blank.", bindingResult.getFieldError("addressForm.addressLine1").getDefaultMessage());
        assertEquals("The town cannot be blank.", bindingResult.getFieldError("addressForm.town").getDefaultMessage());
        assertEquals("The postcode cannot be blank.", bindingResult.getFieldError("addressForm.postcode").getDefaultMessage());
    }

    private UserResource buildTestUser() {
        return newUserResource().withEmail("felix.wilson@gmail.com").build();
    }

    private UserProfileResource buildTestUserProfile() {
        AddressResource address = newAddressResource()
                .withAddressLine1("address1")
                .withAddressLine2()
                .withAddressLine3()
                .withPostcode("postcode")
                .withTown("town")
                .withCounty()
                .build();

        return newUserProfileResource()
                .withFirstName("Felix")
                .withLastName("Wilson")
                .withPhoneNumber("12345678")
                .withEmail("felix.wilson@gmail.com")
                .withAddress(address)
                .build();
    }
}