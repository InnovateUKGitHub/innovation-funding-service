package com.worth.ifs.finance.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.finance.resource.CostFieldResource;
import com.worth.ifs.finance.transactional.CostFieldService;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CostFieldControllerTest extends BaseControllerMockMVCTest<CostFieldController> {

    @Mock
    private CostFieldService costFieldService;

    @Override
    protected CostFieldController supplyControllerUnderTest() {
        return new CostFieldController();
    }

    @Test
    public void findAllShouldReturnListOfCostFields() throws Exception{
        when(costFieldService.findAll()).thenReturn(serviceSuccess(asList(new CostFieldResource(), new CostFieldResource())));

        mockMvc.perform(get("/costfield/findAll/"))
                .andExpect(status().isOk());

        verify(costFieldService, times(1)).findAll();
        verifyNoMoreInteractions(costFieldService);
    }
}
