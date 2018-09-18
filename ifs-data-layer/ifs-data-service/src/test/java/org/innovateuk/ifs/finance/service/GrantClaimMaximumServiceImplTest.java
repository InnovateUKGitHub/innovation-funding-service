package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumServiceImpl;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.finance.domain.builder.GrantClaimMaximumBuilder.newGrantClaimMaximum;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class GrantClaimMaximumServiceImplTest extends BaseServiceUnitTest<GrantClaimMaximumServiceImpl> {

    @Mock
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    @Mock
    private CompetitionTypeRepository competitionTypeRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private GrantClaimMaximumMapper grantClaimMaximumMapper;

    @Override
    protected GrantClaimMaximumServiceImpl supplyServiceUnderTest() {
        return new GrantClaimMaximumServiceImpl(grantClaimMaximumRepository, competitionTypeRepository,
                competitionRepository, grantClaimMaximumMapper);
    }

    @Test
    public void getGrantClaimMaximumById() {
        GrantClaimMaximum gcm = newGrantClaimMaximum().build();
        GrantClaimMaximumResource gcmResource = newGrantClaimMaximumResource()
                .withMaximum(100).build();

        when(grantClaimMaximumRepository.findOne(gcm.getId())).thenReturn(gcm);
        when(grantClaimMaximumMapper.mapToResource(gcm)).thenReturn(gcmResource);

        ServiceResult<GrantClaimMaximumResource> result = service.getGrantClaimMaximumById(gcm.getId());
        assertTrue(result.isSuccess());
        assertEquals(gcmResource, result.getSuccess());
        assertEquals(Integer.valueOf(100), result.getSuccess().getMaximum());
        assertEquals(gcmResource.getId(), result.getSuccess().getId());
    }

    @Test
    public void getNotFoundGrantClaimMaximum() {
        GrantClaimMaximum gcm = newGrantClaimMaximum().build();
        ServiceResult<GrantClaimMaximumResource> result = service.getGrantClaimMaximumById(gcm.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getErrors().contains(notFoundError(GrantClaimMaximum.class, gcm.getId())));
    }

    @Test
    public void getGrantClaimMaximumsForCompetitionType() {
        List<GrantClaimMaximum> grantClaimMaximums = newGrantClaimMaximum().build(2);
        Competition competition = newCompetition()
                .withGrantClaimMaximums(grantClaimMaximums)
                .build();
        CompetitionType competitionType = newCompetitionType()
                .withTemplate(competition)
                .build();

        when(competitionTypeRepository.findOne(competitionType.getId())).thenReturn(competitionType);

        ServiceResult<Set<Long>> result = service.getGrantClaimMaximumsForCompetitionType(competitionType.getId());
        assertTrue(result.isSuccess());
        assertEquals(asLinkedSet(grantClaimMaximums.get(0).getId(), grantClaimMaximums.get(1).getId()), result
                .getSuccess());
    }

    @Test
    public void save() {
        GrantClaimMaximum gcm = newGrantClaimMaximum().build();
        GrantClaimMaximumResource gcmResource = newGrantClaimMaximumResource().build();

        when(grantClaimMaximumMapper.mapToDomain(gcmResource)).thenReturn(gcm);
        when(grantClaimMaximumMapper.mapToResource(gcm)).thenReturn(gcmResource);

        when(grantClaimMaximumRepository.save(gcm)).thenReturn(gcm);

        ServiceResult<GrantClaimMaximumResource> result = service.save(gcmResource);
        assertTrue(result.isSuccess());
        assertEquals(gcmResource, result.getSuccess());
    }

    @Test
    public void isMaximumFundingLevelOverridden() {
        List<GrantClaimMaximum> templateGrantClaimMaximums = newGrantClaimMaximum().build(2);

        Competition templateCompetition = newCompetition()
                .withGrantClaimMaximums(templateGrantClaimMaximums)
                .build();

        CompetitionType competitionType = newCompetitionType()
                .withTemplate(templateCompetition)
                .build();

        Competition competition = newCompetition()
                .withGrantClaimMaximums(templateGrantClaimMaximums)
                .withCompetitionType(competitionType)
                .build();

        when(competitionRepository.findOne(competition.getId())).thenReturn(competition);

        ServiceResult<Boolean> isMaximumFundingLevelOverridden = service.isMaximumFundingLevelOverridden(competition
                .getId());

        assertTrue(isMaximumFundingLevelOverridden.isSuccess());
        assertFalse(isMaximumFundingLevelOverridden.getSuccess());
    }

    @Test
    public void isMaximumFundingLevelOverridden_fundingLevelsOverridden() {
        List<GrantClaimMaximum> templateGrantClaimMaximums = newGrantClaimMaximum().build(2);
        List<GrantClaimMaximum> competitionGrantClaimMaximums = newGrantClaimMaximum().build(2);

        Competition templateCompetition = newCompetition()
                .withGrantClaimMaximums(templateGrantClaimMaximums)
                .build();

        CompetitionType competitionType = newCompetitionType()
                .withTemplate(templateCompetition)
                .build();

        Competition competition = newCompetition()
                .withGrantClaimMaximums(competitionGrantClaimMaximums)
                .withCompetitionType(competitionType)
                .build();

        when(competitionRepository.findOne(competition.getId())).thenReturn(competition);

        ServiceResult<Boolean> isMaximumFundingLevelOverridden = service.isMaximumFundingLevelOverridden(competition
                .getId());

        assertTrue(isMaximumFundingLevelOverridden.isSuccess());
        assertTrue(isMaximumFundingLevelOverridden.getSuccess());
    }
}
