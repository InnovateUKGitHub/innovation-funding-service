package org.innovateuk.ifs.crm.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.documentation.OrganisationDocs.organisationResourceBuilder;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IMApplicationOrgLocationControllerTest extends BaseControllerMockMVCTest<IMApplicationOrgLocationController> {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private OrganisationService organisationService;

    @Test
    public void getApplicationLocationInfo() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long leadOrganisationId = 3L;
        long partnerOrganisationId = 4L;

        UserResource user = newUserResource().withId(1L).build();

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(GRANT)
                .build();
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withLeadOrganisationId(leadOrganisationId)
                .build();

        OrganisationResource leadOrganisation = newOrganisationResource()
                .withId(leadOrganisationId)
                .build();

    //    Set<OrganisationResource> organisations = Arrays.asList(leadOrganisation);
        Set<OrganisationResource> organisationResourceSet = organisationResourceBuilder.buildSet(1);

//        OrganisationResource partnerOrganisation = newOrganisationResource().withId(partnerOrganisationId).build();
//

        when(applicationService.getApplicationById(application.getId())).thenReturn(serviceSuccess(application));
        when(applicationService.getCompetitionByApplicationId(application.getId())).thenReturn(serviceSuccess(competition));
        when(applicationService.findLatestEmailFundingDateByCompetitionId(competition.getId())).thenReturn(serviceSuccess(ZonedDateTime.now()));
        when(organisationService.findByApplicationId(application.getId())).thenReturn(serviceSuccess(organisationResourceSet));

        mockMvc.perform(get("/application/v1/{applicationId}", applicationId))
                .andExpect(status().isOk()).andReturn();
    }

    @Override
    protected IMApplicationOrgLocationController supplyControllerUnderTest() {
        return new IMApplicationOrgLocationController();
    }
}
