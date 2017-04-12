package org.innovateuk.ifs.assessment.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.assessment.registration.controller.AssessorRegistrationController;
import org.innovateuk.ifs.assessment.registration.form.AssessorRegistrationForm;
import org.innovateuk.ifs.assessment.registration.populator.AssessorRegistrationBecomeAnAssessorModelPopulator;
import org.innovateuk.ifs.assessment.registration.populator.AssessorRegistrationModelPopulator;
import org.innovateuk.ifs.assessment.registration.service.AssessorService;
import org.innovateuk.ifs.assessment.registration.registration.AssessorRegistrationBecomeAnAssessorViewModel;
import org.innovateuk.ifs.assessment.registration.registration.AssessorRegistrationViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.invite.service.EthnicityRestService;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.Title;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
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
    private AssessorRegistrationModelPopulator yourDetailsModelPopulator;

    @Mock
    private EthnicityRestService ethnicityRestService;

    @Mock
    private AssessorService assessorService;

    @Mock
    private AddressRestService addressRestService;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(controller, "validator", validator);
    }

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
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(newEthnicityResource().build(1)));
        AssessorRegistrationViewModel expectedViewModel = new AssessorRegistrationViewModel("hash", "test@test.com");

        mockMvc.perform(get("/registration/{inviteHash}/register", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("registration/register"));
    }

    @Test
    public void submitYourDetails() throws Exception {
        Title title = Title.Mr;
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

        String inviteHash = "hash";

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(competitionInviteRestService.getInvite(inviteHash)).thenReturn(RestResult.restSuccess(competitionInviteResource));
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));
        when(assessorService.createAssessorByInviteHash(inviteHash, expectedForm)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/registration/{inviteHash}/register", inviteHash)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("title", title.name())
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
                .andExpect(redirectedUrl(format("/registration/%s/register/account-created", inviteHash)));

        verify(assessorService).createAssessorByInviteHash(inviteHash, expectedForm);
    }


    @Test
    public void submitYourDetails_weakPassword() throws Exception {
        Title title = Title.Mr;
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

        String inviteHash = "hash";

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(competitionInviteRestService.getInvite(inviteHash)).thenReturn(RestResult.restSuccess(competitionInviteResource));
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));
        when(assessorService.createAssessorByInviteHash(inviteHash, expectedForm)).thenReturn(
                serviceFailure(asList(fieldError("password", HttpStatus.CONFLICT.getReasonPhrase(), "INVALID_PASSWORD", HttpStatus.CONFLICT))));

        mockMvc.perform(post("/registration/{inviteHash}/register", inviteHash)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("title", title.name())
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
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrorCode("form", "password", "registration.INVALID_PASSWORD"))
                .andReturn();

        verify(assessorService).createAssessorByInviteHash(inviteHash, expectedForm);
    }

    @Test
    public void submitYourDetails_Incomplete() throws Exception {
        Title title = Title.Mr;
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
                .param("title", title.name())
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
        assertTrue(bindingResult.hasFieldErrors("addressForm.postcodeInput"));
        assertEquals("Please enter a first name.", bindingResult.getFieldError("firstName").getDefaultMessage());
        assertEquals("Please enter a last name.", bindingResult.getFieldError("lastName").getDefaultMessage());
        assertEquals("validation.standard.postcodesearch.required", bindingResult.getFieldError("addressForm.postcodeInput").getCode());
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

    @Test
    public void submitYourDetails_withoutSelectedAddressResultsInError() throws Exception {
        EthnicityResource ethnicity = newEthnicityResource().withId(1L).build();

        String title = "Mr";
        Long selectedPostcodeIndex = 0L;
        String inviteHash = "hash";

        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(competitionInviteRestService.getInvite(inviteHash)).thenReturn(RestResult.restSuccess(competitionInviteResource));
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(ethnicity)));

        mockMvc.perform(post("/registration/{inviteHash}/register", inviteHash)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("title", title)
                .param("addressForm.selectedPostcodeIndex", selectedPostcodeIndex.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeHasFieldErrorCode("form", "addressForm.postcodeOptions", "validation.standard.postcodeoptions.required"));

        verifyZeroInteractions(assessorService);
    }

    @Test
    public void accountCreated() throws Exception {
        String inviteHash = "hash";

        setLoggedInUser(null);
        when(competitionInviteRestService.checkExistingUser(inviteHash)).thenReturn(restSuccess(true));

        mockMvc.perform(get("/registration/{inviteHash}/register/account-created", inviteHash))
                .andExpect(status().isOk())
                .andExpect(model().attribute("competitionInviteHash", inviteHash))
                .andExpect(view().name("registration/account-created"));

        verify(competitionInviteRestService, only()).checkExistingUser(inviteHash);
    }

    @Test
    public void accountCreated_loggedIn() throws Exception {
        String inviteHash = "hash";

        setLoggedInUser(assessor);

        mockMvc.perform(get("/registration/{inviteHash}/register/account-created", inviteHash))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/invite/competition/%s", inviteHash)));

        verifyZeroInteractions(competitionInviteRestService);
    }

    @Test
    public void accountCreated_accountNotCreated() throws Exception {
        String inviteHash = "hash";

        setLoggedInUser(null);
        when(competitionInviteRestService.checkExistingUser(inviteHash)).thenReturn(restSuccess(false));

        mockMvc.perform(get("/registration/{inviteHash}/register/account-created", inviteHash))
                .andExpect(redirectedUrl(format("/invite/competition/%s", inviteHash)));

        verify(competitionInviteRestService, only()).checkExistingUser(inviteHash);
    }

    @Test
    public void accountCreated_hashNotExists() throws Exception {
        String inviteHash = "hash";

        setLoggedInUser(null);
        when(competitionInviteRestService.checkExistingUser(inviteHash)).thenReturn(restFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/registration/{inviteHash}/register/account-created", inviteHash))
                .andExpect(status().isNotFound());

        verify(competitionInviteRestService, only()).checkExistingUser(inviteHash);
    }
}
