package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder;
import org.innovateuk.ifs.application.finance.view.DefaultFinanceFormHandler;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.Materials;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.format.DateTimeFormatter;

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.noErrors;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationAjaxControllerTest extends BaseControllerMockMVCTest<ApplicationAjaxController> {

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private DefaultFinanceFormHandler defaultFinanceFormHandler;

    private ApplicationResource application;
    private Long questionId;
    private Long formInputId;
    private Long costId;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
    private ApplicantSectionResourceBuilder sectionBuilder;

    @Override
    protected ApplicationAjaxController supplyControllerUnderTest() {
        return new ApplicationAjaxController();
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();

        application = applications.get(0);
        questionId = 1L;
        formInputId = 111L;
        costId = 1L;

        // save actions should always succeed.
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), eq(""), anyBoolean())).thenReturn(restSuccess(new ValidationMessages(fieldError("value", "", "Please enter some text 123"))));
        when(formInputResponseRestService.saveQuestionResponse(anyLong(), anyLong(), anyLong(), anyString(), anyBoolean())).thenReturn(restSuccess(noErrors()));
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
        String questionId = "formInput[cost-subcontracting-13-subcontractingCost]";

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
        String questionId = "formInput[cost-subcontracting-13-subcontractingCost]";

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
}
