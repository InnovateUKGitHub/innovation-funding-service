package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.resource.FinanceRowMetaFieldResource;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceRowMetaFieldControllerTest extends BaseControllerMockMVCTest<FinanceRowMetaFieldController> {

    @Mock
    private FinanceRowCostsService costFieldService;

    @Override
    protected FinanceRowMetaFieldController supplyControllerUnderTest() {
        return new FinanceRowMetaFieldController();
    }

    @Test
    public void findAllShouldReturnListOfCostFields() throws Exception{
        when(costFieldService.findAllCostFields()).thenReturn(serviceSuccess(asList(new FinanceRowMetaFieldResource(), new FinanceRowMetaFieldResource())));

        mockMvc.perform(get("/costfield/findAll/"))
                .andExpect(status().isOk());

        verify(costFieldService, times(1)).findAllCostFields();
        verifyNoMoreInteractions(costFieldService);
    }
}
