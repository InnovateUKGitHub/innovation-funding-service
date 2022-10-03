package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.controller.OrganisationCreationTypeController;
import org.innovateuk.ifs.organisation.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static java.lang.String.valueOf;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.util.CookieTestUtil.setupEncryptedCookieService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(Parameterized.class)
public class KtpOrganisationCreationTypeControllerTest extends BaseControllerMockMVCTest<OrganisationCreationTypeController>  {

    private static final String BASE_URL = "/organisation/create";

    private final FundingType fundingType;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    @Mock
    private EncryptedCookieService cookieUtil;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Mock
    private InviteRestService inviteRestService;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    protected OrganisationCreationTypeController supplyControllerUnderTest() {
        return new OrganisationCreationTypeController();
    }

    public KtpOrganisationCreationTypeControllerTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Before
    public void setup(){
        setupEncryptedCookieService(cookieUtil);

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = new CompetitionOrganisationConfigResource();
        competitionOrganisationConfigResource.setInternationalOrganisationsAllowed(true);
        competitionOrganisationConfigResource.setInternationalLeadOrganisationAllowed(true);

        CompetitionResource competitionResource = newCompetitionResource()
                .withFundingType(fundingType)
                .build();
        ApplicationInviteResource applicationInviteResource = newApplicationInviteResource().withCompetitionId(1L).build();

        when(registrationCookieService.getCompetitionIdCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(1L));
        when(registrationCookieService.isLeadJourney(any(HttpServletRequest.class))).thenReturn(true);
        when(competitionRestService.getPublishedCompetitionById(1L)).thenReturn(restSuccess(competitionResource));
        when(inviteRestService.getInviteByHash("")).thenReturn(restSuccess(applicationInviteResource));
    }

    @Test
    public void testSelectedLeadOrganisationTypeEligible() throws Exception {
        mockMvc.perform(get("/organisation/create/organisation-type")
                        .param("organisationTypeId", valueOf(OrganisationTypeEnum.RTO.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/knowledge-base"));
    }
}
