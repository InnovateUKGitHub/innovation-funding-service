package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionThirdPartyConfigRestService;
import org.innovateuk.ifs.competition.viewmodel.CompetitionTermsViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionTermsAndConditionsPopulatorTest {

    @InjectMocks
    private CompetitionTermsAndConditionsPopulator competitionTermsAndConditionsPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionThirdPartyConfigRestService competitionThirdPartyConfigRestService;

    private long competitionId;

    private CompetitionResource competitionResource;

    private GrantTermsAndConditionsResource grantTermsAndConditionsResource;

    private CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource;

    @Before
    public void setup() {
        competitionId = 1;

        competitionResource = newCompetitionResource()
                .withId(competitionId)
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(RestResult.restSuccess(competitionResource));
    }

    @Test
    public void populateProcurementThirdParty() {
        grantTermsAndConditionsResource = new GrantTermsAndConditionsResource("Procurement Third Party", "third-party-terms-and-conditions", 1);
        competitionThirdPartyConfigResource = new CompetitionThirdPartyConfigResource("TermsAndConditionsLabel",
                "TermsAndConditionsGuidance", null);

        competitionResource.setTermsAndConditions(grantTermsAndConditionsResource);

        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competitionId)).thenReturn(RestResult.restSuccess(competitionThirdPartyConfigResource));

        CompetitionTermsViewModel competitionTermsViewModel = competitionTermsAndConditionsPopulator.populate(competitionId);

        assertEquals(competitionId, competitionTermsViewModel.getCompetitionId());
        assertEquals(grantTermsAndConditionsResource, competitionTermsViewModel.getTermsAndConditions());
        assertEquals("TermsAndConditionsLabel", competitionTermsViewModel.getTermsAndConditionsLabel());
        assertEquals("TermsAndConditionsGuidance", competitionTermsViewModel.getTermsAndConditionsGuidance());
        assertEquals("CompetitionTermsViewModel{\n" +
                "competitionId=1\n" +
                "termsAndConditions{\n" +
                "id=null\n" +
                "name=Procurement Third Party\n" +
                "template=third-party-terms-and-conditions\n" +
                "version=1\n" +
                "}\n" +
                "termsAndConditionsLabel=TermsAndConditionsLabel\n" +
                "termsAndConditionsGuidance=TermsAndConditionsGuidance\n" +
                "}", competitionTermsViewModel.toString());

        verify(competitionRestService).getCompetitionById(competitionId);
        verify(competitionThirdPartyConfigRestService).findOneByCompetitionId(competitionId);
    }

    @Test
    public void populateNonProcurementThirdParty() {
        grantTermsAndConditionsResource = new GrantTermsAndConditionsResource("T&C", "special-terms-and-conditions", 3);

        competitionResource.setTermsAndConditions(grantTermsAndConditionsResource);

        CompetitionTermsViewModel competitionTermsViewModel = competitionTermsAndConditionsPopulator.populate(competitionId);

        assertEquals(competitionId, competitionTermsViewModel.getCompetitionId());
        assertEquals(grantTermsAndConditionsResource, competitionTermsViewModel.getTermsAndConditions());
        assertNull(competitionTermsViewModel.getTermsAndConditionsLabel());
        assertNull(competitionTermsViewModel.getTermsAndConditionsGuidance());
        assertEquals("CompetitionTermsViewModel{\n" +
                "competitionId=1\n" +
                "termsAndConditions{\n" +
                "id=null\n" +
                "name=T&C\n" +
                "template=special-terms-and-conditions\n" +
                "version=3\n" +
                "}\n" +
                "}", competitionTermsViewModel.toString());

        verify(competitionRestService).getCompetitionById(competitionId);
    }
}
