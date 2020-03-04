package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupFinanceResourceBuilder.newCompetitionSetupFinanceResource;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.NO_FINANCES;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Tests the CompetitionSetupFinanceServiceImpl with mocked repository.
 */
public class CompetitionSetupFinanceServiceImplTest extends BaseServiceUnitTest<CompetitionSetupFinanceServiceImpl> {

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
        boolean isIncludeYourOrganisationSection = false;
        boolean includeJesForm = true;
        CompetitionSetupFinanceResource compSetupFinanceRes = newCompetitionSetupFinanceResource()
                .withCompetitionId(competitionId)
                .withIncludeGrowthTable(isIncludeGrowthTable)
                .withIncludeJesForm(includeJesForm)
                .withIncludeYourOrganisationSection(isIncludeYourOrganisationSection)
                .withApplicationFinanceType(STANDARD)
                .build();

        // Make sure that the values in the competition and the form inputs are the negation of what we are changing
        // them to so that we can check they've been altered. Note that isIncludeGrowthTable being true should result in
        // deactivated turn over and count form inputs and activated financial inputs.
        Competition c = newCompetition().with(id(competitionId))
                .withApplicationFinanceType(NO_FINANCES)
                .build();
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(c));

        // Method under test
        ServiceResult<Void> save = service.save(compSetupFinanceRes);

        // Assertions
        assertTrue(save.isSuccess());
        assertEquals(STANDARD, c.getApplicationFinanceType());
        assertFalse(c.getIncludeProjectGrowthTable());
        assertFalse(c.getIncludeYourOrganisationSection());
        assertEquals(c.getIncludeJesForm(), includeJesForm);
    }

    @Test
    public void getForCompetition_standardFinanceType() {
        Competition competition = newCompetition()
                .withApplicationFinanceType(STANDARD)
                .withIncludeProjectGrowthTable(true)
                .withIncludeYourOrganisationSection(true)
                .build();
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));

        CompetitionSetupFinanceResource result = service.getForCompetition(competition.getId()).getSuccess();

        assertEquals(STANDARD, result.getApplicationFinanceType());
        assertTrue(result.getIncludeGrowthTable());
        assertTrue(result.getIncludeYourOrganisationSection());
    }

    @Test
    public void getForCompetition_noFinanceType() {
        Competition competition = newCompetition()
                .withApplicationFinanceType(NO_FINANCES)
                .withIncludeProjectGrowthTable(false)
                .withIncludeYourOrganisationSection(false)
                .build();
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));

        CompetitionSetupFinanceResource result = service.getForCompetition(competition.getId()).getSuccess();

        assertEquals(NO_FINANCES, result.getApplicationFinanceType());
        assertFalse(result.getIncludeGrowthTable());
        assertFalse(result.getIncludeYourOrganisationSection());
    }

    @Test
    public void getForCompetition_nullFields() {
        Competition competition = newCompetition().build();
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));

        CompetitionSetupFinanceResource result = service.getForCompetition(competition.getId()).getSuccess();

        assertNull(result.getApplicationFinanceType());
        assertNull(result.getIncludeGrowthTable());
        assertNull(result.getIncludeYourOrganisationSection());
    }
}
