package org.innovateuk.ifs.management.supporters.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.supporter.resource.SupportersAvailableForApplicationPageResource;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.supporters.viewmodel.AssignSupportersViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AssignSupportersViewModelPopulatorTest {

    @InjectMocks
    private AssignSupportersViewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Test
    public void populate() {
        // given
        long competitionId = 5L;
        String competitionName = "my comp";
        long applicationId = 7L;
        String applicationName = "my app";
        String innovationAreaName = "innovation area";
        String filter = "w";
        int page = 1;

        CompetitionResource competition = newCompetitionResource().withId(competitionId).withName(competitionName).build();
        given(competitionRestService.getCompetitionById(competitionId)).willReturn(restSuccess(competition));

        ApplicationResource application = newApplicationResource().withId(applicationId).withName(applicationName)
                .withInnovationArea(newInnovationAreaResource().withSectorName(innovationAreaName).build()).build();
        given(applicationRestService.getApplicationById(applicationId)).willReturn(restSuccess(application));

        SupportersAvailableForApplicationPageResource supportersAvailableForApplication = new SupportersAvailableForApplicationPageResource();
        given(supporterAssignmentRestService.findAvailableSupportersForApplication(applicationId, filter, 0)).willReturn(restSuccess(supportersAvailableForApplication));

        List<OrganisationResource> organisations = newOrganisationResource().withName("first", "second", "third").build(3);
        given(organisationRestService.getOrganisationsByApplicationId(applicationId)).willReturn(restSuccess(organisations));

        // when
        AssignSupportersViewModel result = populator.populateModel(competitionId, applicationId, filter, page);

        // then
        assertThat(result.getApplicationId()).isEqualTo(applicationId);
        assertThat(result.getApplicationName()).isEqualTo(applicationName);
        assertThat(result.getCompetitionId()).isEqualTo(competitionId);
        assertThat(result.getCompetitionName()).isEqualTo(competitionName);
        assertThat(result.getFilter()).isEqualTo(filter);
        assertThat(result.getInnovationArea()).isEqualTo(innovationAreaName);
        assertThat(result.getSupportersAvailableForApplicationPage()).isEqualTo(supportersAvailableForApplication);
        assertThat(result.getPartners()).isEqualTo(asList("first", "second", "third"));
    }

}
