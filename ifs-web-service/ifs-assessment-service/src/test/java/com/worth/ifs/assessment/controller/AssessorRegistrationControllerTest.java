package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.assessment.form.AssessorRegistrationForm;
import com.worth.ifs.assessment.model.AssessorRegistrationBecomeAnAssessorModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationYourDetailsModelPopulator;
import com.worth.ifs.assessment.service.AssessorService;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationBecomeAnAssessorViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationYourDetailsViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.form.AddressForm;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.service.EthnicityRestService;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.Gender;
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

import java.util.List;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static java.lang.String.format;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorRegistrationControllerTest extends BaseControllerMockMVCTest<AssessorRegistrationController> {
    @Spy
    @InjectMocks
    private AssessorRegistrationBecomeAnAssessorModelPopulator becomeAnAssessorModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationYourDetailsModelPopulator yourDetailsModelPopulator;

    @Mock
    private EthnicityRestService ethnicityRestService;

    @Mock
    private AssessorService assessorService;

    @Mock
    private AddressRestService addressRestService;

    @Mock
    private Validator validator;

    @Override
    protected AssessorRegistrationController supplyControllerUnderTest() {
        return new AssessorRegistrationController();
    }

    @Test
    public void becomeAnAssessor() throws Exception {
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().build();

        when(competitionInviteRestService.getInvite("hash")).thenReturn(restSuccess(competitionInviteResource));

        AssessorRegistrationBecomeAnAssessorViewModel expectedViewModel = new AssessorRegistrationBecomeAnAssessorViewModel("hash");

        mockMvc.perform(get("/registration/{inviteHash}/start", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("registration/become-assessor"));
    }

    @Test
    public void yourDetails() throws Exception {
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(competitionInviteRestService.getInvite("hash")).thenReturn(RestResult.restSuccess(competitionInviteResource));
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(newEthnicityResource())));
        AssessorRegistrationYourDetailsViewModel expectedViewModel = new AssessorRegistrationYourDetailsViewModel("hash", "test@test.com");

        mockMvc.perform(get("/registration/{inviteHash}/register", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("registration/register"));
    }

    @Test
    public void submitYourDetails() throws Exception {
        String title = "Mr";
        String firstName = "Felix";
        String lastName = "Wilson";
        String phoneNumber = "12345678";
        Gender gender = Gender.MALE;
        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();
        Disability disability = Disability.NO;
        String password = "P@ssword1234";

        String addressLine1 = "address1";
        String town = "town";
        String postcode = "postcode";

        AssessorRegistrationForm expectedForm = new AssessorRegistrationForm();
        expectedForm.setTitle(title);
        expectedForm.setFirstName(firstName);
        expectedForm.setLastName(lastName);
        expectedForm.setPhoneNumber(phoneNumber);
        expectedForm.setGender(gender);
        expectedForm.setEthnicity(ethnicity);
        expectedForm.setDisability(disability);
        expectedForm.setPassword(password);
        expectedForm.setRetypedPassword(password);

        AddressForm addressForm = expectedForm.getAddressForm();

        AddressResource addressResource = new AddressResource();

        addressResource.setAddressLine1(addressLine1);
        addressResource.setPostcode(postcode);
        addressResource.setTown(town);

        addressForm.setSelectedPostcode(addressResource);
        addressForm.setTriedToSave(true);

        String inviteHash = "hash";

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(competitionInviteRestService.getInvite(inviteHash)).thenReturn(RestResult.restSuccess(competitionInviteResource));
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));
        when(assessorService.createAssessorByInviteHash(inviteHash, expectedForm)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/registration/{inviteHash}/register", inviteHash)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("title", title)
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("phoneNumber", phoneNumber)
                .param("gender", gender.name())
                .param("ethnicity", ethnicity.getId().toString())
                .param("disability", disability.name())
                .param("password", password)
                .param("retypedPassword", password)
                .param("addressForm.selectedPostcode.addressLine1", addressLine1)
                .param("addressForm.selectedPostcode.town", town)
                .param("addressForm.selectedPostcode.postcode", postcode))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(redirectedUrl( format("/invite-accept/competition/%s/accept", inviteHash) ));

        verify(assessorService).createAssessorByInviteHash(inviteHash, expectedForm);
    }

    @Test
    public void submitYourDetails_Incomplete() throws Exception {
        String title = "Mr";
        String phoneNumber = "12345678";
        Gender gender = Gender.MALE;
        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();
        Disability disability = Disability.NO;
        String password = "P@ssword1234";

        String inviteHash = "hash";
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(competitionInviteRestService.getInvite(inviteHash)).thenReturn(RestResult.restSuccess(competitionInviteResource));
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));

        MvcResult result = mockMvc.perform(post("/registration/{inviteHash}/register", inviteHash)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("title", title)
                .param("phoneNumber", phoneNumber)
                .param("gender", gender.name())
                .param("ethnicity", ethnicity.getId().toString())
                .param("disability", disability.name())
                .param("password", password)
                .param("retypedPassword", password))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "firstName"))
                .andExpect(model().attributeHasFieldErrors("form", "lastName"))
                .andExpect(view().name("registration/register"))
                .andReturn();

        AssessorRegistrationForm form = (AssessorRegistrationForm) result.getModelAndView().getModel().get("form");

        assertEquals(title, form.getTitle());
        assertEquals(phoneNumber, form.getPhoneNumber());
        assertEquals(gender, form.getGender());
        assertEquals(ethnicity, form.getEthnicity());
        assertEquals(disability, form.getDisability());
        assertEquals(password, form.getPassword());
        assertEquals(password, form.getRetypedPassword());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(3, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("firstName"));
        assertTrue(bindingResult.hasFieldErrors("lastName"));
        assertEquals("Please enter a first name", bindingResult.getFieldError("firstName").getDefaultMessage());
        assertEquals("Please enter a last name", bindingResult.getFieldError("lastName").getDefaultMessage());
        assertEquals("Please enter your address details", bindingResult.getFieldError("address").getDefaultMessage());
    }

    @Test
    public void manualAddress_showsNoErrorsAndSetsAddressFormToManual() throws Exception {
        String inviteHash = "hash";

        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));
        when(competitionInviteRestService.getInvite(inviteHash)).thenReturn(RestResult.restSuccess(competitionInviteResource));

        MvcResult result = mockMvc.perform(post("/registration/{inviteHash}/register", inviteHash)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("manual-address", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("registration/register"))
                .andReturn();

        AssessorRegistrationForm form = (AssessorRegistrationForm) result.getModelAndView().getModel().get("form");

        assertEquals(form.getAddressForm().isManualAddress(), true);
    }

    @Test
    public void searchAddress_showsNoErrorsAndFillsAddressList() throws Exception {
        String inviteHash = "hash";
        String postcodeInput = "1234";

        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();
        List<AddressResource> addressResourceList = newAddressResource().withAddressLine1("address resource line 1", "address resource line 2").build(2);
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(addressRestService.doLookup(postcodeInput)).thenReturn(RestResult.restSuccess(addressResourceList));
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));
        when(competitionInviteRestService.getInvite(inviteHash)).thenReturn(RestResult.restSuccess(competitionInviteResource));

        MvcResult result = mockMvc.perform(post("/registration/{inviteHash}/register", inviteHash)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("search-address", "")
                .param("addressForm.postcodeInput", postcodeInput))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("registration/register"))
                .andReturn();

        AssessorRegistrationForm form = (AssessorRegistrationForm) result.getModelAndView().getModel().get("form");

        assertEquals(postcodeInput, form.getAddressForm().getPostcodeInput());
        assertEquals(addressResourceList.get(0), form.getAddressForm().getPostcodeOptions().get(0));
        assertEquals(addressResourceList.get(1), form.getAddressForm().getPostcodeOptions().get(1));
    }

    @Test
    public void searchAddress_emptyPostcode() throws Exception {
        String inviteHash = "hash";
        String postcodeInput = "";

        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();
        List<AddressResource> addressResourceList = newAddressResource().withAddressLine1("address resource line 1", "address resource line 2").build(2);
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));
        when(competitionInviteRestService.getInvite(inviteHash)).thenReturn(RestResult.restSuccess(competitionInviteResource));

        MvcResult result = mockMvc.perform(post("/registration/{inviteHash}/register", inviteHash)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("search-address", "")
                .param("addressForm.postcodeInput", postcodeInput))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "addressForm.postcodeInput"))
                .andExpect(view().name("registration/register"))
                .andReturn();

        AssessorRegistrationForm form = (AssessorRegistrationForm) result.getModelAndView().getModel().get("form");
        assertEquals(postcodeInput, form.getAddressForm().getPostcodeInput());

        verifyZeroInteractions(addressRestService);
    }


    @Test
    public void selectAddress_showsNoErrorsAndAddsSelectedAddressToForm() throws Exception {
        String inviteHash = "hash";
        String postcodeInput = "1234";

        List<AddressResource> addressResourceList = newAddressResource().withAddressLine1("address resource line 1", "address resource line 2").build(2);
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();
        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();


        when(addressRestService.doLookup(postcodeInput)).thenReturn(RestResult.restSuccess(addressResourceList));
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));
        when(competitionInviteRestService.getInvite(inviteHash)).thenReturn(RestResult.restSuccess(competitionInviteResource));


        MvcResult result = mockMvc.perform(post("/registration/{inviteHash}/register", inviteHash)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("addressForm.postcodeInput", postcodeInput)
                .param("select-address", "")
                .param("addressForm.selectedPostcodeIndex", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("registration/register"))
                .andReturn();

        AssessorRegistrationForm form = (AssessorRegistrationForm) result.getModelAndView().getModel().get("form");

        assertEquals(form.getAddressForm().getSelectedPostcode(), addressResourceList.get(1));
    }
}