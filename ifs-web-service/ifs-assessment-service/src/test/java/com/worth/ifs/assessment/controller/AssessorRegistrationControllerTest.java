package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.form.registration.AssessorRegistrationForm;
import com.worth.ifs.assessment.model.registration.AssessorRegistrationBecomeAnAssessorModelPopulator;
import com.worth.ifs.assessment.model.registration.AssessorRegistrationModelPopulator;
import com.worth.ifs.assessment.service.AssessorService;
import com.worth.ifs.assessment.viewmodel.registration.AssessorRegistrationBecomeAnAssessorViewModel;
import com.worth.ifs.assessment.viewmodel.registration.AssessorRegistrationViewModel;
import com.worth.ifs.commons.rest.RestResult;
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

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
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
        AssessorRegistrationViewModel expectedViewModel = new AssessorRegistrationViewModel("hash", "test@test.com");

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
                .param("retypedPassword", password))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

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
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("firstName"));
        assertTrue(bindingResult.hasFieldErrors("lastName"));
        assertEquals("Please enter a first name", bindingResult.getFieldError("firstName").getDefaultMessage());
        assertEquals("Please enter a last name", bindingResult.getFieldError("lastName").getDefaultMessage());
    }
}