package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.repository.CompetitionOrganisationConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionOrganisationConfigServiceImplTest extends BaseServiceUnitTest<CompetitionOrganisationConfigServiceImpl> {

    @Mock
    private CompetitionOrganisationConfigRepository competitionOrganisationConfigRepository;


    @Override
    protected CompetitionOrganisationConfigServiceImpl supplyServiceUnderTest() {
        return new CompetitionOrganisationConfigServiceImpl();
    }

    @Test
    public void findOneByCompetitionId() {
        long competitionId = 100L;
        CompetitionOrganisationConfig config = new CompetitionOrganisationConfig(newCompetition().build(),false, false);

        when(competitionOrganisationConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(config));

        ServiceResult<Optional<CompetitionOrganisationConfigResource>> result = service.findOneByCompetitionId(competitionId);

        assertTrue(result.isSuccess());
    }
}