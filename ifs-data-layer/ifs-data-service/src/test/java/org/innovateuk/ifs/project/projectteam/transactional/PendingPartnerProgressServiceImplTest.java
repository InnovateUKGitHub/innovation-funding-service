package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.repository.PendingPartnerProgressRepository;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.projectteam.mapper.PendingPartnerProgressMapper;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PARTNER_NOT_READY_TO_JOIN_PROJECT;
import static org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId.id;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PendingPartnerProgressServiceImplTest {
    private static final long PROJECT_ID = 1L;
    private static final long ORGANISATION_ID = 2L;

    @InjectMocks
    private PendingPartnerProgressServiceImpl service;

    @Mock
    private PendingPartnerProgressMapper pendingPartnerProgressMapper;

    @Mock
    private PendingPartnerProgressRepository pendingPartnerProgressRepository;

    @Test
    public void getPendingPartnerProgress() {
        PendingPartnerProgress pendingPartnerProgress = new PendingPartnerProgress(null);
        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(PROJECT_ID, ORGANISATION_ID)).thenReturn(Optional.of(pendingPartnerProgress));
        PendingPartnerProgressResource resource = new PendingPartnerProgressResource();
        when(pendingPartnerProgressMapper.mapToResource(pendingPartnerProgress)).thenReturn(resource);

        ServiceResult<PendingPartnerProgressResource> result = service.getPendingPartnerProgress(id(PROJECT_ID, ORGANISATION_ID));

        assertTrue(result.isSuccess());
        assertEquals(resource, result.getSuccess());
    }

    @Test
    public void markYourOrganisationComplete() {
        PendingPartnerProgress pendingPartnerProgress = new PendingPartnerProgress(null);
        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(PROJECT_ID, ORGANISATION_ID)).thenReturn(Optional.of(pendingPartnerProgress));

        ServiceResult<Void> result = service.markYourOrganisationComplete(id(PROJECT_ID, ORGANISATION_ID));

        assertTrue(result.isSuccess());
        assertTrue(pendingPartnerProgress.isYourOrganisationComplete());
    }

    @Test
    public void markYourFundingComplete() {
        PendingPartnerProgress pendingPartnerProgress = new PendingPartnerProgress(null);
        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(PROJECT_ID, ORGANISATION_ID)).thenReturn(Optional.of(pendingPartnerProgress));

        ServiceResult<Void> result = service.markYourFundingComplete(id(PROJECT_ID, ORGANISATION_ID));

        assertTrue(result.isSuccess());
        assertTrue(pendingPartnerProgress.isYourFundingComplete());
    }

    @Test
    public void markTermsAndConditionsComplete() {
        PendingPartnerProgress pendingPartnerProgress = new PendingPartnerProgress(null);
        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(PROJECT_ID, ORGANISATION_ID)).thenReturn(Optional.of(pendingPartnerProgress));

        ServiceResult<Void> result = service.markTermsAndConditionsComplete(id(PROJECT_ID, ORGANISATION_ID));

        assertTrue(result.isSuccess());
        assertTrue(pendingPartnerProgress.isTermsAndConditionsComplete());
    }

    @Test
    public void markYourOrganisationIncomplete() {
        PendingPartnerProgress pendingPartnerProgress = new PendingPartnerProgress(null);
        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(PROJECT_ID, ORGANISATION_ID)).thenReturn(Optional.of(pendingPartnerProgress));

        ServiceResult<Void> result = service.markYourOrganisationIncomplete(id(PROJECT_ID, ORGANISATION_ID));

        assertTrue(result.isSuccess());
        assertFalse(pendingPartnerProgress.isYourOrganisationComplete());
    }

    @Test
    public void markYourFundingIncomplete() {
        PendingPartnerProgress pendingPartnerProgress = new PendingPartnerProgress(null);
        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(PROJECT_ID, ORGANISATION_ID)).thenReturn(Optional.of(pendingPartnerProgress));

        ServiceResult<Void> result = service.markYourFundingIncomplete(id(PROJECT_ID, ORGANISATION_ID));

        assertTrue(result.isSuccess());
        assertFalse(pendingPartnerProgress.isYourFundingComplete());
    }

    @Test
    public void markTermsAndConditionsIncomplete() {
        PendingPartnerProgress pendingPartnerProgress = new PendingPartnerProgress(null);
        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(PROJECT_ID, ORGANISATION_ID)).thenReturn(Optional.of(pendingPartnerProgress));

        ServiceResult<Void> result = service.markTermsAndConditionsIncomplete(id(PROJECT_ID, ORGANISATION_ID));

        assertTrue(result.isSuccess());
        assertFalse(pendingPartnerProgress.isTermsAndConditionsComplete());
    }

    @Test
    public void completePartnerSetup_failure() {
        PendingPartnerProgress pendingPartnerProgress = new PendingPartnerProgress(null);
        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(PROJECT_ID, ORGANISATION_ID)).thenReturn(Optional.of(pendingPartnerProgress));

        ServiceResult<Void> result = service.completePartnerSetup(id(PROJECT_ID, ORGANISATION_ID));

        assertFalse(result.isSuccess());
        assertEquals(result.getFailure().getErrors().get(0).getErrorKey(), PARTNER_NOT_READY_TO_JOIN_PROJECT.name());
    }

    @Test
    public void completePartnerSetup_success() {
        PendingPartnerProgress pendingPartnerProgress = new PendingPartnerProgress(null);
        pendingPartnerProgress.markYourOrganisationComplete();
        pendingPartnerProgress.markYourFundingComplete();
        pendingPartnerProgress.markTermsAndConditionsComplete();

        when(pendingPartnerProgressRepository.findByOrganisationIdAndProjectId(PROJECT_ID, ORGANISATION_ID)).thenReturn(Optional.of(pendingPartnerProgress));

        ServiceResult<Void> result = service.completePartnerSetup(id(PROJECT_ID, ORGANISATION_ID));

        assertTrue(result.isSuccess());
        assertTrue(pendingPartnerProgress.isComplete());
    }
}
