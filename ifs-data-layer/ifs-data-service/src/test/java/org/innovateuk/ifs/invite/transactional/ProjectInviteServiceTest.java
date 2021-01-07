package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.invite.mapper.ProjectUserInviteMapper;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectInviteServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ProjectInviteService projectInviteService = new ProjectInviteServiceImpl();

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private ProjectUserInviteRepository projectUserInviteRepositoryMock;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private ProjectUserInviteMapper projectInviteMapperMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private ProjectUserRepository projectUserRepositoryMock;

    @Test
    public void acceptProjectInvite_success() {

        Project project = newProject().build();
        Organisation organisation = newOrganisation().build();

        User user = newUser()
                .withEmailAddress("email@example.com")
                .build();

        ProjectUser projectUser = newProjectUser().build();

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withEmail(user.getEmail())
                .withHash("hash")
                .withProject(project).withOrganisation(organisation)
                .build();

        when(projectUserInviteRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectUserInviteRepositoryMock.save(projectInvite)).thenReturn(projectInvite);
        when(projectServiceMock.addPartner(projectInvite.getTarget().getId(), user.getId(), projectInvite.getOrganisation().getId())).thenReturn(serviceSuccess(projectUser));
        when(projectUserRepositoryMock.save(projectUser)).thenReturn(projectUser);

        ServiceResult<Void> result = projectInviteService.acceptProjectInvite(projectInvite.getHash(), user.getId());
        assertTrue(result.isSuccess());
    }


    @Test
    public void acceptProjectInvite_hashDoesNotExist() {

        String hash = "hash";

        User user = newUser()
                .withEmailAddress("email@example.com")
                .build();

        when(projectUserInviteRepositoryMock.getByHash(hash)).thenReturn(null);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));

        ServiceResult<Void> result = projectInviteService.acceptProjectInvite(hash, user.getId());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(ProjectUserInvite.class, hash)));
    }

    @Test
    public void acceptProjectInvite_userDoesNotExist() {

        Long userId = 1L;

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withEmail("email@example.com")
                .withHash("hash")
                .build();

        when(projectUserInviteRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.empty());

        ServiceResult<Void> result = projectInviteService.acceptProjectInvite(projectInvite.getHash(), userId);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, userId)));
    }

    @Test
    public void checkUserExistsForInvite_success() {

        User user = newUser()
                .withEmailAddress("email@example.com")
                .build();

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withEmail(user.getEmail())
                .withHash("hash")
                .build();

        when(projectUserInviteRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(projectInvite.getEmail())).thenReturn(of(user));

        ServiceResult<Boolean> result = projectInviteService.checkUserExistsForInvite(projectInvite.getHash());

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess());
    }

    @Test
    public void checkUserExistsForInvite_hashHashNotFound() {

        String hash = "hash";

        when(projectUserInviteRepositoryMock.getByHash(hash)).thenReturn(null);

        ServiceResult<Boolean> result = projectInviteService.checkUserExistsForInvite(hash);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(ProjectUserInvite.class, hash)));
    }

    @Test
    public void checkUserExistsForInvite_hashNoUserFound() {

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withEmail("email@example.com")
                .withHash("hash")
                .build();

        when(projectUserInviteRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(projectInvite.getEmail())).thenReturn(empty());

        ServiceResult<Boolean> result = projectInviteService.checkUserExistsForInvite(projectInvite.getHash());

        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }

    @Test
    public void getInvitesByProject() {

        ProjectResource projectResource = newProjectResource()
                .build();

        Organisation organisation = newOrganisation()
                .build();

        ProjectUserInviteResource projectUserInviteResource = newProjectUserInviteResource()
                .withProject(projectResource.getId())
                .withLeadOrganisation(organisation.getId())
                .build();

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .build();

        when(projectUserInviteRepositoryMock.findByProjectId(projectResource.getId())).thenReturn(singletonList(projectInvite));
        when(projectInviteMapperMock.mapToResource(projectInvite)).thenReturn(projectUserInviteResource);
        when(organisationRepositoryMock.findById(projectUserInviteResource.getLeadOrganisationId())).thenReturn(Optional.of(organisation));
        when(projectServiceMock.getProjectById(projectResource.getId())).thenReturn(serviceSuccess(projectResource));

        ServiceResult<List<ProjectUserInviteResource>> invitesByProject = projectInviteService.getInvitesByProject(projectResource.getId());
        assertTrue(invitesByProject.isSuccess());
        assertEquals(singletonList(projectUserInviteResource), invitesByProject.getSuccess());
    }


    @Test
    public void acceptProjectInviteGetsApplicantRoleIfTheyDoNotHaveIt() {

        Project project = newProject().build();
        Organisation organisation = newOrganisation().build();

        User user = newUser()
                .withUid(UUID.randomUUID().toString())
                .withEmailAddress("email@example.com")
                .withRoles(EnumSet.of(Role.LIVE_PROJECTS_USER))
                .build();

        ProjectUser projectUser = newProjectUser().build();

        ProjectUserInvite projectInvite = newProjectUserInvite()
                .withEmail(user.getEmail())
                .withHash("hash")
                .withProject(project).withOrganisation(organisation)
                .build();

        when(projectUserInviteRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectUserInviteRepositoryMock.save(projectInvite)).thenReturn(projectInvite);
        when(projectServiceMock.addPartner(projectInvite.getTarget().getId(), user.getId(), projectInvite.getOrganisation().getId())).thenReturn(serviceSuccess(projectUser));
        when(projectUserRepositoryMock.save(projectUser)).thenReturn(projectUser);
        when(userRepositoryMock.save(user)).thenReturn(user);
        when(userServiceMock.evictUserCache(user.getUid())).thenReturn(serviceSuccess());

        ServiceResult<Void> result = projectInviteService.acceptProjectInvite(projectInvite.getHash(), user.getId());
        assertTrue(result.isSuccess());
        assertTrue(user.getRoles().contains(APPLICANT));
    }

}