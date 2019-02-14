package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        long applicationId = project.getApplication().getId();
        when(projectRepository.findOneByApplicationId(applicationId)).thenReturn(project);
        when(grantMapper.mapToGrant(any())).thenReturn(new Grant().id(APPLICATION_ID));
        when(grantEndpoint.send(any())).thenReturn(serviceSuccess());
        GrantProcess process = new GrantProcess();
        process.setApplicationId(APPLICATION_ID);
        when(grantProcessService.findReadyToSend()).thenReturn(asList(process));
        ServiceResult<Void> result = service.sendReadyProjects();
        assertThat(result.isSuccess(), equalTo(true));
        verify(grantEndpoint).send(LambdaMatcher.createLambdaMatcher(matchGrant(project)));
    }

    private Predicate<Grant> matchGrant(Project project) {
        return grant -> {
            assertThat(grant.getId(), equalTo(project.getApplication().getId()));
            return true;
        };
    }
}
