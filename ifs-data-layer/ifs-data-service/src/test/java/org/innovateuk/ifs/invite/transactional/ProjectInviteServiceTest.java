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
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_INVITE_TARGET_USER_ALREADY_EXISTS_ON_PROJECT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ProjectInviteServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ProjectInviteService projectInviteService = new ProjectInviteServiceImpl();

    @Mock
    private UserRepository userRepositoryMock;

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
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectUserInviteRepositoryMock.save(projectInvite)).thenReturn(projectInvite);
        when(projectServiceMock.addPartner(projectInvite.getTarget().getId(), user.getId(), projectInvite.getOrganisation().getId())).thenReturn(serviceSuccess(projectUser));

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
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);

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
        when(userRepositoryMock.findOne(userId)).thenReturn(null);

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
    public void saveProjectInvite_success() {

        Organisation organisation = newOrganisation().build();

        when(organisationRepositoryMock.findDistinctByUsers(any(User.class))).thenReturn(singletonList(organisation));

        Project project = newProject()
                .withName("project name")
                .build();

        User user = newUser().
                withEmailAddress("email@example.com").
                build();

        ProjectUserInvite projectInvite = newProjectUserInvite().
                withProject(project).
                withOrganisation(organisation).
                withName("project name").
                withEmail(user.getEmail()).
                build();

        ProjectUserInviteResource projectUserInviteResource = getMapper(ProjectUserInviteMapper.class).mapToResource(projectInvite);

        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(projectInviteMapperMock.mapToDomain(projectUserInviteResource)).thenReturn(projectInvite);

        ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectUserInviteResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void saveProjectInvite_validationFailure() {

        Organisation organisation = newOrganisation().build();
        Project project = newProject().withName("project name").build();
        User user = newUser().withEmailAddress("email@example.com").build();

        {
            ProjectUserInvite projectInviteNoName = newProjectUserInvite()
                    .withProject(project)
                    .withOrganisation(organisation)
                    .withEmail(user.getEmail())
                    .build();

            ProjectUserInviteResource projectInviteNoNameResource = getMapper(ProjectUserInviteMapper.class).mapToResource(projectInviteNoName);

            when(projectInviteMapperMock.mapToDomain(projectInviteNoNameResource)).thenReturn(projectInviteNoName);

            ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectInviteNoNameResource);

            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(PROJECT_INVITE_INVALID));
        }

        {
            ProjectUserInvite projectInviteNoEmail = newProjectUserInvite()
                    .withProject(project)
                    .withOrganisation(organisation)
                    .withName("project name")
                    .build();

            ProjectUserInviteResource projectInviteNoEmailResource = getMapper(ProjectUserInviteMapper.class).mapToResource(projectInviteNoEmail);

            when(projectInviteMapperMock.mapToDomain(projectInviteNoEmailResource)).thenReturn(projectInviteNoEmail);

            ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectInviteNoEmailResource);

            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(PROJECT_INVITE_INVALID));
        }

        {
            ProjectUserInvite projectInviteNoOrganisation = newProjectUserInvite()
                    .withProject(project)
                    .withName("project name")
                    .withEmail(user.getEmail())
                    .build();

            ProjectUserInviteResource projectInviteNoOrganisationResource = getMapper(ProjectUserInviteMapper.class).mapToResource(projectInviteNoOrganisation);

            when(projectInviteMapperMock.mapToDomain(projectInviteNoOrganisationResource)).thenReturn(projectInviteNoOrganisation);

            ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectInviteNoOrganisationResource);

            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(PROJECT_INVITE_INVALID));
        }

        {
            ProjectUserInvite projectInviteNoProject = newProjectUserInvite()
                    .withOrganisation(organisation)
                    .withName("project name")
                    .withEmail(user.getEmail())
                    .build();

            ProjectUserInviteResource projectInviteNoProjectResource = getMapper(ProjectUserInviteMapper.class).mapToResource(projectInviteNoProject);

            when(projectInviteMapperMock.mapToDomain(projectInviteNoProjectResource)).thenReturn(projectInviteNoProject);

            ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectInviteNoProjectResource);

            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(PROJECT_INVITE_INVALID));
        }
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
        when(organisationRepositoryMock.findOne(projectUserInviteResource.getLeadOrganisationId())).thenReturn(organisation);
        when(projectServiceMock.getProjectById(projectResource.getId())).thenReturn(serviceSuccess(projectResource));

        ServiceResult<List<ProjectUserInviteResource>> invitesByProject = projectInviteService.getInvitesByProject(projectResource.getId());
        assertTrue(invitesByProject.isSuccess());
        assertEquals(singletonList(projectUserInviteResource), invitesByProject.getSuccess());
    }

    @Test
    public void validateUserIsNotAlreadyPartnerInOrganisationSuccess() {

        Organisation organisation = newOrganisation().build();

        Project project = newProject()
                .withName("project name")
                .build();

        User user = newUser().
                withEmailAddress("email@example.com").
                build();

        ProjectUserInvite projectInvite = newProjectUserInvite().
                withProject(project).
                withOrganisation(organisation).
                withName("project name").
                withEmail(user.getEmail()).
                build();

        ProjectUserInviteResource projectUserInviteResource = getMapper(ProjectUserInviteMapper.class).mapToResource(projectInvite);

        when(projectInviteMapperMock.mapToDomain(projectUserInviteResource)).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(projectUserInviteResource.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectUserInviteResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void validateUserIsNotAlreadyPartnerInOrganisationFailure() {

        Organisation organisation = newOrganisation().build();

        Project project = newProject()
                .withName("project name")
                .build();

        User user = newUser().
                withEmailAddress("email@example.com").
                build();

        ProjectUserInvite projectInvite = newProjectUserInvite().
                withProject(project).
                withOrganisation(organisation).
                withName("project name").
                withEmail(user.getEmail()).
                build();

        ProjectUser projectUser = newProjectUser().withProject(project).build();

        ProjectUserInviteResource projectUserInviteResource = getMapper(ProjectUserInviteMapper.class).mapToResource(projectInvite);

        when(projectInviteMapperMock.mapToDomain(projectUserInviteResource)).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(singletonList(projectUser));

        ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectUserInviteResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_INVITE_TARGET_USER_ALREADY_EXISTS_ON_PROJECT));
    }

    @Test
    public void validateUserWithNoOrganisationCanBeInvitedIntoProjectSuccess() {

        Organisation organisation = newOrganisation().build();

        Project project = newProject()
                .withName("project name")
                .build();

        User user = newUser().
                withEmailAddress("email@example.com").
                build();

        ProjectUserInvite projectInvite = newProjectUserInvite().
                withProject(project).
                withOrganisation(organisation).
                withName("project name").
                withEmail(user.getEmail()).
                build();

        ProjectUserInviteResource projectUserInviteResource = getMapper(ProjectUserInviteMapper.class).mapToResource(projectInvite);

        when(projectInviteMapperMock.mapToDomain(projectUserInviteResource)).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectUserInviteResource);

        assertTrue(result.isSuccess());
    }
}