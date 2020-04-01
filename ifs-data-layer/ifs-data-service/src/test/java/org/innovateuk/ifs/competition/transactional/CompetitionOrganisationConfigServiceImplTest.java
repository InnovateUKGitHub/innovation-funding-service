package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionOrganisationConfigMapper;
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

    @Mock
    private CompetitionOrganisationConfigMapper mapper;

    @Override
    protected CompetitionOrganisationConfigServiceImpl supplyServiceUnderTest() {
        return new CompetitionOrganisationConfigServiceImpl();
    }

    @Test
    public void findOneByCompetitionId() {
        long competitionId = 100L;
        CompetitionOrganisationConfig config = new CompetitionOrganisationConfig(newCompetition().withId(competitionId).build(),
                false,
                false);
        CompetitionOrganisationConfigResource resource = new CompetitionOrganisationConfigResource();

        when(competitionOrganisationConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(config));
        when(mapper.mapToResource(config)).thenReturn(resource);

        ServiceResult<CompetitionOrganisationConfigResource> result = service.findOneByCompetitionId(competitionId);

        assertTrue(result.isSuccess());
    }
}