package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.builder.PreviousApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.PreviousApplicationPageResource;
import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.application.list.populator.PreviousApplicationsModelPopulator;
import org.innovateuk.ifs.management.application.list.viewmodel.PreviousApplicationsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PreviousApplicationsModelPopulatorTest {

    @InjectMocks
    private PreviousApplicationsModelPopulator previousApplicationsModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private UserService userService;

    @Test
    public void populateModel() {

        Long competitionId = 1L;
        int pageNumber = 0;
        int pageSize = 20;
        String sortField = "id";
        String filter = "ALL";
        String existingQueryString = "";
        boolean isIfsAdmin = true;

        String competitionName = "Competition One";

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(competitionId)
                .withName(competitionName)
                .build();

        UserResource userResource = newUserResource()
                .withId(5L)
                .build();

        List<PreviousApplicationResource> previousApplications = PreviousApplicationResourceBuilder.newPreviousApplicationResource().build(2);
        PreviousApplicationPageResource previousApplicationsPagedResult = Mockito.mock(PreviousApplicationPageResource.class);
        when(previousApplicationsPagedResult.getContent()).thenReturn(previousApplications);
        when(previousApplicationsPagedResult.getTotalElements()).thenReturn((long) previousApplications.size());

        when(competitionRestService.getCompetitionById(competitionId))
                .thenReturn(restSuccess(competitionResource));
        when(applicationRestService.findPreviousApplications(competitionId, pageNumber, pageSize, sortField, filter))
                .thenReturn(restSuccess(previousApplicationsPagedResult));
        when(userService.existsAndHasRole(5L, IFS_ADMINISTRATOR)).thenReturn(true);

        PreviousApplicationsViewModel viewModel = previousApplicationsModelPopulator.populateModel(competitionId,
                pageNumber, pageSize, sortField, filter, userResource, existingQueryString);

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(competitionName, viewModel.getCompetitionName());
        assertEquals(isIfsAdmin, viewModel.isIfsAdmin());
        assertEquals(previousApplications, viewModel.getPreviousApplications());
        assertEquals(previousApplications.size(), viewModel.getPreviousApplicationsSize());
        assertEquals(new Pagination(previousApplicationsPagedResult, existingQueryString), viewModel.getPreviousApplicationsPagination());
    }
}