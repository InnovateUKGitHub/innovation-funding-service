package org.innovateuk.ifs.assessment.controller.profile;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileEditDetailsForm;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileDetailsModelPopulator;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileEditDetailsModelPopulator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.service.EthnicityRestService;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorProfileDetailsControllerTest extends BaseControllerMockMVCTest<AssessorProfileDetailsController> {
    @Spy
    @InjectMocks
    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileEditDetailsModelPopulator assessorProfileEditDetailsModelPopulator;

    @Mock
    private EthnicityRestService ethnicityRestService;

    @Mock
    private Validator validator;

    @Override
    protected AssessorProfileDetailsController supplyControllerUnderTest() {
        return new AssessorProfileDetailsController();
    }

    @Test
    public void getDetails() throws Exception {
        UserResource user = buildTestUser();
        setLoggedInUser(user);

        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(buildTestEthnicity())));
        when(userService.getUserProfile(user.getId())).thenReturn(buildTestUserProfile());

        mockMvc.perform(get("/profile/details"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/details"));
    }

    @Test
    public void getEditDetails() throws Exception {
        UserResource user = buildTestUser();
        setLoggedInUser(user);
        UserProfileResource userProfile = buildTestUserProfile();

        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(buildTestEthnicity())));
        when(userService.getUserProfile(user.getId())).thenReturn(userProfile);

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

        when(userService.updateUserProfile(user.getId(), profileDetails)).thenReturn(ServiceResult.serviceSuccess());
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(buildTestEthnicity())));

        MvcResult result = mockMvc.perform(post("/profile/details/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("firstName", profileDetails.getFirstName())
                .param("lastName", profileDetails.getLastName())
                .param("phoneNumber", profileDetails.getPhoneNumber())
                .param("gender", profileDetails.getGender().name())
                .param("ethnicity", profileDetails.getEthnicity().getId().toString())
                .param("disability", profileDetails.getDisability().name())
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
        assertEquals(profileDetails.getGender(), form.getGender());
        assertEquals(profileDetails.getEthnicity(), form.getEthnicity());
        assertEquals(profileDetails.getDisability(), form.getDisability());
        assertEquals(profileDetails.getAddress().getPostcode(), form.getAddressForm().getPostcode());

        verify(userService).updateUserProfile(user.getId(), profileDetails);
    }

    @Test
    public void submitDetails_changeDetails() throws Exception {
        String firstName = "Felicia";
        String lastName = "Wilkinson";
        String phoneNumber = "87654321";
        Gender gender = Gender.NOT_STATED;
        EthnicityResource ethnicity = buildTestEthnicity();
        Disability disability = Disability.NOT_STATED;
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
                .withGender(gender)
                .withEthnicity(ethnicity)
                .withDisability(disability)
                .withAddress(newAddressResource()
                        .with(id(null))
                        .withAddressLine1(addressLine1)
                        .withTown(town)
                        .withPostcode(postcode)
                        .build())
                .build();

        when(userService.updateUserProfile(user.getId(), profileDetails)).thenReturn(ServiceResult.serviceSuccess());
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));

        MvcResult result = mockMvc.perform(post("/profile/details/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("phoneNumber", phoneNumber)
                .param("gender", gender.name())
                .param("ethnicity", ethnicity.getId().toString())
                .param("disability", disability.name())
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
        assertEquals(form.getGender(), gender);
        assertEquals(form.getEthnicity(), ethnicity);
        assertEquals(form.getDisability(), disability);
        assertEquals(form.getAddressForm().getAddressLine1(), addressLine1);
        assertEquals(form.getAddressForm().getTown(), town);
        assertEquals(form.getAddressForm().getPostcode(), postcode);

        verify(userService).updateUserProfile(user.getId(), profileDetails);
    }

    @Test
    public void submitDetails_partialRequest() throws Exception {
        UserResource user = buildTestUser();
        setLoggedInUser(user);

        UserProfileResource profileDetails = buildTestUserProfile();

        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(buildTestEthnicity())));

        MvcResult result = mockMvc.perform(post("/profile/details/edit")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("firstName", "")
                .param("lastName", "")
                .param("phoneNumber", profileDetails.getPhoneNumber())
                .param("gender", profileDetails.getGender().name())
                .param("ethnicity", profileDetails.getEthnicity().getId().toString())
                .param("disability", profileDetails.getDisability().name())
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
    public void submitDetails_emptyRequest() throws Exception {
        UserResource user = buildTestUser();
        setLoggedInUser(user);
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(buildTestEthnicity())));

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
        assertEquals("The address cannot be blank.", bindingResult.getFieldError("addressForm.addressLine1").getDefaultMessage());
        assertEquals("The town cannot be blank.", bindingResult.getFieldError("addressForm.town").getDefaultMessage());
        assertEquals("The postcode cannot be blank.", bindingResult.getFieldError("addressForm.postcode").getDefaultMessage());
    }

    private UserResource buildTestUser() {
        return newUserResource().withEmail("felix.wilson@gmail.com").build();
    }

    private UserProfileResource buildTestUserProfile() {
        AddressResource address = newAddressResource()
                .with(id(null))
                .withAddressLine1("address1")
                .withPostcode("postcode")
                .withTown("town")
                .build();

        return newUserProfileResource()
                .withFirstName("Felix")
                .withLastName("Wilson")
                .withPhoneNumber("12345678")
                .withEmail("felix.wilson@gmail.com")
                .withGender(Gender.NOT_STATED)
                .withEthnicity(buildTestEthnicity())
                .withDisability(Disability.NOT_STATED)
                .withAddress(address)
                .build();
    }

    private EthnicityResource buildTestEthnicity() {
        return newEthnicityResource().withId(7L).build();
    }
}
