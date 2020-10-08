package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectDetailsViewModelTest {

    private static final Long ORG_ID = 1L;
    private static final String POSTCODE = "POSTCODE";
    private static final String LOCATION = "LOCATION";

    private CompetitionResource competitionResource;
    private ProjectResource project = new ProjectResource();
    private UserResource currentUser = null;
    private List<Long> usersPartnerOrganisations = null;
    OrganisationResource leadOrganisation = null;
    boolean userIsLeadPartner = false;
    boolean spendProfileGenerated = false;
    boolean grantOfferLetterGenerated = false;
    boolean readOnlyView = false;

    @Before
    public void setup() {
        competitionResource = CompetitionResourceBuilder.newCompetitionResource().build();
    }

    @Test
    public void shouldShowPostcodeForLocationGivenNotInternational() {
        // given
        List<OrganisationResource> organisations = Collections.emptyList();
        PartnerOrganisationResource partnerOrg = new PartnerOrganisationResource();
        partnerOrg.setOrganisation(ORG_ID);
        partnerOrg.setPostcode(POSTCODE);
        List<PartnerOrganisationResource> partnerOrganisations = Collections.singletonList(partnerOrg);

        ProjectDetailsViewModel model = new ProjectDetailsViewModel(project, currentUser, usersPartnerOrganisations,
                organisations, partnerOrganisations, leadOrganisation, userIsLeadPartner, spendProfileGenerated,
                grantOfferLetterGenerated, readOnlyView, competitionResource);

        // when
        String result = model.getLocationForPartnerOrganisation(ORG_ID);

        // then
        assertEquals(POSTCODE, result);
    }

    @Test
    public void shouldShowInternationalLocationGivenInternational() {
        // given
        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setInternational(true);
        organisationResource.setId(ORG_ID);
        List<OrganisationResource> organisations = Collections.singletonList(organisationResource);
        PartnerOrganisationResource partnerOrg = new PartnerOrganisationResource();
        partnerOrg.setOrganisation(ORG_ID);
        partnerOrg.setInternationalLocation(LOCATION);
        List<PartnerOrganisationResource> partnerOrganisations = Collections.singletonList(partnerOrg);

        ProjectDetailsViewModel model = new ProjectDetailsViewModel(project, currentUser, usersPartnerOrganisations,
                organisations, partnerOrganisations, leadOrganisation, userIsLeadPartner, spendProfileGenerated,
                grantOfferLetterGenerated, readOnlyView, competitionResource);

        // when
        String result = model.getLocationForPartnerOrganisation(ORG_ID);

        // then
        assertEquals(LOCATION, result);
    }

    @Test
    public void testKtpCompetition() {
        competitionResource = CompetitionResourceBuilder.newCompetitionResource().withFundingType(FundingType.KTP).build();

        ProjectDetailsViewModel viewModel = new ProjectDetailsViewModel(project, currentUser, usersPartnerOrganisations,
                null, null, leadOrganisation, userIsLeadPartner, spendProfileGenerated,
                grantOfferLetterGenerated, readOnlyView, competitionResource);

        assertTrue(viewModel.isKtpCompetition());
    }
}
