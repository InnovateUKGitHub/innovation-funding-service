package org.innovateuk.ifs.validator.util;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.validation.SpendProfileCostValidator;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

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

        Mockito.when(spendProfileCostValidator.supports(Matchers.eq(SpendProfileTableResource.class))).thenReturn(Boolean.TRUE);

        validationUtil.validateSpendProfileTableResource(tableResource);

        Mockito.verify(spendProfileCostValidator).validate(Matchers.eq(tableResource), Matchers.anyObject());
    }



}
