package org.innovateuk.ifs.competitionsetup;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupSummaryForm;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.fundinginformation.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.initialdetail.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.initialdetail.populator.ManageInnovationLeadsModelPopulator;
import org.innovateuk.ifs.fixtures.CompetitionFundersFixture;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeResourceBuilder.newCompetitionTypeResource;
import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.*;
import static org.innovateuk.ifs.competitionsetup.initialdetail.sectionupdater.InitialDetailsSectionUpdater.OPENINGDATE_FIELDNAME;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link CompetitionSetupController}
 */
@RunWith(MockitoJUnitRunner.class)
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
    private CompetitionRestService competitionRestService;

    @Override
    protected CompetitionSetupController supplyControllerUnderTest() {
        return new CompetitionSetupController();
    }

    @Before
    public void setUp() {
        super.setUp();

        when(userService.findUserByType(COMP_ADMIN))
                .thenReturn(
                        newUserResource()
                                .withFirstName("Comp")
                                .withLastName("Admin")
                                .build(1)
                );

        when(userService.findUserByType(INNOVATION_LEAD))
                .thenReturn(
                        newUserResource()
                                .withFirstName("Comp")
                                .withLastName("Technologist")
                                .build(1)
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

        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(true);
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
    public void editCompetitionSetupSection_redirectsIfInitialDetailsNotCompleted() throws Exception {
        CompetitionResource competition = newCompetitionResource().withId(COMPETITION_ID).build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(Boolean.FALSE);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/section/application"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/" + COMPETITION_ID));
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
    public void submitAutoSave() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        String fieldName = "title";
        String value = "New Title";
        Long objectId = 2L;

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.autoSaveCompetitionSetupSection(
                isA(CompetitionResource.class),
                eq(CompetitionSetupSection.INITIAL_DETAILS),
                eq(fieldName),
                eq(value),
                eq(Optional.of(objectId)))
        )
                .thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial/saveFormElement")
                .param("fieldName", fieldName)
                .param("value", value)
                .param("objectId", String.valueOf(objectId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("success", is("true")));

        verify(competitionSetupService).autoSaveCompetitionSetupSection(
                isA(CompetitionResource.class),
                eq(CompetitionSetupSection.INITIAL_DETAILS),
                eq(fieldName),
                eq(value),
                eq(Optional.of(objectId))
        );
    }

    @Test
    public void submitAutoSaveValidationErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        String fieldName = "openingDate";
        String value = "20-02-2002";
        String errorKey = "competition.setup.opening.date.not.in.future";
        Long objectId = 2L;

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(competitionSetupService.autoSaveCompetitionSetupSection(
                isA(CompetitionResource.class),
                eq(CompetitionSetupSection.INITIAL_DETAILS),
                eq(fieldName),
                eq(value),
                eq(Optional.of(objectId)))
        )
                .thenReturn(serviceFailure(fieldError(OPENINGDATE_FIELDNAME, value, errorKey)));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial/saveFormElement")
                .param("fieldName", fieldName)
                .param("value", value)
                .param("objectId", String.valueOf(objectId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("success", is("true")));

        verify(competitionSetupService).autoSaveCompetitionSetupSection(
                isA(CompetitionResource.class),
                eq(CompetitionSetupSection.INITIAL_DETAILS),
                eq(fieldName),
                eq(value),
                eq(Optional.of(objectId))
        );
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
        assertEquals(9, bindingResult.getFieldErrorCount());
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
                .map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList());
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
                .map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList());
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
                .map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList());
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

        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(Boolean.FALSE);

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
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(Boolean.FALSE);

        List<CompetitionSetupSection> sections = asList(
                CompetitionSetupSection.ADDITIONAL_INFO,
                CompetitionSetupSection.ELIGIBILITY,
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
                .withFullApplicationFinance(true)
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
                .withFullApplicationFinance(null)
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
    public void testCoFundersForCompetition() throws Exception {
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
                .param("funders[0].funder", "asdf")
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
    public void testSetCompetitionAsReadyToOpen() throws Exception {
        when(competitionSetupService.setCompetitionAsReadyToOpen(COMPETITION_ID)).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/ready-to-open"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/" + COMPETITION_ID));

        verify(competitionSetupService, only()).setCompetitionAsReadyToOpen(COMPETITION_ID);
    }

    @Test
    public void testSetCompetitionAsReadyToOpen_failure() throws Exception {
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
    public void testInitialDetailsRestriction() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withId(COMPETITION_ID)
                .build();

        Map<CompetitionSetupSection, Boolean> sectionSetupStatus = new HashMap<>();
        sectionSetupStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute("restrictInitialDetailsEdit", Boolean.TRUE));
    }

    @Test
    public void testInitialDetailsNoRestriction() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withId(COMPETITION_ID)
                .build();

        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(Boolean.FALSE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute("restrictInitialDetailsEdit", nullValue()));
    }


    @Test
    public void testSubmitAssessorsSectionDetailsWithErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(TRUE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));

        verify(competitionSetupRestService, never()).update(competition);
    }

    @Test
    public void testSubmitAssessorsSectionDetailsWithoutErrors() throws Exception {
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
    public void testSubmitAssessorsSectionDetailsWithInvalidAssessorCount() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(Boolean.TRUE);
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
    public void testSubmitAssessorsSectionDetailsWithInvalidAssessorPay() throws Exception {
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
    public void testSubmitAssessorsSectionDetailsWithInvalidAssessorPay_Bignumber() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .build();

        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(TRUE);
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

        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(Boolean.TRUE);
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
    public void manageInnovationLeadWhenInitialDetailsNotComplete() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(FALSE);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/manage-innovation-leads/find"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID));

        verify(manageInnovationLeadsModelPopulator, never()).populateModel(any());
    }

    @Test
    public void manageInnovationLead() throws Exception {

        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/manage-innovation-leads/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-innovation-leads-find"));

        verify(manageInnovationLeadsModelPopulator).populateModel(any());
    }

    @Test
    public void manageInnovationLeadOverviewWhenInitialDetailsNotComplete() throws Exception {

        CompetitionResource competitionResource = newCompetitionResource().build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(Boolean.FALSE);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/manage-innovation-leads/overview"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID));

        verify(manageInnovationLeadsModelPopulator, never()).populateModel(any());
    }

    @Test
    public void manageInnovationLeadOverview() throws Exception {

        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/manage-innovation-leads/overview"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-innovation-leads-overview"));

        verify(manageInnovationLeadsModelPopulator).populateModel(any());
    }

    @Test
    public void addInnovationLeadWhenInitialDetailsNotComplete() throws Exception {
        Long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = newCompetitionResource().build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/add-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID));

        verify(competitionSetupService, never()).addInnovationLead(COMPETITION_ID, innovationLeadUserId);
        verify(manageInnovationLeadsModelPopulator, never()).populateModel(any());
    }

    @Test
    public void addInnovationLead() throws Exception {
        Long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.addInnovationLead(COMPETITION_ID, innovationLeadUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/add-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-innovation-leads-find"));

        verify(competitionSetupService).addInnovationLead(COMPETITION_ID, innovationLeadUserId);
        verify(manageInnovationLeadsModelPopulator).populateModel(any());
    }

    @Test
    public void removeInnovationLeadWhenInitialDetailsNotComplete() throws Exception {

        CompetitionResource competitionResource = newCompetitionResource().build();

        when(competitionSetupService.isInitialDetailsCompleteOrTouched(COMPETITION_ID)).thenReturn(Boolean.FALSE);
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        Long innovationLeadUserId = 2L;
        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/remove-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/" + COMPETITION_ID));

        verify(competitionSetupService, never()).removeInnovationLead(COMPETITION_ID, innovationLeadUserId);
        verify(manageInnovationLeadsModelPopulator, never()).populateModel(any());
    }

    @Test
    public void removeInnovationLead() throws Exception {
        Long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.removeInnovationLead(COMPETITION_ID, innovationLeadUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/remove-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-innovation-leads-overview"));

        verify(competitionSetupService).removeInnovationLead(COMPETITION_ID, innovationLeadUserId);
        verify(manageInnovationLeadsModelPopulator).populateModel(any());
    }

    public void removeInnovationLeadFailure() throws Exception {
        Long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = newCompetitionResource()
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupService.removeInnovationLead(COMPETITION_ID, innovationLeadUserId)).thenReturn(
                serviceFailure(new Error(COMPETITION_WITH_ASSESSORS_CANNOT_BE_DELETED, HttpStatus.BAD_REQUEST)));

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/remove-innovation-lead/" + innovationLeadUserId))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(view().name("competition/setup"))
                .andReturn();
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
}
