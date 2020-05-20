package org.innovateuk.ifs.management.competition.setup.initialdetail.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupInnovationLeadRestService;
import org.innovateuk.ifs.management.competition.setup.initialdetail.viewmodel.ManageInnovationLeadsViewModel;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_SET;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ManageInnovationLeadsModelPopulatorTest {

    @InjectMocks
    private ManageInnovationLeadsModelPopulator populator;

    @Mock
    private CompetitionSetupInnovationLeadRestService competitionSetupInnovationLeadRestService;

    private List<UserResource> availableInnovationLeads;
    private List<UserResource> innovationLeadsAssignedToCompetition;

    private UserResource innLead1;
    private UserResource innLead2;
    private UserResource innLead3;
    private UserResource innLead4;

    @Before
    public void setUp() {

        innLead1 = UserResourceBuilder.newUserResource()
                .withId(1L)
                .withFirstName("Zach")
                .withLastName("Noah")
                .withStatus(ACTIVE)
                .build();

        innLead2 = UserResourceBuilder.newUserResource()
                .withId(2L)
                .withFirstName("Lucas")
                .withLastName("Oliver")
                .withStatus(ACTIVE)
                .build();

        innLead3 = UserResourceBuilder.newUserResource()
                .withId(3L)
                .withFirstName("Aiden")
                .withLastName("James")
                .withStatus(ACTIVE)
                .build();

        innLead4 = UserResourceBuilder.newUserResource()
                .withId(4L)
                .withFirstName("Joshua")
                .withLastName("Ethan")
                .withStatus(ACTIVE)
                .build();

        availableInnovationLeads = asList(innLead1, innLead3, innLead4);
        innovationLeadsAssignedToCompetition = singletonList(innLead2);
    }

    @Test
    public void populateModel() {
        Long competitionId = 1L;

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(competitionId)
                .withName("comp name")
                .withLeadTechnologist(4L)
                .withLeadTechnologistName("lead technologist name")
                .withExecutiveName("executive name")
                .withInnovationSectorName("innovation sector name")
                .withInnovationAreaNames(EMPTY_SET)
                .build();

        when(competitionSetupInnovationLeadRestService.findAvailableInnovationLeadsNotAssignedToCompetition(competitionId)).thenReturn(restSuccess(availableInnovationLeads));
        when(competitionSetupInnovationLeadRestService.findInnovationLeadsAssignedToCompetition(competitionId)).thenReturn(restSuccess(innovationLeadsAssignedToCompetition));

        ManageInnovationLeadsViewModel viewModel = populator.populateModel(competitionResource);

        // Only two innovation leads should be available
        assertEquals(3, viewModel.getAvailableInnovationLeads().size());

        // Should be sorted by their names
        assertEquals(innLead3, viewModel.getAvailableInnovationLeads().get(0));
        assertEquals(innLead4, viewModel.getAvailableInnovationLeads().get(1));

        // One should be assigned to competition
        assertEquals(1, viewModel.getInnovationLeadsAssignedToCompetition().size());
        assertEquals(innLead2, viewModel.getInnovationLeadsAssignedToCompetition().get(0));

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(competitionResource.getName(), viewModel.getCompetitionName());
        assertEquals(competitionResource.getLeadTechnologistName(), viewModel.getLeadTechnologistName());
        assertEquals(competitionResource.getExecutiveName(), viewModel.getExecutiveName());
        assertEquals(competitionResource.getInnovationSectorName(), viewModel.getInnovationSectorName());
        assertEquals(competitionResource.getInnovationAreaNames(), viewModel.getInnovationAreaNames());
    }
}

