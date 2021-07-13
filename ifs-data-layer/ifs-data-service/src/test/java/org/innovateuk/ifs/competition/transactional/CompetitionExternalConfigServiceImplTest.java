package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionExternalConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionExternalConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionExternalConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionExternalConfigResourceBuilder.newCompetitionExternalConfigResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionExternalConfigServiceImplTest extends BaseServiceUnitTest<CompetitionExternalConfigServiceImpl> {

    @Mock
    private CompetitionExternalConfigRepository competitionExternalConfigRepository;

    @Mock
    private CompetitionExternalConfigMapper mapper;

    @Override
    protected CompetitionExternalConfigServiceImpl supplyServiceUnderTest() {
        return new CompetitionExternalConfigServiceImpl();
    }

    private long competitionId;
    private CompetitionExternalConfig config;

    @Before
    public void setup() {
        competitionId = 100L;
        config = new CompetitionExternalConfig(newCompetition()
                .withId(competitionId)
                .build(), null);
    }

    @Test
    public void findOneByCompetitionId() {
        CompetitionExternalConfigResource resource = new CompetitionExternalConfigResource();

        when(competitionExternalConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(config));
        when(mapper.mapToResource(config)).thenReturn(resource);

        ServiceResult<CompetitionExternalConfigResource> result = service.findOneByCompetitionId(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(config.getId(), result.getSuccess().getId());
    }

    @Test
    public void updateExternalCompetitionId() {
        String externalCompId= "Test external client competition 123";
        CompetitionExternalConfigResource resource =
                newCompetitionExternalConfigResource().
                        withExternalCompetitionId(externalCompId).build();

        when(competitionExternalConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(config));
        ServiceResult<Void> result = service.update(competitionId, resource);

        assertTrue(result.isSuccess());
        assertEquals(externalCompId, config.getExternalCompetitionId());

    }
}