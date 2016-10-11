package com.worth.ifs.project.finance.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.controller.FinanceCheckController;
import com.worth.ifs.project.finance.resource.FinanceCheckPartnerStatusResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.FinanceCheckDocs.financeCheckApprovalStatusFields;
import static com.worth.ifs.documentation.FinanceCheckDocs.financeCheckSummaryResourceFields;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.project.controller.FinanceCheckController.*;
import static com.worth.ifs.project.finance.builder.FinanceCheckPartnerStatusResourceBuilder.newFinanceCheckPartnerStatusResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static com.worth.ifs.project.finance.resource.FinanceCheckState.READY_TO_APPROVE;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceCheckControllerDocumentation extends BaseControllerMockMVCTest<FinanceCheckController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setup(){
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void generateFinanceCheck(){
        // TODO RP
    }

    @Test
    public void saveFinanceCheck() {
        // TODO RP
    }

    @Test
    public void getFinanceCheck() {
        // TODO RP
    }

    @Test
    public void approveFinanceCheck() throws Exception {

        when(financeCheckServiceMock.approve(123L, 456L)).thenReturn(serviceSuccess());

        String url = FINANCE_CHECK_BASE_URL + "/{projectId}" + FINANCE_CHECK_ORGANISATION_PATH + "/{organisationId}" +
                FINANCE_CHECK_PATH + "/approve";

        mockMvc.perform(post(url, 123L, 456L)).
                andExpect(status().isOk()).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Finance Check is linked"),
                                parameterWithName("organisationId").description("Id of the organisation to which the Finance Check is linked")
                        )
                ));

        verify(financeCheckServiceMock).approve(123L, 456L);
    }

    @Test
    public void getFinanceCheckApprovalStatus() throws Exception {

        FinanceCheckProcessResource status = newFinanceCheckProcessResource().
                withCanApprove(true).
                withInternalParticipant(newUserResource().withFirstName("John").withLastName("Doe").withEmail("john.doe@innovateuk.gov.uk").build()).
                withParticipant(newProjectUserResource().withUserName("Steve Smith").withEmail("steve.smith@empire.com").withProject(123L).withOrganisation(456L).build()).
                withState(READY_TO_APPROVE).
                withModifiedDate(LocalDateTime.of(2016, 10, 04, 12, 10, 02)).
                build();

        when(financeCheckServiceMock.getFinanceCheckApprovalStatus(123L, 456L)).thenReturn(serviceSuccess(status));

        String url = FINANCE_CHECK_BASE_URL + "/{projectId}" + FINANCE_CHECK_ORGANISATION_PATH + "/{organisationId}" +
                FINANCE_CHECK_PATH + "/status";

        mockMvc.perform(get(url, 123L, 456L)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(status))).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Finance Check is linked"),
                                parameterWithName("organisationId").description("Id of the organisation to which the Finance Check is linked")
                        ),
                        responseFields(financeCheckApprovalStatusFields)
                ));

        verify(financeCheckServiceMock).getFinanceCheckApprovalStatus(123L, 456L);
    }

    @Test
    public void getFinanceCheckSummary() throws Exception {
        Long projectId = 123L;
        Long competitionId = 456L;

        List<FinanceCheckPartnerStatusResource> partnerStatusResources = newFinanceCheckPartnerStatusResource().withId(1L, 2L, 3L).withName("Organisation A", "Organisation B", "Organisation C").withEligibility(FinanceCheckPartnerStatusResource.Eligibility.REVIEW, FinanceCheckPartnerStatusResource.Eligibility.APPROVED, FinanceCheckPartnerStatusResource.Eligibility.APPROVED).build(3);

        FinanceCheckSummaryResource expected = newFinanceCheckSummaryResource().
                withProjectId(projectId).
                withCompetitionId(competitionId).
                withProjectStartDate(LocalDate.now()).
                withDurationInMonths(6).
                withTotalProjectCost(new BigDecimal(10000.00)).
                withGrantAppliedFor(new BigDecimal(5000.00)).
                withOtherPublicSectorFunding(new BigDecimal(0.00)).
                withTotalPercentageGrant(new BigDecimal(50.00)).
                withSpendProfilesGenerated(false).
                withFinanceChecksAllApproved(false).
                withPartnerStatusResources(partnerStatusResources).
                build();

        when(financeCheckServiceMock.getFinanceCheckSummary(123L)).thenReturn(serviceSuccess(expected));

        String url = FINANCE_CHECK_BASE_URL + "/{projectId}" + FINANCE_CHECK_PATH;

        mockMvc.perform(get(url, 123L)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(expected))).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Finance Check is linked")
                        ),
                        responseFields(financeCheckSummaryResourceFields)
                ));

        verify(financeCheckServiceMock).getFinanceCheckSummary(123L);
    }

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}
