package org.innovateuk.ifs.eugrant.funding.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.eugrant.*;
import org.innovateuk.ifs.eugrant.funding.controller.EuFundingController;
import org.innovateuk.ifs.eugrant.funding.form.EuFundingForm;
import org.innovateuk.ifs.eugrant.funding.populator.EuFundingFormPopulator;
import org.innovateuk.ifs.eugrant.funding.saver.EuFundingSaver;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.eugrant.builder.EuActionTypeResourceBuilder.newEuActionTypeResource;
import static org.innovateuk.ifs.eugrant.builder.EuFundingResourceBuilder.newEuFundingResource;
import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class EuFundingControllerTest extends BaseControllerMockMVCTest<EuFundingController> {


    @Mock
    private EuGrantCookieService euGrantCookieService;

    @Mock
    private EuActionTypeRestService euActionTypeRestService;

    @Mock
    private EuFundingSaver euFundingSaver;

    @Spy
    @InjectMocks
    private EuFundingFormPopulator euFundingFormPopulator;

    protected static ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected EuFundingController supplyControllerUnderTest() {
        return new EuFundingController();
    }

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void fundingDetails() throws Exception {

        EuActionTypeResource euActionTypeResource = newEuActionTypeResource()
                .withId(1L)
                .withName("Action Type")
                .withDescription("Description")
                .withPriority(1)
                .build();

        EuFundingResource euFundingResource = newEuFundingResource()
                .withActionType(euActionTypeResource)
                .withFundingContribution(BigDecimal.valueOf(100000L))
                .withGrantAgreementNumber("123456")
                .withProjectCoordinator(true)
                .withProjectStartDate(LocalDate.now())
                .withProjectEndDate(LocalDate.now().plusYears(1L))
                .withProjectName("Project Name")
                .withParticipantId("123456")
                .build();

        EuGrantResource euGrantResource = newEuGrantResource()
                .withFunding(euFundingResource)
                .build();

        when(euGrantCookieService.get()).thenReturn(euGrantResource);
        when(euActionTypeRestService.getById(euActionTypeResource.getId())).thenReturn(restSuccess(euActionTypeResource));

        mockMvc.perform(get("/funding-details"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("funding/funding-details"));
    }

    @Test
    public void redirectToEditWhenFDetailsNotFilledIn() throws Exception {

        EuGrantResource euGrantResource = new EuGrantResource();

        when(euGrantCookieService.get()).thenReturn(euGrantResource);

        mockMvc.perform(get("/funding-details"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/funding-details/edit"));
    }

    @Test
    public void submitFundingDetails() throws Exception {

        EuActionTypeResource euActionTypeResource = newEuActionTypeResource()
                .withId(1L)
                .withName("Action Type")
                .withDescription("Description")
                .withPriority(1)
                .build();

        EuGrantResource euGrantResource = newEuGrantResource()
                .build();

        EuFundingForm fundingForm = new EuFundingForm();
        fundingForm.setActionType(1L);
        fundingForm.setFundingContribution(BigDecimal.valueOf(100000L));
        fundingForm.setGrantAgreementNumber("123456");
        fundingForm.setProjectCoordinator(true);
        fundingForm.setStartDateMonth(10);
        fundingForm.setStartDateYear(2000);
        fundingForm.setEndDateMonth(10);
        fundingForm.setEndDateYear(2020);
        fundingForm.setParticipantId("123456");
        fundingForm.setProjectName("Project Name");

        when(euGrantCookieService.get()).thenReturn(euGrantResource);
        when(euFundingSaver.save(fundingForm)).thenReturn(restSuccess());
        when(euActionTypeRestService.findAll()).thenReturn(restSuccess(Arrays.asList(euActionTypeResource)));

        mockMvc.perform(post("/funding-details/edit")
                .param("grantAgreementNumber", fundingForm.getGrantAgreementNumber())
                .param("participantId", fundingForm.getParticipantId())
                .param("projectName", fundingForm.getProjectName())
                .param("startDateMonth", String.valueOf(fundingForm.getStartDateMonth()))
                .param("startDateYear", String.valueOf(fundingForm.getStartDateYear()))
                .param("endDateMonth", String.valueOf(fundingForm.getEndDateMonth()))
                .param("endDateYear", String.valueOf(fundingForm.getEndDateYear()))
                .param("fundingContribution", String.valueOf(fundingForm.getFundingContribution()))
                .param("projectCoordinator", String.valueOf(fundingForm.isProjectCoordinator()))
                .param("actionType", String.valueOf(fundingForm.getActionType())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/funding-details"));
    }
}
