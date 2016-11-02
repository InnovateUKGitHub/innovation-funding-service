package com.worth.ifs.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionResource.Status;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import com.worth.ifs.competitionsetup.controller.CompetitionSetupController;
import com.worth.ifs.competitionsetup.form.AdditionalInfoForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.InitialDetailsForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.CompetitionSetupService;
import com.worth.ifs.fixtures.CompetitionFundersFixture;
import com.worth.ifs.user.builder.UserResourceBuilder;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.util.*;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competitionsetup.service.sectionupdaters.InitialDetailsSectionSaver.OPENINGDATE_FIELDNAME;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
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

        when(userService.findUserByType(UserRoleType.COMP_EXEC)).thenReturn(asList(UserResourceBuilder.newUserResource().withFirstName("Comp").withLastName("Exec").build()));

        when(userService.findUserByType(UserRoleType.COMP_TECHNOLOGIST)).thenReturn(asList(UserResourceBuilder.newUserResource().withFirstName("Comp").withLastName("Technologist").build()));

        CategoryResource c1 = new CategoryResource();
        c1.setType(CategoryType.INNOVATION_SECTOR);
        c1.setName("A Innovation Sector");
        c1.setId(1L);
        when(categoryService.getCategoryByType(CategoryType.INNOVATION_SECTOR)).thenReturn(asList(c1));

        CategoryResource c2 = new CategoryResource();
        c2.setType(CategoryType.INNOVATION_AREA);
        c2.setName("A Innovation Area");
        c2.setId(2L);
        c2.setParent(1L);
        when(categoryService.getCategoryByType(CategoryType.INNOVATION_AREA)).thenReturn(asList(c2));

        CompetitionTypeResource ct1 = new CompetitionTypeResource();
        ct1.setId(1L);
        ct1.setName("Comptype with stateAid");
        ct1.setStateAid(true);
        ct1.setCompetitions(asList(COMPETITION_ID));
        when(competitionService.getAllCompetitionTypes()).thenReturn(asList(ct1));
    }
    
    @Test
    public void initCompetitionSetupSection() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

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

        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).withName("Test competition").withCompetitionCode("Code").withCompetitionType(2L).build();
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
        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"));
    }

    @Test
    public void getInnovationAreas() throws Exception {
        Long innovationSectorId = 1L;
        CategoryResource category = new CategoryResource();
        category.setType(CategoryType.INNOVATION_AREA);
        category.setId(1L);
        category.setName("Innovation Area 1");

        when(categoryService.getCategoryByParentId(innovationSectorId)).thenReturn(asList(category));

        mockMvc.perform(get(URL_PREFIX + "/getInnovationArea/" + innovationSectorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is(1)))
                .andExpect(jsonPath("[0]name", is("Innovation Area 1")))
                .andExpect(jsonPath("[0]type", is(CategoryType.INNOVATION_AREA.toString())));

    }

    @Test
    public void submitAutoSave() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

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
        ).thenReturn(Collections.emptyList());

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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

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
        ).thenReturn(asList(Error.fieldError(OPENINGDATE_FIELDNAME, value, errorKey)));

        when(messageSource.getMessage(anyString(), anyObject(), any(Locale.class))).thenReturn("Please enter a future date");

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial/saveFormElement")
                .param("fieldName", fieldName)
                .param("value", value)
                .param("objectId", String.valueOf(objectId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("success", is("false")))
                .andExpect(jsonPath("validation_errors[0]", is("Please enter a future date")));

        verify(competitionSetupService).autoSaveCompetitionSetupSection(isA(CompetitionResource.class), eq(CompetitionSetupSection.INITIAL_DETAILS), eq(fieldName), eq(value), eq(Optional.of(objectId)));
    }

    @Test
    public void generateCompetitionCode() throws Exception {
        LocalDateTime time = LocalDateTime.of(2016, 12, 1, 0, 0);
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).withName("Test competition").withCompetitionCode("Code").withCompetitionType(2L).build();
        competition.setStartDate(time);
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        when(competitionService.generateCompetitionCode(COMPETITION_ID, time)).thenReturn("1612-1");

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/generateCompetitionCode?day=01&month=12&year=2016"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", is("1612-1")));
    }

    @Test
    public void submitSectionInitialDetailsWithErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void submitSectionInitialDetailsWithoutErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial")
        				.param("executiveUserId", "1")
        				.param("openingDateDay", "1")
        				.param("openingDateMonth", "1")
        				.param("openingDateYear", "2016")
        				.param("innovationSectorCategoryId", "1")
        				.param("innovationAreaCategoryId", "1")
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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/eligibility"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));

        verify(competitionService, never()).update(competition);
    }

    @Test
    public void submitSectionEligibilityWithoutErrors() throws Exception {
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

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
        CompetitionResource competition = newCompetitionResource().withCompetitionStatus(Status.COMPETITION_SETUP).build();

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
                .withInnovateBudget("Innovate Budget")
                .withCompetitionCode("c123")
                .withPafCode("p123")
                .withBudgetCode("b123")
                .withCompetitionStatus(Status.COMPETITION_SETUP)
                .withFunders(CompetitionFundersFixture.getTestCoFunders())
                .withId(8L).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

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
    public void testSendToDashboard() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withActivityCode("Activity Code")
                .withInnovateBudget("Innovate Budget")
                .withCompetitionCode("c123")
                .withPafCode("p123")
                .withBudgetCode("b123")
                .withCompetitionStatus(Status.OPEN)
                .withFunders(CompetitionFundersFixture.getTestCoFunders())
                .withId(COMPETITION_ID).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/dashboard"));
    }

    @Test
    public void testSetCompetitionAsReadyToOpen() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(Status.READY_TO_OPEN)
                .withId(COMPETITION_ID).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/ready-to-open"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/setup/"+COMPETITION_ID));
    }

    @Test
    public void testInitialDetailsRestriction() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionStatus(Status.COMPETITION_SETUP)
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
                .withCompetitionStatus(Status.COMPETITION_SETUP)
                .withId(COMPETITION_ID).build();

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);

        mockMvc.perform(get(URL_PREFIX + "/" + COMPETITION_ID + "/section/initial"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attribute("restrictInitialDetailsEdit", nullValue()));
    }
}