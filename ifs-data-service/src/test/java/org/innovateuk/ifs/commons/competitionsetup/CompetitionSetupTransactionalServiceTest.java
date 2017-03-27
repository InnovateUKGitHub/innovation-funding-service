package org.innovateuk.ifs.commons.competitionsetup;


import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompetitionSetupTransactionalServiceTest {
    @Test
    public void test_GetForCompetitionErrorCountTurnover() {
        // Should never happen but check that reasonable error codes get returned in the event that the database
        // becomes inconsistent

        // Turnover and count - these should always be in sync - but here we test when they are not.
        FormInput staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(true).build();
        FormInput staffTurnoverFormInput = newFormInput().withType(STAFF_TURNOVER).withActive(false).build();

        // Method under test
        TestCompetitionSetupTransactionalService service = new TestCompetitionSetupTransactionalService();
        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTableByCountAndTurnover(staffCountFormInput, staffTurnoverFormInput);

        // Assertions
        assertTrue(shouldBeFailure.isFailure());
        assertEquals(1, shouldBeFailure.getErrors().size());
        assertEquals("include.growth.table.count.turnover.input.active.not.consistent", shouldBeFailure.getErrors().get(0).getErrorKey());
    }


    @Test
    public void test_GetForCompetitionErrorFinance() {
        // Should never happen but check that reasonable error codes get returned in the event that the database
        // becomes inconsistent

        // Financial inputs - these should always be in sync - but here we test when they are not.
        FormInput yearEnd = newFormInput().withType(FINANCIAL_YEAR_END).withActive(true).build();
        List<FormInput> overviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(true, true, true, false /*Inconsistent*/).build(4);
        FormInput count = newFormInput().withType(FormInputType.FINANCIAL_STAFF_COUNT).withActive(true).build();

        // Method under test
        TestCompetitionSetupTransactionalService service = new TestCompetitionSetupTransactionalService();
        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTableByFinance(yearEnd, overviewRows, count);

        // Assertions
        assertTrue(shouldBeFailure.isFailure());
        assertEquals(1, shouldBeFailure.getErrors().size());
        assertEquals("include.growth.table.finance.input.active.not.consistent", shouldBeFailure.getErrors().get(0).getErrorKey());
    }

    @Test
    public void test_GetForCompetitionErrorCountTurnoverFinance() {
        // Should never happen but check that reasonable error codes get returned in the event that the database
        // becomes inconsistent

        // Not consistent
        boolean byFinance = false;
        boolean byCountAndTurnover = true;
        // Method under test
        TestCompetitionSetupTransactionalService service = new TestCompetitionSetupTransactionalService();
        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTableByCountTurnoverAndFinance(byFinance, byCountAndTurnover);

        // Assertions
        assertTrue(shouldBeFailure.isFailure());
        assertEquals(1, shouldBeFailure.getErrors().size());
        assertEquals("include.growth.table.count.turnover.finance.input.active.not.consistent", shouldBeFailure.getErrors().get(0).getErrorKey());
    }

    public static class TestCompetitionSetupTransactionalService extends CompetitionSetupTransactionalService {
        @Override
        ServiceResult<Boolean> isIncludeGrowthTableByFinance(FormInput yearEnd, List<FormInput> overviewRows, FormInput count) {
            return super.isIncludeGrowthTableByFinance(yearEnd, overviewRows, count);
        }

        @Override
        ServiceResult<Boolean> isIncludeGrowthTableByCountAndTurnover(FormInput count, FormInput turnover) {
            return super.isIncludeGrowthTableByCountAndTurnover(count, turnover);
        }

        @Override
        ServiceResult<Boolean> isIncludeGrowthTableByCountTurnoverAndFinance(boolean byCountAndTurnover, boolean byFinance) {
            return super.isIncludeGrowthTableByCountTurnoverAndFinance(byCountAndTurnover, byFinance);
        }
    }
}
