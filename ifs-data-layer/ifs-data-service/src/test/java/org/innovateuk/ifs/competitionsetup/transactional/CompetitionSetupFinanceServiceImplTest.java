package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.competitionsetup.CompetitionSetupTransactionalService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Tests the CompetitionSetupFinanceServiceImpl with mocked repository.
 */
public class CompetitionSetupFinanceServiceImplTest extends BaseServiceUnitTest<CompetitionSetupFinanceServiceImpl> {

    @Mock
    private CompetitionSetupTransactionalService competitionSetupTransactionalServiceMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Override
    protected CompetitionSetupFinanceServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupFinanceServiceImpl();
    }

    @Test
    public void save() {
        long competitionId = 1L;
        boolean isIncludeGrowthTable = false;
        Boolean includeJesForm = true;
        CompetitionSetupFinanceResource compSetupFinanceRes = newCompetitionSetupFinanceResource()
                .withCompetitionId(competitionId)
                .withIncludeGrowthTable(isIncludeGrowthTable)
                .withIncludeJesForm(includeJesForm)
                .withApplicationFinanceType(STANDARD)
                .build();

        // Make sure that the values in the competition and the form inputs are the negation of what we are changing
        // them to so that we can check they've been altered. Note that isIncludeGrowthTable being true should result in
        // deactivated turn over and count form inputs and activated financial inputs.
        Competition c = newCompetition().with(id(competitionId))
                .withApplicationFinanceType(NO_FINANCES)
                .build();
        when(competitionRepositoryMock.findOne(competitionId)).thenReturn(c);
        // Turnover and count - these should be active in sync with each other.
        FormInput staffCountFormInput = newFormInput().withType(STAFF_COUNT).withActive(isIncludeGrowthTable).build();
        FormInput organisationTurnoverFormInput = newFormInput().withType(ORGANISATION_TURNOVER).withActive(isIncludeGrowthTable).build();
        // Financial inputs - these should be active in sync with each other and opposite to turnover and count.
        FormInput financialYearEnd = newFormInput().withType(FINANCIAL_YEAR_END).withActive(!isIncludeGrowthTable).build();
        List<FormInput> financialOverviewRows = newFormInput().withType(FINANCIAL_OVERVIEW_ROW).withActive(!isIncludeGrowthTable).build(4);
        FormInput financialCount = newFormInput().withType(FormInputType.FINANCIAL_STAFF_COUNT).withActive(!isIncludeGrowthTable).build();

        when(competitionSetupTransactionalServiceMock.countInput(competitionId)).thenReturn(serviceSuccess(staffCountFormInput));
        when(competitionSetupTransactionalServiceMock.turnoverInput(competitionId)).thenReturn(serviceSuccess(organisationTurnoverFormInput));
        when(competitionSetupTransactionalServiceMock.financeYearEnd(competitionId)).thenReturn(serviceSuccess(financialYearEnd));
        when(competitionSetupTransactionalServiceMock.financeCount(competitionId)).thenReturn(serviceSuccess(financialCount));
        when(competitionSetupTransactionalServiceMock.financeOverviewRow(competitionId)).thenReturn(serviceSuccess(financialOverviewRows));

        // Method under test
        ServiceResult<Void> save = service.save(compSetupFinanceRes);

        // Assertions
        assertTrue(save.isSuccess());
        assertEquals(STANDARD, c.getApplicationFinanceType());
        assertEquals(isIncludeGrowthTable, !staffCountFormInput.getActive());
        assertEquals(isIncludeGrowthTable, !organisationTurnoverFormInput.getActive());
        assertEquals(isIncludeGrowthTable, financialYearEnd.getActive());
        assertTrue(!simpleMap(financialOverviewRows, FormInput::getActive).contains(!isIncludeGrowthTable));
        assertEquals(isIncludeGrowthTable, financialYearEnd.getActive());
        assertEquals(c.getIncludeJesForm(), includeJesForm);
    }

    @Test
    public void getForCompetition_standardFinanceType() {
        Competition competition = newCompetition()
                .withApplicationFinanceType(STANDARD)
                .withIncludeProjectGrowthTable(true)
                .build();
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        CompetitionSetupFinanceResource result = service.getForCompetition(competition.getId()).getSuccess();

        assertEquals(STANDARD, result.getApplicationFinanceType());
        assertTrue(result.getIncludeGrowthTable());
    }

    @Test
    public void getForCompetition_noFinanceType() {
        Competition competition = newCompetition()
                .withApplicationFinanceType(NO_FINANCES)
                .withIncludeProjectGrowthTable(true)
                .build();
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        CompetitionSetupFinanceResource result = service.getForCompetition(competition.getId()).getSuccess();

        assertEquals(NO_FINANCES, result.getApplicationFinanceType());
        assertTrue(result.getIncludeGrowthTable());
    }

    @Test
    public void getForCompetition_nullFinanceType() {
        Competition competition = newCompetition()
                .withIncludeProjectGrowthTable(true)
                .build();
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        CompetitionSetupFinanceResource result = service.getForCompetition(competition.getId()).getSuccess();

        assertNull(result.getApplicationFinanceType());
        assertTrue(result.getIncludeGrowthTable());
    }

    @Test
    public void getForCompetition_nullIncludeProjectGrowthTable() {
        Competition competition = newCompetition().build();
        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        CompetitionSetupFinanceResource result = service.getForCompetition(competition.getId()).getSuccess();

        assertNull(result.getApplicationFinanceType());
        assertNull(result.getIncludeGrowthTable());
    }
}
