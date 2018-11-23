package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
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
    public void testSend() {
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
        ServiceResult<Void> result = service.sendProject(applicationId);
        assertThat(result.isSuccess(), equalTo(true));
        verify(grantEndpoint).send(LambdaMatcher.createLambdaMatcher(matchGrant(project)));
        //verify(grantProcessService).sendSucceeded(LambdaMatcher.createLambdaMatcher(it -> it == APPLICATION_ID));
    }

    private Predicate<Grant> matchGrant(Project project) {
        return grant -> {
            assertThat(grant.getId(), equalTo(project.getApplication().getId()));
            return true;
        };
    }
}
