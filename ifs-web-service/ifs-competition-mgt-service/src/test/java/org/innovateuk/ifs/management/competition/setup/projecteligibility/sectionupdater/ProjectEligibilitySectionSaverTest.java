package org.innovateuk.ifs.management.competition.setup.projecteligibility.sectionupdater;

import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.form.ProjectEligibilityForm;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.ApplicationFinanceType.STANDARD;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectEligibilitySectionSaverTest {

    @InjectMocks
    private ProjectEligibilitySectionUpdater service;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Test
    public void saveSection() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setSingleOrCollaborative("collaborative");
        competitionSetupForm.setMultipleStream("yes");
        competitionSetupForm.setStreamName("streamname");
        competitionSetupForm.setLeadApplicantTypes(asList(1L, 2L));
        competitionSetupForm.setResearchParticipationPercentage(30);
        competitionSetupForm.setResubmission("yes");

        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource().build(2);

        CompetitionResource competition = newCompetitionResource()
                .withGrantClaimMaximums(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .withApplicationFinanceType(STANDARD)
                .build();

        UserResource loggedInUser = newUserResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        service.saveSection(competition, competitionSetupForm, loggedInUser).getSuccess();

        assertEquals(asList(BUSINESS.getId(), OrganisationTypeEnum.RESEARCH.getId()), competition.getLeadApplicantTypes());
        assertTrue(competition.isMultiStream());
        assertEquals("streamname", competition.getStreamName());
        assertEquals(new Integer(30), competition.getMaxResearchRatio());
        assertEquals(CollaborationLevel.COLLABORATIVE, competition.getCollaborationLevel());

        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void saveSection_withoutResearchParticipationAmountIdDefaultsToNone() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .build(2);

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(STANDARD)
                .withGrantClaimMaximums(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        UserResource loggedInUser = newUserResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        service.saveSection(competition, competitionSetupForm, loggedInUser).getSuccess();

        assertEquals(new Integer(0), competition.getMaxResearchRatio());

        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void saveSection_withoutResearchParticipationAndFundingLevels() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .build(2);

        CompetitionResource competition = newCompetitionResource()
                .withApplicationFinanceType(STANDARD)
                .withGrantClaimMaximums(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        UserResource loggedInUser = newUserResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        service.saveSection(competition, competitionSetupForm, loggedInUser).getSuccess();

        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void saveSection_defaultsMaxResearchRatioToNoneForCompetitionsWithNoFinances() {
        ProjectEligibilityForm competitionSetupForm = new ProjectEligibilityForm();
        competitionSetupForm.setResearchParticipationPercentage(100);

        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource().build(2);

        CompetitionResource competition = newCompetitionResource()
                .withNonFinanceType(true)
                .withGrantClaimMaximums(asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        UserResource loggedInUser = newUserResource().build();

        when(competitionSetupRestService.update(competition)).thenReturn(restSuccess());

        service.saveSection(competition, competitionSetupForm, loggedInUser).getSuccess();

        assertEquals(0, competition.getMaxResearchRatio().intValue());

        verify(competitionSetupRestService).update(competition);
    }

    @Test
    public void supportsForm() {
        assertTrue(service.supportsForm(ProjectEligibilityForm.class));
        assertFalse(service.supportsForm(CompetitionSetupForm.class));
    }
}
