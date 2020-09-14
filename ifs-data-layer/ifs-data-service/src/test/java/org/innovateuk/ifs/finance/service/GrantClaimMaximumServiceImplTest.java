package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.CommonBuilders;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumServiceImpl;
import org.innovateuk.ifs.form.resource.SectionType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.finance.domain.builder.GrantClaimMaximumBuilder.newGrantClaimMaximum;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GrantClaimMaximumServiceImplTest extends BaseServiceUnitTest<GrantClaimMaximumServiceImpl> {

    @Mock
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private GrantClaimMaximumMapper grantClaimMaximumMapper;

    @Mock
    private CommonBuilders commonBuilders;

    @Override
    protected GrantClaimMaximumServiceImpl supplyServiceUnderTest() {
        return new GrantClaimMaximumServiceImpl();
    }

    @Test
    public void getGrantClaimMaximumById() {
        GrantClaimMaximum gcm = newGrantClaimMaximum().build();
        GrantClaimMaximumResource gcmResource = newGrantClaimMaximumResource()
                .withMaximum(100).build();

        when(grantClaimMaximumRepository.findById(gcm.getId())).thenReturn(Optional.of(gcm));
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
    public void revertToDefault() {
        List<GrantClaimMaximum> defaultGrantClaimMaximums = newGrantClaimMaximum().build(3);
        List<GrantClaimMaximum> grantClaimMaximums = newGrantClaimMaximum().build(2);
        Competition competition = newCompetition()
                .withGrantClaimMaximums(grantClaimMaximums)
                .build();

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(commonBuilders.getDefaultGrantClaimMaximums()).thenReturn(defaultGrantClaimMaximums);
        when(grantClaimMaximumRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResult<Set<Long>> result = service.revertToDefault(competition.getId());
        assertTrue(result.isSuccess());

        assertTrue(result.getSuccess().containsAll(defaultGrantClaimMaximums.stream().map(GrantClaimMaximum::getId).collect(Collectors.toList())));
        verify(grantClaimMaximumRepository, times(defaultGrantClaimMaximums.size())).save(any());
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
        List<GrantClaimMaximum> grantClaimMaximums = newGrantClaimMaximum().build(2);

        Competition competition = newCompetition()
                .withGrantClaimMaximums(grantClaimMaximums)
                .build();

        when(commonBuilders.getDefaultGrantClaimMaximums()).thenReturn(grantClaimMaximums);
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Boolean> isMaximumFundingLevelOverridden = service.isMaximumFundingLevelOverridden(competition
                .getId());

        assertTrue(isMaximumFundingLevelOverridden.isSuccess());
        assertFalse(isMaximumFundingLevelOverridden.getSuccess());
    }

    @Test
    public void isMaximumFundingLevelOverridden_fundingLevelsOverridden() {
        List<GrantClaimMaximum> defaultGrantClaimMaximums = newGrantClaimMaximum().build(3);
        List<GrantClaimMaximum> competitionGrantClaimMaximums = newGrantClaimMaximum().build(2);

        Competition competition = newCompetition()
                .withGrantClaimMaximums(competitionGrantClaimMaximums)
                .withSections(newSection().withSectionType(SectionType.FINANCE).build(1))
                .build();

        when(commonBuilders.getDefaultGrantClaimMaximums()).thenReturn(defaultGrantClaimMaximums);
        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Boolean> isMaximumFundingLevelOverridden = service.isMaximumFundingLevelOverridden(competition
                .getId());

        assertTrue(isMaximumFundingLevelOverridden.isSuccess());
        assertTrue(isMaximumFundingLevelOverridden.getSuccess());
    }
}
