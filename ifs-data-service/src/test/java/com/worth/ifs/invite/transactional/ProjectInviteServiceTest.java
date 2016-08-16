package com.worth.ifs.invite.transactional;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.mapper.InviteOrganisationMapper;
import com.worth.ifs.invite.mapper.InviteProjectMapper;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.project.builder.ProjectBuilder;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.ProjectInviteBuilder.newInvite;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertTrue;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class ProjectInviteServiceTest extends BaseUnitTestMocksTest {
    private final Log log = LogFactory.getLog(getClass());

    @Mock
    NotificationService notificationService;
    @Mock
    InviteProjectMapper inviteProjectMapperMock;
    @Mock
    InviteOrganisationMapper inviteOrganisationMapper;

    @InjectMocks
    private InviteProjectService inviteProjectService = new InviteProjectServiceImpl();
    private LocalValidatorFactoryBean localValidatorFactory;


    @Before
    public void setup() {
        when(applicationInviteRepositoryMock.save(any(ApplicationInvite.class))).thenReturn(new ApplicationInvite());
        ServiceResult<Void> result = serviceSuccess();
        when(notificationService.sendNotification(any(), eq(NotificationMedium.EMAIL))).thenReturn(result);

        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    @Test
    public void testAcceptProjectInviteSuccess() throws Exception {
        User user = newUser().withEmailAddress("email@example.com").build();
        ProjectInvite projectInvite = newInvite().withEmailAddress(user.getEmail()).withHash("hash").build();
        when(inviteProjectRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findOne(user.getId())).thenReturn(user);
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
        ServiceResult<Void> result = inviteProjectService.checkUserExistingByInviteHash(projectInvite.getHash());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testCheckUserExistingByInviteHashHashNotFound() throws Exception {
        String hash = "hash";
        when(inviteProjectRepositoryMock.getByHash(hash)).thenReturn(null);
        ServiceResult<Void> result = inviteProjectService.checkUserExistingByInviteHash(hash);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(ProjectInvite.class, hash)));
    }

    @Test
    public void testCheckUserExistingByInviteHashNoUserFound() throws Exception {
        ProjectInvite projectInvite = newInvite().withEmailAddress("email@example.com").withHash("hash").build();
        when(inviteProjectRepositoryMock.getByHash(projectInvite.getHash())).thenReturn(projectInvite);
        when(userRepositoryMock.findByEmail(projectInvite.getEmail())).thenReturn(empty());
        ServiceResult<Void> result = inviteProjectService.checkUserExistingByInviteHash(projectInvite.getHash());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class)));
    }


    @Test
    public void testSaveFinanceContactInviteSuccess() throws Exception {
        Organisation organisation = newOrganisation().build();
        Project project = ProjectBuilder.newProject().withName("project name").build();
        User user = newUser().withEmailAddress("email@example.com").build();
        ProjectInvite projectInvite = newInvite().withProject(project).withOrganisation(organisation).withName("project name").withEmailAddress(user.getEmail()).build();
        InviteProjectResource inviteProjectResource = getMapper(InviteProjectMapper.class).mapToResource(projectInvite);
        when(inviteProjectMapperMock.mapToDomain(inviteProjectResource)).thenReturn(projectInvite);
        ServiceResult<Void> result = inviteProjectService.saveFinanceContactInvite(inviteProjectResource);
        assertTrue(result.isSuccess());
    }
}