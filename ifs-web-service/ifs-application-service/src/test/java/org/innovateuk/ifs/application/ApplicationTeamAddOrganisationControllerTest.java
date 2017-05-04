package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.form.ApplicantInviteForm;
import org.innovateuk.ifs.application.form.ApplicationTeamAddOrganisationForm;
import org.innovateuk.ifs.application.populator.ApplicationTeamAddOrganisationModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.ApplicationTeamAddOrganisationViewModel;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteResultResourceBuilder.newInviteResultResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationTeamAddOrganisationControllerTest extends BaseControllerMockMVCTest<ApplicationTeamAddOrganisationController> {

    @Spy
    @InjectMocks
    private ApplicationTeamAddOrganisationModelPopulator applicationTeamAddOrganisationModelPopulator;

    @Override
    protected ApplicationTeamAddOrganisationController supplyControllerUnderTest() {
        return new ApplicationTeamAddOrganisationController();
    }

    @Test
    public void getAddOrganisation() throws Exception {
        ApplicationResource applicationResource = setupApplicationResource();
        UserResource leadApplicant = setupLeadApplicant(applicationResource);
        setLoggedInUser(leadApplicant);

        ApplicationTeamAddOrganisationForm expectedForm = new ApplicationTeamAddOrganisationForm();
        expectedForm.setApplicants(singletonList(new ApplicantInviteForm()));

        ApplicationTeamAddOrganisationViewModel expectedViewModel = new ApplicationTeamAddOrganisationViewModel(
                applicationResource.getId(),
                "Application name"
        );

        mockMvc.perform(get("/application/{applicationId}/team/addOrganisation", applicationResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/add-organisation"))
                .andReturn();

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAddOrganisation() throws Exception {
        ApplicationResource applicationResource = setupApplicationResource();
        UserResource leadApplicant = setupLeadApplicant(applicationResource);
        setLoggedInUser(leadApplicant);

        List<ApplicationInviteResource> expectedInvites = newApplicationInviteResource()
                .with(BaseBuilderAmendFunctions.id(null))
                .withName("Jessica Doe", "Ryan Dell")
                .withEmail("jessica.doe@ludlow.co.uk", "ryan.dell@ludlow.co.uk")
                .withApplication(applicationResource.getId())
                .build(2);

        when(inviteRestService.createInvitesByInviteOrganisation("Ludlow", expectedInvites))
                .thenReturn(restSuccess(newInviteResultResource().build()));

        mockMvc.perform(post("/application/{applicationId}/team/addOrganisation", applicationResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("organisationName", "Ludlow")
                .param("applicants[0].name", "Jessica Doe")
                .param("applicants[0].email", "jessica.doe@ludlow.co.uk")
                .param("applicants[1].name", "Ryan Dell")
                .param("applicants[1].email", "ryan.dell@ludlow.co.uk"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", applicationResource.getId())));

        InOrder inOrder = inOrder(inviteRestService);
        inOrder.verify(inviteRestService).createInvitesByInviteOrganisation("Ludlow", expectedInvites);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAddOrganisation_emptyForm() throws Exception {
        ApplicationResource applicationResource = setupApplicationResource();
        UserResource leadApplicant = setupLeadApplicant(applicationResource);
        setLoggedInUser(leadApplicant);

        ApplicationTeamAddOrganisationViewModel expectedViewModel = new ApplicationTeamAddOrganisationViewModel(
                applicationResource.getId(),
                "Application name"
        );

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/addOrganisation", applicationResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "organisationName"))
                .andExpect(model().attributeHasFieldErrors("form", "applicants"))
                .andExpect(view().name("application-team/add-organisation"))
                .andReturn();

        ApplicationTeamAddOrganisationForm form = (ApplicationTeamAddOrganisationForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();

        assertNull(form.getOrganisationName());
        assertEquals(singletonList(new ApplicantInviteForm()), form.getApplicants());
        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("organisationName"));
        assertEquals("An organisation name is required.", bindingResult.getFieldError("organisationName").getDefaultMessage());
        assertTrue(bindingResult.hasFieldErrors("applicants"));
        assertEquals("Please add at least one person to invite.", bindingResult.getFieldError("applicants").getDefaultMessage());

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService, times(1)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAddOrganisation_incompleteApplicant() throws Exception {
        ApplicationResource applicationResource = setupApplicationResource();
        UserResource leadApplicant = setupLeadApplicant(applicationResource);
        setLoggedInUser(leadApplicant);

        ApplicationTeamAddOrganisationViewModel expectedViewModel = new ApplicationTeamAddOrganisationViewModel(
                applicationResource.getId(),
                "Application name"
        );

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/addOrganisation", applicationResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("organisationName", "Ludlow")
                .param("applicants[0].name", "Jessica Doe")
                .param("applicants[0].email", "jessica.doe@ludlow.co.uk")
                .param("applicants[1].name", "")
                .param("applicants[1].email", ""))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "applicants[1].name"))
                .andExpect(model().attributeHasFieldErrors("form", "applicants[1].email"))
                .andExpect(view().name("application-team/add-organisation"))
                .andReturn();

        ApplicationTeamAddOrganisationForm form = (ApplicationTeamAddOrganisationForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        bindingResult.getFieldErrors();

        assertEquals("Ludlow", form.getOrganisationName());
        assertEquals(asList(new ApplicantInviteForm("Jessica Doe", "jessica.doe@ludlow.co.uk"),
                new ApplicantInviteForm("", "")), form.getApplicants());
        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("applicants[1].name"));
        assertEquals("Please enter a name.", bindingResult.getFieldError("applicants[1].name").getDefaultMessage());
        assertTrue(bindingResult.hasFieldErrors("applicants[1].email"));
        assertEquals("Please enter an email address.", bindingResult.getFieldError("applicants[1].email").getDefaultMessage());

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService, times(1)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAddOrganisation_invalidApplicantEmail() throws Exception {
        ApplicationResource applicationResource = setupApplicationResource();
        UserResource leadApplicant = setupLeadApplicant(applicationResource);
        setLoggedInUser(leadApplicant);

        ApplicationTeamAddOrganisationViewModel expectedViewModel = new ApplicationTeamAddOrganisationViewModel(
                applicationResource.getId(),
                "Application name"
        );

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/addOrganisation", applicationResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("organisationName", "Ludlow")
                .param("applicants[0].name", "Jessica Doe")
                .param("applicants[0].email", "jessica.doe@ludlow.co.uk")
                .param("applicants[1].name", "Ryan Dell")
                .param("applicants[1].email", "ryan.dell.ludlow.co.uk"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "applicants[1].email"))
                .andExpect(view().name("application-team/add-organisation"))
                .andReturn();

        ApplicationTeamAddOrganisationForm form = (ApplicationTeamAddOrganisationForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        bindingResult.getFieldErrors();

        assertEquals("Ludlow", form.getOrganisationName());
        assertEquals(asList(new ApplicantInviteForm("Jessica Doe", "jessica.doe@ludlow.co.uk"),
                new ApplicantInviteForm("Ryan Dell", "ryan.dell.ludlow.co.uk")), form.getApplicants());
        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("applicants[1].email"));
        assertEquals("Please enter a valid email address.", bindingResult.getFieldError("applicants[1].email").getDefaultMessage());

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService, times(1)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitAddOrganisation_duplicateApplicantEmail() throws Exception {
        ApplicationResource applicationResource = setupApplicationResource();
        UserResource leadApplicant = setupLeadApplicant(applicationResource);
        setLoggedInUser(leadApplicant);

        ApplicationTeamAddOrganisationViewModel expectedViewModel = new ApplicationTeamAddOrganisationViewModel(
                applicationResource.getId(),
                "Application name"
        );

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/addOrganisation", applicationResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("organisationName", "Ludlow")
                .param("applicants[0].name", "Jessica Doe")
                .param("applicants[0].email", "jessica.doe@ludlow.co.uk")
                .param("applicants[1].name", "Ryan Dell")
                .param("applicants[1].email", "jessica.doe@ludlow.co.uk"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "applicants[1].email"))
                .andExpect(view().name("application-team/add-organisation"))
                .andReturn();

        ApplicationTeamAddOrganisationForm form = (ApplicationTeamAddOrganisationForm) result.getModelAndView().getModel().get("form");

        BindingResult bindingResult = form.getBindingResult();
        bindingResult.getFieldErrors();

        assertEquals("Ludlow", form.getOrganisationName());
        assertEquals(asList(new ApplicantInviteForm("Jessica Doe", "jessica.doe@ludlow.co.uk"),
                new ApplicantInviteForm("Ryan Dell", "jessica.doe@ludlow.co.uk")), form.getApplicants());
        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("applicants[1].email"));
        assertEquals("validation.applicationteamaddorganisationform.email.notUnique", bindingResult.getFieldError("applicants[1].email").getCode());

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService, times(1)).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void addApplicant() throws Exception {
        ApplicationResource applicationResource = setupApplicationResource();
        UserResource leadApplicant = setupLeadApplicant(applicationResource);
        setLoggedInUser(leadApplicant);

        ApplicationTeamAddOrganisationViewModel expectedViewModel = new ApplicationTeamAddOrganisationViewModel(
                applicationResource.getId(),
                applicationResource.getName()
        );

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/addOrganisation", applicationResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("addApplicant", "")
                .param("organisationName", "Ludlow")
                .param("applicants[0].name", "Jessica Doe")
                .param("applicants[0].email", "jessica.doe@ludlow.co.uk"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/add-organisation"))
                .andReturn();

        ApplicationTeamAddOrganisationForm form = (ApplicationTeamAddOrganisationForm) result.getModelAndView().getModel().get("form");

        assertEquals("Ludlow", form.getOrganisationName());
        assertEquals("The applicant rows should contain the existing applicant as well as a blank one", asList(new ApplicantInviteForm("Jessica Doe", "jessica.doe@ludlow.co.uk"), new ApplicantInviteForm()), form.getApplicants());

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void removeApplicant() throws Exception {
        ApplicationResource applicationResource = setupApplicationResource();
        UserResource leadApplicant = setupLeadApplicant(applicationResource);
        setLoggedInUser(leadApplicant);

        ApplicationTeamAddOrganisationViewModel expectedViewModel = new ApplicationTeamAddOrganisationViewModel(
                applicationResource.getId(),
                applicationResource.getName()
        );

        MvcResult result = mockMvc.perform(post("/application/{applicationId}/team/addOrganisation", applicationResource.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                // Remove the row at index 0
                .param("removeApplicant", "0")
                .param("organisationName", "Ludlow")
                .param("applicants[0].name", "Jessica Doe")
                .param("applicants[0].email", "jessica.doe@ludlow.co.uk"))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("application-team/add-organisation"))
                .andReturn();

        ApplicationTeamAddOrganisationForm form = (ApplicationTeamAddOrganisationForm) result.getModelAndView().getModel().get("form");

        assertEquals("Ludlow", form.getOrganisationName());
        assertTrue("The list of applicants should be empty", form.getApplicants().isEmpty());

        InOrder inOrder = inOrder(applicationService, inviteRestService);
        inOrder.verify(applicationService).getById(applicationResource.getId());
        inOrder.verifyNoMoreInteractions();
    }

    private ApplicationResource setupApplicationResource() {
        ApplicationResource applicationResource = newApplicationResource()
                .withName("Application name")
                .build();

        when(applicationService.getById(applicationResource.getId())).thenReturn(applicationResource);
        return applicationResource;
    }

    private UserResource setupLeadApplicant(ApplicationResource applicationResource) {
        UserResource leadApplicant = newUserResource().build();
        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource)).thenReturn(newProcessRoleResource()
                .withUser(leadApplicant)
                .build());
        return leadApplicant;
    }
}