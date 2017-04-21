package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.builder.SectionResourceBuilder;
import org.innovateuk.ifs.application.finance.view.DefaultFinanceFormHandler;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceOverviewViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.populator.*;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.resource.cost.Materials;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.isA;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationFormControllerTest extends BaseControllerMockMVCTest<ApplicationFormController> {

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

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
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private OverheadFileSaver overheadFileSaver;

    @Mock
    private DefaultFinanceFormHandler defaultFinanceFormHandler;

    private ApplicationResource application;
    private Long sectionId;
    private Long questionId;
    private Long formInputId;
    private Long costId;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    @Override
    protected ApplicationFormController supplyControllerUnderTest() {
        return new ApplicationFormController();
    }

    @Before
    @Override
    public void setUp() {

        // Process mock annotations
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
        formInputId = 111L;
        costId = 1L;

        // save actions should always succeed.
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), eq(""), anyBoolean())).thenReturn(restSuccess(new ValidationMessages(fieldError("value", "", "Please enter some text 123"))));
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean())).thenReturn(restSuccess(noErrors()));
        when(organisationService.getOrganisationById(anyLong())).thenReturn(organisations.get(0));
        when(overheadFileSaver.handleOverheadFileRequest(any())).thenReturn(noErrors());
        when(financeHandler.getFinanceFormHandler(any())).thenReturn(defaultFinanceFormHandler);

        ApplicationFinanceOverviewViewModel financeOverviewViewModel = new ApplicationFinanceOverviewViewModel();
        when(applicationFinanceOverviewModelManager.getFinanceDetailsViewModel(competitionResource.getId(), application.getId())).thenReturn(financeOverviewViewModel);

        FinanceViewModel financeViewModel = new FinanceViewModel();
        financeViewModel.setOrganisationGrantClaimPercentage(76);

        when(defaultFinanceModelManager.getFinanceViewModel(anyLong(), anyList(), anyLong(), any(Form.class), anyLong())).thenReturn(financeViewModel);
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
        assertEquals(OpenSectionViewModel.class, viewModelResult.getClass());
        OpenSectionViewModel viewModel = (OpenSectionViewModel) viewModelResult;
        assertEquals(application, viewModel.getApplication().getCurrentApplication());
        assertEquals(application1Organisations.get(0), viewModel.getLeadOrganisation());
        assertEquals(application1Organisations.size(), viewModel.getApplicationOrganisations().size());
        assertTrue(viewModel.getApplicationOrganisations().contains(application1Organisations.get(0)));
        assertTrue(viewModel.getApplicationOrganisations().contains(application1Organisations.get(1)));
        assertEquals(Boolean.TRUE, viewModel.getUserIsLeadApplicant());
        assertEquals(users.get(0), viewModel.getLeadApplicant());
        assertEquals(currentSectionId, viewModel.getCurrentSectionId());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationFormWithOpenSectionWhenTraversedFromSummaryPage() throws Exception {
        Long currentSectionId = sectionResources.get(2).getId();

        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        MvcResult result = mockMvc.perform(get("/application/1/form/section/" + currentSectionId).header("referer", "/application/1/summary"))
                .andExpect(view().name("application-form"))
                .andReturn();

        Object viewModelResult = result.getModelAndView().getModelMap().get("model");
        assertEquals(OpenSectionViewModel.class, viewModelResult.getClass());
        OpenSectionViewModel viewModel = (OpenSectionViewModel) viewModelResult;
        assertEquals(application, viewModel.getApplication().getCurrentApplication());
        assertEquals(application1Organisations.get(0), viewModel.getLeadOrganisation());
        assertEquals(application1Organisations.size(), viewModel.getApplicationOrganisations().size());
        assertTrue(viewModel.getApplicationOrganisations().contains(application1Organisations.get(0)));
        assertTrue(viewModel.getApplicationOrganisations().contains(application1Organisations.get(1)));
        assertEquals(Boolean.TRUE, viewModel.getUserIsLeadApplicant());
        assertEquals(users.get(0), viewModel.getLeadApplicant());
        assertEquals(currentSectionId, viewModel.getCurrentSectionId());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationFormWithOpenFinanceSection() throws Exception {
        Long currentSectionId = sectionResources.get(6).getId();

        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);

        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(application.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), application.getId())).thenReturn(restSuccess(userApplicationRole));

        MvcResult result = mockMvc.perform(get("/application/1/form/section/" + currentSectionId))
                .andExpect(view().name("application-form"))
                .andReturn();

        Object viewModelResult = result.getModelAndView().getModelMap().get("model");
        assertEquals(OpenFinanceSectionViewModel.class, viewModelResult.getClass());
        OpenFinanceSectionViewModel viewModel = (OpenFinanceSectionViewModel) viewModelResult;
        assertEquals(application, viewModel.getApplication().getCurrentApplication());
        assertEquals(Boolean.TRUE, viewModel.getUserIsLeadApplicant());
        assertEquals(users.get(0), viewModel.getLeadApplicant());
        assertEquals(currentSectionId, viewModel.getCurrentSectionId());
        assertEquals(Boolean.TRUE, viewModel.getHasFinanceSection());
        assertEquals(currentSectionId, viewModel.getFinanceSectionId());
        assertEquals(Boolean.TRUE, viewModel.getApplication().getAllReadOnly());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testQuestionPage() throws Exception {
        ApplicationResource application = applications.get(0);

        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        when(applicationService.getById(application.getId())).thenReturn(application);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        // just check if these pages are not throwing errors.
        mockMvc.perform(get("/application/1/form/question/10")).andExpect(status().isOk());
        mockMvc.perform(get("/application/1/form/question/21")).andExpect(status().isOk());
        mockMvc.perform(get("/application/1/form/section/1")).andExpect(status().isOk());
        mockMvc.perform(get("/application/1/form/section/2")).andExpect(status().isOk());
        mockMvc.perform(get("/application/1/form/question/edit/1")).andExpect(status().isOk());
        mockMvc.perform(get("/application/1/form/question/edit/21")).andExpect(status().isOk());
    }

    @Test
    public void testQuestionSubmit() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);
        mockMvc.perform(
                post("/application/1/form/question/1")
                        .param("formInput[1]", "Some Value...")

        )
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testQuestionSubmitEdit() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);
        mockMvc.perform(
                post("/application/1/form/question/1")
                        .param(ApplicationFormController.EDIT_QUESTION, "1_2")
        )
                .andExpect(view().name("application-form"));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testQuestionSubmitAssign() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);
        mockMvc.perform(
                post("/application/1/form/question/1")
                        .param(ApplicationFormController.ASSIGN_QUESTION_PARAM, "1_2")

        )
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testQuestionSubmitMarkAsCompleteQuestion() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);
        mockMvc.perform(
                post("/application/1/form/question/1")
                        .param(ApplicationFormController.MARK_AS_COMPLETE, "1")
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    public void testQuestionSubmitSaveElement() throws Exception {
        ApplicationResource application = applications.get(0);

        when(applicationService.getById(application.getId())).thenReturn(application);

        mockMvc.perform(post("/application/1/form/question/1"))
                .andExpect(status().is3xxRedirection());
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
    public void testAjaxAddCost() throws Exception {
        FinanceRowItem costItem = new Materials();
        when(defaultFinanceFormHandler.addCostWithoutPersisting(anyLong(), anyLong(), anyLong())).thenReturn(costItem);
        mockMvc.perform(
                get("/application/{applicationId}/form/add_cost/{questionId}", application.getId(), questionId)
        );
    }

    @Test
    public void testAjaxRemoveCost() throws Exception {
        ValidationMessages costItemMessages = new ValidationMessages();
        when(financeRowRestService.add(anyLong(), anyLong(), any())).thenReturn(restSuccess(costItemMessages));
        mockMvc.perform(
                get("/application/{applicationId}/form/remove_cost/{costId}", application.getId(), costId)
        );
    }

    @Test
    public void testApplicationFormSubmit() throws Exception {

        LocalDate futureDate = LocalDate.now().plusDays(1);

        MvcResult result = mockMvc.perform(
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
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME))
                .andReturn();
    }

    @Test
    public void testNotRequestingFunding() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();
        when(sectionService.getById(1L)).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).build());
        QuestionResource financeQuestion = newQuestionResource().build();
        when(questionService.getQuestionByCompetitionIdAndFormInputType(application.getCompetition(), FormInputType.FINANCE)).thenReturn(ServiceResult.serviceSuccess(financeQuestion));
        when(financeRowRestService.add(anyLong(), eq(financeQuestion.getId()), any(GrantClaim.class))).thenReturn(restSuccess(ValidationMessages.noErrors()));
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.NOT_REQUESTING_FUNDING, "1")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));

        ArgumentCaptor<GrantClaim> argument = ArgumentCaptor.forClass(GrantClaim.class);
        verify(financeRowRestService).add(anyLong(), eq(financeQuestion.getId()), argument.capture());
        assertThat(argument.getValue().getGrantClaimPercentage(), equalTo(0));
        verify(sectionService, times(2)).markAsNotRequired(anyLong(), anyLong(), anyLong());
    }

    @Test
    public void testRequestingFunding() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();
        when(sectionService.getById(1L)).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).build());
        QuestionResource financeQuestion = newQuestionResource().build();
        when(questionService.getQuestionByCompetitionIdAndFormInputType(application.getCompetition(), FormInputType.FINANCE)).thenReturn(ServiceResult.serviceSuccess(financeQuestion));
        when(financeRowRestService.add(anyLong(), eq(financeQuestion.getId()), any(GrantClaim.class))).thenReturn(restSuccess(ValidationMessages.noErrors()));
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.REQUESTING_FUNDING, "1")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));

        ArgumentCaptor<GrantClaim> argument = ArgumentCaptor.forClass(GrantClaim.class);
        verify(financeRowRestService).add(anyLong(), eq(financeQuestion.getId()), argument.capture());
        assertThat(argument.getValue().getGrantClaimPercentage(), equalTo(0));
        verify(sectionService, times(2)).markAsInComplete(anyLong(), anyLong(), anyLong());
    }

    @Test
    public void testSubmitFinanceSubSectionWithRedirectToYourFinances() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).build());
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE)).thenReturn(sectionResourceBuilder.withType(SectionType.FINANCE).build(1));
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
                        .param(ApplicationFormController.TERMS_AGREED_KEY, "1")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFinanceMarkAsCompleteFailWithTerms() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).build());

        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(application.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), application.getId())).thenReturn(restSuccess(userApplicationRole));

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1))
                .andExpect(model().attributeHasFieldErrors("form", ApplicationFormController.TERMS_AGREED_KEY));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationFinanceMarkAsCompleteFailWithStateAid() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your project costs")).withType(SectionType.PROJECT_COST_FINANCES).build());

        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(application.getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), application.getId())).thenReturn(restSuccess(userApplicationRole));

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1))
                .andExpect(model().attributeHasFieldErrors("form", ApplicationFormController.STATE_AID_AGREED_KEY));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationYourOrganisationMarkAsCompleteFailWithoutOrganisationSize() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your organisation")).withType(SectionType.ORGANISATION_FINANCES).build());
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationFormSubmitMarkSectionInComplete() throws Exception {

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(ApplicationFormController.MARK_SECTION_AS_INCOMPLETE, String.valueOf(sectionId))

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFormSubmitMarkAsComplete() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(ApplicationFormController.MARK_AS_COMPLETE, "12")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/" + sectionId + "**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }


    @Test
    public void testAcademicFinanceFundingQuestionSubmitAlsoMarksOrganisationFinanceAsNotRequired() throws Exception {

        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();

        when(organisationService.getOrganisationType(any(), any())).thenReturn(2L);
        when(overheadFileSaver.handleOverheadFileRequest(any())).thenReturn(new ValidationMessages());
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).build());
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.ORGANISATION_FINANCES)).thenReturn(sectionResourceBuilder.withType(SectionType.ORGANISATION_FINANCES).build(1));
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
                        .param(ApplicationFormController.TERMS_AGREED_KEY, "1")
        ).andExpect(status().is3xxRedirection());

        verify(sectionService, times(1)).markAsComplete(isA(Long.class), isA(Long.class), isA(Long.class));
        verify(sectionService, times(1)).markAsNotRequired(isA(Long.class), isA(Long.class), isA(Long.class));

    }

    @Test
    public void testAcademicFinanceProjectCostsQuestionSubmitAlsoMarksOrganisationFinanceAsNotRequired() throws Exception {

        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();

        when(organisationService.getOrganisationType(any(), any())).thenReturn(2L);
        when(overheadFileSaver.handleOverheadFileRequest(any())).thenReturn(new ValidationMessages());
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).build());
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.PROJECT_COST_FINANCES)).thenReturn(sectionResourceBuilder.withType(SectionType.PROJECT_COST_FINANCES).build(1));
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
                        .param(ApplicationFormController.TERMS_AGREED_KEY, "1")
        ).andExpect(status().is3xxRedirection());

        verify(sectionService, times(1)).markAsComplete(isA(Long.class), isA(Long.class), isA(Long.class));
        verify(sectionService, times(1)).markAsNotRequired(isA(Long.class), isA(Long.class), isA(Long.class));
    }

    @Test
    public void testApplicationFormSubmitMarkAsIncomplete() throws Exception {

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(ApplicationFormController.MARK_AS_INCOMPLETE, "3")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/" + sectionId + "**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorsWithEmptyFields() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.name", "")
                        .param("application.resubmission", "")
                        .param("application.startDate", "")
                        .param("application.startDate.year", "")
                        .param("application.startDate.dayOfMonth", "")
                        .param("application.startDate.monthValue", "")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");

        assertEquals("NotBlank", bindingResult.getFieldError("application.name").getCode());
        assertEquals("NotNull", bindingResult.getFieldError("application.durationInMonths").getCode());
        assertEquals("FutureLocalDate", bindingResult.getFieldError("application.startDate").getCode());
        assertEquals("NotNull", bindingResult.getFieldError("application.resubmission").getCode());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorsWithResubmissionSelected() throws Exception {

        LocalDate yesterday = LocalDate.now().minusDays(1L);

        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.resubmission", "1")
                        .param("application.previousApplicationNumber", "")
                        .param("application.previousApplicationTitle", "")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");

        assertEquals("FieldRequiredIf", bindingResult.getFieldError("application.previousApplicationNumber").getCode());
        assertEquals("FieldRequiredIf", bindingResult.getFieldError("application.previousApplicationTitle").getCode());

        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorsForPastDate() throws Exception {

        LocalDate yesterday = LocalDate.now().minusDays(1L);

        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.startDate", "")
                        .param("application.startDate.year", String.valueOf(yesterday.getDayOfYear()))
                        .param("application.startDate.dayOfMonth", String.valueOf(yesterday.getDayOfMonth()))
                        .param("application.startDate.monthValue", String.valueOf(yesterday.getMonthValue()))
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals("FutureLocalDate", bindingResult.getFieldError("application.startDate").getCode());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorForTooFewMonths() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.durationInMonths", "0")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals("Min", bindingResult.getFieldError("application.durationInMonths").getCode());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorForTooManyMonths() throws Exception {
        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.durationInMonths", "37")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals("Max", bindingResult.getFieldError("application.durationInMonths").getCode());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void testApplicationDetailsFormSubmitMarkAsComplete_returnsErrorsWithInvalidValues() throws Exception {

        LocalDate yesterday = LocalDate.now().minusDays(1L);

        MvcResult result = mockMvc.perform(
                post("/application/{applicationId}/form/question/{questionId}", application.getId(), questionId)
                        .param("mark_as_complete", questionId.toString())
                        .param("application.resubmission", "0")
                        .param("application.previousApplicationNumber", "")
                        .param("application.previousApplicationTitle", "")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");

        assertNull(bindingResult.getFieldError("application.previousApplicationNumber"));
        assertNull(bindingResult.getFieldError("application.previousApplicationTitle"));
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
    }

    @Test
    public void applicationDetailsFormSubmit_incorrectFileType() throws Exception {
        long formInputId = 2L;
        long processRoleId = 5L;
        String fileError = "file error";
        MockMultipartFile file = new MockMultipartFile("formInput[" + formInputId +"]", "filename.txt", "text/plain", "someText".getBytes());

        long fileQuestionId = 31L;
        when(formInputResponseRestService.createFileEntry(formInputId, application.getId(), processRoleId,
                file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes()))
                .thenReturn(restFailure(new Error(fileError,UNSUPPORTED_MEDIA_TYPE)));

        MvcResult result = mockMvc.perform(
            fileUpload("/application/{applicationId}/form/question/{questionId}", application.getId(), fileQuestionId)
                        .file(file)
                        .param("upload_file", "")
        ).andReturn();

        BindingResult bindingResult = (BindingResult) result.getModelAndView().getModel().get("org.springframework.validation.BindingResult.form");
        assertEquals(fileError, bindingResult.getFieldError("formInput[" + formInputId + "]").getCode());
    }

    @Test
    public void testApplicationFormSubmitGivesNoValidationErrorsIfNoQuestionIsEmptyOnSectionSubmit() throws Exception {
        Long userId = loggedInUser.getId();

        when(formInputResponseRestService.saveQuestionResponse(userId, application.getId(), 1L, "", false)).thenReturn(restSuccess(new ValidationMessages(globalError("Please enter some text"))));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
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
        Long userId = loggedInUser.getId();

        when(formInputResponseRestService.saveQuestionResponse(userId, application.getId(), 1L, "", false)).thenReturn(restSuccess(new ValidationMessages(globalError("Please enter some text"))));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
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

        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), eq(""), eq(false))).thenReturn(restSuccess(new ValidationMessages(globalError("please.enter.some.text"))));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("formInput[1]", "")
                        .param(ApplicationFormController.MARK_AS_COMPLETE, "1")
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1))
                .andExpect(model().hasErrors());
        verify(applicationNavigationPopulator).addAppropriateBackURLToModel(any(Long.class), any(Model.class), any(SectionResource.class));
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
    public void testSaveFormElement() throws Exception {
        String value = "Form Input " + formInputId + " Response";

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", formInputId.toString())
                        .param("fieldName", "formInput[" + formInputId + "]")
                        .param("value", value)
        ).andExpect(status().isOk());

        Mockito.inOrder(formInputResponseRestService).verify(formInputResponseRestService, calls(1)).saveQuestionResponse(loggedInUser.getId(), application.getId(), formInputId, value, false);
    }

    @Test
    public void testSaveFormElementApplicationTitle() throws Exception {
        String value = "New application title #216";
        String fieldName = "application.name";

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", "")
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andExpect(content().json("{\"success\":\"true\"}"));

        Mockito.inOrder(applicationService).verify(applicationService, calls(1)).save(any(ApplicationResource.class));
    }

    @Test
    public void testSaveFormElementEmptyApplicationTitle() throws Exception {
        String value = "";
        String fieldName = "application.name";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", "")
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementSpacesApplicationTitle() throws Exception {
        String value = " ";
        String fieldName = "application.name";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", "")
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementApplicationDuration() throws Exception {
        String value = "12";
        String fieldName = "application.durationInMonths";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", "")
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        assertEquals(jsonExpectedContent, content);
        Mockito.inOrder(applicationService).verify(applicationService, calls(1)).save(any(ApplicationResource.class));
    }

    @Test
    public void testSaveFormElementApplicationInvalidDurationNonInteger() throws Exception {
        String value = "aaaa";
        String fieldName = "application.durationInMonths";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", "")
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"false\"}";
        assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementApplicationInvalidDurationLength() throws Exception {
        String value = "37";
        String fieldName = "application.durationInMonths";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", "")
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        Assert.assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementCostSubcontracting() throws Exception {
        String value = "123";
        String questionId = "cost-subcontracting-13-subcontractingCost";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", "subcontracting_costs-cost-13")
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementCostSubcontractingWithErrors() throws Exception {
        String value = "BOB";
        String questionId = "cost-subcontracting-13-subcontractingCost";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", "bobbins")
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        Assert.assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementFinancePosition() throws Exception {
        String value = "222";
        String questionId = "financePosition-organisationSize";
        String fieldName = "financePosition.organisationSize";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        Assert.assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementApplicationValidStartDateDDMMYYYY() throws Exception {
        String value = "25-10-2025";
        String questionId = "application_details-startdate";
        String fieldName = "application.startDate";

        MvcResult result = mockMvc.perform(
                post("/application/1/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        Assert.assertEquals(jsonExpectedContent, content);
        Mockito.inOrder(applicationService).verify(applicationService, calls(1)).save(any(ApplicationResource.class));

    }

    @Test
    public void testSaveFormElementApplicationInvalidStartDateMMDDYYYY() throws Exception {
        String value = "10-25-2025";
        String questionId = "application_details-startdate";
        String fieldName = "application.startDate";

        mockMvc.perform(
                post("/application/1/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)

        ).andExpect(status().isOk())
                .andExpect(content().json("{\"success\":\"false\"}"));
    }

    @Test
    public void testSaveFormElementApplicationStartDateValidDay() throws Exception {
        String value = "25";
        String questionId = "application_details-startdate_day";
        String fieldName = "application.startDate.dayOfMonth";

        MvcResult result = mockMvc.perform(
                post("/application/1/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        assertEquals(jsonExpectedContent, content);
        Mockito.inOrder(applicationService).verify(applicationService, calls(1)).save(any(ApplicationResource.class));

    }

    //TODO: Change this to AutosaveElementException
    @Test
    public void testSaveFormElementApplicationAttributeInvalidDay() throws Exception {
        String questionId = "application_details-startdate_day";
        String fieldName = "application.startDate.dayOfMonth";
        String value = "35";

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().json("{\"success\":\"false\"}"));
    }

    @Test
    public void testSaveFormElementApplicationAttributeInvalidMonth() throws Exception {
        String questionId = "application_details-startdate_month";
        String fieldName = "application.startDate.monthValue";
        String value = "13";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        log.info("Response : " + content);

        String jsonExpectedContent = "{\"success\":\"false\"}";
        assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementApplicationAttributeInvalidYear() throws Exception {

        String questionId = "application_details-startdate_year";
        String fieldName = "application.startDate.year";
        String value = "2015";

        when(sectionService.getById(anyLong())).thenReturn(null);

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(content().json("{\"success\":\"true\"}"));
    }

    @Test
    public void testSaveFormElementApplicationResubmission() throws Exception {
        String value = "true";
        String questionId = "application_details-resubmission";
        String fieldName = "application.resubmission";

        MvcResult result = mockMvc.perform(
                post("/application/1/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        Assert.assertEquals(jsonExpectedContent, content);
        Mockito.inOrder(applicationService).verify(applicationService, calls(1)).save(any(ApplicationResource.class));

    }

    @Test
    public void testSaveFormElementApplicationPreviousApplicationNumber() throws Exception {
        String value = "999";
        String questionId = "application_details-previousapplicationnumber";
        String fieldName = "application.previousApplicationNumber";

        MvcResult result = mockMvc.perform(
                post("/application/1/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        Assert.assertEquals(jsonExpectedContent, content);
        Mockito.inOrder(applicationService).verify(applicationService, calls(1)).save(any(ApplicationResource.class));

    }

    @Test
    public void testSaveFormElementApplicationPreviousApplicationTitle() throws Exception {
        String value = "test";
        String questionId = "application_details-previousapplicationtitle";
        String fieldName = "application.previousApplicationTitle";

        MvcResult result = mockMvc.perform(
                post("/application/1/form/123/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        Assert.assertEquals(jsonExpectedContent, content);
        Mockito.inOrder(applicationService).verify(applicationService, calls(1)).save(any(ApplicationResource.class));

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
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();

        when(overheadFileSaver.handleOverheadFileRequest(any())).thenReturn(new ValidationMessages());
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).build());
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE)).thenReturn(sectionResourceBuilder.withType(SectionType.FINANCE).build(1));
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
                        .param(ApplicationFormController.TERMS_AGREED_KEY, "1")
        ).andExpect(status().is3xxRedirection());

        verify(overheadFileSaver, times(1)).handleOverheadFileRequest(isA(HttpServletRequest.class));
    }

    @Test
    public void testOverheadFile_errorsAreNotShownOnOverheadFileRequest() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();

        when(overheadFileSaver.handleOverheadFileRequest(any())).thenReturn(new ValidationMessages());


        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.addError(new org.innovateuk.ifs.commons.error.Error("save_application_error", HttpStatus.FORBIDDEN));
        when(overheadFileSaver.isOverheadFileRequest(any())).thenReturn(true);
        when(financeHandler.getFinanceFormHandler(any()).update(any(), any(), any(), any())).thenReturn(validationMessages);
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).build());
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE)).thenReturn(sectionResourceBuilder.withType(SectionType.FINANCE).build(1));
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(OverheadFileSaver.OVERHEAD_FILE_SUBMIT, "")
        ).andExpect(status().is2xxSuccessful())
                .andExpect(model().hasNoErrors());

        verify(overheadFileSaver, times(1)).handleOverheadFileRequest(isA(HttpServletRequest.class));
    }

    @Test
    public void testOverheadFile_pageIsNotRedirectedOnOverheadFileUploadRequest() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();

        when(overheadFileSaver.handleOverheadFileRequest(any())).thenReturn(new ValidationMessages());
        when(overheadFileSaver.isOverheadFileRequest(any())).thenReturn(true);

        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your funding")).withType(SectionType.FUNDING_FINANCES).build());
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE)).thenReturn(sectionResourceBuilder.withType(SectionType.FINANCE).build(1));
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(OverheadFileSaver.OVERHEAD_FILE_SUBMIT, "")
        ).andExpect(status().is2xxSuccessful());

        verify(overheadFileSaver, times(1)).handleOverheadFileRequest(isA(HttpServletRequest.class));
    }
}
