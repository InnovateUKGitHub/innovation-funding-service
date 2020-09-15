package org.innovateuk.ifs.management.competition.setup.core.service;

import org.innovateuk.ifs.competition.service.CompetitionSetupInnovationLeadRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionSetupInnovationLeadServiceImplTest {

    @InjectMocks
    private CompetitionSetupInnovationLeadServiceImpl competitionSetupInnovationLeadService;

    @Mock
    private CompetitionSetupInnovationLeadRestService competitionSetupInnovationLeadRestService;

    @Test
    public void addInnovationLead() {
        long competitionId = 1L;
        long innovationLeadUserId = 2L;

        when(competitionSetupInnovationLeadRestService.addInnovationLead(competitionId, innovationLeadUserId)).thenReturn(restSuccess());

        competitionSetupInnovationLeadService.addInnovationLead(competitionId, innovationLeadUserId);
        verify(competitionSetupInnovationLeadRestService).addInnovationLead(competitionId, innovationLeadUserId);
    }

    @Test
    public void removeInnovationLead() {
        long competitionId = 1L;
        long innovationLeadUserId = 2L;

        when(competitionSetupInnovationLeadRestService.removeInnovationLead(competitionId, innovationLeadUserId)).thenReturn(restSuccess());

        competitionSetupInnovationLeadService.removeInnovationLead(competitionId, innovationLeadUserId);
        verify(competitionSetupInnovationLeadRestService).removeInnovationLead(competitionId, innovationLeadUserId);
    }

}