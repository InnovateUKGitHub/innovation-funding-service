package org.innovateuk.ifs.competitionsetup.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupFinanceController;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupFinanceService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;
import static org.innovateuk.ifs.documentation.CompetitionSetupFinanceDocs.COMPETITION_SETUP_FINANCE_RESOURCE_FIELDS;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

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
        long competitionId = 1L;
        CompetitionSetupFinanceResource resource = newCompetitionSetupFinanceResource()
                .withCompetitionId(competitionId)
                .withApplicationFinanceType(NO_FINANCES)
                .withIncludeGrowthTable(false)
                .build();
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
        long competitionId = 1L;
        CompetitionSetupFinanceResource resource = newCompetitionSetupFinanceResource()
                .withCompetitionId(competitionId)
                .withApplicationFinanceType(NO_FINANCES)
                .withIncludeGrowthTable(false)
                .build();
        when(competitionSetupFinanceService.getForCompetition(competitionId)).thenReturn(serviceSuccess(resource));
        mockMvc.perform(
                get(baseUrl + "/{competitionId}", competitionId).
                        contentType(APPLICATION_JSON).
                        content(toJson(resource))).
                andDo(document("competition-setup-finance/{method-name}",
                        pathParameters(parameterWithName("competitionId").description("The competition id")),
                        responseFields(COMPETITION_SETUP_FINANCE_RESOURCE_FIELDS)));
    }
}
