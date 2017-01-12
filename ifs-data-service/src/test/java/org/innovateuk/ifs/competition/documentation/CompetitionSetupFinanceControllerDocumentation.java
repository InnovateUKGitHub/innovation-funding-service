package org.innovateuk.ifs.competition.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.controller.CompetitionSetupFinanceController;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupFinanceService;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.project.bankdetails.controller.CompetitionBankDetailsController;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.user.domain.Organisation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.List;


import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

public class CompetitionSetupFinanceControllerDocumentation extends BaseControllerMockMVCTest<CompetitionSetupFinanceController> {

    @Mock
    private CompetitionSetupFinanceService competitionSetupFinanceService;

    private static String baseUrl = "/competition-setup-finance";

    @Override
    protected CompetitionSetupFinanceController supplyControllerUnderTest() {
        return new CompetitionSetupFinanceController();
    }

    @Test
    public void save() throws Exception {
        final Long competitionId = 1L;
        CompetitionSetupFinanceResource resource = newCompetitionSetupFinanceResource().
                withCompetitionId(competitionId).
                withFullApplicationFinance(false).
                withIncludeGrowthTable(false).
                build();
        when(competitionSetupFinanceService.save(resource)).thenReturn(serviceSuccess());
        mockMvc.perform(
                put(baseUrl + "/{competitionId}", competitionId).
                        contentType(APPLICATION_JSON).
                        content(toJson(resource))).
                andDo(document("competition-setup-finance/{method-name}", pathParameters(parameterWithName("competitionId").description("The competition id")), requestFields(
                        COMPETITION_SETUP_FINANCE_RESOURCE_FIELDS
                )));
    }

    @Test
    public void getByCompetition() throws Exception {
        final Long competitionId = 1L;
        CompetitionSetupFinanceResource resource = newCompetitionSetupFinanceResource().
                withCompetitionId(competitionId).
                withFullApplicationFinance(false).
                withIncludeGrowthTable(false).
                build();
        when(competitionSetupFinanceService.getForCompetition(competitionId)).thenReturn(serviceSuccess(resource));
        mockMvc.perform(
                get(baseUrl + "/{competitionId}", competitionId).
                        contentType(APPLICATION_JSON).
                        content(toJson(resource))).
                andDo(document("competition-setup-finance/{method-name}",
                        pathParameters(parameterWithName("competitionId").description("The competition id")),
                        responseFields(COMPETITION_SETUP_FINANCE_RESOURCE_FIELDS)));
    }

    public static final FieldDescriptor[] COMPETITION_SETUP_FINANCE_RESOURCE_FIELDS = {
            fieldWithPath("competitionId").description("The id of the competition"),
            fieldWithPath("fullApplicationFinance").description("Full application finance"),
            fieldWithPath("includeGrowthTable").description("The active status of staff count and staff turnover form inputs are false when this is true"),
    };
}
