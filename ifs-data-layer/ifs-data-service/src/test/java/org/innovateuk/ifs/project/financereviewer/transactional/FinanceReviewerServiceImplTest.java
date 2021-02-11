package org.innovateuk.ifs.project.financereviewer.transactional;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.financereviewer.repository.FinanceReviewerRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.financereviewer.builder.FinanceReviewerBuilder.newFinanceReviewer;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinanceReviewerServiceImplTest {

    @InjectMocks
    private FinanceReviewerServiceImpl service;

    @Mock
    private FinanceReviewerRepository financeReviewerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Test
    public void findAll() {
        User user = newUser().build();
        when(userRepository.findDistinctByRolesInAndStatusIn(newArrayList(PROJECT_FINANCE, IFS_ADMINISTRATOR), EnumSet.of(ACTIVE))).thenReturn(singletonList(user));

        List<SimpleUserResource> result = service.findFinanceUsers().getSuccess();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), (long) user.getId());
    }

    @Test
    public void getFinanceReviewerForProject() {
        long projectId = 1L;
        User user = newUser().build();
        Project project = newProject()
                .withId(projectId)
                .withFinanceReviewer(newFinanceReviewer()
                        .withUser(user)
                        .build())
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        SimpleUserResource result = service.getFinanceReviewerForProject(projectId).getSuccess();

        assertEquals(result.getId(), (long) user.getId());
    }

    @Test
    public void assignFinanceReviewer() {
        long projectId = 1L;
        User existing = newUser().build();
        User financeReviewerUser = newUser().build();
        Project project = newProject()
                .withId(projectId)
                .withFinanceReviewer(newFinanceReviewer()
                        .withUser(existing)
                        .build())
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(financeReviewerUser.getId())).thenReturn(Optional.of(financeReviewerUser));

        service.assignFinanceReviewer(financeReviewerUser.getId(), projectId).getSuccess();

        assertEquals(project.getFinanceReviewer().getUser().getId(), financeReviewerUser.getId());
    }
}