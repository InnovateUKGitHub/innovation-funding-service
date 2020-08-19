package org.innovateuk.ifs.application.finance.populator.util;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.HttpServletUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FinanceLinksUtilTest {

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private HttpServletUtil httpServletUtil;

    @Mock
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @InjectMocks
    private FinanceLinksUtil financeLinksUtil;

    @Test
    public void financesLinkForInternalUser() {
        long applicationId = 1L;
        long competitionId = 2L;
        long organisationId = 3L;
        long userId = 4L;

        UserResource user = newUserResource()
                .withId(userId)
                .withRoleGlobal(Role.IFS_ADMINISTRATOR)
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

        when(userAuthenticationService.getAuthenticatedUser(any())).thenReturn(user);

        Optional<String> financeLink = financeLinksUtil.financesLink(organisation, Collections.singletonList(processRole), user, application, competition);

        assertTrue(financeLink.isPresent());
        assertEquals("/application/1/form/FINANCE/3", financeLink.get());
    }
}
