package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.management.viewmodel.UnsuccessfulApplicationsViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UnsuccessfulApplicationsModelPopulatorTest {

    @InjectMocks
    private UnsuccessfulApplicationsModelPopulator unsuccessfulApplicationsModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private UserService userService;

    @Test
    public void populateModel() throws Exception {

        Long competitionId = 1L;
        int pageNumber = 0;
        int pageSize = 20;
        String sortField = "id";
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

        List<ApplicationResource> unsuccessfulApplications = ApplicationResourceBuilder.newApplicationResource().build(2);
        ApplicationPageResource unsuccessfulApplicationsPagedResult = Mockito.mock(ApplicationPageResource.class);
        when(unsuccessfulApplicationsPagedResult.getContent()).thenReturn(unsuccessfulApplications);
        when(unsuccessfulApplicationsPagedResult.getTotalElements()).thenReturn((long)unsuccessfulApplications.size());

        when(competitionRestService.getCompetitionById(competitionId))
                .thenReturn(restSuccess(competitionResource));
        when(applicationRestService.findUnsuccessfulApplications(competitionId, pageNumber, pageSize, sortField))
                .thenReturn(restSuccess(unsuccessfulApplicationsPagedResult));
        when(userService.existsAndHasRole(5L, IFS_ADMINISTRATOR)).thenReturn(true);

        UnsuccessfulApplicationsViewModel viewModel = unsuccessfulApplicationsModelPopulator.populateModel(competitionId,
                pageNumber, pageSize, sortField, userResource, existingQueryString);

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(competitionName, viewModel.getCompetitionName());
        assertEquals(isIfsAdmin, viewModel.isIfsAdmin());
        assertEquals(unsuccessfulApplications, viewModel.getUnsuccessfulApplications());
        assertEquals(unsuccessfulApplications.size(), viewModel.getUnsuccessfulApplicationsSize());
        assertEquals(new PaginationViewModel(unsuccessfulApplicationsPagedResult, existingQueryString), viewModel.getUnsuccessfulApplicationsPagination());
    }
}