package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.junit.Test;
import org.mockito.Mock;

import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests around the {@link GrantServiceImpl}.
 */
public class GrantServiceImplTest extends BaseServiceUnitTest<GrantServiceImpl> {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private GrantEndpoint grantEndpoint;

    @Override
    protected GrantServiceImpl supplyServiceUnderTest() {
        return new GrantServiceImpl();
    }

    @Test
    public void testSend() {
        long applicationId = 1L;
        Project project = newProject().withId(applicationId).build();
        when(projectRepository.findOneByApplicationId(applicationId)).thenReturn(project);
        ServiceResult<Void> result = service.sendProject(applicationId);
        assertThat(result.isSuccess(), equalTo(true));
        verify(grantEndpoint).send(LambdaMatcher.createLambdaMatcher(matchGrant(project)));
    }

    private Predicate<Grant> matchGrant(Project project) {
        return grant -> {
            assertThat(grant.getId(), equalTo(project.getId()));
            return true;
        };
    }
}
