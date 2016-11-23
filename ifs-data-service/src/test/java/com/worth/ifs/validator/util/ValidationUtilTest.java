package com.worth.ifs.validator.util;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.validator.SpendProfileCostValidator;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link ValidationUtil}
 *
 * Created by xiaonan.zhang on 21/11/2016.
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
