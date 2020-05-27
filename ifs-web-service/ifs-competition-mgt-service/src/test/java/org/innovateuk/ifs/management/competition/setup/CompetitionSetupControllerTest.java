package org.innovateuk.ifs.management.competition.setup;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.management.competition.setup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupSummaryForm;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.fundinginformation.form.AdditionalInfoForm;
import org.innovateuk.ifs.management.competition.setup.initialdetail.form.InitialDetailsForm;
import org.innovateuk.ifs.management.competition.setup.initialdetail.populator.ManageInnovationLeadsModelPopulator;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
import org.innovateuk.ifs.management.fixtures.CompetitionFundersFixture;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeResourceBuilder.newCompetitionTypeResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link CompetitionSetupController}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionSetupControllerTest extends BaseControllerMockMVCTest<CompetitionSetupController> {

    private static final Long COMPETITION_ID = 12L;
    private static final String URL_PREFIX = "/competition/setup";

    @Mock
    private CategoryRestService categoryRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Mock
    private Validator validator;

    @Mock
    private ManageInnovationLeadsModelPopulator manageInnovationLeadsModelPopulator;

    @Mock
    private UserService userService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Override
    protected CompetitionSetupController supplyControllerUnderTest() {
        return new CompetitionSetupController();
    }

    @Before
    public void setUp() {

        when(userRestService.findByUserRole(COMP_ADMIN))
                .thenReturn(
                        restSuccess(newUserResource()
                                .withFirstName("Comp")
                                .withLastName("Admin")
                                .build(1))
                );

        when(userRestService.findByUserRole(INNOVATION_LEAD))
                .thenReturn(
                        restSuccess(newUserResource()
                                .withFirstName("Comp")
                                .withLastName("Technologist")
                                .build(1))
                );

        List<InnovationSectorResource> innovationSectorResources = newInnovationSectorResource()
                .withName("A Innovation Sector")
                .withId(1L)
                .build(1);
        when(categoryRestService.getInnovationSectors()).thenReturn(restSuccess(innovationSectorResources));

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withName("A Innovation Area")
                .withId(2L)
                .withSector(1L)
                .build(1);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        List<CompetitionTypeResource> competitionTypeResources = newCompetitionTypeResource()
                .withId(1L)
                .withName("Programme")
                .withCompetitions(singletonList(COMPETITION_ID))
                .build(1);
        when(competitionRestService.getCompetitionTypes()).thenReturn(restSuccess(competitionTypeResources));

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(true);
    }

    @Test
    public void initCompetitionSetupSection() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        when(competitionSetupService.isCompetitionReadyToOpen(competition)).thenReturn(FALSE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute(SETUP_READY_KEY, FALSE))
                .andExpect(model().attribute(READY_TO_OPEN_KEY, FALSE));
    }

    @Test
    public void initCompetitionSetupSectionSetupComplete() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        when(competitionSetupService.isCompetitionReadyToOpen(competition)).thenReturn(TRUE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute(SETUP_READY_KEY, TRUE))
                .andExpect(model().attribute(READY_TO_OPEN_KEY, FALSE));
    }

    @Test
    public void initCompetitionSetupSectionReadyToOpen() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        when(competitionSetupService.isCompetitionReadyToOpen(competition)).thenReturn(FALSE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute(SETUP_READY_KEY, FALSE))
                .andExpect(model().attribute(READY_TO_OPEN_KEY, TRUE));
    }

    @Test
    public void editCompetitionSetupSectionInitial() throws Exception {
        InitialDetailsForm competitionSetupInitialDetailsForm = new InitialDetailsForm();
        competitionSetupInitialDetailsForm.setTitle("Test competition");
        competitionSetupInitialDetailsForm.setCompetitionTypeId(2L);

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withName("Test competition")
                .withCompetitionCode("Code")
                .withCompetitionType(2L)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        CompetitionSetupForm compSetupForm = mock(CompetitionSetupForm.class);
        when(competitionSetupService.getSectionFormData(competition, CompetitionSetupSection.INITIAL_DETAILS))
                .thenReturn(compSetupForm);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute("competitionSetupForm", compSetupForm));

        verify(competitionSetupService).populateCompetitionSectionModelAttributes(
                eq(competition),
                eq(CompetitionSetupSection.INITIAL_DETAILS)
        );
    }

    @Test
    public void setSectionAsIncomplete() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withName(
                "Test competition").withCompetitionCode("Code").withCompetitionType(2L).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupRestService.markSectionIncomplete(anyLong(),
                any(CompetitionSetupSection.class))).thenReturn(restSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"));
    }

    @Test
    public void generateCompetitionCode() throws Exception {
        ZonedDateTime time = ZonedDateTime.of(2016, 12, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withName("Test competition")
                .withCompetitionCode("Code")
                .withCompetitionType(2L)
                .withStartDate(time)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupRestService.generateCompetitionCode(COMPETITION_ID, time))
                .thenReturn(restSuccess("1612-1"));

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/generateCompetitionCode?day=01&month=12&year=2016"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", is("1612-1")));
    }

    @Test
    public void submitUnrestrictedSectionInitialDetailsInvalidWithRequiredFieldsEmpty() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        MvcResult mvcResult = mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
                .param("unrestricted", "1"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors(
                        COMPETITION_SETUP_FORM_KEY,
                        "executiveUserId",
                        "title",
                        "innovationLeadUserId",
                        "openingDate",
                        "innovationSectorCategoryId",
                        "innovationAreaCategoryIds",
                        "competitionTypeId",
                        "stateAid"))
                .andExpect(view().name("competition/setup"))
                .andReturn();

        InitialDetailsForm initialDetailsForm = (InitialDetailsForm) mvcResult.getModelAndView().getModel()
                .get(COMPETITION_SETUP_FORM_KEY);

        BindingResult bindingResult = initialDetailsForm.getBindingResult();

        bindingResult.getAllErrors();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(10, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("executiveUserId"));
        assertEquals(
                "Please select a Portfolio Manager.",
                bindingResult.getFieldError("executiveUserId").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("title"));
        assertEquals(
                "Please enter a title.",
                bindingResult.getFieldError("title").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("innovationLeadUserId"));
        assertEquals(
                "Please select an Innovation Lead.",
                bindingResult.getFieldError("innovationLeadUserId").getDefaultMessage());
        assertEquals(bindingResult.getFieldErrorCount("openingDate"), 2);
        List<String> errorsOnOpeningDate = bindingResult.getFieldErrors("openingDate").stream()
                .map(fieldError -> fieldError.getDefaultMessage()).collect(toList());
        assertTrue(errorsOnOpeningDate.contains("Please enter a valid date."));
        assertTrue(errorsOnOpeningDate.contains("Please enter a future date."));
        assertTrue(bindingResult.hasFieldErrors("innovationSectorCategoryId"));
        assertEquals(
                "Please select an innovation sector.",
                bindingResult.getFieldError("innovationSectorCategoryId").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("innovationAreaCategoryIds"));
        assertEquals(
                "Please select an innovation area.",
                bindingResult.getFieldError("innovationAreaCategoryIds").getDefaultMessage());
        assertTrue(bindingResult.hasFieldErrors("competitionTypeId"));
        assertEquals(
                "Please select a competition type.",
                bindingResult.getFieldError("competitionTypeId").getDefaultMessage()
        );
        assertEquals(
                "Enter a valid funding type.",
                bindingResult.getFieldError("fundingType").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("stateAid"));
        assertEquals(
                "Please select a state aid option.",
                bindingResult.getFieldError("stateAid").getDefaultMessage()
        );

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void submitSectionInitialDetailsInvalidWithRequiredFieldsEmpty() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        MvcResult mvcResult = mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors(
                        COMPETITION_SETUP_FORM_KEY,
                        "executiveUserId",
                        "title",
                        "innovationLeadUserId",
                        "openingDate",
                        "innovationSectorCategoryId",
                        "innovationAreaCategoryIds",
                        "stateAid"
                ))
                .andExpect(view().name("competition/setup"))
                .andReturn();

        InitialDetailsForm initialDetailsForm = (InitialDetailsForm) mvcResult.getModelAndView().getModel()
                .get(COMPETITION_SETUP_FORM_KEY);

        BindingResult bindingResult = initialDetailsForm.getBindingResult();

        bindingResult.getAllErrors();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(7, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("executiveUserId"));
        assertEquals(
                "Please select a Portfolio Manager.",
                bindingResult.getFieldError("executiveUserId").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("title"));
        assertEquals(
                "Please enter a title.",
                bindingResult.getFieldError("title").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("innovationLeadUserId"));
        assertEquals(
                "Please select an Innovation Lead.",
                bindingResult.getFieldError("innovationLeadUserId").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("openingDate"));
        assertEquals(
                "Please enter a valid date.",
                bindingResult.getFieldError("openingDate").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("innovationSectorCategoryId"));
        assertEquals(
                "Please select an innovation sector.",
                bindingResult.getFieldError("innovationSectorCategoryId").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("innovationAreaCategoryIds"));
        assertEquals(
                "Please select an innovation area.",
                bindingResult.getFieldError("innovationAreaCategoryIds").getDefaultMessage()
        );
        assertTrue(bindingResult.hasFieldErrors("stateAid"));
        assertEquals(
                "Please select a state aid option.",
                bindingResult.getFieldError("stateAid").getDefaultMessage()
        );

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void submitUnrestrictedSectionInitialDetailsWithInvalidOpenDate() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        Integer invalidDateDay = 1;
        Integer invalidDateMonth = 1;
        Integer invalidDateYear = 1999;

        MvcResult mvcResult = mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
                .param("executiveUserId", "1")
                .param("openingDateDay", invalidDateDay.toString())
                .param("openingDateMonth", invalidDateMonth.toString())
                .param("openingDateYear", invalidDateYear.toString())
                .param("innovationSectorCategoryId", "1")
                .param("innovationAreaCategoryIds", "1", "2", "3")
                .param("competitionTypeId", "1")
                .param("fundingType", FundingType.GRANT.name())
                .param("innovationLeadUserId", "1")
                .param("title", "My competition")
                .param("unrestricted", "1")
                .param("stateAid", "true"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors(COMPETITION_SETUP_FORM_KEY, "openingDate"))
                .andExpect(view().name("competition/setup"))
                .andReturn();

        InitialDetailsForm initialDetailsForm = (InitialDetailsForm) mvcResult.getModelAndView().getModel()
                .get(COMPETITION_SETUP_FORM_KEY);

        assertEquals(new Long(1L), initialDetailsForm.getExecutiveUserId());
        assertEquals(invalidDateDay, initialDetailsForm.getOpeningDateDay());
        assertEquals(invalidDateMonth, initialDetailsForm.getOpeningDateMonth());
        assertEquals(invalidDateYear, initialDetailsForm.getOpeningDateYear());
        assertEquals(new Long(1L), initialDetailsForm.getInnovationSectorCategoryId());
        assertEquals(asList(1L, 2L, 3L), initialDetailsForm.getInnovationAreaCategoryIds());
        assertEquals(new Long(1L), initialDetailsForm.getCompetitionTypeId());
        assertEquals(new Long(1L), initialDetailsForm.getInnovationLeadUserId());
        assertEquals("My competition", initialDetailsForm.getTitle());

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void submitUnrestrictedSectionInitialDetailsWithInvalidFieldsExceedRangeMax() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        Integer invalidDateDay = 32;
        Integer invalidDateMonth = 13;
        Integer invalidDateYear = 10000;

        MvcResult mvcResult = mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
                .param("executiveUserId", "1")
                .param("openingDateDay", invalidDateDay.toString())
                .param("openingDateMonth", invalidDateMonth.toString())
                .param("openingDateYear", invalidDateYear.toString())
                .param("innovationSectorCategoryId", "1")
                .param("innovationAreaCategoryIds", "1", "2", "3")
                .param("competitionTypeId", "1")
                .param("fundingType", FundingType.GRANT.name())
                .param("innovationLeadUserId", "1")
                .param("title", "My competition")
                .param("unrestricted", "1")
                .param("stateAid", "true"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors(COMPETITION_SETUP_FORM_KEY, "openingDate"))
                .andExpect(view().name("competition/setup"))
                .andReturn();

        InitialDetailsForm initialDetailsForm = (InitialDetailsForm) mvcResult.getModelAndView().getModel()
                .get(COMPETITION_SETUP_FORM_KEY);

        assertEquals(new Long(1L), initialDetailsForm.getExecutiveUserId());
        assertEquals(invalidDateDay, initialDetailsForm.getOpeningDateDay());
        assertEquals(invalidDateMonth, initialDetailsForm.getOpeningDateMonth());
        assertEquals(invalidDateYear, initialDetailsForm.getOpeningDateYear());
        assertEquals(new Long(1L), initialDetailsForm.getInnovationSectorCategoryId());
        assertEquals(asList(1L, 2L, 3L), initialDetailsForm.getInnovationAreaCategoryIds());
        assertEquals(new Long(1L), initialDetailsForm.getCompetitionTypeId());
        assertEquals(new Long(1L), initialDetailsForm.getInnovationLeadUserId());
        assertEquals("My competition", initialDetailsForm.getTitle());

        BindingResult bindingResult = initialDetailsForm.getBindingResult();

        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("openingDate"));
        List<String> errorsOnOpeningDate = bindingResult.getFieldErrors("openingDate").stream()
                .map(fieldError -> fieldError.getDefaultMessage()).collect(toList());
        assertTrue(errorsOnOpeningDate.contains("Please enter a valid date."));
        assertTrue(errorsOnOpeningDate.contains("Please enter a future date."));

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void submitUnrestrictedSectionInitialDetailsWithInvalidFieldsExceedRangeMin() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        Integer invalidDateDay = 0;
        Integer invalidDateMonth = 0;
        Integer invalidDateYear = 1899;

        MvcResult mvcResult = mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
                .param("executiveUserId", "1")
                .param("openingDateDay", invalidDateDay.toString())
                .param("openingDateMonth", invalidDateMonth.toString())
                .param("openingDateYear", invalidDateYear.toString())
                .param("innovationSectorCategoryId", "1")
                .param("innovationAreaCategoryIds", "1", "2", "3")
                .param("competitionTypeId", "1")
                .param("fundingType", FundingType.GRANT.name())
                .param("innovationLeadUserId", "1")
                .param("title", "My competition")
                .param("unrestricted", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors(COMPETITION_SETUP_FORM_KEY, "openingDate"))
                .andExpect(view().name("competition/setup"))
                .andReturn();

        InitialDetailsForm initialDetailsForm = (InitialDetailsForm) mvcResult.getModelAndView().getModel()
                .get(COMPETITION_SETUP_FORM_KEY);

        BindingResult bindingResult = initialDetailsForm.getBindingResult();

        assertEquals(new Long(1L), initialDetailsForm.getExecutiveUserId());
        assertEquals(invalidDateDay, initialDetailsForm.getOpeningDateDay());
        assertEquals(invalidDateMonth, initialDetailsForm.getOpeningDateMonth());
        assertEquals(invalidDateYear, initialDetailsForm.getOpeningDateYear());
        assertEquals(new Long(1L), initialDetailsForm.getInnovationSectorCategoryId());
        assertEquals(asList(1L, 2L, 3L), initialDetailsForm.getInnovationAreaCategoryIds());
        assertEquals(new Long(1L), initialDetailsForm.getCompetitionTypeId());
        assertEquals(new Long(1L), initialDetailsForm.getInnovationLeadUserId());
        assertEquals("My competition", initialDetailsForm.getTitle());

        bindingResult.getAllErrors();
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount("openingDate"));
        List<String> errorsOnOpeningDate = bindingResult.getFieldErrors("openingDate").stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(toList());
        assertTrue(errorsOnOpeningDate.contains("Please enter a valid date."));
        assertTrue(errorsOnOpeningDate.contains("Please enter a future date."));

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void submitSectionInitialDetailsWithoutErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(Boolean.FALSE);

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.saveCompetitionSetupSection(
                isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.INITIAL_DETAILS))
        )
                .thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
                .param("executiveUserId", "1")
                .param("openingDateDay", "10")
                .param("openingDateMonth", "10")
                .param("openingDateYear", "2100")
                .param("innovationSectorCategoryId", "1")
                .param("innovationAreaCategoryIds", "1", "2", "3")
                .param("competitionTypeId", "1")
                .param("fundingType", FundingType.GRANT.name())
                .param("innovationLeadUserId", "1")
                .param("title", "My competition")
                .param("stateAid", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"));

        verify(competitionSetupService).saveCompetitionSetupSection(isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.INITIAL_DETAILS));
    }

    @Test
    public void submitSectionDetails_redirectsIfInitialDetailsIncomplete() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(Boolean.FALSE);

        List<CompetitionSetupSection> sections = asList(
                CompetitionSetupSection.ADDITIONAL_INFO,
                CompetitionSetupSection.ELIGIBILITY,
                CompetitionSetupSection.COMPLETION_STAGE,
                CompetitionSetupSection.MILESTONES,
                CompetitionSetupSection.APPLICATION_FORM,
                CompetitionSetupSection.ASSESSORS
        );

        for (CompetitionSetupSection section : sections) {
            mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/" + section.getPath()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/competition/setup/" + competition.getId()));
        }
    }

    @Test
    public void submitSectionEligibilityWithErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void submitSectionEligibilityWithoutErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.saveCompetitionSetupSection(
                isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.ELIGIBILITY))
        )
                .thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility")
                .param("multipleStream", "yes")
                .param("streamName", "stream")
                .param("researchCategoryId", "1", "2", "3")
                .param("researchCategoriesApplicable", "true")
                .param("singleOrCollaborative", "collaborative")
                .param("leadApplicantTypes", "1", "2", "3")
                .param("researchParticipationAmountId", "1")
                .param("resubmission", "yes")
                .param("overrideFundingRules", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility"));

        verify(competitionSetupService).saveCompetitionSetupSection(isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.ELIGIBILITY));
    }

    @Test
    public void submitSectionEligibilityWithoutStreamName() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility")
                .param("multipleStream", "yes")
                .param("streamName", "")
                .param("researchCategoryId", "1", "2", "3")
                .param("singleOrCollaborative", "collaborative")
                .param("leadApplicantTypes", "1")
                .param("researchParticipationAmountId", "1")
                .param("resubmission", "yes")
                .param("overrideFundingRules", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "streamName"));

        verify(competitionSetupService, never()).saveCompetitionSetupSection(
                isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.ELIGIBILITY)
        );
    }

    @Test
    public void submitSectionEligibilityFailsWithoutResearchParticipationIfCompetitionHasFullApplicationFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withApplicationFinanceType(STANDARD)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility")
                .param("multipleStream", "yes")
                .param("streamName", "stream")
                .param("researchCategoryId", "1", "2", "3")
                .param("singleOrCollaborative", "collaborative")
                .param("leadApplicantTypes", "1", "2", "3")
                .param("researchParticipationAmountId", "")
                .param("resubmission", "no"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "researchParticipationAmountId"));

        verify(competitionSetupService, never()).saveCompetitionSetupSection(
                isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.ELIGIBILITY)
        );
    }

    @Test
    public void submitSectionEligibilitySucceedsWithoutResearchParticipationIfCompetitionHasNoApplicationFinance() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withApplicationFinanceType((ApplicationFinanceType) null)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.saveCompetitionSetupSection(
                isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.ELIGIBILITY))
        )
                .thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility")
                .param("multipleStream", "yes")
                .param("streamName", "stream")
                .param("researchCategoryId", "1", "2", "3")
                .param("researchCategoriesApplicable", "true")
                .param("singleOrCollaborative", "collaborative")
                .param("leadApplicantTypes", "1", "2", "3")
                .param("resubmission", "no")
                .param("overrideFundingRules", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility"));

        verify(competitionSetupService).saveCompetitionSetupSection(
                isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.ELIGIBILITY)
        );
    }

    @Test
    public void coFundersForCompetition() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withActivityCode("Activity Code")
                .withCompetitionCode("c123")
                .withPafCode("p123")
                .withBudgetCode("b123")
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withFunders(CompetitionFundersFixture.getTestCoFunders())
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.saveCompetitionSetupSection(
                any(AdditionalInfoForm.class),
                any(CompetitionResource.class), any(CompetitionSetupSection.class))
        )
                .thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/additional")
                .param("activityCode", "a123")
                .param("pafNumber", "p123")
                .param("competitionCode", "c123")
                .param("funders[0].funder", Funder.ADVANCED_PROPULSION_CENTRE_APC.name())
                .param("funders[0].funderBudget", "93129")
                .param("funders[0].coFunder", "false")
                .param("budgetCode", "b123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/additional"));

        verify(competitionSetupService, atLeastOnce()).saveCompetitionSetupSection(
                any(AdditionalInfoForm.class),
                any(CompetitionResource.class),
                any(CompetitionSetupSection.class)
        );

        verify(validator).validate(any(AdditionalInfoForm.class), any(BindingResult.class));
    }

    @Test
    public void submitCompletionStageSectionDetails() throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        when(competitionSetupService.saveCompetitionSetupSection(
                any(CompletionStageForm.class),
                eq(competition),
                eq(CompetitionSetupSection.COMPLETION_STAGE))).thenReturn(serviceSuccess());

        // assert that after a successful submission, the view moves on to the Milestones page
        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/completion-stage")
                .param("selectedCompletionStage", CompetitionCompletionStage.PROJECT_SETUP.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/milestones"));

        verify(competitionSetupService, times(1)).saveCompetitionSetupSection(
                createLambdaMatcher(form -> {
                    assertThat(((CompletionStageForm) form).getSelectedCompletionStage()).isEqualTo(CompetitionCompletionStage.PROJECT_SETUP);
                }),
                eq(competition),
                eq(CompetitionSetupSection.COMPLETION_STAGE));
    }

    @Test
    public void submitCompletionStageSectionDetailsWithValidationErrors() throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/completion-stage"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrorCode("competitionSetupForm",
                        "selectedCompletionStage", "NotNull"))
                .andExpect(view().name("competition/setup"));

        verify(competitionSetupService, never()).saveCompetitionSetupSection(any(), any(), any());
    }

    @Test
    public void markCompletionStageSectionIncomplete() throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        when(competitionSetupRestService.markSectionIncomplete(competition.getId(), CompetitionSetupSection.COMPLETION_STAGE)).
                thenReturn(restSuccess());

        // assert that after successful marking incomplete, the view remains on the editable view of the Completion Stage page
        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/completion-stage/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/completion-stage"));

        verify(competitionSetupRestService).markSectionIncomplete(competition.getId(), CompetitionSetupSection.COMPLETION_STAGE);
    }

    @Test
    public void submitMilestonesSectionDetails() throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        when(competitionSetupService.saveCompetitionSetupSection(
                any(MilestonesForm.class),
                eq(competition),
                eq(CompetitionSetupSection.MILESTONES))).thenReturn(serviceSuccess());

        // assert that after successful submission, the view remains on the read-only view of the Milestones page
        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/milestones"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/milestones"));

        verify(competitionSetupService, times(1)).saveCompetitionSetupSection(
                any(MilestonesForm.class),
                eq(competition),
                eq(CompetitionSetupSection.MILESTONES));
    }

    @Test
    public void markMilestonesSectionIncomplete() throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        when(competitionSetupRestService.markSectionIncomplete(competition.getId(), CompetitionSetupSection.MILESTONES)).
                thenReturn(restSuccess());

        // assert that after successful marking incomplete, the view remains on the editable view of the Milestones page
        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/milestones/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/milestones"));

        verify(competitionSetupRestService).markSectionIncomplete(competition.getId(), CompetitionSetupSection.MILESTONES);
    }

    @Test
    public void setCompetitionAsReadyToOpen() throws Exception {
        when(competitionSetupService.setCompetitionAsReadyToOpen(COMPETITION_ID)).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/ready-to-open"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/" + COMPETITION_ID));

        verify(competitionSetupService, only()).setCompetitionAsReadyToOpen(COMPETITION_ID);
    }

    @Test
    public void setCompetitionAsReadyToOpen_failure() throws Exception {
        when(competitionSetupService.setCompetitionAsReadyToOpen(COMPETITION_ID)).thenReturn(
                serviceFailure(new Error("competition.setup.not.ready.to.open", HttpStatus.BAD_REQUEST)));

        // For re-display of Competition Setup following the failure
        CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withId(COMPETITION_ID)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/ready-to-open"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(view().name("competition/setup"))
                .andReturn();

        verify(competitionSetupService).setCompetitionAsReadyToOpen(COMPETITION_ID);

        CompetitionSetupSummaryForm form = (CompetitionSetupSummaryForm) result.getModelAndView().getModel()
                .get(COMPETITION_SETUP_FORM_KEY);
        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getGlobalErrorCount());
        assertEquals("competition.setup.not.ready.to.open", bindingResult.getGlobalErrors().get(0).getCode());
    }

    @Test
    public void submitAssessorsSectionDetailsWithErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(TRUE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void submitAssessorsSectionDetailsWithoutErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.saveCompetitionSetupSection(
                isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.ASSESSORS))).thenReturn(serviceSuccess()
        );

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("averageAssessorScore", "0")
                .param("assessorCount", "1")
                .param("assessorPay", "10")
                .param("hasAssessmentPanel", "0")
                .param("hasInterviewStage", "0")
                .param("assessorFinanceView", "OVERVIEW"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors"));

        verify(competitionSetupService).saveCompetitionSetupSection(
                isA(CompetitionSetupForm.class),
                eq(competition),
                eq(CompetitionSetupSection.ASSESSORS));
    }

    @Test
    public void submitAssessorsSectionDetailsWithInvalidAssessorCount() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(Boolean.TRUE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("assessorCount", "")
                .param("assessorPay", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "assessorCount"))
                .andExpect(view().name("competition/setup"));

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void submitAssessorsSectionDetailsWithInvalidAssessorPay() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("assessorCount", "3")
                .param("assessorPay", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "assessorPay"))
                .andExpect(view().name("competition/setup"));

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void submitAssessorsSectionDetailsWithInvalidAssessorPay_Bignumber() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(TRUE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("assessorCount", "3")
                .param("assessorPay", "12345678912334"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "assessorPay"))
                .andExpect(view().name("competition/setup"));

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void testSubmitAssessorsSectionDetailsWithInvalidAssessorPay_NegativeNumber() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withNonIfs(FALSE)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(Boolean.TRUE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("assessorCount", "3")
                .param("assessorPay", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "assessorPay"))
                .andExpect(view().name("competition/setup"));

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void deleteCompetition() throws Exception {
        when(competitionSetupService.deleteCompetition(COMPETITION_ID)).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(competitionSetupService, only()).deleteCompetition(COMPETITION_ID);
    }

    @Test
    public void deleteCompetition_failure() throws Exception {
        when(competitionSetupService.deleteCompetition(COMPETITION_ID)).thenReturn(
                serviceFailure(new Error(COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED, HttpStatus.BAD_REQUEST)));

        // For re-display of Competition Setup following the failure
        CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withId(COMPETITION_ID)
                .build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        MvcResult result = mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/delete"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(view().name("competition/setup"))
                .andReturn();

        CompetitionSetupSummaryForm form = (CompetitionSetupSummaryForm) result.getModelAndView().getModel()
                .get(COMPETITION_SETUP_FORM_KEY);
        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getGlobalErrorCount());
        assertEquals("COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED", bindingResult.getGlobalErrors().get(0).getCode());
    }

    @Test
    public void uploadTermsAndConditions() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        String fileName = "termsAndConditionsDoc";
        String originalFileName = "original filename";
        String contentType = "application/json";
        String content = "content";

        MockMultipartFile file = new MockMultipartFile(fileName, originalFileName, contentType, content.getBytes());
        FileEntryResource fileEntryResource = newFileEntryResource().build();

        TermsAndConditionsForm form = new TermsAndConditionsForm();
        form.setTermsAndConditionsDoc(file);

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupRestService.uploadCompetitionTerms(COMPETITION_ID, file.getContentType(), file.getSize(),
                file.getOriginalFilename(), getMultipartFileBytes(file))).thenReturn(restSuccess(fileEntryResource));

        mockMvc.perform(multipart(format("%s/%d/section/terms-and-conditions", URL_PREFIX, COMPETITION_ID))
                .file(file)
                .param("uploadTermsAndConditionsDoc", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("%s/%d/section/terms-and-conditions", URL_PREFIX, COMPETITION_ID)));

        InOrder inOrder = inOrder(competitionRestService, competitionSetupRestService, competitionSetupService);
        inOrder.verify(competitionRestService).getCompetitionById(COMPETITION_ID);
        inOrder.verify(competitionSetupRestService)
                .uploadCompetitionTerms(COMPETITION_ID, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
        inOrder.verify(competitionSetupService)
                .saveCompetitionSetupSection(form, competitionResource, CompetitionSetupSection.TERMS_AND_CONDITIONS);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitTermsAndConditionsSectionDetails() throws Exception {
        GrantTermsAndConditionsResource nonProcurementTerms = newGrantTermsAndConditionsResource()
                .withName("Non procurement terms")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionTerms(newFileEntryResource().build())
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(termsAndConditionsRestService.getById(nonProcurementTerms.getId())).thenReturn(restSuccess(nonProcurementTerms));
        when(competitionSetupService.saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competition),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions")
                .param("termsAndConditionsId", String.valueOf(nonProcurementTerms.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions"));

        InOrder inOrder = inOrder(competitionSetupService, competitionSetupRestService, competitionRestService, termsAndConditionsRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(termsAndConditionsRestService).getById(nonProcurementTerms.getId());
        inOrder.verify(competitionSetupRestService).deleteCompetitionTerms(competition.getId());
        inOrder.verify(competitionSetupService).saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competition),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitTermsAndConditionsSectionDetails_procurement() throws Exception {
        GrantTermsAndConditionsResource procurementTerms = newGrantTermsAndConditionsResource().withName("Procurement").build();
        CompetitionResource competitionWithTermsDoc = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionTerms(newFileEntryResource().build())
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionWithTermsDoc));
        when(termsAndConditionsRestService.getById(procurementTerms.getId())).thenReturn(restSuccess(procurementTerms));
        when(competitionSetupService.saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competitionWithTermsDoc),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions")
                .param("termsAndConditionsId", String.valueOf(procurementTerms.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions"));

        verify(competitionSetupService).saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competitionWithTermsDoc),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS));
    }

    @Test
    public void submitTermsAndConditionsSectionDetails_procurementNoFileUploaded() throws Exception {
        GrantTermsAndConditionsResource procurementTerms = newGrantTermsAndConditionsResource().withName("Procurement").build();
        CompetitionResource competitionWithoutTermsDoc = newCompetitionResource().withId(COMPETITION_ID).build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionWithoutTermsDoc));
        when(termsAndConditionsRestService.getById(procurementTerms.getId())).thenReturn(restSuccess(procurementTerms));
        when(competitionSetupService.saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competitionWithoutTermsDoc),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions")
                .param("termsAndConditionsId", String.valueOf(procurementTerms.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "termsAndConditionsDoc"));
    }

    @Test
    public void deleteTermsAndConditions() throws Exception {
        CompetitionResource competitionWithTermsDoc = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionTerms(newFileEntryResource().build())
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionWithTermsDoc));
        when(competitionSetupRestService.deleteCompetitionTerms(COMPETITION_ID)).thenReturn(restSuccess());

        mockMvc.perform(multipart(format("%s/%d/section/terms-and-conditions", URL_PREFIX, COMPETITION_ID))
                .param("deleteTermsAndConditionsDoc", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("%s/%d/section/terms-and-conditions", URL_PREFIX, COMPETITION_ID)));

        verify(competitionRestService).getCompetitionById(COMPETITION_ID);
        verify(competitionSetupRestService).deleteCompetitionTerms(COMPETITION_ID);
    }
}