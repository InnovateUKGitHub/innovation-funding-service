package org.innovateuk.ifs.application.forms.academiccosts.saver;

import org.hamcrest.Matcher;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class AcademicCostSaverTest extends BaseServiceUnitTest<AcademicCostSaver> {

    private static final long APPLICATION_ID = 1L;
    private static final long ORGANISATION_ID = 2L;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private DefaultFinanceRowRestService financeRowRestService;

    @Mock
    private AsyncFuturesGenerator futuresGeneratorMock;

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGeneratorMock);
    }

    @Override
    protected AcademicCostSaver supplyServiceUnderTest() {
        return new AcademicCostSaver();
    }

    @Test
    public void save() {
        ApplicationFinanceResource finance = newApplicationFinanceResource()
                .withAcademicCosts()
                .build();
        AcademicCostForm form = new AcademicCostForm();
        BigDecimal cost = new BigDecimal("50");
        form.setExceptionsOtherCosts(cost);
        form.setExceptionsStaff(cost);
        form.setIndirectCosts(cost);
        form.setAllocatedOtherCosts(cost);
        form.setAllocatedEstateCosts(cost);
        form.setAllocatedInvestigators(cost);
        form.setIncurredOtherCosts(cost);
        form.setIncurredTravel(cost);
        form.setIncurredStaff(cost);
        form.setTsbReference("NewRef");

        when(financeRowRestService.update(argThat(hasNameAndItem("tsb_reference", "NewRef")))).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.update(argThat(hasNameAndCost("incurred_staff", cost)))).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.update(argThat(hasNameAndCost("incurred_travel_subsistence", cost)))).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.update(argThat(hasNameAndCost("incurred_other_costs", cost)))).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.update(argThat(hasNameAndCost("allocated_investigators", cost)))).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.update(argThat(hasNameAndCost("allocated_estates_costs", cost)))).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.update(argThat(hasNameAndCost("allocated_other_costs", cost)))).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.update(argThat(hasNameAndCost("indirect_costs", cost)))).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.update(argThat(hasNameAndCost("exceptions_staff", cost)))).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.update(argThat(hasNameAndCost("exceptions_other_costs", cost)))).thenReturn(restSuccess(new ValidationMessages()));

        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(RestResult.restSuccess(finance));

        ServiceResult<Void> result = service.save(form, APPLICATION_ID, ORGANISATION_ID);

        assertTrue(result.isSuccess());

        verify(financeRowRestService).update(argThat(hasNameAndItem("tsb_reference", "NewRef")));
        verify(financeRowRestService).update(argThat(hasNameAndCost("incurred_staff", cost)));
        verify(financeRowRestService).update(argThat(hasNameAndCost("incurred_travel_subsistence", cost)));
        verify(financeRowRestService).update(argThat(hasNameAndCost("incurred_other_costs", cost)));
        verify(financeRowRestService).update(argThat(hasNameAndCost("allocated_investigators", cost)));
        verify(financeRowRestService).update(argThat(hasNameAndCost("allocated_estates_costs", cost)));
        verify(financeRowRestService).update(argThat(hasNameAndCost("allocated_other_costs", cost)));
        verify(financeRowRestService).update(argThat(hasNameAndCost("indirect_costs", cost)));
        verify(financeRowRestService).update(argThat(hasNameAndCost("exceptions_staff", cost)));
        verify(financeRowRestService).update(argThat(hasNameAndCost("exceptions_other_costs", cost)));
        verifyNoMoreInteractions(financeRowRestService);

    }

    private Matcher<AcademicCost> hasNameAndCost(String name, BigDecimal value) {
        return lambdaMatches(cost -> cost.getName().equals(name) && cost.getCost().equals(value));
    }
    private Matcher<AcademicCost> hasNameAndItem(String name, String item) {
        return lambdaMatches(cost -> cost.getName().equals(name) && cost.getItem().equals(item));
    }
}
