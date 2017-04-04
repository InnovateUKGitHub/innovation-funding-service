package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competitionsetup.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.fixtures.CompetitionFundersFixture;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.category.resource.CategoryType.INNOVATION_AREA;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeResourceBuilder.newCompetitionTypeResource;
import static org.innovateuk.ifs.competitionsetup.service.sectionupdaters.InitialDetailsSectionSaver.OPENINGDATE_FIELDNAME;
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

    private static final Long COMPETITION_ID = Long.valueOf(12);
    private static final String URL_PREFIX = "/competition/setup";

    @Mock
    private CategoryService categoryService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Mock
    private Validator validator;

    @Override
    protected CompetitionSetupController supplyControllerUnderTest() { return new CompetitionSetupController(); }

    @Before
    public void setUp() {
        super.setUp();

        when(userService.findUserByType(UserRoleType.COMP_ADMIN)).thenReturn(asList(UserResourceBuilder.newUserResource().withFirstName("Comp").withLastName("Admin").build()));

        when(userService.findUserByType(UserRoleType.COMP_TECHNOLOGIST)).thenReturn(asList(UserResourceBuilder.newUserResource().withFirstName("Comp").withLastName("Technologist").build()));

        List<InnovationSectorResource> innovationSectorResources = newInnovationSectorResource()
                .withName("A Innovation Sector")
                .withId(1L)
                .build(1);
        when(categoryService.getInnovationSectors()).thenReturn(innovationSectorResources);

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withName("A Innovation Area")
                .withId(2L)
                .withSector(1L)
                .build(1);
        when(categoryService.getInnovationAreas()).thenReturn(innovationAreaResources);

        List<CompetitionTypeResource> competitionTypeResources = newCompetitionTypeResource()
                .withId(1L)
                .withName("Comptype with stateAid")
                .withStateAid(true)
                .withCompetitions(asList(COMPETITION_ID))
                .build(1);
        when(competitionService.getAllCompetitionTypes()).thenReturn(competitionTypeResources);
    }

    @Test
    public void initCompetitionSetupSection() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"));
    }

    @Test
    public void editCompetitionSetupSectionInitial() throws Exception{
        InitialDetailsForm competitionSetupInitialDetailsForm = new InitialDetailsForm();
        competitionSetupInitialDetailsForm.setTitle("Test competition");
        competitionSetupInitialDetailsForm.setCompetitionTypeId(2L);

        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withName("Test competition").withCompetitionCode("Code").withCompetitionType(2L).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        CompetitionSetupForm compSetupForm = mock(CompetitionSetupForm.class);
        when(competitionSetupService.getSectionFormData(competition, CompetitionSetupSection.INITIAL_DETAILS)).thenReturn(compSetupForm);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute("competitionSetupForm", compSetupForm));

        verify(competitionSetupService).populateCompetitionSectionModelAttributes(isA(Model.class), eq(competition), eq(CompetitionSetupSection.INITIAL_DETAILS));
    }

    @Test
    public void setSectionAsIncomplete() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withName("Test competition").withCompetitionCode("Code").withCompetitionType(2L).build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionService.setSetupSectionMarkedAsIncomplete(anyLong(), any(CompetitionSetupSection.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"));
    }

    @Test
    public void getInnovationAreas() throws Exception {
        Long innovationSectorId = 1L;
        InnovationAreaResource category = newInnovationAreaResource()
                .withId(1L)
                .withName("Innovation Area 1")
                .build();

        when(categoryService.getInnovationAreasBySector(innovationSectorId)).thenReturn(asList(category));

        mockMvc.perform(get(URL_PREFIX + "/getInnovationArea/" + innovationSectorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is(1)))
                .andExpect(jsonPath("[0]name", is("Innovation Area 1")))
                .andExpect(jsonPath("[0]type", is(INNOVATION_AREA.toString())));

    }

    @Test
    public void submitAutoSave() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        String fieldName = "title";
        String value = "New Title";
        Long objectId = 2L;

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.autoSaveCompetitionSetupSection(
                isA(CompetitionResource.class),
                eq(CompetitionSetupSection.INITIAL_DETAILS),
                eq(fieldName),
                eq(value),
                eq(Optional.of(objectId)))
        ).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial/saveFormElement")
                .param("fieldName", fieldName)
                .param("value", value)
                .param("objectId", String.valueOf(objectId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("success", is("true")));

        verify(competitionSetupService).autoSaveCompetitionSetupSection(isA(CompetitionResource.class), eq(CompetitionSetupSection.INITIAL_DETAILS), eq(fieldName), eq(value), eq(Optional.of(objectId)));
    }

    @Test
    public void submitAutoSaveValidationErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        String fieldName = "openingDate";
        String value = "20-02-2002";
        String errorKey = "competition.setup.opening.date.not.in.future";
        Long objectId = 2L;

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.autoSaveCompetitionSetupSection(
                isA(CompetitionResource.class),
                eq(CompetitionSetupSection.INITIAL_DETAILS),
                eq(fieldName),
                eq(value),
                eq(Optional.of(objectId)))
        ).thenReturn(serviceFailure(fieldError(OPENINGDATE_FIELDNAME, value, errorKey)));

        when(messageSource.getMessage(anyString(), anyObject(), any(Locale.class))).thenReturn("Please enter a future date");

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial/saveFormElement")
                .param("fieldName", fieldName)
                .param("value", value)
                .param("objectId", String.valueOf(objectId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("success", is("true")));

        verify(competitionSetupService).autoSaveCompetitionSetupSection(isA(CompetitionResource.class), eq(CompetitionSetupSection.INITIAL_DETAILS), eq(fieldName), eq(value), eq(Optional.of(objectId)));
    }

    @Test
    public void generateCompetitionCode() throws Exception {
        ZonedDateTime time = ZonedDateTime.of(2016, 12, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).withName("Test competition").withCompetitionCode("Code").withCompetitionType(2L).build();
        competition.setStartDate(time);
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionService.generateCompetitionCode(COMPETITION_ID, time)).thenReturn("1612-1");

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/generateCompetitionCode?day=01&month=12&year=2016"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", is("1612-1")));
    }

    @Test
    public void submitSectionInitialDetailsWithErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void submitSectionInitialDetailsInvalidWithRequiredFieldsEmpty() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        String invalidDateDay = "32";

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
                .param("budgetCode", "Bcode1")
                .param("pafNumber", "1123")
                .param("competitionCode", "12312-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "title"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "competitionTypeId"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "innovationSectorCategoryId"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "innovationAreaCategoryIds"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "openingDateDay"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "openingDateMonth"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "openingDateYear"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "leadTechnologistUserId"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "executiveUserId"))
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void submitSectionInitialDetailsWithInvalidFieldsExceedRangeMax() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        String invalidDateDay = "32";
        String invalidDateMonth = "13";
        String invalidDateYear = "10000";

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
                .param("executiveUserId", "1")
                .param("openingDateDay", invalidDateDay)
                .param("openingDateMonth", invalidDateMonth)
                .param("openingDateYear", invalidDateYear)
                .param("innovationSectorCategoryId", "1")
                .param("innovationAreaCategoryIds", "1", "2", "3")
                .param("competitionTypeId", "1")
                .param("leadTechnologistUserId", "1")
                .param("title", "My competition")
                .param("budgetCode", "Bcode1")
                .param("pafNumber", "1123")
                .param("competitionCode", "12312-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "openingDateDay"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "openingDateMonth"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "openingDateYear"))
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void submitSectionInitialDetailsWithInvalidFieldsExceedRangeMin() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        String invalidDateDay = "0";
        String invalidDateMonth = "0";
        String invalidDateYear = "1899";

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
                .param("executiveUserId", "1")
                .param("openingDateDay", invalidDateDay)
                .param("openingDateMonth", invalidDateMonth)
                .param("openingDateYear", invalidDateYear)
                .param("innovationSectorCategoryId", "1")
                .param("innovationAreaCategoryIds", "1", "2", "3")
                .param("competitionTypeId", "1")
                .param("leadTechnologistUserId", "1")
                .param("title", "My competition")
                .param("budgetCode", "Bcode1")
                .param("pafNumber", "1123")
                .param("competitionCode", "12312-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "openingDateDay"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "openingDateMonth"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "openingDateYear"))
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void submitSectionInitialDetailsWithoutErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSection(isA(CompetitionSetupForm.class), eq(competition), eq(CompetitionSetupSection.INITIAL_DETAILS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
        				.param("executiveUserId", "1")
        				.param("openingDateDay", "1")
        				.param("openingDateMonth", "1")
        				.param("openingDateYear", "2016")
        				.param("innovationSectorCategoryId", "1")
        				.param("innovationAreaCategoryIds", "1", "2", "3")
        				.param("competitionTypeId", "1")
        				.param("leadTechnologistUserId", "1")
                        .param("title", "My competition")
                        .param("budgetCode", "Bcode1")
                        .param("pafNumber", "1123")
                        .param("competitionCode", "12312-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"));

        verify(competitionSetupService).saveCompetitionSetupSection(isA(CompetitionSetupForm.class), eq(competition), eq(CompetitionSetupSection.INITIAL_DETAILS));
    }

    @Test
    public void submitSectionEligibilityWithErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void submitSectionEligibilityWithoutErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSection(isA(CompetitionSetupForm.class), eq(competition), eq(CompetitionSetupSection.ELIGIBILITY))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility")
        				.param("multipleStream", "yes")
        				.param("streamName", "stream")
        				.param("researchCategoryId", "1", "2", "3")
        				.param("singleOrCollaborative", "collaborative")
        				.param("leadApplicantType", "business")
        				.param("researchParticipationAmountId", "1")
                        .param("resubmission", "yes"))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility"));

        verify(competitionSetupService).saveCompetitionSetupSection(isA(CompetitionSetupForm.class), eq(competition), eq(CompetitionSetupSection.ELIGIBILITY));
    }

    @Test
    public void submitSectionEligibilityWithoutStreamName() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility")
        				.param("multipleStream", "yes")
        				.param("streamName", "")
        				.param("researchCategoryId", "1", "2", "3")
        				.param("singleOrCollaborative", "collaborative")
        				.param("leadApplicantType", "business")
        				.param("researchParticipationAmountId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testCoFundersForCompetition() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withActivityCode("Activity Code")
                .withCompetitionCode("c123")
                .withPafCode("p123")
                .withBudgetCode("b123")
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withFunders(CompetitionFundersFixture.getTestCoFunders())
                .withId(8L).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSection(any(AdditionalInfoForm.class),
                any(CompetitionResource.class), any(CompetitionSetupSection.class))).thenReturn(serviceSuccess());

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

        verify(competitionSetupService, atLeastOnce()).saveCompetitionSetupSection(any(AdditionalInfoForm.class),
                any(CompetitionResource.class), any(CompetitionSetupSection.class));

        verify(validator).validate(any(AdditionalInfoForm.class), any(BindingResult.class));
    }

    @Test
    public void testSetCompetitionAsReadyToOpen() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.READY_TO_OPEN)
                .withId(COMPETITION_ID).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/ready-to-open"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/"+COMPETITION_ID));
    }

    @Test
    public void testInitialDetailsRestriction() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withId(COMPETITION_ID).build();
        Map<CompetitionSetupSection, Boolean> sectionSetupStatus = new HashMap<>();
        sectionSetupStatus.put(CompetitionSetupSection.INITIAL_DETAILS, Boolean.TRUE);
        competition.setSectionSetupStatus(sectionSetupStatus);

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute("restrictInitialDetailsEdit", Boolean.TRUE));
    }

    @Test
    public void testInitialDetailsNoRestriction() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP)
                .withId(COMPETITION_ID).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute("restrictInitialDetailsEdit", nullValue()));
    }


    @Test
    public void testSubmitAssessorsSectionDetailsWithErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testSubmitAssessorsSectionDetailsWithoutErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionSetupService.saveCompetitionSetupSection(isA(CompetitionSetupForm.class), eq(competition), eq(CompetitionSetupSection.ASSESSORS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("assessorCount", "1")
                .param("assessorPay", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors"));

        verify(competitionSetupService).saveCompetitionSetupSection(isA(CompetitionSetupForm.class), eq(competition), eq(CompetitionSetupSection.ASSESSORS));
    }

    @Test
    public void testSubmitAssessorsSectionDetailsWithInvalidAssessorCount() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("assessorCount", "")
                .param("assessorPay", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "assessorCount"))
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testSubmitAssessorsSectionDetailsWithInvalidAssessorPay() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("assessorCount", "3")
                .param("assessorPay", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "assessorPay"))
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testSubmitAssessorsSectionDetailsWithInvalidAssessorPay_Bignumber() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("assessorCount", "3")
                .param("assessorPay", "12345678912334"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "assessorPay"))
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void testSubmitAssessorsSectionDetailsWithInvalidAssessorPay_NegativeNumber() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/assessors")
                .param("assessorCount", "3")
                .param("assessorPay", "-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "assessorPay"))
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }
}
