package com.worth.ifs.project.finance.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.builder.CostCategoryResourceBuilder;
import com.worth.ifs.project.builder.CostGroupResourceBuilder;
import com.worth.ifs.project.builder.CostResourceBuilder;
import com.worth.ifs.project.builder.FinanceCheckResourceBuilder;
import com.worth.ifs.project.finance.domain.Cost;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.finance.resource.CostCategoryResource;
import com.worth.ifs.project.finance.resource.CostGroupResource;
import com.worth.ifs.project.finance.resource.CostResource;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CONTAINS_FRACTIONS_IN_COST_FOR_SPECIFIED_CATEGORY_AND_MONTH;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.builder.CostCategoryResourceBuilder.newCostCategoryResource;
import static com.worth.ifs.project.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FinanceCheckServiceImplTest extends BaseServiceUnitTest<FinanceCheckServiceImpl> {

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
    protected FinanceCheckServiceImpl supplyServiceUnderTest() {
        return new FinanceCheckServiceImpl();
    }
}
