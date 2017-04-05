package org.innovateuk.ifs.project.financecheck.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.controller.ProjectFinanceRowController;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.PayloadDocumentation;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceRowControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceRowController> {

    private static final String BASE_URL = "/cost/project";

    @Mock
    private ValidationUtil validationUtil;

    @Test
    public void addNewCost() throws Exception {
        String url = BASE_URL + "/add/{projectFinanceId}/{questionId}";

        when(validationUtil.validateProjectCostItem(any(FinanceRowItem.class))).thenReturn(new ValidationMessages());
        when(projectFinanceRowServiceMock.addCost(123L, 456L, null)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(post(url, 123L, 456L).
                contentType(APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andDo(document("project/finance/{method-name}",
                        pathParameters(
                                parameterWithName("projectFinanceId").description("Id of project finance associated with particular project and organisation to which a new cost row should to be added"),
                                parameterWithName("questionId").description("Id of question for which a new finance row should be added")
                        )
                ));
    }

    @Test
    public void getCostItem() throws Exception{
        String url = BASE_URL + "/{id}";

        when(projectFinanceRowServiceMock.getCostItem(123L)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(get(url, 123L).
                contentType(APPLICATION_JSON)).
                andExpect(status().isOk()).
                andDo(document("project/finance/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of cost item to be returned")
                        ),
                        PayloadDocumentation.responseFields(
                                ProjectFinanceResponseFields.projectFinanceGrantClaimRowFields
                        )
                ));
    }

    @Test
    public void update() throws Exception {

        GrantClaim costItem = new GrantClaim();
        when(projectFinanceRowServiceMock.updateCost(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceSuccess(costItem));
        when(validationUtil.validateProjectCostItem(isA(FinanceRowItem.class))).thenReturn(new ValidationMessages());
        mockMvc.perform(put("/cost/project/update/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isOk()).
                andDo(document("project/finance/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of cost item to be updated")
                        )
                ));
    }

    @Test
    public void deleteCostItem() throws Exception {

        when(projectFinanceRowServiceMock.deleteCost(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/cost/project/delete/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()).
                andDo(document("project/finance/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of cost item to be deleted")
                        )
                ));
    }

    @Override
    protected ProjectFinanceRowController supplyControllerUnderTest() {
        return new ProjectFinanceRowController();
    }
}
