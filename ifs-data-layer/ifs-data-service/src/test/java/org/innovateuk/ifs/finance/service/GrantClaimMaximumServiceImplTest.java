package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumServiceImpl;
import org.innovateuk.ifs.form.resource.SectionType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.finance.domain.builder.GrantClaimMaximumBuilder.newGrantClaimMaximum;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class GrantClaimMaximumServiceImplTest extends BaseServiceUnitTest<GrantClaimMaximumServiceImpl> {

    @Mock
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private GrantClaimMaximumMapper grantClaimMaximumMapper;

    @Mock
    private ResearchCategoryRepository researchCategoryRepository;

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
    public void getGrantClaimMaximumByCompetitionId() {
        long competitionId = 1L;
        List<GrantClaimMaximum> gcm = newGrantClaimMaximum().build(1);
        List<GrantClaimMaximumResource> gcmResource = newGrantClaimMaximumResource().build(1);

        when(grantClaimMaximumRepository.findByCompetitionsId(competitionId)).thenReturn(gcm);
        when(grantClaimMaximumMapper.mapToResource(gcm.get(0))).thenReturn(gcmResource.get(0));

        ServiceResult<List<GrantClaimMaximumResource>> result = service.getGrantClaimMaximumByCompetitionId(competitionId);
        assertTrue(result.isSuccess());
        assertEquals(gcmResource, result.getSuccess());
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
        Competition competition = newCompetition()
                .withFundingRules(FundingRules.STATE_AID)
                .withResearchCategories(newResearchCategory().buildSet(1))
                .build();

        ResearchCategory feasability = newResearchCategory().build();
        ResearchCategory industrial = newResearchCategory().build();
        ResearchCategory experimental = newResearchCategory().build();

        when(researchCategoryRepository.findById(ResearchCategory.FEASIBILITY_STUDIES_ID)).thenReturn(Optional.of(feasability));
        when(researchCategoryRepository.findById(ResearchCategory.INDUSTRIAL_RESEARCH_ID)).thenReturn(Optional.of(industrial));
        when(researchCategoryRepository.findById(ResearchCategory.EXPERIMENTAL_DEVELOPMENT_ID)).thenReturn(Optional.of(experimental));

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(grantClaimMaximumRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResult<Set<Long>> result = service.revertToDefault(competition.getId());

        assertTrue(result.isSuccess());

        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(feasability.getId())
                && max.getOrganisationSize() == OrganisationSize.SMALL
                && max.getMaximum() == 70)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(feasability.getId())
                && max.getOrganisationSize() == OrganisationSize.MEDIUM
                && max.getMaximum() == 60)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(feasability.getId())
                && max.getOrganisationSize() == OrganisationSize.LARGE
                && max.getMaximum() == 50)));

        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(industrial.getId())
                && max.getOrganisationSize() == OrganisationSize.SMALL
                && max.getMaximum() == 70)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(industrial.getId())
                && max.getOrganisationSize() == OrganisationSize.MEDIUM
                && max.getMaximum() == 60)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(industrial.getId())
                && max.getOrganisationSize() == OrganisationSize.LARGE
                && max.getMaximum() == 50)));

        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(experimental.getId())
                && max.getOrganisationSize() == OrganisationSize.SMALL
                && max.getMaximum() == 45)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(experimental.getId())
                && max.getOrganisationSize() == OrganisationSize.MEDIUM
                && max.getMaximum() == 35)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(experimental.getId())
                && max.getOrganisationSize() == OrganisationSize.LARGE
                && max.getMaximum() == 25)));
    }

    @Test
    public void revertToDefault_nonaid() {
        Competition competition = newCompetition()
                .withFundingRules(FundingRules.NOT_AID)
                .build();

        ResearchCategory feasability = newResearchCategory().build();
        ResearchCategory industrial = newResearchCategory().build();
        ResearchCategory experimental = newResearchCategory().build();

        when(researchCategoryRepository.findById(ResearchCategory.FEASIBILITY_STUDIES_ID)).thenReturn(Optional.of(feasability));
        when(researchCategoryRepository.findById(ResearchCategory.INDUSTRIAL_RESEARCH_ID)).thenReturn(Optional.of(industrial));
        when(researchCategoryRepository.findById(ResearchCategory.EXPERIMENTAL_DEVELOPMENT_ID)).thenReturn(Optional.of(experimental));

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(grantClaimMaximumRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResult<Set<Long>> result = service.revertToDefault(competition.getId());

        assertTrue(result.isSuccess());


        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(feasability.getId())
                && max.getOrganisationSize() == OrganisationSize.SMALL
                && max.getMaximum() == null)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(feasability.getId())
                && max.getOrganisationSize() == OrganisationSize.MEDIUM
                && max.getMaximum() == null)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(feasability.getId())
                && max.getOrganisationSize() == OrganisationSize.LARGE
                && max.getMaximum() == null)));

        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(industrial.getId())
                && max.getOrganisationSize() == OrganisationSize.SMALL
                && max.getMaximum() == null)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(industrial.getId())
                && max.getOrganisationSize() == OrganisationSize.MEDIUM
                && max.getMaximum() == null)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(industrial.getId())
                && max.getOrganisationSize() == OrganisationSize.LARGE
                && max.getMaximum() == null)));

        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(experimental.getId())
                && max.getOrganisationSize() == OrganisationSize.SMALL
                && max.getMaximum() == null)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(experimental.getId())
                && max.getOrganisationSize() == OrganisationSize.MEDIUM
                && max.getMaximum() == null)));
        verify(grantClaimMaximumRepository).save(argThat(lambdaMatches(max -> max.getResearchCategory().getId().equals(experimental.getId())
                && max.getOrganisationSize() == OrganisationSize.LARGE
                && max.getMaximum() == null)));
    }

    @Test
    public void save() {
        GrantClaimMaximum gcm = newGrantClaimMaximum().build();
        GrantClaimMaximumResource gcmResource = newGrantClaimMaximumResource().build();
        when(grantClaimMaximumRepository.findById(gcm.getId())).thenReturn(Optional.of(gcm));

        when(grantClaimMaximumRepository.save(gcm)).thenReturn(gcm);
        when(grantClaimMaximumMapper.mapToResource(gcm)).thenReturn(gcmResource);

        ServiceResult<GrantClaimMaximumResource> result = service.save(gcmResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void isMaximumFundingLevelOverridden() {
        List<GrantClaimMaximum> grantClaimMaximums = newGrantClaimMaximum().build(2);

        Competition competition = newCompetition()
                .withGrantClaimMaximums(grantClaimMaximums)
                .build();

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Boolean> isMaximumFundingLevelOverridden = service.isMaximumFundingLevelConstant(competition
                .getId());

        assertTrue(isMaximumFundingLevelOverridden.isSuccess());
        assertFalse(isMaximumFundingLevelOverridden.getSuccess());
    }

    @Test
    public void isMaximumFundingLevelConstant() {
        List<GrantClaimMaximum> competitionGrantClaimMaximums = newGrantClaimMaximum().withMaximum(1, 2).build(2);

        Competition competition = newCompetition()
                .withGrantClaimMaximums(competitionGrantClaimMaximums)
                .withSections(newSection().withSectionType(SectionType.FINANCE).build(1))
                .build();

        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));

        ServiceResult<Boolean> isMaximumFundingLevelConstant = service.isMaximumFundingLevelConstant(competition
                .getId());

        assertTrue(isMaximumFundingLevelConstant.isSuccess());
        assertFalse(isMaximumFundingLevelConstant.getSuccess());
    }
}
