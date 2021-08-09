package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionThirdPartyConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionThirdPartyConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionThirdPartyConfigRepository;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionThirdPartyConfigServiceImplTest extends BaseServiceUnitTest<CompetitionThirdPartyConfigServiceImpl> {

    @Mock
    private CompetitionThirdPartyConfigRepository competitionThirdPartyConfigRepository;

    @Mock
    private CompetitionThirdPartyConfigMapper mapper;

    @Override
    protected CompetitionThirdPartyConfigServiceImpl supplyServiceUnderTest() {
        return new CompetitionThirdPartyConfigServiceImpl();
    }

    @Test
    public void findOneByCompetitionId() {
        long competitionId = 1;

        CompetitionThirdPartyConfig competitionThirdPartyConfig = new CompetitionThirdPartyConfig();
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = new CompetitionThirdPartyConfigResource();
        when(competitionThirdPartyConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(competitionThirdPartyConfig));
        when(mapper.mapToResource(competitionThirdPartyConfig)).thenReturn(competitionThirdPartyConfigResource);

        CompetitionThirdPartyConfigResource response = service.findOneByCompetitionId(competitionId).getSuccess();

        assertEquals(competitionThirdPartyConfigResource, response);
    }

    @Test
    public void findOneByCompetitionIdNotFound() {
        long competitionId = 1;

        when(competitionThirdPartyConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.empty());
        ServiceResult<CompetitionThirdPartyConfigResource> response = service.findOneByCompetitionId(competitionId);

        assertTrue(response.isSuccess());
        Optional<CompetitionThirdPartyConfigResource> competitionThirdPartyConfigResource = response.getOptionalSuccessObject();

        assertEquals(null, competitionThirdPartyConfigResource.get().getId());
        assertEquals("", competitionThirdPartyConfigResource.get().getTermsAndConditionsLabel());
        assertEquals("", competitionThirdPartyConfigResource.get().getTermsAndConditionsGuidance());
        assertEquals("", competitionThirdPartyConfigResource.get().getProjectCostGuidanceUrl());

    }

    @Test
    public void update() {
        long competitionId = 1;

        CompetitionThirdPartyConfig competitionThirdPartyConfig = new CompetitionThirdPartyConfig();
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = new CompetitionThirdPartyConfigResource();
        when(competitionThirdPartyConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(competitionThirdPartyConfig));

        ServiceResult<Void> response = service.update(competitionId, competitionThirdPartyConfigResource);

        assertTrue(response.isSuccess());
    }

    @Test
    public void updateNotFound() {
        long competitionId = 1;

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = new CompetitionThirdPartyConfigResource();
        when(competitionThirdPartyConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.empty());

        ServiceResult<Void> response = service.update(competitionId, competitionThirdPartyConfigResource);

        assertTrue(response.isFailure());

        List<Error> errors = response.getErrors();

        assertEquals(1, errors.size());
        assertEquals(HttpStatus.NOT_FOUND, errors.get(0).getStatusCode());
    }
}
