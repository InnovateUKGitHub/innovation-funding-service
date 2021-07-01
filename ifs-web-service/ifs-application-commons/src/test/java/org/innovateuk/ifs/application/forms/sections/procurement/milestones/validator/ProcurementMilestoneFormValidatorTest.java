package org.innovateuk.ifs.application.forms.sections.procurement.milestones.validator;

import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.application.forms.sections.procurement.milestones.form.ProcurementMilestoneForm;
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

    private static final ProcurementMilestonesForm form;
    static {
        ProcurementMilestoneForm row = new ProcurementMilestoneForm();
        row.setPayment(new BigInteger("100"));
        form = new ProcurementMilestonesForm(ImmutableMap.<String, ProcurementMilestoneForm>builder()
                .put("Id", row)
                .build());
    }

    @Test
    public void validate_pass() {
        BaseFinanceResource finance = mock(BaseFinanceResource.class);
        ValidationHandler validationHandler = mock(ValidationHandler.class);

        when(finance.getTotalFundingSought()).thenReturn(new BigDecimal("100"));

        validator.validate(form, finance, validationHandler);

        verify(validationHandler, never()).addError(any());
    }

    @Test
    public void validate_higher() {
        BaseFinanceResource finance = mock(BaseFinanceResource.class);
        ValidationHandler validationHandler = mock(ValidationHandler.class);

        when(finance.getTotalFundingSought()).thenReturn(new BigDecimal("99"));

        validator.validate(form, finance, validationHandler);

        verify(validationHandler).addError(argThat(lambdaMatches(error -> {
            assertThat(error.getErrorKey(), is(equalTo("validation.procurement.milestones.total.higher")));
            assertThat(error.getFieldName(), is(equalTo("totalErrorHolder")));
            return true;
        })));
    }

    @Test
    public void validate_lower() {
        BaseFinanceResource finance = mock(BaseFinanceResource.class);
        ValidationHandler validationHandler = mock(ValidationHandler.class);

        when(finance.getTotalFundingSought()).thenReturn(new BigDecimal("101"));

        validator.validate(form, finance, validationHandler);

        verify(validationHandler).addError(argThat(lambdaMatches(error -> {
            assertThat(error.getErrorKey(), is(equalTo("validation.procurement.milestones.total.lower")));
            assertThat(error.getFieldName(), is(equalTo("totalErrorHolder")));
            return true;
        })));
    }

    @Test
    public void validate_paymentNull() {
        ProcurementMilestoneForm row = new ProcurementMilestoneForm();
        ProcurementMilestonesForm nullForm = new ProcurementMilestonesForm(ImmutableMap.<String, ProcurementMilestoneForm>builder()
                .put("Id", row)
                .build());
        BaseFinanceResource finance = mock(BaseFinanceResource.class);
        ValidationHandler validationHandler = mock(ValidationHandler.class);

        when(finance.getTotalFundingSought()).thenReturn(new BigDecimal("100"));

        validator.validate(nullForm, finance, validationHandler);

        verify(validationHandler).addError(argThat(lambdaMatches(error -> {
            assertThat(error.getErrorKey(), is(equalTo("validation.procurement.milestones.total.lower")));
            assertThat(error.getFieldName(), is(equalTo("totalErrorHolder")));
            return true;
        })));
    }

    @Test
    public void validate_paymentNegative() {
        ProcurementMilestoneForm row = new ProcurementMilestoneForm();
        row.setPayment(new BigInteger("-1"));
        ProcurementMilestonesForm nullForm = new ProcurementMilestonesForm(ImmutableMap.<String, ProcurementMilestoneForm>builder()
                .put("Id", row)
                .build());
        BaseFinanceResource finance = mock(BaseFinanceResource.class);
        ValidationHandler validationHandler = mock(ValidationHandler.class);

        when(finance.getTotalFundingSought()).thenReturn(new BigDecimal("100"));

        validator.validate(nullForm, finance, validationHandler);

        verify(validationHandler).addError(argThat(lambdaMatches(error -> {
            assertThat(error.getErrorKey(), is(equalTo("validation.procurement.milestones.total.lower")));
            assertThat(error.getFieldName(), is(equalTo("totalErrorHolder")));
            return true;
        })));
    }
}