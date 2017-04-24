package org.innovateuk.ifs.commons.competitionsetup;


import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionSetupTransactionalServiceTest extends BaseServiceUnitTest<CompetitionSetupTransactionalService> {
    private Long competitionId = 123L;
    private FormInput staffCountFormInput;
    private FormInput staffTurnoverFormInput;
    private FormInput yearEnd;
    private List<FormInput> overviewRows;
    private FormInput count;

    @Override
    protected CompetitionSetupTransactionalService supplyServiceUnderTest() {
        return new CompetitionSetupTransactionalServiceImpl();
    }

    @Before
    public void setUp() throws Exception {
        staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(true).build();
        staffTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(true).build();
        yearEnd = newFormInput().withType(FINANCIAL_YEAR_END).withActive(true).build();
        overviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(true, true, true, true).build(4);
        count = newFormInput().withType(FormInputType.FINANCIAL_STAFF_COUNT).withActive(true).build();
    }

    @Test
    public void test_GetForCompetitionErrorCountTurnover() {
        // Should never happen but check that reasonable error codes get returned in the event that the database
        // becomes inconsistent

        // Turnover and count - these should always be in sync - but here we test when they are not.
        staffTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(false).build();

        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_COUNT))).thenReturn(asList(staffCountFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(ORGANISATION_TURNOVER))).thenReturn(asList(staffTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_STAFF_COUNT))).thenReturn(asList(count));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_YEAR_END))).thenReturn(asList(yearEnd));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_OVERVIEW_ROW))).thenReturn(overviewRows);

        // Method under test
        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTable(competitionId);

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
        overviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(true, true, true, false /*Inconsistent*/).build(4);

        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_COUNT))).thenReturn(asList(staffCountFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(ORGANISATION_TURNOVER))).thenReturn(asList(staffTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_STAFF_COUNT))).thenReturn(asList(count));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_YEAR_END))).thenReturn(asList(yearEnd));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_OVERVIEW_ROW))).thenReturn(overviewRows);

        // Method under test
        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTable(competitionId);

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
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_COUNT))).thenReturn(asList(staffCountFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(ORGANISATION_TURNOVER))).thenReturn(asList(staffTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_STAFF_COUNT))).thenReturn(asList(count));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_YEAR_END))).thenReturn(asList(yearEnd));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competitionId, asList(FINANCIAL_OVERVIEW_ROW))).thenReturn(overviewRows);

        // Method under test
        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTable(competitionId);

        // Assertions
        assertTrue(shouldBeFailure.isFailure());
        assertEquals(1, shouldBeFailure.getErrors().size());
        assertEquals("include.growth.table.count.turnover.finance.input.active.not.consistent", shouldBeFailure.getErrors().get(0).getErrorKey());
    }

}
