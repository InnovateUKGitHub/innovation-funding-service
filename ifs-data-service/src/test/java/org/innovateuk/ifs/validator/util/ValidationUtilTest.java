package org.innovateuk.ifs.validator.util;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.validation.SpendProfileCostValidator;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.handler.item.MaterialsHandler;
import org.innovateuk.ifs.finance.resource.cost.Materials;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.validator.transactional.ValidatorService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link ValidationUtil}
 */

public class ValidationUtilTest extends BaseUnitTestMocksTest {

    @Mock
    private SpendProfileCostValidator spendProfileCostValidator;

    @Mock
    private ValidatorService validatorServiceMock;

    @InjectMocks
    private ValidationUtil validationUtil;

    private ProjectFinanceRow materialCost;

    @Test
    public void testValidateSpendProfileTableResource() {

        SpendProfileTableResource tableResource = new SpendProfileTableResource();

        when(spendProfileCostValidator.supports(Matchers.eq(SpendProfileTableResource.class))).thenReturn(Boolean.TRUE);

        validationUtil.validateSpendProfileTableResource(tableResource);

        Mockito.verify(spendProfileCostValidator).validate(Matchers.eq(tableResource), Matchers.anyObject());
    }


    @Test
    public void testValidateProjectCostItem(){
        Materials material = new Materials();
        material.setCost(BigDecimal.valueOf(100));
        material.setItem("");
        material.setQuantity(5);

        when(validatorServiceMock.getProjectCostHandler(material)).thenReturn(new MaterialsHandler());

        ValidationMessages validationMessages = validationUtil.validateProjectCostItem(material);

        assertNotNull(validationMessages);
        assertEquals(validationMessages.getErrors().size(), 1);
        assertEquals(validationMessages.getFieldErrors("item").get(0).getErrorKey(), "validation.field.must.not.be.blank");
        assertEquals(validationMessages.getFieldErrors("item").get(0).getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
    }
}
