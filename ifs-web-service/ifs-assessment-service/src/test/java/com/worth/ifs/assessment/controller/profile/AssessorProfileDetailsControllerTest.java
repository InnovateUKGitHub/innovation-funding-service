package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.assessment.form.profile.AssessorProfileEditDetailsForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileDetailsModelPopulator;
import com.worth.ifs.assessment.model.profile.AssessorProfileEditDetailsModelPopulator;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.service.EthnicityRestService;
import com.worth.ifs.user.resource.*;
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

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

        assertEquals(userProfile.getTitle(), form.getTitle());
        assertEquals(userProfile.getFirstName(), form.getFirstName());
        assertEquals(userProfile.getLastName(), form.getLastName());
        assertEquals(userProfile.getEmail(), user.getEmail());
        assertEquals(userProfile.getPhoneNumber(), form.getPhoneNumber());
        assertEquals(userProfile.getGender(), form.getGender());
        assertEquals(userProfile.getDisability(), form.getDisability());
        assertEquals(userProfile.getEthnicity(), form.getEthnicity());
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
                .param("title", profileDetails.getTitle())
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
        assertEquals(profileDetails.getTitle(), form.getTitle());
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
        String title = "Mrs";
        String firstName = "Felicia";
        String lastName = "Wilkinson";
        String phoneNumber = "87654321";
        Gender gender = Gender.FEMALE;
        EthnicityResource ethnicity = buildTestEthnicity();
        Disability disability = Disability.YES;
        String addressLine1 = "notAddress1";
        String town = "notTown";
        String postcode = "notPost";

        UserResource user = buildTestUser();
        setLoggedInUser(user);

        UserProfileResource profileDetails = newUserProfileResource()
                .withTitle(title)
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
                .param("title", title)
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
        assertEquals(form.getTitle(), title);
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
                .param("title", profileDetails.getTitle())
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

        assertEquals("Your first name should have at least {2} characters", bindingResult.getFieldErrors("firstName").get(0).getDefaultMessage());
        assertEquals("Please enter a first name", bindingResult.getFieldErrors("firstName").get(1).getDefaultMessage());

        assertEquals("Your last name should have at least {2} characters", bindingResult.getFieldErrors("lastName").get(0).getDefaultMessage());
        assertEquals("Please enter a last name", bindingResult.getFieldErrors("lastName").get(1).getDefaultMessage());
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
                .andExpect(model().attributeHasFieldErrors("form", "gender"))
                .andExpect(model().attributeHasFieldErrors("form", "ethnicity"))
                .andExpect(model().attributeHasFieldErrors("form", "title"))
                .andExpect(model().attributeHasFieldErrors("form", "disability"))
                .andExpect(model().attributeHasFieldErrors("form", "addressForm.addressLine1"))
                .andExpect(model().attributeHasFieldErrors("form", "addressForm.town"))
                .andExpect(model().attributeHasFieldErrors("form", "addressForm.postcode"))
                .andExpect(view().name("profile/details-edit"))
                .andReturn();

        AssessorProfileEditDetailsForm form = (AssessorProfileEditDetailsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(10, bindingResult.getFieldErrorCount());
        assertEquals("Please select a title", bindingResult.getFieldError("title").getDefaultMessage());
        assertEquals("Please enter a first name", bindingResult.getFieldError("firstName").getDefaultMessage());
        assertEquals("Please enter a last name", bindingResult.getFieldError("lastName").getDefaultMessage());
        assertEquals("Please enter a phone number", bindingResult.getFieldError("phoneNumber").getDefaultMessage());
        assertEquals("Please select a gender", bindingResult.getFieldError("gender").getDefaultMessage());
        assertEquals("Please select an ethnicity", bindingResult.getFieldError("ethnicity").getDefaultMessage());
        assertEquals("Please select a disability", bindingResult.getFieldError("disability").getDefaultMessage());
        assertEquals("The address cannot be blank", bindingResult.getFieldError("addressForm.addressLine1").getDefaultMessage());
        assertEquals("The town cannot be blank", bindingResult.getFieldError("addressForm.town").getDefaultMessage());
        assertEquals("The postcode cannot be blank", bindingResult.getFieldError("addressForm.postcode").getDefaultMessage());
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
                .withTitle("Mr")
                .withFirstName("Felix")
                .withLastName("Wilson")
                .withPhoneNumber("12345678")
                .withEmail("felix.wilson@gmail.com")
                .withGender(Gender.MALE)
                .withEthnicity(buildTestEthnicity())
                .withDisability(Disability.NO)
                .withAddress(address)
                .build();
    }

    private EthnicityResource buildTestEthnicity() {
        return newEthnicityResource().withId(1L).build();
    }
}
