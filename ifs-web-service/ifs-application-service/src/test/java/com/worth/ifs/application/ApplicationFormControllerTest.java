package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.application.builder.SectionResourceBuilder;
import com.worth.ifs.application.model.*;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.Materials;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.BaseBuilderAmendFunctions.name;
import static com.worth.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.error.Error.globalError;
import static com.worth.ifs.commons.rest.ValidationMessages.noErrors;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations="classpath:application.properties")
public class ApplicationFormControllerTest extends BaseUnitTest {

    @InjectMocks
    private ApplicationFormController applicationFormController;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Spy
    @InjectMocks
    private QuestionModelPopulator questionModelPopulator;

    @Spy
    @InjectMocks
    private OpenSectionModelPopulator openSectionModel;

    @Mock
    private ApplicationModelPopulator applicationModelPopulator;

    @Mock
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private Model model;

    private ApplicationResource application;
    private Long sectionId;
    private Long questionId;
    private Long formInputId;
    private Long costId;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");

    @Before
    public void setUp(){

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        super.setup();

        mockMvc = setupMockMvc(applicationFormController, () -> loggedInUser, env, messageSource);

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupUserRoles();
        this.setupFinances();
        this.setupInvites();
        this.setupQuestionStatus(applications.get(0));

        application = applications.get(0);
        sectionId = Long.valueOf(1);
        questionId = Long.valueOf(1);
        formInputId = Long.valueOf(111);
        costId = Long.valueOf(1);

        // save actions should always succeed.
        when(formInputResponseService.save(anyLong(), anyLong(), anyLong(), eq(""), anyBoolean())).thenReturn(new ValidationMessages(fieldError("value", "", "Please enter some text 123")));
        when(formInputResponseService.save(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean())).thenReturn(noErrors());
    }

    @Test
    public void testApplicationFormWithOpenSection() throws Exception {

        Long currentSectionId = sectionResources.get(2).getId();

        //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        mockMvc.perform(get("/application/1/form/section/"+currentSectionId))
                .andExpect(view().name("application-form"))
                .andExpect(model().attribute("currentApplication", application))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(1))))
                .andExpect(model().attribute("userIsLeadApplicant", true))
                .andExpect(model().attribute("leadApplicant", users.get(0)))
                .andExpect(model().attribute("currentSectionId", currentSectionId));
    }

    @Test
    public void testQuestionPage() throws Exception {
        ApplicationResource application = applications.get(0);

        when(sectionService.getAllByCompetitionId(anyLong())).thenReturn(sectionResources);
        when(applicationService.getById(application.getId())).thenReturn(application);
        when(competitionService.getById(anyLong())).thenReturn(newCompetitionResource().withCompetitionStatus(CompetitionResource.Status.OPEN).build());
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
        when(financeRowService.add(anyLong(),anyLong(), any())).thenReturn(costItemMessages);
        mockMvc.perform(
            get("/application/{applicationId}/form/remove_cost/{costId}", application.getId(), costId)
        );
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
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() +"**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFormSubmitMarkSectionComplete() throws Exception {

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf(sectionId))
                        .param(ApplicationFormController.TERMS_AGREED_KEY, "1")
                        .param(ApplicationFormController.STATE_AID_AGREED_KEY, "1")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() +"**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFinanceMarkAsCompleteFailWithTerms() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your finances")).withType(SectionType.FINANCE).build());
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1))
                .andExpect(model().attributeHasFieldErrors("form", ApplicationFormController.TERMS_AGREED_KEY));
    }

    @Test
    public void testApplicationFinanceMarkAsCompleteFailWithStateAid() throws Exception {
        SectionResourceBuilder sectionResourceBuilder = SectionResourceBuilder.newSectionResource();
        when(sectionService.getById(anyLong())).thenReturn(sectionResourceBuilder.with(id(1L)).with(name("Your finances")).withType(SectionType.FINANCE).build());
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), "1")
                        .param(ApplicationFormController.MARK_SECTION_AS_COMPLETE, String.valueOf("1"))
                        .param(ApplicationFormController.TERMS_AGREED_KEY, "1")
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 1))
                .andExpect(model().attributeHasFieldErrors("form", ApplicationFormController.STATE_AID_AGREED_KEY));
    }

    @Test
    public void testApplicationFormSubmitMarkSectionInComplete() throws Exception {

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(ApplicationFormController.MARK_SECTION_AS_INCOMPLETE, String.valueOf(sectionId))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() +"/form/section/**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFormSubmitMarkAsComplete() throws Exception {
        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(ApplicationFormController.MARK_AS_COMPLETE, "12")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/" + sectionId+"**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFormSubmitMarkAsIncomplete() throws Exception {

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param(ApplicationFormController.MARK_AS_INCOMPLETE, "3")
        ).andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/application/" + application.getId() + "/form/section/" + sectionId +"**"))
                .andExpect(cookie().exists(CookieFlashMessageFilter.COOKIE_NAME));
    }

    @Test
    public void testApplicationFormSubmitGivesNoValidationErrorsIfNoQuestionIsEmptyOnSectionSubmit() throws Exception {
        Long userId = loggedInUser.getId();

        when(formInputResponseService.save(userId, application.getId(), 1L, "", false)).thenReturn(new ValidationMessages(globalError("Please enter some text")));
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

        when(formInputResponseService.save(userId, application.getId(), 1L, "", false)).thenReturn(new ValidationMessages(globalError("Please enter some text")));
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

        when(formInputResponseService.save(anyLong(), anyLong(), anyLong(), eq(""), eq(false))).thenReturn(new ValidationMessages(globalError("please.enter.some.text")));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("formInput[1]", "")
                        .param(ApplicationFormController.MARK_AS_COMPLETE, "1")
        ).andExpect(status().isOk())
                .andExpect(view().name("application-form"))
                .andExpect(model().attributeErrorCount("form", 2))
                .andExpect(model().attributeHasFieldErrors("form", "formInput[1]"));
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
        String value = "Form Input "+formInputId+" Response";

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/saveFormElement")
                        .param("formInputId", formInputId.toString())
                        .param("fieldName", "formInput["+formInputId+"]")
                        .param("value", value)
        ).andExpect(status().isOk());

        Mockito.inOrder(formInputResponseService).verify(formInputResponseService, calls(1)).save(loggedInUser.getId(), application.getId(), formInputId, value, false);
    }

    @Test
    public void testSaveFormElementApplicationTitle() throws Exception {
        String value = "New application title #216";
        String fieldName = "application.name";

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/saveFormElement")
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
                post("/application/" + application.getId().toString() + "/form/saveFormElement")
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
    public void testSaveFormElementSpacesApplicationTitle() throws Exception {
        String value = " ";
        String fieldName = "application.name";

        MvcResult result = mockMvc.perform(
                post("/application/"+application.getId().toString()+"/form/saveFormElement")
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
     public void testSaveFormElementApplicationDuration() throws Exception {
        String value = "12";
        String fieldName = "application.durationInMonths";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/saveFormElement")
                        .param("formInputId", "")
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
    public void testSaveFormElementApplicationInvalidDuration() throws Exception {
        String value = "aaaa";
        String fieldName = "application.durationInMonths";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/saveFormElement")
                        .param("formInputId", "")
                        .param("fieldName", fieldName)
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"false\"}";
        Assert.assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementCostSubcontracting() throws Exception {
        String value = "123";
        String questionId = "cost-subcontracting-13-subcontractingCost";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", "subcontracting_costs-cost-13")
                        .param("value", value)
        ).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        String jsonExpectedContent = "{\"success\":\"true\"}";
        Assert.assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementApplicationStartDate() throws Exception {
        String value = "25";
        String questionId= "application_details-startdate_day";
        String fieldName = "application.startDate.dayOfMonth";

        MvcResult result = mockMvc.perform(
                post("/application/1/form/saveFormElement")
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

    //TODO: Change this to AutosaveElementException
    @Test
     public void testSaveFormElementApplicationAttributeInvalidDay() throws Exception {
        String questionId= "application_details-startdate_day";
        String fieldName = "application.startDate.dayOfMonth";
        String value = "35";

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/saveFormElement")
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
        String questionId= "application_details-startdate_month";
        String fieldName = "application.startDate.monthValue";
        String value = "13";

        MvcResult result = mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", fieldName)
                        .param("value", value)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        log.info("Response : "+ content);

        String jsonExpectedContent = "{\"success\":\"false\"}";
        Assert.assertEquals(jsonExpectedContent, content);
    }

    @Test
    public void testSaveFormElementApplicationAttributeValidYear() throws Exception {

        String questionId = "application_details-startdate_year";
        String value = "2015";

        when(sectionService.getById(anyLong())).thenReturn(null);

        mockMvc.perform(
                post("/application/" + application.getId().toString() + "/form/saveFormElement")
                        .param("formInputId", questionId)
                        .param("fieldName", "question[" + questionId + "]")
                        .param("value", value)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void testDeleteCost() throws Exception {
        String sectionId = "1";
        Long costId = 1L;

        mockMvc.perform(
                post("/application/{applicationId}/form/section/{sectionId}", application.getId(), sectionId)
                        .param("remove_cost", String.valueOf(costId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/"+application.getId()+"/form/section/" + sectionId));

        // verify that the method is called to send the data to the data services.
        //Mockito.inOrder(financeRowService).verify(financeRowService, calls(1)).delete(costId);
    }

//    @Test
//    public void testAssignQuestion() throws Exception {
//        Model model = new RequestModel;
//        applicationFormController.assignQuestion(model, application.getId(), sectionId);
//    }

}
