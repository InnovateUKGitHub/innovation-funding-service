package org.innovateuk.ifs.management.cofunders.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.cofunders.viewmodel.AssignCofundersViewModel;
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
public class AssignCofundersViewModelPopulatorTest {

    @InjectMocks
    private AssignCofundersViewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private CofunderAssignmentRestService cofunderAssignmentRestService;

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

        CofundersAvailableForApplicationPageResource cofundersAvailableForApplication = new CofundersAvailableForApplicationPageResource();
        given(cofunderAssignmentRestService.findAvailableCofundersForApplication(applicationId, filter, 0)).willReturn(restSuccess(cofundersAvailableForApplication));

        List<OrganisationResource> organisations = newOrganisationResource().withName("first", "second", "third").build(3);
        given(organisationRestService.getOrganisationsByApplicationId(applicationId)).willReturn(restSuccess(organisations));

        // when
        AssignCofundersViewModel result = populator.populateModel(competitionId, applicationId, filter, page);

        // then
        assertThat(result.getApplicationId()).isEqualTo(applicationId);
        assertThat(result.getApplicationName()).isEqualTo(applicationName);
        assertThat(result.getCompetitionId()).isEqualTo(competitionId);
        assertThat(result.getCompetitionName()).isEqualTo(competitionName);
        assertThat(result.getFilter()).isEqualTo(filter);
        assertThat(result.getInnovationArea()).isEqualTo(innovationAreaName);
        assertThat(result.getCofundersAvailableForApplicationPage()).isEqualTo(cofundersAvailableForApplication);
        assertThat(result.getPartners()).isEqualTo(asList("first", "second", "third"));
    }

}
