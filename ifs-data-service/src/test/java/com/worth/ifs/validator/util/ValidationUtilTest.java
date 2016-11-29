package com.worth.ifs.validator.util;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.validation.SpendProfileCostValidator;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link ValidationUtil}
 */

public class ValidationUtilTest extends BaseUnitTestMocksTest {

    @Mock
    private SpendProfileCostValidator spendProfileCostValidator;

    @InjectMocks
    private ValidationUtil validationUtil;

    @Test
    public void testValidateSpendProfileTableResource() {

        SpendProfileTableResource tableResource = new SpendProfileTableResource();

        when(spendProfileCostValidator.supports(eq(SpendProfileTableResource.class))).thenReturn(Boolean.TRUE);

        validationUtil.validateSpendProfileTableResource(tableResource);

        verify(spendProfileCostValidator).validate(eq(tableResource), anyObject());
    }



}
