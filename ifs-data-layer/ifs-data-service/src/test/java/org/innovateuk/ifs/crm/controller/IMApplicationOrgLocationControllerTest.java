package org.innovateuk.ifs.crm.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.documentation.OrganisationDocs.organisationResourceBuilder;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IMApplicationOrgLocationControllerTest extends BaseControllerMockMVCTest<IMApplicationOrgLocationController> {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private ApplicationFinanceService applicationFinanceService;

    @Test
    public void getApplicationLocationInfoWithSingleOrg() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(GRANT)
                .build();

        Set<OrganisationResource> organisationResourceSet = organisationResourceBuilder.buildSet(1);
        Long organisationId = getOrganisationId(organisationResourceSet);

        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withLeadOrganisationId(organisationId)
                .build();

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withOrganisation(organisationId)
                .withOrganisationSize(OrganisationSize.MEDIUM)
                .withWorkPostcode("RH6 0NT")
                .build();

        when(applicationService.getApplicationById(application.getId())).thenReturn(serviceSuccess(application));
        when(applicationService.getCompetitionByApplicationId(application.getId())).thenReturn(serviceSuccess(competition));
        when(applicationService.findLatestEmailFundingDateByCompetitionId(competition.getId())).thenReturn(serviceSuccess(ZonedDateTime.now()));
        when(organisationService.findByApplicationId(application.getId())).thenReturn(serviceSuccess(organisationResourceSet));
        when(applicationFinanceService.financeDetails(application.getId(), organisationId)).thenReturn(serviceSuccess(applicationFinanceResource));

        mockMvc.perform(get("/application/v1/{applicationId}", applicationId))
                .andExpect(status().isOk()).andReturn();
    }


    private Long getOrganisationId(Set<OrganisationResource> organisationResourceSet) {
        for (OrganisationResource org : organisationResourceSet) {
            return org.getId();
        }
        return 0L;
    }

    @Override
    protected IMApplicationOrgLocationController supplyControllerUnderTest() {
        return new IMApplicationOrgLocationController();
    }
}
