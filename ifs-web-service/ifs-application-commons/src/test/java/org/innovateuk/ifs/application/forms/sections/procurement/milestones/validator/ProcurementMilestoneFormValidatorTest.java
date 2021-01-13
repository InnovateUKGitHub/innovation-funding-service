package org.innovateuk.ifs.application.forms.sections.procurement.milestones.validator;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestonesForm;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcurementMilestoneFormValidatorTest {

    @InjectMocks
    private ProcurementMilestoneFormValidator validator;

    @Test
    public void validate_pass() {
        ProcurementMilestonesForm form = mock(ProcurementMilestonesForm.class);
        BaseFinanceResource finance = mock(BaseFinanceResource.class);
        ValidationHandler validationHandler = mock(ValidationHandler.class);

        when(form.getTotalPayments()).thenReturn(new BigInteger("100"));
        when(finance.getTotalFundingSought()).thenReturn(new BigDecimal("100"));

        validator.validate(form, finance, validationHandler);

        verifyZeroInteractions(validationHandler);
    }

    @Test
    public void validate_higher() {
        ProcurementMilestonesForm form = mock(ProcurementMilestonesForm.class);
        BaseFinanceResource finance = mock(BaseFinanceResource.class);
        ValidationHandler validationHandler = mock(ValidationHandler.class);

        when(form.getTotalPayments()).thenReturn(new BigInteger("101"));
        when(finance.getTotalFundingSought()).thenReturn(new BigDecimal("100"));

        validator.validate(form, finance, validationHandler);

        verify(validationHandler).addError(argThat(lambdaMatches(error -> {
            assertThat(error.getErrorKey(), is(equalTo("validation.procurement.milestones.total.higher")));
            assertThat(error.getFieldName(), is(equalTo("totalErrorHolder")));
            return true;
        })));
    }

    @Test
    public void validate_lower() {
        ProcurementMilestonesForm form = mock(ProcurementMilestonesForm.class);
        BaseFinanceResource finance = mock(BaseFinanceResource.class);
        ValidationHandler validationHandler = mock(ValidationHandler.class);

        when(form.getTotalPayments()).thenReturn(new BigInteger("99"));
        when(finance.getTotalFundingSought()).thenReturn(new BigDecimal("100"));

        validator.validate(form, finance, validationHandler);

        verify(validationHandler).addError(argThat(lambdaMatches(error -> {
            assertThat(error.getErrorKey(), is(equalTo("validation.procurement.milestones.total.lower")));
            assertThat(error.getFieldName(), is(equalTo("totalErrorHolder")));
            return true;
        })));
    }
}