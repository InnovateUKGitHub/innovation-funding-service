package com.worth.ifs.invite.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.mapper.InviteOrganisationMapper;
import com.worth.ifs.invite.mapper.InviteProjectMapper;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.ProjectInviteBuilder.newInvite;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.Mockito.when;

public class ProjectInviteServiceTest extends BaseUnitTestMocksTest {

    @Mock
    NotificationService notificationService;

    @Mock
    InviteOrganisationMapper inviteOrganisationMapper;


    @InjectMocks
    private InviteProjectService inviteProjectService = new InviteProjectServiceImpl();

    @Test
    public void testAcceptProjectInviteSuccess() throws Exception {
        Project project = newProject().build();
        Organisation organisation = newOrganisation().build();
        User user = newUser().withEmailAddress("email@example.com").build();
        ProjectUser projectUser = newProjectUser().build();
        ProjectInvite projectInvite = newInvite().withEmailAddress(user.getEmail()).withHash("hash").withProject(project).withOrganisation(organisation).build();
        when(inviteProjectRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        when(inviteProjectRepositoryMock.save(projectInvite)).thenReturn(projectInvite);
        when(projectServiceMock.addPartner(projectInvite.getTarget().getId(), user.getId(), projectInvite.getOrganisation().getId())).thenReturn(serviceSuccess(projectUser));
        ServiceResult<Void> result = inviteProjectService.acceptProjectInvite(projectInvite.getHash(), user.getId());
        assertTrue(result.isSuccess());
    }


    @Test
    public void testAcceptProjectInviteHashDoesNotExist() throws Exception {
        String hash = "hash";
        User user = newUser().withEmailAddress("email@example.com").build();
        when(inviteProjectRepositoryMock.getByHash(hash)).thenReturn(null);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
        ServiceResult<Void> result = inviteProjectService.acceptProjectInvite(hash, user.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(ProjectInvite.class, hash)));
    }

    @Test
    public void testAcceptProjectInviteUserDoesNotExist() throws Exception {
        Long userId = 1L;
        ProjectInvite projectInvite = newInvite().withEmailAddress("email@example.com").withHash("hash").build();
        when(inviteProjectRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findOne(userId)).thenReturn(null);
        ServiceResult<Void> result = inviteProjectService.acceptProjectInvite(projectInvite.getHash(), userId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, userId)));
    }


    @Test
    public void testCheckUserExistingByInviteHashSuccess() throws Exception {
        User user = newUser().withEmailAddress("email@example.com").build();
        ProjectInvite projectInvite = newInvite().withEmailAddress(user.getEmail()).withHash("hash").build();
        when(inviteProjectRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(projectInvite.getEmail())).thenReturn(of(user));
        ServiceResult<Boolean> result = inviteProjectService.checkUserExistingByInviteHash(projectInvite.getHash());
        assertTrue(result.isSuccess());
        assertTrue(result.getSuccessObject());
    }

    @Test
    public void testCheckUserExistingByInviteHashHashNotFound() throws Exception {
        String hash = "hash";
        when(inviteProjectRepositoryMock.getByHash(hash)).thenReturn(null);
        ServiceResult<Boolean> result = inviteProjectService.checkUserExistingByInviteHash(hash);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(ProjectInvite.class, hash)));
    }

    @Test
    public void testCheckUserExistingByInviteHashNoUserFound() throws Exception {
        ProjectInvite projectInvite = newInvite().withEmailAddress("email@example.com").withHash("hash").build();
        when(inviteProjectRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(projectInvite.getEmail())).thenReturn(empty());
        ServiceResult<Boolean> result = inviteProjectService.checkUserExistingByInviteHash(projectInvite.getHash());
        assertTrue(result.isSuccess());
        assertFalse(result.getSuccessObject());
    }


    @Test
    public void testSaveFinanceContactInviteSuccess() throws Exception {
        Organisation organisation = newOrganisation().build();
        Project project = newProject().withName("project name").build();
        User user = newUser().withEmailAddress("email@example.com").build();
        ProjectInvite projectInvite = newInvite().withProject(project).withOrganisation(organisation).withName("project name").withEmailAddress(user.getEmail()).build();
        InviteProjectResource inviteProjectResource = getMapper(InviteProjectMapper.class).mapToResource(projectInvite);
        when(inviteProjectMapperMock.mapToDomain(inviteProjectResource)).thenReturn(projectInvite);
        ServiceResult<Void> result = inviteProjectService.saveProjectInvite(inviteProjectResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveFinanceContactInviteValidationFailure() throws Exception {
        Organisation organisation = newOrganisation().build();
        Project project = newProject().withName("project name").build();
        User user = newUser().withEmailAddress("email@example.com").build();

        {
            ProjectInvite projectInviteNoName = newInvite().withProject(project).withOrganisation(organisation).withEmailAddress(user.getEmail()).build();
            InviteProjectResource projectInviteNoNameResource = getMapper(InviteProjectMapper.class).mapToResource(projectInviteNoName);
            when(inviteProjectMapperMock.mapToDomain(projectInviteNoNameResource)).thenReturn(projectInviteNoName);
            ServiceResult<Void> result = inviteProjectService.saveProjectInvite(projectInviteNoNameResource);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(badRequestError("The Invite is not valid")));
        }

        {
            ProjectInvite projectInviteNoEmail = newInvite().withProject(project).withOrganisation(organisation).withName("project name").build();
            InviteProjectResource projectInviteNoEmailResource = getMapper(InviteProjectMapper.class).mapToResource(projectInviteNoEmail);
            when(inviteProjectMapperMock.mapToDomain(projectInviteNoEmailResource)).thenReturn(projectInviteNoEmail);
            ServiceResult<Void> result = inviteProjectService.saveProjectInvite(projectInviteNoEmailResource);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(badRequestError("The Invite is not valid")));
        }

        {
            ProjectInvite projectInviteNoOrganisation = newInvite().withProject(project).withName("project name").withEmailAddress(user.getEmail()).build();
            InviteProjectResource projectInviteNoOrganisationResource = getMapper(InviteProjectMapper.class).mapToResource(projectInviteNoOrganisation);
            when(inviteProjectMapperMock.mapToDomain(projectInviteNoOrganisationResource)).thenReturn(projectInviteNoOrganisation);
            ServiceResult<Void> result = inviteProjectService.saveProjectInvite(projectInviteNoOrganisationResource);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(badRequestError("The Invite is not valid")));
        }

        {
            ProjectInvite projectInviteNoProject = newInvite().withOrganisation(organisation).withName("project name").withEmailAddress(user.getEmail()).build();
            InviteProjectResource projectInviteNoProjectResource = getMapper(InviteProjectMapper.class).mapToResource(projectInviteNoProject);
            when(inviteProjectMapperMock.mapToDomain(projectInviteNoProjectResource)).thenReturn(projectInviteNoProject);
            ServiceResult<Void> result = inviteProjectService.saveProjectInvite(projectInviteNoProjectResource);
            assertTrue(result.isFailure());
            assertTrue(result.getFailure().is(badRequestError("The Invite is not valid")));
        }
    }

    @Test
    public void testGetInvitesByProject() throws Exception {
        Project project = newProject().build();
        ProjectInvite inviteProject = newInvite().build();
        InviteProjectResource inviteProjectResource = getMapper(InviteProjectMapper.class).mapToResource(inviteProject);
        when(inviteProjectRepositoryMock.findByProjectId(project.getId())).thenReturn(asList(inviteProject));
        when(inviteProjectMapperMock.mapToResource(asList(inviteProject))).thenReturn(asList(inviteProjectResource));
        ServiceResult<List<InviteProjectResource>> invitesByProject = inviteProjectService.getInvitesByProject(project.getId());
        assertTrue(invitesByProject.isSuccess());
        assertEquals(asList(inviteProjectResource), invitesByProject.getSuccessObject());
    }
}
