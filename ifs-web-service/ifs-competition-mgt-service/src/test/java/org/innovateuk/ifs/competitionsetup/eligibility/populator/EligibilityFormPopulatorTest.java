package org.innovateuk.ifs.competitionsetup.eligibility.populator;

import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.eligibility.form.EligibilityForm;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.GrantClaimMaximumResourceBuilder.newGrantClaimMaximumResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EligibilityFormPopulatorTest {

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    private EligibilityFormPopulator service;

    @Before
    public void setUp() {
        service = new EligibilityFormPopulator(competitionRestService,
                grantClaimMaximumRestService);
    }

    @Test
    public void testSectionToFill() {
        CompetitionSetupSection result = service.sectionToFill();
        assertEquals(CompetitionSetupSection.ELIGIBILITY, result);
    }

    @Test
    public void testGetSectionFormDataInitialDetails() {
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .withOrganisationType(newOrganisationTypeResource()
                        .withId(OrganisationTypeEnum.BUSINESS.getId())
                        .build())
                .build(4);

        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .withMaxResearchRatio(50)
                .withMultiStream(true)
                .withStreamName("streamname")
                .withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
                .withLeadApplicantType(asList(2L))
                .withCompetitionType(OrganisationTypeEnum.BUSINESS.getId())
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .build();

        CompetitionResource template = newCompetitionResource()
                .withCompetitionType(OrganisationTypeEnum.BUSINESS.getId())
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(2).getId(), gcms.get(3).getId()))
                .build();

        when(competitionRestService.findTemplateCompetitionForCompetitionType(competition.getCompetitionType())).thenReturn(restSuccess(template));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(1).getId())).thenReturn(restSuccess(gcms.get(1)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(2).getId())).thenReturn(restSuccess(gcms.get(2)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(3).getId())).thenReturn(restSuccess(gcms.get(3)));

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof EligibilityForm);
        EligibilityForm form = (EligibilityForm) result;
        assertEquals(CollectionFunctions.asLinkedSet(2L, 3L), form.getResearchCategoryId());
        assertEquals("no", form.getMultipleStream());
        assertEquals(null, form.getStreamName());
        assertEquals("collaborative", form.getSingleOrCollaborative());
        assertEquals(asList(2L), form.getLeadApplicantTypes());
        assertEquals(2, form.getResearchParticipationAmountId());
    }

    @Test
    public void testGetDefaultResearchParticipationAmountId() {
        List<GrantClaimMaximumResource> gcms = newGrantClaimMaximumResource()
                .withOrganisationType(newOrganisationTypeResource()
                        .withId(OrganisationTypeEnum.BUSINESS.getId())
                        .build())
                .build(4);
        CompetitionResource template = newCompetitionResource()
                .withCompetitionType(OrganisationTypeEnum.BUSINESS.getId())
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(2).getId(), gcms.get(3).getId()))
                .build();

        CompetitionResource competition = newCompetitionResource()
                .withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
                .withMultiStream(true)
                .withCompetitionType(OrganisationTypeEnum.BUSINESS.getId())
                .withStreamName("streamname")
                .withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
                .withGrantClaimMaximums(CollectionFunctions.asLinkedSet(gcms.get(0).getId(), gcms.get(1).getId()))
                .withLeadApplicantType(asList(2L))
                .build();

        when(competitionRestService.findTemplateCompetitionForCompetitionType(competition.getCompetitionType())).thenReturn(restSuccess(template));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(0).getId())).thenReturn(restSuccess(gcms.get(0)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(1).getId())).thenReturn(restSuccess(gcms.get(1)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(2).getId())).thenReturn(restSuccess(gcms.get(2)));
        when(grantClaimMaximumRestService.getGrantClaimMaximumById(gcms.get(3).getId())).thenReturn(restSuccess(gcms.get(3)));


        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof EligibilityForm);
        EligibilityForm form = (EligibilityForm) result;
        assertEquals(1, form.getResearchParticipationAmountId());
    }
}
