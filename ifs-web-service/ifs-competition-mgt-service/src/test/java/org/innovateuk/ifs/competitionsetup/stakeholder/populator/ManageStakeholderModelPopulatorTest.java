package org.innovateuk.ifs.competitionsetup.stakeholder.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.competitionsetup.stakeholder.viewmodel.ManageStakeholderViewModel;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ManageStakeholderModelPopulatorTest {

    private static final Long COMPETITION_ID = 8L;

    @InjectMocks
    private ManageStakeholderModelPopulator populator;

    @Mock
    private UserRestService userRestServiceMock;

    @Mock
    private CompetitionSetupStakeholderRestService competitionSetupStakeholderRestServiceMock;

    @Test
    public void populateModel() {

        String tab = "add";
        String competitionName = "competition1";
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withName(competitionName)
                .build();

        long availableStakeholderUser1 = 14L;
        long availableStakeholderUser2 = 15L;
        long stakeholderAssignedToCompetition = 16L;
        long pendingStakeholderInvitesUser1 = 17L;
        long pendingStakeholderInvitesUser2 = 18L;

        List<UserResource> stakeholders = UserResourceBuilder.newUserResource()
                .withId(availableStakeholderUser1, availableStakeholderUser2, stakeholderAssignedToCompetition)
                .withFirstName("Sonal", "Rayon", "Kingsley")
                .withLastName("Dsilva", "Kevin", "Roy")
                .build(3);

        List<UserResource> pendingStakeholderInvites = UserResourceBuilder.newUserResource()
                .withId(pendingStakeholderInvitesUser1, pendingStakeholderInvitesUser2)
                .withFirstName("Rui", "Lance")
                .withLastName("Lemos", "Lemos")
                .build(2);

        when(userRestServiceMock.findByUserRole(STAKEHOLDER)).thenReturn(restSuccess(stakeholders));
        when(competitionSetupStakeholderRestServiceMock.findStakeholders(COMPETITION_ID)).thenReturn(restSuccess(singletonList(stakeholders.get(2))));

        when(competitionSetupStakeholderRestServiceMock.findPendingStakeholderInvites(COMPETITION_ID)).thenReturn(restSuccess(pendingStakeholderInvites));

        ManageStakeholderViewModel viewModel = populator.populateModel(competitionResource, tab);

        assertEquals(COMPETITION_ID, viewModel.getCompetitionId());
        assertEquals(competitionName, viewModel.getCompetitionName());
        assertEquals(2, viewModel.getAvailableStakeholders().size());

        //Ensure that available stakeholders are sorted correctly by the name
        assertEquals((Long)availableStakeholderUser2, viewModel.getAvailableStakeholders().get(0).getId());
        assertEquals((Long)availableStakeholderUser1, viewModel.getAvailableStakeholders().get(1).getId());

        assertEquals(1, viewModel.getStakeholdersAssignedToCompetition().size());
        assertEquals((Long)stakeholderAssignedToCompetition, viewModel.getStakeholdersAssignedToCompetition().get(0).getId());

        //Ensure that pending stakeholder invites are sorted correctly by the name
        assertEquals((Long)pendingStakeholderInvitesUser2, viewModel.getPendingStakeholderInvitesForCompetition().get(0).getId());
        assertEquals((Long)pendingStakeholderInvitesUser1, viewModel.getPendingStakeholderInvitesForCompetition().get(1).getId());
    }
}


