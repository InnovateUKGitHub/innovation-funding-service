package org.innovateuk.ifs.project.financecheck.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.controller.ProjectFinanceRowController;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.validator.FinanceValidationUtil;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ValidationMessagesDocs.validationMessagesFields;
import static org.innovateuk.ifs.project.financecheck.documentation.ProjectFinanceResponseFields.projectFinanceGrantClaimRowFields;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceRowControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceRowController> {

    private static final String BASE_URL = "/project-finance-row";

    @Mock
    private ProjectFinanceRowService projectFinanceRowServiceMock;

    @Mock
    private FinanceValidationUtil validationUtil;


    @Test
    public void getCostItem() throws Exception{
        String url = BASE_URL + "/{id}";

        when(projectFinanceRowServiceMock.get(123L)).thenReturn(serviceSuccess(new GrantClaim(1L)));

        mockMvc.perform(get(url, 123L)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("project/finance/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of cost item to be returned")
                        ),
                        responseFields(projectFinanceGrantClaimRowFields)
                ));
    }

    @Test
    public void updateCostItem() throws Exception {

        GrantClaim costItem = new GrantClaim(1L);
        when(projectFinanceRowServiceMock.update(eq(123L), isA(FinanceRowItem.class))).thenReturn(serviceSuccess(costItem));
        when(validationUtil.validateProjectCostItem(isA(FinanceRowItem.class))).thenReturn(new ValidationMessages());
        mockMvc.perform(put(BASE_URL + "/{id}", "123")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(costItem)))
                .andExpect(status().isOk()).
                andDo(document("project/finance/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of cost item to be updated")
                        ),
                        responseFields(validationMessagesFields)
                ));
    }

    @Test
    public void deleteCostItem() throws Exception {

        when(projectFinanceRowServiceMock.delete(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(delete(BASE_URL + "/{id}", "123")
                .header("IFS_AUTH_TOKEN", "123abc")
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
