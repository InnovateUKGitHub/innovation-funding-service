package org.innovateuk.ifs.grants;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grants.transactional.GrantsInviteService;
import org.innovateuk.ifs.grants.transactional.GrantsInviteServiceImpl;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GrantsInviteServiceImplTest extends BaseServiceUnitTest<GrantsInviteService> {

    @Override
    protected GrantsInviteService supplyServiceUnderTest() { return new GrantsInviteServiceImpl(); }

    @Mock
    private ProjectRepository projectRepository;

    @Test
    public void getById() {

        Project project = newProject().build();

        long projectId = 1L;
        GrantsInviteResource invite = new GrantsInviteResource();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ServiceResult<Void> result = service.sendInvite(projectId, invite);

        assertTrue(result.isSuccess());
    }
}
