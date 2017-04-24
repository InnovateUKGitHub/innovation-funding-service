package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.competitionsetup.CompetitionSetupTransactionalService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the CompetitionSetupFinanceServiceImpl with mocked repository.
 */
public class CompetitionSetupFinanceServiceImplTest extends BaseServiceUnitTest<CompetitionSetupFinanceServiceImpl> {

    @Override
    protected CompetitionSetupFinanceServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupFinanceServiceImpl();
    }

    @Test
    public void test_save() {
        long competitionId = 1L;
        boolean isFullFinance = false;
        boolean isIncludeGrowthTable = false;
        CompetitionSetupFinanceResource compSetupFinanceRes = newCompetitionSetupFinanceResource()
                .withCompetitionId(competitionId)
                .withIncludeGrowthTable(isFullFinance)
                .withFullApplicationFinance(isIncludeGrowthTable)
                .build();

        // Make sure that the booleans in the competition and the form inputs are the negation of what we are changing
        // them to so that we can check they've been altered. Note that isIncludeGrowthTable being true should result in
        // deactivated turn over and count form inputs and activated financial inputs.
        Competition c = newCompetition().with(id(competitionId)).withFullFinance(!isFullFinance).build();
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(c);
        // Turnover and count - these should be active in sync with each other.
        FormInput staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(isIncludeGrowthTable).build();
        FormInput staffTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(isIncludeGrowthTable).build();
        // Financial inputs - these should be active in sync with each other and opposite to turnover and count.
        FormInput financialYearEnd = newFormInput().withType(FINANCIAL_YEAR_END).withActive(!isIncludeGrowthTable).build();
        List<FormInput> financialOverviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(!isIncludeGrowthTable).build(4);
        FormInput financialCount = newFormInput().withType(FormInputType.FINANCIAL_STAFF_COUNT).withActive(!isIncludeGrowthTable).build();

        when(competitionSetupTransactionalServiceMock.countInput(competitionId)).thenReturn(serviceSuccess(staffCountFormInput));
        when(competitionSetupTransactionalServiceMock.turnoverInput(competitionId)).thenReturn(serviceSuccess(staffTurnoverFormInput));
        when(competitionSetupTransactionalServiceMock.financeYearEnd(competitionId)).thenReturn(serviceSuccess(financialYearEnd));
        when(competitionSetupTransactionalServiceMock.financeCount(competitionId)).thenReturn(serviceSuccess(financialCount));
        when(competitionSetupTransactionalServiceMock.financeOverviewRow(competitionId)).thenReturn(serviceSuccess(financialOverviewRows));

        // Method under test
        ServiceResult<Void> save = service.save(compSetupFinanceRes);

        // Assertions
        assertTrue(save.isSuccess());
        assertEquals(isFullFinance, c.isFullApplicationFinance());
        assertEquals(isIncludeGrowthTable, !staffCountFormInput.getActive());
        assertEquals(isIncludeGrowthTable, !staffTurnoverFormInput.getActive());
        assertEquals(isIncludeGrowthTable, financialYearEnd.getActive());
        assertTrue(!simpleMap(financialOverviewRows, FormInput::getActive).contains(!isIncludeGrowthTable));
        assertEquals(isIncludeGrowthTable, financialYearEnd.getActive());
    }

    @Test
    public void test_GetForCompetition() {
        boolean isIncludeGrowthTable = true;
        boolean isFullFinance = true;
        Long competitionId = 1L;

        // Set up a competition with consistent active form inputs
        Competition c = newCompetition().with(id(competitionId)).withFullFinance(isFullFinance).build();
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(c);

        FormInputResponse headcount = newFormInputResponse().withValue("1").build();
        FormInputResponse turnover = newFormInputResponse().withValue("2").build();
        // Turnover and count - these should be active in sync with each other.
        FormInput staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(!isIncludeGrowthTable).build();
        FormInput staffTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(!isIncludeGrowthTable).build();
        // Financial inputs - these should be active in sync with each other and opposite to turnover and count.
        FormInput financialYearEnd = newFormInput().withType(FINANCIAL_YEAR_END).withActive(isIncludeGrowthTable).withResponses(asList(turnover)).build();
        List<FormInput> financialOverviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(isIncludeGrowthTable).build(4);
        FormInput financialCount = newFormInput().withType(FormInputType.FINANCIAL_STAFF_COUNT).withActive(isIncludeGrowthTable).withResponses(asList(headcount)).build();

        when(competitionSetupTransactionalServiceMock.isIncludeGrowthTable(competitionId)).thenReturn(serviceSuccess(isIncludeGrowthTable));

        // Method under test
        ServiceResult<CompetitionSetupFinanceResource> compSetupFinanceRes = service.getForCompetition(competitionId);

        // Assertions
        assertTrue(compSetupFinanceRes.isSuccess());
        assertEquals(isFullFinance, compSetupFinanceRes.getSuccessObject().isFullApplicationFinance());
        assertEquals(isIncludeGrowthTable, compSetupFinanceRes.getSuccessObject().isIncludeGrowthTable());
    }

}
