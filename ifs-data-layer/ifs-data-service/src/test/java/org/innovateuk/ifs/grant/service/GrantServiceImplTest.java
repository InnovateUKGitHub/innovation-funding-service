package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.junit.Test;
import org.mockito.Mock;

import java.util.function.Predicate;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests around the {@link GrantServiceImpl}.
 */
public class GrantServiceImplTest extends BaseServiceUnitTest<GrantServiceImpl> {
    private static final long APPLICATION_ID = 9L;

    @Mock
    private GrantEndpoint grantEndpoint;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private GrantProcessService grantProcessService;

    @Mock
    protected GrantMapper grantMapper;

    @Override
    protected GrantServiceImpl supplyServiceUnderTest() {
        return new GrantServiceImpl();
    }

    @Test
    public void sendReadyProjects() {
        Project project = newProject()
                .withId(1L)
                .withDuration(12L)
                .withApplication(
                        newApplication()
                                .withId(APPLICATION_ID)
                                .withCompetition(
                                        newCompetition().withId(2L).build())
                                .build()).build();
        GrantProcess process = new GrantProcess(APPLICATION_ID);
        Grant grant = new Grant().id(APPLICATION_ID);

        when(projectRepository.findOneByApplicationId(APPLICATION_ID)).thenReturn(project);
        when(grantMapper.mapToGrant(project)).thenReturn(grant);
        when(grantEndpoint.send(grant)).thenReturn(serviceSuccess());
        when(grantProcessService.findReadyToSend()).thenReturn(singletonList(process));

        ServiceResult<Void> result = service.sendReadyProjects();

        assertThat(result.isSuccess(), equalTo(true));

        verify(grantEndpoint, only()).send(createLambdaMatcher(matchGrant(project)));
    }

    private static Predicate<Grant> matchGrant(Project project) {
        return grant -> {
            assertThat(grant.getId(), equalTo(project.getApplication().getId()));
            return true;
        };
    }
}
