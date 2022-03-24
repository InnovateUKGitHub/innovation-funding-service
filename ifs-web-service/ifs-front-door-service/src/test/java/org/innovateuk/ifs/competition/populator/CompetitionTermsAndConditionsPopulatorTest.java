package org.innovateuk.ifs.competition.populator;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.viewmodel.CompetitionTermsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionTermsAndConditionsPopulatorTest {

    @InjectMocks
    private CompetitionTermsAndConditionsPopulator competitionTermsAndConditionsPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    private long competitionId;

    private CompetitionResource competitionResource;

    private GrantTermsAndConditionsResource grantTermsAndConditionsResource;

    private CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource;

    private FileEntryResource fileEntryResource;

    @Before
    public void setup() {
        competitionId = 1;

        competitionResource = newCompetitionResource()
                .withId(competitionId)
                .withCompetitionThirdPartyConfig(competitionThirdPartyConfigResource)
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(RestResult.restSuccess(competitionResource));
    }

    @Test
    public void populateProcurementThirdParty() {
        grantTermsAndConditionsResource = new GrantTermsAndConditionsResource("Third Party", "third-party-terms-and-conditions", 1);
        competitionThirdPartyConfigResource = new CompetitionThirdPartyConfigResource("TermsAndConditionsLabel",
                "TermsAndConditionsGuidance", null);
        fileEntryResource = newFileEntryResource().build();

        competitionResource.setTermsAndConditions(grantTermsAndConditionsResource);
        competitionResource.setCompetitionTerms(fileEntryResource);
        competitionResource.setCompetitionThirdPartyConfigResource(competitionThirdPartyConfigResource);

        CompetitionTermsViewModel competitionTermsViewModel = competitionTermsAndConditionsPopulator.populate(competitionId);

        assertEquals(competitionId, competitionTermsViewModel.getCompetitionId());
        assertTrue(competitionTermsViewModel.isProcurementThirdParty());
        assertTrue(competitionTermsViewModel.isTermsAndConditionsUploaded());
        assertEquals(grantTermsAndConditionsResource, competitionTermsViewModel.getTermsAndConditions());
        assertEquals("TermsAndConditionsLabel", competitionTermsViewModel.getThirdPartyConfig().getTermsAndConditionsLabel());
        assertEquals("TermsAndConditionsGuidance", competitionTermsViewModel.getThirdPartyConfig().getTermsAndConditionsGuidance());
        assertEquals("CompetitionTermsViewModel{\n" +
                "competitionId=1\n" +
                "termsAndConditions{\n" +
                "id=null\n" +
                "name=Third Party\n" +
                "template=third-party-terms-and-conditions\n" +
                "version=1\n" +
                "}\n" +
                "termsAndConditionsLabel=TermsAndConditionsLabel\n" +
                "termsAndConditionsGuidance=TermsAndConditionsGuidance\n" +
                "}", competitionTermsViewModel.toString());

        verify(competitionRestService).getCompetitionById(competitionId);
    }

    @Test
    public void populateNonProcurementThirdParty() {
        grantTermsAndConditionsResource = new GrantTermsAndConditionsResource("T&C", "special-terms-and-conditions", 3);

        competitionResource.setTermsAndConditions(grantTermsAndConditionsResource);

        CompetitionTermsViewModel competitionTermsViewModel = competitionTermsAndConditionsPopulator.populate(competitionResource.getId());

        assertEquals(competitionId, competitionTermsViewModel.getCompetitionId());
        assertFalse(competitionTermsViewModel.isProcurementThirdParty());
        assertFalse(competitionTermsViewModel.isTermsAndConditionsUploaded());
        assertEquals(grantTermsAndConditionsResource, competitionTermsViewModel.getTermsAndConditions());
        assertNull(competitionTermsViewModel.getThirdPartyConfig());
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
