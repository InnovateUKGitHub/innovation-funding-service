package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.finance.domain.GrantClaimMaximum;
import org.innovateuk.ifs.finance.mapper.GrantClaimMaximumMapper;
import org.innovateuk.ifs.finance.repository.GrantClaimMaximumRepository;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeResourceBuilder.newCompetitionTypeResource;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.finance.domain.builder.GrantClaimMaximumBuilder.newGrantClaimMaximum;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class GrantClaimMaximumServiceImplTest extends BaseServiceUnitTest<GrantClaimMaximumServiceImpl> {

    @Mock
    private GrantClaimMaximumRepository grantClaimMaximumRepository;

    @Mock
    private CompetitionTypeRepository competitionTypeRepository;

    @Mock
    private CompetitionMapper competitionMapper;

    @Mock
    private GrantClaimMaximumMapper grantClaimMaximumMapper;

    @Override
    protected GrantClaimMaximumServiceImpl supplyServiceUnderTest() {
        return new GrantClaimMaximumServiceImpl(grantClaimMaximumRepository, competitionTypeRepository,
                grantClaimMaximumMapper, competitionMapper);
    }

    @Test
    public void testGetGrantClaimMaximumById() {
        Integer expectedMaximum = 100;
        Long expectedId = 1L;
        GrantClaimMaximum gcm = newGrantClaimMaximum().withId(expectedId).withMaximum(expectedMaximum).build();
        GrantClaimMaximumResource gcmResource = newGrantClaimMaximumResource().withId(expectedId).withMaximum(expectedMaximum).build();

        when(grantClaimMaximumRepository.findOne(gcm.getId())).thenReturn(gcm);
        when(grantClaimMaximumMapper.mapToResource(gcm)).thenReturn(gcmResource);

        ServiceResult<GrantClaimMaximumResource> result = service.getGrantClaimMaximumById(gcm.getId());
        assertTrue(result.isSuccess());
        assertEquals(gcmResource, result.getSuccess());
        assertEquals(result.getSuccess().getMaximum(), expectedMaximum);
        assertEquals(result.getSuccess().getId(), expectedId);
    }

    @Test
    public void testGetNotFoundGrantClaimMaximum() {
        GrantClaimMaximum gcm = newGrantClaimMaximum().build();
        ServiceResult<GrantClaimMaximumResource> result = service.getGrantClaimMaximumById(gcm.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getErrors().contains(notFoundError(GrantClaimMaximum.class, gcm.getId())));
    }

    @Test
    public void testGetGrantClaimMaximumsForCompetitionType() {
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .withOrganisationType(newOrganisationTypeResource()
                        .withId(OrganisationTypeEnum.BUSINESS.getId())
                        .build())
                .build(2);
        CompetitionResource competitionResource = newCompetitionResource()
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();
        Competition competition = newCompetition().build();
        CompetitionType competitionType = newCompetitionType().withTemplate(competition).build();

        when(competitionTypeRepository.findOne(competitionType.getId())).thenReturn(competitionType);
        when(competitionMapper.mapToResource(competitionType.getTemplate())).thenReturn(competitionResource);

        ServiceResult<Set<Long>> result = service.getGrantClaimMaximumsForCompetitionType(competitionType.getId());
        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), CollectionFunctions.asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()));
    }

    @Test
    public void testSave() {
        GrantClaimMaximum gcm = newGrantClaimMaximum().build();
        GrantClaimMaximumResource gcmResource = newGrantClaimMaximumResource().build();

        when(grantClaimMaximumMapper.mapToDomain(gcmResource)).thenReturn(gcm);
        when(grantClaimMaximumMapper.mapToResource(gcm)).thenReturn(gcmResource);

        when(grantClaimMaximumRepository.save(gcm)).thenReturn(gcm);

        ServiceResult<GrantClaimMaximumResource> result = service.save(gcmResource);
        assertTrue(result.isSuccess());
        assertEquals(gcmResource, result.getSuccess());
    }
}
