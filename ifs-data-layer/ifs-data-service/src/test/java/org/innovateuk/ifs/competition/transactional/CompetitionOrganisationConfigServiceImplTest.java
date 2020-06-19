package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionOrganisationConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionOrganisationConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.junit.Assert.*;
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

    private long competitionId;
    private CompetitionOrganisationConfig config;

    @Before
    public void setup() {
        competitionId = 100L;
        config = new CompetitionOrganisationConfig(newCompetition()
                .withId(competitionId)
                .build(), null, null);

    }

    @Test
    public void findOneByCompetitionId() {
        CompetitionOrganisationConfigResource resource = new CompetitionOrganisationConfigResource();

        when(competitionOrganisationConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(config));
        when(mapper.mapToResource(config)).thenReturn(resource);

        ServiceResult<CompetitionOrganisationConfigResource> result = service.findOneByCompetitionId(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(config.getId(), result.getSuccess().getId());
    }

    @Test
    public void updateTrueInternationalOrganisationAllowed() {
        CompetitionOrganisationConfigResource resource = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(true)
                .withInternationalLeadOrganisationAllowed(false)
                .build();

        when(competitionOrganisationConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(config));
        ServiceResult<Void> result = service.update(competitionId, resource);

        assertTrue(result.isSuccess());
        assertTrue(config.getInternationalOrganisationsAllowed());
        assertFalse(config.getInternationalLeadOrganisationAllowed());
    }

    @Test
    public void updateFalseInternationalOrganisationAllowed() {
        CompetitionOrganisationConfigResource resource = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(false)
                .withInternationalLeadOrganisationAllowed()
                .build();

        when(competitionOrganisationConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(config));
        ServiceResult<Void> result = service.update(competitionId, resource);

        assertTrue(result.isSuccess());
        assertFalse(config.getInternationalOrganisationsAllowed());
        assertNull(config.getInternationalLeadOrganisationAllowed());
    }
}