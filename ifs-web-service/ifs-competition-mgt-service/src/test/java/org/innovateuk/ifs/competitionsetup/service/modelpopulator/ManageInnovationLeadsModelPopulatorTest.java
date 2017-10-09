package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.viewmodel.ManageInnovationLeadsViewModel;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManageInnovationLeadsModelPopulatorTest {

    @InjectMocks
    private ManageInnovationLeadsModelPopulator populator;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private UserService userService;

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
                .build();

        innLead2 = UserResourceBuilder.newUserResource()
                .withId(2L)
                .withFirstName("Lucas")
                .withLastName("Oliver")
                .build();

        innLead3 = UserResourceBuilder.newUserResource()
                .withId(3L)
                .withFirstName("Aiden")
                .withLastName("James")
                .build();

        innLead4 = UserResourceBuilder.newUserResource()
                .withId(4L)
                .withFirstName("Joshua")
                .withLastName("Ethan")
                .build();

        availableInnovationLeads = new ArrayList<>();
        availableInnovationLeads.add(innLead1);
        availableInnovationLeads.add(innLead2);
        availableInnovationLeads.add(innLead3);
        availableInnovationLeads.add(innLead4);

        innovationLeadsAssignedToCompetition = new ArrayList<>();
        innovationLeadsAssignedToCompetition.add(innLead2);
    }

    @Test
    public void testPopulateModel() {
        Long competitionId = 1L;

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(competitionId)
                .withName("comp name")
                .withLeadTechnologist(4L)
                .withLeadTechnologistName("lead technologist name")
                .withExecutiveName("executive name")
                .withInnovationSectorName("innovation sector name")
                .withInnovationAreaNames(Collections.EMPTY_SET)
                .build();

        when(userService.findUserByType(UserRoleType.INNOVATION_LEAD)).thenReturn(availableInnovationLeads);
        when(competitionService.findInnovationLeads(competitionId)).thenReturn(innovationLeadsAssignedToCompetition);
        when(userService.findById(competitionResource.getLeadTechnologist())).thenReturn(innLead4);

        ManageInnovationLeadsViewModel viewModel = populator.populateModel(competitionResource);

        // Only two innovation leads should be available
        assertEquals(2, viewModel.getAvailableInnovationLeads().size());

        // Should be sorted by their names
        assertEquals(innLead3, viewModel.getAvailableInnovationLeads().get(0));
        assertEquals(innLead1, viewModel.getAvailableInnovationLeads().get(1));

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

