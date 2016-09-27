package com.worth.ifs.project.finance.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.project.builder.SpendProfileResourceBuilder;
import com.worth.ifs.project.controller.FinanceCheckController;
import com.worth.ifs.project.controller.ProjectFinanceController;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Test;

import java.math.BigDecimal;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceCheckControllerTest extends BaseControllerMockMVCTest<FinanceCheckController> {

    @Test
    public void testGenerateFinanceCheck(){
        // TODO RP
    }

    @Test
    public void testSaveFinanceCheck() {
        // TODO RP
    }

    @Test
    public void testGetFinanceCheck() {
        // TODO RP
    }

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}
