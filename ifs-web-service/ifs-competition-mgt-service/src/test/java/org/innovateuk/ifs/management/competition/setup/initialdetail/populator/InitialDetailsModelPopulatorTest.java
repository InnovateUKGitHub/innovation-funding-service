package org.innovateuk.ifs.management.competition.setup.initialdetail.populator;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.initialdetail.viewmodel.InitialDetailsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeResourceBuilder.newCompetitionTypeResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InitialDetailsModelPopulatorTest {

    @InjectMocks
    private InitialDetailsModelPopulator populator;

    @Mock
    private CategoryRestService categoryRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Test
    public void sectionToPopulateModel() {
        CompetitionSetupSection result = populator.sectionToPopulateModel();

        assertEquals(CompetitionSetupSection.INITIAL_DETAILS, result);
    }

    @Test
    public void populateModel() {
        long competitionId = 8L;

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("code")
                .withName("name")
                .withId(competitionId)
                .withResearchCategories(asLinkedSet(2L, 3L))
                .build();

        List<UserResource> compExecs = newUserResource().build(1);
        List<InnovationSectorResource> innovationSectors = newInnovationSectorResource().build(2);
        List<InnovationAreaResource> innovationAreas = newInnovationAreaResource().build(2);
        List<CompetitionTypeResource> competitionTypes = newCompetitionTypeResource().build(2);
        List<UserResource> leadTechs = newUserResource().build(1);


        when(userRestService.findByUserRole(COMP_ADMIN)).thenReturn(restSuccess(compExecs));
        when(categoryRestService.getInnovationSectors()).thenReturn(restSuccess(innovationSectors));
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreas));
        when(competitionRestService.getCompetitionTypes()).thenReturn(restSuccess(competitionTypes));
        when(userRestService.findByUserRoleAndUserStatus(INNOVATION_LEAD, ACTIVE)).thenReturn(restSuccess(leadTechs));
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId())).thenReturn(true);

        InitialDetailsViewModel viewModel =  populator.populateModel(getBasicGeneralSetupView(competition), competition);

        assertEquals(compExecs, viewModel.getCompetitionExecutiveUsers());
        assertEquals(innovationSectors, viewModel.getInnovationSectors());
        assertTrue(viewModel.getInnovationAreas().containsAll(innovationAreas));
        assertEquals(competitionTypes, viewModel.getCompetitionTypes());
        assertEquals(leadTechs, viewModel.getInnovationLeadTechUsers());
        assertEquals(CompetitionSetupSection.INITIAL_DETAILS, viewModel.getGeneral().getCurrentSection());
        assertTrue(viewModel.getRestricted());

        InOrder inOrder = inOrder(userRestService, categoryRestService, competitionRestService, userRestService, competitionSetupService);
        inOrder.verify(userRestService).findByUserRole(COMP_ADMIN);
        inOrder.verify(categoryRestService).getInnovationSectors();
        inOrder.verify(categoryRestService).getInnovationAreas();
        inOrder.verify(competitionRestService).getCompetitionTypes();
        inOrder.verify(userRestService).findByUserRoleAndUserStatus(INNOVATION_LEAD, ACTIVE);
        inOrder.verify(competitionSetupService).hasInitialDetailsBeenPreviouslySubmitted(competition.getId());
        inOrder.verifyNoMoreInteractions();
    }

    private static GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competition) {
        return new GeneralSetupViewModel(false, competition, CompetitionSetupSection.INITIAL_DETAILS, CompetitionSetupSection.values(), true);
    }
}