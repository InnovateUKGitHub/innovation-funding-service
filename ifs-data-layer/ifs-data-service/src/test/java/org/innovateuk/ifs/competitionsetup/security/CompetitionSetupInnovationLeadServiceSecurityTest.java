package org.innovateuk.ifs.competitionsetup.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.security.CompetitionLookupStrategy;
import org.innovateuk.ifs.competition.security.CompetitionPermissionRules;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupInnovationLeadService;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupInnovationLeadServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CompetitionSetupInnovationLeadServiceSecurityTest extends BaseServiceSecurityTest<CompetitionSetupInnovationLeadService> {

    private CompetitionLookupStrategy competitionLookupStrategy;

    private CompetitionPermissionRules rules;

    @Before
    public void lookupPermissionRules() {

        rules = getMockPermissionRulesBean(CompetitionPermissionRules.class);
        competitionLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionLookupStrategy.class);

        initMocks(this);
    }

    @Override
    protected Class<? extends CompetitionSetupInnovationLeadService> getClassUnderTest() {
        return CompetitionSetupInnovationLeadServiceImpl.class;
    }

    @Test
    public void findInnovationLeads() {

        long competitionId = 1L;
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource().build();

        when(competitionLookupStrategy.getCompetitionResource(competitionId)).thenReturn(competitionResource);

        assertAccessDenied(
                () -> classUnderTest.findInnovationLeads(1L),
                () -> {
                    verify(rules).internalAdminCanManageInnovationLeadsForCompetition(any(CompetitionResource.class), any(UserResource.class));
                    verifyNoMoreInteractions(rules);
                });
    }

    @Test
    public void addInnovationLead() {
        long competitionId = 1L;
        long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource().build();

        when(competitionLookupStrategy.getCompetitionResource(competitionId)).thenReturn(competitionResource);

        assertAccessDenied(() -> classUnderTest.addInnovationLead(competitionId, innovationLeadUserId), () -> {
            verify(rules).internalAdminCanManageInnovationLeadsForCompetition(any(CompetitionResource.class), any(UserResource.class));
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void removeInnovationLead() {
        long competitionId = 1L;
        long innovationLeadUserId = 2L;
        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource().build();

        when(competitionLookupStrategy.getCompetitionResource(competitionId)).thenReturn(competitionResource);

        assertAccessDenied(() -> classUnderTest.removeInnovationLead(competitionId, innovationLeadUserId), () -> {
            verify(rules).internalAdminCanManageInnovationLeadsForCompetition(any(CompetitionResource.class), any(UserResource.class));
            verifyNoMoreInteractions(rules);
        });
    }
}
