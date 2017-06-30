package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.builder.SectionResourceBuilder;
import org.innovateuk.ifs.application.finance.view.DefaultFinanceFormHandler;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceOverviewViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.forms.populator.OrganisationDetailsViewModelPopulator;
import org.innovateuk.ifs.application.forms.populator.QuestionModelPopulator;
import org.innovateuk.ifs.application.forms.saver.ApplicationSectionSaver;
import org.innovateuk.ifs.application.forms.service.ApplicationRedirectionService;
import org.innovateuk.ifs.application.overheads.OverheadFileSaver;
import org.innovateuk.ifs.application.populator.*;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.populator.section.AbstractSectionPopulator;
import org.innovateuk.ifs.application.populator.section.YourFinancesSectionPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.viewmodel.section.YourFinancesSectionViewModel;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationSectionControllerTest extends BaseControllerMockMVCTest<ApplicationSectionController> {

    @Spy
    @InjectMocks
    private QuestionModelPopulator questionModelPopulator;

    @Spy
    @InjectMocks
    private OpenSectionModelPopulator openSectionModel;

    @Spy
    @InjectMocks
    private OpenApplicationFinanceSectionModelPopulator openFinanceSectionModel;

    @Spy
    @InjectMocks
    private OrganisationDetailsViewModelPopulator organisationDetailsViewModelPopulator;

    @Mock
    private ApplicationModelPopulator applicationModelPopulator;

    @Mock
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    private DefaultFinanceFormHandler defaultFinanceFormHandler;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Mock
    private YourFinancesSectionPopulator yourFinancesSectionPopulator;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private OverheadFileSaver overheadFileSaver;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Spy
    @InjectMocks
    private ApplicationRedirectionService applicationRedirectionService;

    @Mock
    private ApplicationSectionSaver applicationSaver;

    private ApplicationResource application;
    private Long sectionId;
    private Long questionId;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
    private ApplicantSectionResourceBuilder sectionBuilder;

    @Override
    protected ApplicationSectionController supplyControllerUnderTest() {
        return new ApplicationSectionController();
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupUserRoles();
        this.setupFinances();
        this.setupInvites();
        this.setupQuestionStatus(applications.get(0));

        application = applications.get(0);
        sectionId = 1L;
        questionId = 1L;

        // save actions should always succeed.
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), eq(""), anyBoolean())).thenReturn(restSuccess(new ValidationMessages(fieldError("value", "", "Please enter some text 123"))));
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean())).thenReturn(restSuccess(noErrors()));
        when(organisationService.getOrganisationById(anyLong())).thenReturn(organisations.get(0));
        when(overheadFileSaver.handleOverheadFileRequest(any())).thenReturn(noErrors());
        when(overheadFileSaver.isOverheadFileRequest(any(HttpServletRequest.class))).thenCallRealMethod();
        when(financeHandler.getFinanceFormHandler(any())).thenReturn(defaultFinanceFormHandler);

        ApplicantResource applicant = newApplicantResource().withProcessRole(processRoles.get(0)).withOrganisation(organisations.get(0)).build();
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(newApplicantQuestionResource().withApplication(application).withCompetition(competitionResource).withCurrentApplicant(applicant).withApplicants(asList(applicant)).withQuestion(questionResources.values().iterator().next()).withCurrentUser(loggedInUser).build());
        sectionBuilder = newApplicantSectionResource().withApplication(application).withCompetition(competitionResource).withCurrentApplicant(applicant).withApplicants(asList(applicant)).withSection(newSectionResource().withType(SectionType.FINANCE).build()).withCurrentUser(loggedInUser);
        when(applicantRestService.getSection(anyLong(), anyLong(), anyLong())).thenReturn(sectionBuilder.build());
        when(formInputViewModelGenerator.fromQuestion(any(), any())).thenReturn(Collections.emptyList());
        when(formInputViewModelGenerator.fromSection(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(yourFinancesSectionPopulator.populate(any(), any(), any(), any(), any(), any(), any())).thenReturn(new YourFinancesSectionViewModel(null, null, null, false, Optional.empty(), false));

        ApplicationFinanceOverviewViewModel financeOverviewViewModel = new ApplicationFinanceOverviewViewModel();
        when(applicationFinanceOverviewModelManager.getFinanceDetailsViewModel(competitionResource.getId(), application.getId())).thenReturn(financeOverviewViewModel);

        FinanceViewModel financeViewModel = new FinanceViewModel();
        financeViewModel.setOrganisationGrantClaimPercentage(76);

        when(defaultFinanceModelManager.getFinanceViewModel(anyLong(), anyList(), anyLong(), any(Form.class), anyLong())).thenReturn(financeViewModel);
        Map<SectionType, AbstractSectionPopulator> sectionPopulators = mock(Map.class);
        when(sectionPopulators.get(any())).thenReturn(yourFinancesSectionPopulator);
        ReflectionTestUtils.setField(controller, "sectionPopulators", sectionPopulators);

        when(applicationSaver.saveApplicationForm(any(ApplicationResource.class), anyLong(), any(ApplicationForm.class), anyLong(), anyLong(), any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean()))
                .thenReturn(new ValidationMessages());
    }

    @Test
    public void testApplicationFormWithOpenSection() throws Exception {

        Long currentSectionId = sectionResources.get(2).getId();

        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        MvcResult result = mockMvc.perform(get("/application/1/form/section/" + currentSectionId).header("referer", "/application/1"))
                .andExpect(view().name("application-form"))
                .andReturn();

        Object viewModelResult = result.getModelAndView().getModelMap().get("model");
        assertEquals(YourFinancesSectionViewModel.class, viewModelResult.getClass());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationFormWithOpenSectionWhenTraversedFromSummaryPage() throws Exception {
        Long currentSectionId = sectionResources.get(2).getId();

        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        mockMvc.perform(get("/application/1/form/section/" + currentSectionId).header("referer", "/application/1/summary"))
                .andExpect(view().name("application-form"));

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationFormWithOpenFinanceSection() throws Exception {
        Long currentSectionId = sectionResources.get(6).getId();
        MvcResult result = mockMvc.perform(get("/application/1/form/section/" + currentSectionId))
                .andExpect(view().name("application-form"))
                .andReturn();

        Object viewModelResult = result.getModelAndView().getModelMap().get("model");
        assertEquals(YourFinancesSectionViewModel.class, viewModelResult.getClass());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testAddAnother() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("add_cost", String.valueOf(questionId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/" + application.getId() + "/form/section/" + sectionId));
    }

    @Test
    public void testApplicationFormSubmit() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("application.startDate", futureDate.format(FORMATTER))
                        .param("application.startDate.year", Integer.toString(futureDate.getYear()))
                        .param("application.startDate.dayOfMonth", Integer.toString(futureDate.getDayOfMonth()))
                        .param("application.startDate.monthValue", Integer.toString(futureDate.getMonthValue()))
                        .param("application.name", "New Application Title")
                        .param("application.durationInMonths", "12")
                        .param("submit-section", "Save")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testNotRequestingFunding() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(NOT_REQUESTING_FUNDING, "1")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testRequestingFunding() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(REQUESTING_FUNDING, "1")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testSubmitFinanceSubSectionWithRedirectToYourFinances() throws Exception {
        when(applicantRestService.getSection(any(), any(), any())).thenReturn(sectionBuilder.withSection(newSectionResource().withType(SectionType.FUNDING_FINANCES).build()).build());
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE)).thenReturn(sectionResourceBuilder.withType(SectionType.FINANCE).build(1));
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
                        .param(TERMS_AGREED_KEY, "1")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFinanceMarkAsCompleteFailWithTerms() throws Exception {
        when(applicantRestService.getSection(any(), any(), any())).thenReturn(sectionBuilder.withSection(newSectionResource().withType(SectionType.FUNDING_FINANCES).build()).build());
        FormInputResource resource = newFormInputResource().withId(1L).withType(FormInputType.YOUR_FINANCE).build();
        when(formInputRestService.getByQuestionIdAndScope(questionId, FormInputScope.APPLICATION)).thenReturn(restSuccess(Collections.singletonList(resource)));
        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(application.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), application.getId())).thenReturn(restSuccess(userApplicationRole));

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1))
                .andExpect(model().attributeHasFieldErrors("form", TERMS_AGREED_KEY));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationFinanceMarkAsCompleteFailWithoutStateAid() throws Exception {
        when(applicantRestService.getSection(any(), any(), any())).thenReturn(sectionBuilder.withSection(newSectionResource().withType(SectionType.PROJECT_COST_FINANCES).build()).build());

        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(application.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), application.getId())).thenReturn(restSuccess(userApplicationRole));

        when(organisationService.getOrganisationForUser(anyLong())).thenReturn(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build());
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1))
                .andExpect(model().attributeHasFieldErrors("form", STATE_AID_AGREED_KEY));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationFinanceMarkAsCompleteSuccessWithoutStateAidForAcademic() throws Exception {
        when(applicantRestService.getSection(any(), any(), any())).thenReturn(sectionBuilder.withSection(newSectionResource().withType(SectionType.PROJECT_COST_FINANCES).build()).build());
        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(application.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), application.getId())).thenReturn(restSuccess(userApplicationRole));

        when(organisationService.getOrganisationForUser(anyLong())).thenReturn(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).build());
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/**"));
    }

    @Test
    public void testApplicationYourOrganisationMarkAsCompleteFailWithoutOrganisationSize() throws Exception {
        when(applicantRestService.getSection(any(), any(), any())).thenReturn(sectionBuilder.withSection(newSectionResource().withType(SectionType.ORGANISATION_FINANCES).build()).build());
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationFormSubmitMarkSectionInComplete() throws Exception {

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(MARK_SECTION_AS_INCOMPLETE, String.valueOf(sectionId))

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFormSubmitMarkAsComplete() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(MARK_AS_COMPLETE, "12")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/" + sectionId + "**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testAcademicFinanceProjectCostsQuestionSubmitAlsoMarksOrganisationFinanceAndYourFundingAsNotRequired() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
                        .param(TERMS_AGREED_KEY, "1")
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    public void testApplicationFormSubmitMarkAsIncomplete() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(MARK_AS_INCOMPLETE, "3")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/" + sectionId + "**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFormSubmitGivesNoValidationErrorsIfNoQuestionIsEmptyOnSectionSubmit() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("formInput[1]", "Question 1 Response")
                        .param("formInput[2]", "Question 2 Response")
                        .param("submit-section", "Save")
        ).andExpect(status().is3xxRedirection());
    }

    // See INFUND-1222 - not checking empty values on save now (only on mark as complete).
    @Test
    public void testApplicationFormSubmitGivesNoValidationErrorsIfQuestionIsEmptyOnSectionSubmit() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("formInput[1]", "")
                        .param("formInput[2]", "Question 2 Response")
                        .param("submit-section", "Save")
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    public void testApplicationFormSubmitNotAllowedMarkAsComplete() throws Exception {
        // Question should not be marked as complete, since the input is not valid.

        when(applicationSaver.saveApplicationForm(any(ApplicationResource.class), anyLong(), any(ApplicationForm.class), anyLong(), anyLong(), any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean()))
                .thenReturn(new ValidationMessages(globalError("please.enter.some.text")));

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("formInput[1]", "")
                        .param(MARK_AS_COMPLETE, "1")
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1))
                .andExpect(model().hasErrors());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }

    @Test
    public void testApplicationFormSubmitAssignQuestion() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("formInput[1]", "Question 1 Response")
                        .param("formInput[2]", "Question 2 Response")
                        .param("formInput[3]", "Question 3 Response")
                        .param("submit-section", "Save")
                        .param("assign_question", questionId + "_" + loggedInUser.getId())
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testDeleteCost() throws Exception {
        String sectionId = "1";
        Long costId = 1L;

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("remove_cost", String.valueOf(costId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/" + application.getId() + "/form/section/" + sectionId));
    }

    @Test
    public void testRedirectToSectionUnique() throws Exception {
        SectionResource financeSection = newSectionResource().withType(SectionType.FINANCE).build();
        when(sectionService.getSectionsForCompetitionByType(competitionResource.getId(), SectionType.FINANCE))
                .thenReturn(asList(financeSection));

        mockMvc.perform(
                get("/application/{applicationId}/form/{sectionType}", application.getId(), SectionType.FINANCE))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/" + application.getId() + "/form/section/" + financeSection.getId()));

    }

    @Test
    public void testRedirectToSectionNotUnique() throws Exception {
        SectionResource financeSection = newSectionResource().withType(SectionType.FINANCE).build();
        when(sectionService.getSectionsForCompetitionByType(competitionResource.getId(), SectionType.FINANCE))
                .thenReturn(asList(financeSection, newSectionResource().build()));

        mockMvc.perform(
                get("/application/{applicationId}/form/{sectionType}", application.getId(), SectionType.FINANCE))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/" + application.getId()));

    }

    @Test
    public void testRedirectToSectionMissing() throws Exception {
        when(sectionService.getSectionsForCompetitionByType(competitionResource.getId(), SectionType.FINANCE))
                .thenReturn(asList());

        mockMvc.perform(
                get("/application/{applicationId}/form/{sectionType}", application.getId(), SectionType.FINANCE))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/" + application.getId()));
    }

    @Test
    public void testOverheadFile_overheadFileSaverIsCalledOnFormSubmit() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
                        .param(TERMS_AGREED_KEY, "1")
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    public void testOverheadFile_errorsAreNotShownOnOverheadFileRequest() throws Exception {
        mockMvc.perform(
                fileUpload("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(OverheadFileSaver.OVERHEAD_FILE_SUBMIT, "")
        ).andExpect(status().is2xxSuccessful())
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void testOverheadFile_pageIsNotRedirectedOnOverheadFileUploadRequest() throws Exception {
        mockMvc.perform(
                fileUpload("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(OverheadFileSaver.OVERHEAD_FILE_SUBMIT, "")
        ).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testApplicationFormWithOpenSectionForApplicant() throws Exception {

        Long currentSectionId = sectionResources.get(2).getId();
        ApplicationResource application = newApplicationResource().build();
        RoleResource role = newRoleResource().withType(UserRoleType.COLLABORATOR).build();
        ProcessRoleResource processRole = newProcessRoleResource().withOrganisation(2L).withRole(role).build();

        when(applicationService.getById(1L)).thenReturn(application);
        when(processRoleService.getByApplicationId(application.getId())).thenReturn(asList(processRole));
        when(applicantRestService.getSection(processRole.getUser(), application.getId(), currentSectionId)).thenReturn(sectionBuilder.build());

        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        MvcResult result = mockMvc.perform(get("/application/1/form/section/" + currentSectionId + "/2").header("referer", "/application/1"))
                .andExpect(view().name("application-form"))
                .andReturn();

        Object viewModelResult = result.getModelAndView().getModelMap().get("model");
        assertEquals(YourFinancesSectionViewModel.class, viewModelResult.getClass());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class), any(Optional.class));
    }
}
