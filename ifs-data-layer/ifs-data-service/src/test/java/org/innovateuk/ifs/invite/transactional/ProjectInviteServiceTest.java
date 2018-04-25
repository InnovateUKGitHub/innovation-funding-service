package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.invite.mapper.InviteProjectMapper;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectInviteBuilder.newProjectInvite;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class ProjectInviteServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ProjectInviteService projectInviteService = new ProjectInviteServiceImpl();

    @Test
    public void testAcceptProjectInviteSuccess() throws Exception {
        Project project = newProject().build();
        Organisation organisation = newOrganisation().build();
        User user = newUser().withEmailAddress("email@example.com").build();
        ProjectUser projectUser = newProjectUser().build();
        ProjectInvite projectInvite = newProjectInvite().withEmail(user.getEmail()).withHash("hash").withProject(project).withOrganisation(organisation).build();
        when(projectInviteRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(projectInviteRepositoryMock.save(projectInvite)).thenReturn(projectInvite);
        when(projectServiceMock.addPartner(projectInvite.getTarget().getId(), user.getId(), projectInvite.getOrganisation().getId())).thenReturn(serviceSuccess(projectUser));
        ServiceResult<Void> result = projectInviteService.acceptProjectInvite(projectInvite.getHash(), user.getId());
        assertTrue(result.isSuccess());
    }


    @Test
    public void testAcceptProjectInviteHashDoesNotExist() throws Exception {
        String hash = "hash";
        User user = newUser().withEmailAddress("email@example.com").build();
        when(projectInviteRepositoryMock.getByHash(hash)).thenReturn(null);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        ServiceResult<Void> result = projectInviteService.acceptProjectInvite(hash, user.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(ProjectInvite.class, hash)));
    }

    @Test
    public void testAcceptProjectInviteUserDoesNotExist() throws Exception {
        Long userId = 1L;
        ProjectInvite projectInvite = newProjectInvite().withEmail("email@example.com").withHash("hash").build();
        when(projectInviteRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findOne(userId)).thenReturn(null);
        ServiceResult<Void> result = projectInviteService.acceptProjectInvite(projectInvite.getHash(), userId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, userId)));
    }


    @Test
    public void testCheckUserExistingByInviteHashSuccess() throws Exception {
        User user = newUser().withEmailAddress("email@example.com").build();
        ProjectInvite projectInvite = newProjectInvite().withEmail(user.getEmail()).withHash("hash").build();
        when(projectInviteRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(projectInvite.getEmail())).thenReturn(of(user));
        ServiceResult<Boolean> result = projectInviteService.checkUserExistsForInvite(projectInvite.getHash());
        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess());
    }

    @Test
    public void testCheckUserExistingByInviteHashHashNotFound() throws Exception {
        String hash = "hash";
        when(projectInviteRepositoryMock.getByHash(hash)).thenReturn(null);
        ServiceResult<Boolean> result = projectInviteService.checkUserExistsForInvite(hash);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(ProjectInvite.class, hash)));
    }

    @Test
    public void testCheckUserExistingByInviteHashNoUserFound() throws Exception {
        ProjectInvite projectInvite = newProjectInvite().withEmail("email@example.com").withHash("hash").build();
        when(projectInviteRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(projectInvite.getEmail())).thenReturn(empty());
        ServiceResult<Boolean> result = projectInviteService.checkUserExistsForInvite(projectInvite.getHash());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccess());
    }


    @Test
    public void testSaveFinanceContactInviteSuccess() throws Exception {
        Organisation organisation = newOrganisation().build();
        when(organisationRepositoryMock.findByUsers(any(User.class))).thenReturn(singletonList(organisation));

        Project project = newProject().withName("project name").build();
        User user = newUser().
                withEmailAddress("email@example.com").
                build();
        ProjectInvite projectInvite = newProjectInvite().
                withProject(project).
                withOrganisation(organisation).
                withName("project name").
                withEmail(user.getEmail()).
                build();
        InviteProjectResource inviteProjectResource = getMapper(InviteProjectMapper.class).mapToResource(projectInvite);
        when(userRepositoryMock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(inviteProjectMapperMock.mapToDomain(inviteProjectResource)).thenReturn(projectInvite);
        ServiceResult<Void> result = projectInviteService.saveProjectInvite(inviteProjectResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveFinanceContactInviteValidationFailure() throws Exception {
        Organisation organisation = newOrganisation().build();
        Project project = newProject().withName("project name").build();
        User user = newUser().withEmailAddress("email@example.com").build();

        {
            ProjectInvite projectInviteNoName = newProjectInvite().withProject(project).withOrganisation(organisation).withEmail(user.getEmail()).build();
            InviteProjectResource projectInviteNoNameResource = getMapper(InviteProjectMapper.class).mapToResource(projectInviteNoName);
            when(inviteProjectMapperMock.mapToDomain(projectInviteNoNameResource)).thenReturn(projectInviteNoName);
            ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectInviteNoNameResource);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(PROJECT_INVITE_INVALID));
        }

        {
            ProjectInvite projectInviteNoEmail = newProjectInvite().withProject(project).withOrganisation(organisation).withName("project name").build();
            InviteProjectResource projectInviteNoEmailResource = getMapper(InviteProjectMapper.class).mapToResource(projectInviteNoEmail);
            when(inviteProjectMapperMock.mapToDomain(projectInviteNoEmailResource)).thenReturn(projectInviteNoEmail);
            ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectInviteNoEmailResource);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(PROJECT_INVITE_INVALID));
        }

        {
            ProjectInvite projectInviteNoOrganisation = newProjectInvite().withProject(project).withName("project name").withEmail(user.getEmail()).build();
            InviteProjectResource projectInviteNoOrganisationResource = getMapper(InviteProjectMapper.class).mapToResource(projectInviteNoOrganisation);
            when(inviteProjectMapperMock.mapToDomain(projectInviteNoOrganisationResource)).thenReturn(projectInviteNoOrganisation);
            ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectInviteNoOrganisationResource);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(PROJECT_INVITE_INVALID));
        }

        {
            ProjectInvite projectInviteNoProject = newProjectInvite().withOrganisation(organisation).withName("project name").withEmail(user.getEmail()).build();
            InviteProjectResource projectInviteNoProjectResource = getMapper(InviteProjectMapper.class).mapToResource(projectInviteNoProject);
            when(inviteProjectMapperMock.mapToDomain(projectInviteNoProjectResource)).thenReturn(projectInviteNoProject);
            ServiceResult<Void> result = projectInviteService.saveProjectInvite(projectInviteNoProjectResource);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(PROJECT_INVITE_INVALID));
        }
    }

    @Test
    public void testGetInvitesByProject() throws Exception {
        Project project = newProject().build();
        ProjectInvite inviteProject = newProjectInvite().withOrganisation(newOrganisation()).build();
        InviteProjectResource inviteProjectResource = getMapper(InviteProjectMapper.class).mapToResource(inviteProject);
        when(projectInviteRepositoryMock.findByProjectId(project.getId())).thenReturn(asList(inviteProject));
        when(inviteProjectMapperMock.mapToResource(inviteProject)).thenReturn(inviteProjectResource);
        when(organisationRepositoryMock.findOne(anyLong())).thenReturn(inviteProject.getOrganisation());
        ServiceResult<List<InviteProjectResource>> invitesByProject = projectInviteService.getInvitesByProject(project.getId());
        assertTrue(invitesByProject.isSuccess());
        assertEquals(asList(inviteProjectResource), invitesByProject.getSuccess());
    }
}
