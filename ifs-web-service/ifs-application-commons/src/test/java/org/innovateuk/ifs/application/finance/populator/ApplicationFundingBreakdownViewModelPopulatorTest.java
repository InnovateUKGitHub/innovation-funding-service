package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.populator.util.FinanceLinksUtil;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.BreakdownTableRow;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationFundingBreakdownViewModelPopulatorTest {

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private InviteService inviteService;

    @Mock
    private FinanceLinksUtil financeLinksUtil;

    @InjectMocks
    private ApplicationFundingBreakdownViewModelPopulator populator;

    @Test
    public void populateFinanceBreakdownForLeadAndPartnerForNonKtpApplication() {
        long applicationId = 1L;
        long competitionId = 2L;
        long leadOrganisationId = 3L;
        long partnerOrganisationId = 4L;
        long userId = 4L;
        String financeLinkUrl = "some url";

        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.APPLICANT)
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(GRANT)
                .withFinanceRowTypes(Collections.singletonList(FinanceRowType.FINANCE))
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withLeadOrganisationId(leadOrganisationId)
                .build();
        OrganisationResource leadOrganisation = newOrganisationResource().withId(leadOrganisationId).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(partnerOrganisationId).build();
        List<OrganisationResource> organisations = Arrays.asList(leadOrganisation, partnerOrganisation);
        ProcessRoleResource processRole = newProcessRoleResource()
                .withUserId(userId)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisation(leadOrganisationId)
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withOrganisation(leadOrganisationId)
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(Collections.singletonList(processRole)));
        when(applicationFinanceRestService.getFinanceTotals(application.getId())).thenReturn(restSuccess(Collections.singletonList(applicationFinance)));
        when(organisationRestService.getOrganisationsByApplicationId(application.getId())).thenReturn(restSuccess(organisations));
        when(financeLinksUtil.financesLink(leadOrganisation, Collections.singletonList(processRole), user, application, competition)).thenReturn(Optional.of(financeLinkUrl));

        ApplicationFundingBreakdownViewModel model = populator.populate(applicationId, user);

        assertNotNull(model);
        assertEquals(2, model.getRows().size());

        BreakdownTableRow leadOrganisationFinanceBreakdown = model.getRows().get(0);

        assertNotNull(leadOrganisationFinanceBreakdown);
        assertEquals(leadOrganisationId, leadOrganisationFinanceBreakdown.getOrganisationId().longValue());
    }

    @Test
    public void populateFinanceBreakdownOnlyForLeadForKtpApplication() {
        long applicationId = 1L;
        long competitionId = 2L;
        long leadOrganisationId = 3L;
        long partnerOrganisationId = 4L;
        long userId = 4L;
        String financeLinkUrl = "some url";

        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.APPLICANT)
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(KTP)
                .withFinanceRowTypes(Collections.singletonList(FinanceRowType.FINANCE))
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withLeadOrganisationId(leadOrganisationId)
                .build();
        OrganisationResource leadOrganisation = newOrganisationResource().withId(leadOrganisationId).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(partnerOrganisationId).build();
        List<OrganisationResource> organisations = Arrays.asList(leadOrganisation, partnerOrganisation);
        ProcessRoleResource processRole = newProcessRoleResource()
                .withUserId(userId)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisation(leadOrganisationId)
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withOrganisation(leadOrganisationId)
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(Collections.singletonList(processRole)));
        when(applicationFinanceRestService.getFinanceTotals(application.getId())).thenReturn(restSuccess(Collections.singletonList(applicationFinance)));
        when(organisationRestService.getOrganisationsByApplicationId(application.getId())).thenReturn(restSuccess(organisations));
        when(financeLinksUtil.financesLink(leadOrganisation, Collections.singletonList(processRole), user, application, competition)).thenReturn(Optional.of(financeLinkUrl));

        ApplicationFundingBreakdownViewModel model = populator.populate(applicationId, user);

        assertNotNull(model);
        assertEquals(1, model.getRows().size());

        BreakdownTableRow leadOrganisationFinanceBreakdown = model.getRows().get(0);

        assertNotNull(leadOrganisationFinanceBreakdown);
        assertEquals(leadOrganisationId, leadOrganisationFinanceBreakdown.getOrganisationId().longValue());
    }
}
