package com.worth.ifs.project.finance.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.controller.ProjectFinanceController;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.SpendProfileTableDocs.spendProfileTableFields;
import static java.util.Arrays.asList;
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

public class ProjectFinanceControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setup(){
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void generateSpendProfile() throws Exception {

        when(projectFinanceServiceMock.generateSpendProfile(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/spend-profile/generate", 123L)).
                andExpect(status().isCreated()).
                andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the " +
                                        "Spend Profile information is being generated")
                        )
                ));
    }

    @Test
    public void getSpendProfile()  throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        SpendProfileTableResource table = new SpendProfileTableResource();
        table.setMonths(asList(new LocalDateResource(2016, 1, 1), new LocalDateResource(2016, 2, 1), new LocalDateResource(2016, 3, 1)));
        table.setEligibleCostPerCategoryMap(buildEligibleCostPerCategoryMap());
        table.setMonthlyCostsPerCategoryMap(buildSpendProfileCostsPerCategoryMap());

        when(projectFinanceServiceMock.getSpendProfileTable(projectId, organisationId)).thenReturn(serviceSuccess(table));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(table)))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being retrieved")
                        ),
                        responseFields(spendProfileTableFields)
                ));
    }

    @Test
    public void getSpendProfileWhenSpendProfileDataNotInDb()  throws Exception {

        Long projectId = 1L;
        Long organisationId = 1L;

        when(projectFinanceServiceMock.getSpendProfileTable(projectId, organisationId)).
                    thenReturn(serviceFailure(notFoundError(SpendProfileResource.class, projectId, organisationId)));

        mockMvc.perform(get("/project/{projectId}/partner-organisation/{organisationId}/spend-profile", projectId, organisationId))
                .andExpect(status().isNotFound())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the Spend Profile data is being retrieved"),
                                parameterWithName("organisationId").description("Organisation Id for which the Spend Profile data is being retrieved")
                        )
                ));
    }

    private Map<String, BigDecimal> buildEligibleCostPerCategoryMap() {

        Map<String, BigDecimal> eligibleCostPerCategoryMap = new LinkedHashMap<>();

        eligibleCostPerCategoryMap.put("LabourCost", new BigDecimal("240"));
        eligibleCostPerCategoryMap.put("CapitalCost", new BigDecimal("190"));
        eligibleCostPerCategoryMap.put("OtherCost", new BigDecimal("149"));

        return eligibleCostPerCategoryMap;
    }

    private Map<String, List<BigDecimal>> buildSpendProfileCostsPerCategoryMap() {

        Map<String, List<BigDecimal>> spendProfileCostsPerCategoryMap = new LinkedHashMap<>();

        spendProfileCostsPerCategoryMap.put("LabourCost", asList(new BigDecimal("100"), new BigDecimal("120"), new BigDecimal("20")));
        spendProfileCostsPerCategoryMap.put("CapitalCost", asList(new BigDecimal("90"), new BigDecimal("50"), new BigDecimal("50")));
        spendProfileCostsPerCategoryMap.put("OtherCost", asList(new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("149")));

        return spendProfileCostsPerCategoryMap;
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
    }
}
