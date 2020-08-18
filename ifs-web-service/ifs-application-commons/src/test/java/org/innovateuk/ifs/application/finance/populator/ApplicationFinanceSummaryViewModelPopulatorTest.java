package org.innovateuk.ifs.application.finance.populator;

import org.innovateuk.ifs.application.finance.populator.util.FinanceLinksUtil;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceSummaryTableRow;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionApplicationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
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
import java.util.*;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.competition.builder.CompetitionApplicationConfigResourceBuilder.newCompetitionApplicationConfigResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationFinanceSummaryViewModelPopulatorTest {

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
    private SectionStatusRestService sectionStatusRestService;

    @Mock
    private SectionRestService sectionRestService;

    @Mock
    private InviteService inviteService;

    @Mock
    private CompetitionApplicationConfigRestService competitionApplicationConfigRestService;

    @Mock
    private FinanceLinksUtil financeLinksUtil;

    @InjectMocks
    private ApplicationFinanceSummaryViewModelPopulator populator;

    @Test
    public void populateFinanceLinksForNonKtpApplication() {
        long applicationId = 1L;
        long competitionId = 2L;
        long organisationId = 3L;
        long userId = 4L;

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
                .build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();
        ProcessRoleResource processRole = newProcessRoleResource()
                .withUserId(userId)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisation(organisationId)
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withOrganisation(organisationId)
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .build();
        SectionResource section = newSectionResource().build();
        CompetitionApplicationConfigResource competitionApplicationConfig = newCompetitionApplicationConfigResource().build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(Collections.singletonList(processRole)));
        when(applicationFinanceRestService.getFinanceTotals(application.getId())).thenReturn(restSuccess(Collections.singletonList(applicationFinance)));
        when(organisationRestService.getOrganisationsByApplicationId(application.getId())).thenReturn(restSuccess(Collections.singletonList(organisation)));
        when(sectionStatusRestService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(restSuccess(new HashMap<>()));
        when(sectionRestService.getSectionsByCompetitionIdAndType(competitionId, SectionType.FINANCE)).thenReturn(restSuccess(Collections.singletonList(section)));
        when(competitionApplicationConfigRestService.findOneByCompetitionId(competitionId)).thenReturn(restSuccess(competitionApplicationConfig));

        ApplicationFinanceSummaryViewModel model = populator.populate(applicationId, user);

        assertNotNull(model);
        assertEquals(1, model.getRows().size());

        FinanceSummaryTableRow row = model.getRows().get(0);

        assertNotNull(row);
        assertFalse(row.isShowViewFinancesLink());
        assertNull(row.getUrl());

        verify(financeLinksUtil, times(0)).financesLink(organisation, Collections.singletonList(processRole), user, application, competition);
    }

    @Test
    public void populateFinanceLinksForKtpApplication() {
        long applicationId = 1L;
        long competitionId = 2L;
        long organisationId = 3L;
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
                .build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();
        ProcessRoleResource processRole = newProcessRoleResource()
                .withUserId(userId)
                .withRole(Role.LEADAPPLICANT)
                .withOrganisation(organisationId)
                .build();
        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource()
                .withOrganisation(organisationId)
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .build();
        SectionResource section = newSectionResource().build();
        CompetitionApplicationConfigResource competitionApplicationConfig = newCompetitionApplicationConfigResource().build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(userRestService.findProcessRole(application.getId())).thenReturn(restSuccess(Collections.singletonList(processRole)));
        when(applicationFinanceRestService.getFinanceTotals(application.getId())).thenReturn(restSuccess(Collections.singletonList(applicationFinance)));
        when(organisationRestService.getOrganisationsByApplicationId(application.getId())).thenReturn(restSuccess(Collections.singletonList(organisation)));
        when(sectionStatusRestService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(restSuccess(new HashMap<>()));
        when(sectionRestService.getSectionsByCompetitionIdAndType(competitionId, SectionType.FINANCE)).thenReturn(restSuccess(Collections.singletonList(section)));
        when(competitionApplicationConfigRestService.findOneByCompetitionId(competitionId)).thenReturn(restSuccess(competitionApplicationConfig));
        when(financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition)).thenReturn(Optional.of(financeLinkUrl));

        ApplicationFinanceSummaryViewModel model = populator.populate(applicationId, user);

        assertNotNull(model);
        assertEquals(1, model.getRows().size());

        FinanceSummaryTableRow row = model.getRows().get(0);

        assertNotNull(row);
        assertTrue(row.isShowViewFinancesLink());
        assertEquals(financeLinkUrl, row.getUrl());

        verify(financeLinksUtil, times(1)).financesLink(organisation, Collections.singletonList(processRole), user, application, competition);
    }

}
