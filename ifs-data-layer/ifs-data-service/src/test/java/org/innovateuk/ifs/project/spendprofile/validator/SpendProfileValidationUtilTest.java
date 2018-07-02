package org.innovateuk.ifs.project.spendprofile.validator;

import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.spendprofile.validation.SpendProfileCostValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpendProfileValidationUtilTest {

    @Mock
    private SpendProfileCostValidator spendProfileCostValidator;

    @InjectMocks
    private SpendProfileValidationUtil validationUtil;

    @Test
    public void testValidateSpendProfileTableResource() {

        SpendProfileTableResource tableResource = new SpendProfileTableResource();

        when(spendProfileCostValidator.supports(ArgumentMatchers.eq(SpendProfileTableResource.class))).thenReturn(Boolean.TRUE);

        validationUtil.validateSpendProfileTableResource(tableResource);

        Mockito.verify(spendProfileCostValidator).validate(ArgumentMatchers.eq(tableResource), ArgumentMatchers.anyObject());
    }
}
