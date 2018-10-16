package org.innovateuk.ifs.commons.competitionsetup;


import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionSetupTransactionalServiceImplTest extends BaseServiceUnitTest<CompetitionSetupTransactionalService> {
    private Competition competition;
    private FormInput staffCountFormInput;
    private FormInput organisationTurnoverFormInput;
    private FormInput yearEnd;
    private List<FormInput> overviewRows;
    private FormInput count;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private FormInputRepository formInputRepositoryMock;

    @Override
    protected CompetitionSetupTransactionalService supplyServiceUnderTest() {
        return new CompetitionSetupTransactionalServiceImpl();
    }

    @Before
    public void setUp() throws Exception {
        competition = newCompetition().withIncludeProjectGrowthTable(true).build();
        staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(true).build();
        organisationTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(true).build();
        yearEnd = newFormInput().withType(FINANCIAL_YEAR_END).withActive(true).build();
        overviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(true, true, true, true).build(4);
        count = newFormInput().withType(FormInputType.FINANCIAL_STAFF_COUNT).withActive(true).build();
    }

    @Test
    public void isIncludeGrowthTable_consistent() {
        // Staff Count and Turnover inputs should be consistent and have inverse state to Finance inputs.
        // Finance inputs should be consistent and match the Competition includeProjectGrowthTable state.
        staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(!yearEnd.getActive()).build();
        organisationTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(!yearEnd.getActive()).build();

        when(competitionRepositoryMock.findById(competition.getId()).get()).thenReturn(competition);
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(STAFF_COUNT))).thenReturn(singletonList(staffCountFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(ORGANISATION_TURNOVER))).thenReturn(singletonList(organisationTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_STAFF_COUNT))).thenReturn(singletonList(count));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_YEAR_END))).thenReturn(singletonList(yearEnd));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_OVERVIEW_ROW))).thenReturn(overviewRows);

        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTable(competition.getId());

        assertTrue(shouldBeFailure.isSuccess());
    }

    @Test
    public void isIncludeGrowthTable_inconsistentStaffCountAndTurnoverInputs() {
        // Should never happen but check that reasonable error codes get returned in the event that the database
        // becomes inconsistent

        // Turnover and count - these should always be in sync - but here we test when they are not.
        organisationTurnoverFormInput =
                newFormInput().withType(ORGANISATION_TURNOVER).withActive(!staffCountFormInput.getActive()).build();

        when(competitionRepositoryMock.findById(competition.getId()).get()).thenReturn(competition);
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(STAFF_COUNT))).thenReturn(singletonList(staffCountFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(ORGANISATION_TURNOVER))).thenReturn(singletonList(organisationTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_STAFF_COUNT))).thenReturn(singletonList(count));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_YEAR_END))).thenReturn(singletonList(yearEnd));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_OVERVIEW_ROW))).thenReturn(overviewRows);

        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTable(competition.getId());

        assertTrue(shouldBeFailure.isFailure());
        assertEquals(1, shouldBeFailure.getErrors().size());
        assertEquals("include.growth.table.count.turnover.input.active.not.consistent", shouldBeFailure.getErrors().get(0).getErrorKey());
    }


    @Test
    public void isIncludeGrowthTable_inconsistentFinanceInputs() {
        // Should never happen but check that reasonable error codes get returned in the event that the database
        // becomes inconsistent

        // Financial inputs - these should always be in sync - but here we test when they are not.
        overviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(
                yearEnd.getActive(),
                yearEnd.getActive(),
                yearEnd.getActive(),
                !yearEnd.getActive() /*Inconsistent*/).build(4);

        when(competitionRepositoryMock.findById(competition.getId()).get()).thenReturn(competition);
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(STAFF_COUNT))).thenReturn(singletonList(staffCountFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(ORGANISATION_TURNOVER))).thenReturn(singletonList(organisationTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_STAFF_COUNT))).thenReturn(singletonList(count));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_YEAR_END))).thenReturn(singletonList(yearEnd));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_OVERVIEW_ROW))).thenReturn(overviewRows);

        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTable(competition.getId());

        assertTrue(shouldBeFailure.isFailure());
        assertEquals(1, shouldBeFailure.getErrors().size());
        assertEquals("include.growth.table.finance.input.active.not.consistent", shouldBeFailure.getErrors().get(0).getErrorKey());
    }

    @Test
    public void isIncludeGrowthTable_inconsistentStaffCountTurnoverAndFinanceInputs() {
        // Should never happen but check that reasonable error codes get returned in the event that the database
        // becomes inconsistent

        // Not consistent - Staff Count and Turnover inputs do not have inverse state to Finance inputs.
        when(competitionRepositoryMock.findById(competition.getId()).get()).thenReturn(competition);
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(STAFF_COUNT))).thenReturn(singletonList(staffCountFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(ORGANISATION_TURNOVER))).thenReturn(singletonList(organisationTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_STAFF_COUNT))).thenReturn(singletonList(count));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_YEAR_END))).thenReturn(singletonList(yearEnd));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_OVERVIEW_ROW))).thenReturn(overviewRows);

        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTable(competition.getId());

        assertTrue(shouldBeFailure.isFailure());
        assertEquals(1, shouldBeFailure.getErrors().size());
        assertEquals("include.growth.table.competition.count.turnover.finance.input.active.not.consistent",
                shouldBeFailure.getErrors().get(0).getErrorKey());
    }

    @Test
    public void isIncludeGrowthTable_inconsistentCompetition() {
        // Should never happen but check that reasonable error codes get returned in the event that the database
        // becomes inconsistent

        // Not consistent -  Finance inputs do not match the Competition includeProjectGrowthTable state.
        competition = newCompetition().withIncludeProjectGrowthTable(!yearEnd.getActive()).build();
        staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(!yearEnd.getActive()).build();
        organisationTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(!yearEnd.getActive()).build();


        when(competitionRepositoryMock.findById(competition.getId()).get()).thenReturn(competition);
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(STAFF_COUNT))).thenReturn(singletonList(staffCountFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(ORGANISATION_TURNOVER))).thenReturn(singletonList(organisationTurnoverFormInput));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_STAFF_COUNT))).thenReturn(singletonList(count));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_YEAR_END))).thenReturn(singletonList(yearEnd));
        when(formInputRepositoryMock.findByCompetitionIdAndTypeIn(competition.getId(), singletonList(FINANCIAL_OVERVIEW_ROW))).thenReturn(overviewRows);

        ServiceResult<Boolean> shouldBeFailure = service.isIncludeGrowthTable(competition.getId());

        assertTrue(shouldBeFailure.isFailure());
        assertEquals(1, shouldBeFailure.getErrors().size());
        assertEquals("include.growth.table.competition.count.turnover.finance.input.active.not.consistent",
                shouldBeFailure.getErrors().get(0).getErrorKey());
    }
}
