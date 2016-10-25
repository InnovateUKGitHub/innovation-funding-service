package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.assessment.form.profile.AssessorProfileEditDetailsForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileDetailsModelPopulator;
import com.worth.ifs.assessment.model.profile.AssessorProfileEditDetailsModelPopulator;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.AddressForm;
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
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        EthnicityResource ethnicity = newEthnicityResource().build();
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));
        UserProfileResource profileDetails = newUserProfileResource().build();
        when(userService.getProfileDetails(user.getId())).thenReturn(profileDetails);

        mockMvc.perform(get("/profile/details"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/details"));
    }

    @Test
    public void getEditDetails() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);
        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();
        AssessorProfileEditDetailsForm expectedForm = new AssessorProfileEditDetailsForm();
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));
        UserProfileResource profileDetails = newUserProfileResource().build();
        when(userService.getProfileDetails(user.getId())).thenReturn(profileDetails);

        mockMvc.perform(get("/profile/details/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("profile/details-edit"));
    }

    @Test
    public void submitDetails() throws Exception {
        String title = "Mr";
        String firstName = "Felix";
        String lastName = "Wilson";
        String phoneNumber = "12345678";
        String email = "felix.wilson@gmail.com";
        Gender gender = Gender.MALE;
        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();
        Disability disability = Disability.NO;
        String addressLine1 = "address1";
        String town = "town";
        String postcode = "postcode";

        UserResource user = newUserResource().withEmail(email).build();
        setLoggedInUser(user);

        AssessorProfileEditDetailsForm expectedForm = new AssessorProfileEditDetailsForm();
        AddressForm addressForm = expectedForm.getAddressForm();

        AddressResource addressResource = newAddressResource()
                .with(id(null))
                .withAddressLine1(addressLine1)
                .withPostcode(postcode)
                .withTown(town)
                .build();

        addressForm.setSelectedPostcode(addressResource);
        addressForm.setTriedToSave(true);
        UserProfileResource profileDetails = newUserProfileResource()
                .withTitle(title)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withEthnicity(ethnicity)
                .withDisability(disability)
                .withGender(gender)
                .withPhoneNumber(phoneNumber)
                .withEmail(email)
                .withAddress(addressResource)
                .build();

        when(userService.updateProfileDetails(user.getId(), profileDetails)).thenReturn(ServiceResult.serviceSuccess());
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
                .param("addressForm.selectedPostcode.addressLine1", addressLine1)
                .param("addressForm.selectedPostcode.town", town)
                .param("addressForm.selectedPostcode.postcode", postcode))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"))
                .andReturn();

        AssessorProfileEditDetailsForm form = (AssessorProfileEditDetailsForm) result.getModelAndView().getModel().get("form");
        assertEquals(title, form.getTitle());
        assertEquals(firstName, form.getFirstName());
        assertEquals(lastName, form.getLastName());
        assertEquals(phoneNumber, form.getPhoneNumber());
        assertEquals(gender, form.getGender());
        assertEquals(ethnicity, form.getEthnicity());
        assertEquals(disability, form.getDisability());
        assertEquals(addressResource.getPostcode(), form.getAddressForm().getSelectedPostcode().getPostcode());

        verify(userService).updateProfileDetails(user.getId(), profileDetails);
    }

    @Test
    public void submitDetails_incomplete() throws Exception {
        String email = "felix.wilson@gmail.com";
        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();

        UserResource user = newUserResource().withEmail(email).build();
        setLoggedInUser(user);
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));

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
                .andExpect(model().attributeHasFieldErrors("form", "address"))
                .andExpect(view().name("profile/details-edit"))
                .andReturn();

        AssessorProfileEditDetailsForm form = (AssessorProfileEditDetailsForm) result.getModelAndView().getModel().get("form");
        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(8, bindingResult.getFieldErrorCount());
        assertEquals("Please select a title", bindingResult.getFieldError("title").getDefaultMessage());
        assertEquals("Please enter a first name", bindingResult.getFieldError("firstName").getDefaultMessage());
        assertEquals("Please enter a last name", bindingResult.getFieldError("lastName").getDefaultMessage());
        assertEquals("Please enter a phone number", bindingResult.getFieldError("phoneNumber").getDefaultMessage());
        assertEquals("Please select a gender", bindingResult.getFieldError("gender").getDefaultMessage());
        assertEquals("Please select an ethnicity", bindingResult.getFieldError("ethnicity").getDefaultMessage());
        assertEquals("Please select a disability", bindingResult.getFieldError("disability").getDefaultMessage());
        assertEquals("Please enter your address details", bindingResult.getFieldError("address").getDefaultMessage());
     }
}

