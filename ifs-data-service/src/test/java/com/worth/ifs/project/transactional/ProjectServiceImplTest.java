package com.worth.ifs.project.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.project.builder.MonitoringOfficerBuilder;
import com.worth.ifs.project.domain.MonitoringOfficer;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.address.builder.AddressTypeBuilder.newAddressType;
import static com.worth.ifs.address.resource.OrganisationAddressType.*;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectServiceImplTest extends BaseServiceUnitTest<ProjectService> {

	private Long projectId = 123L;
    private Long applicationId = 456L;
	private Long userId = 7L;
	private Long otherUserId = 8L;

	private Application application;
	private Organisation organisation;
	private Role leadApplicantRole;
    private Role projectManagerRole;
    private Role partnerRole;
    private User user;
	private ProcessRole leadApplicantProcessRole;
    private ProjectUser leadPartnerProjectUser;
	private Project project;
    private MonitoringOfficerResource monitoringOfficerResource;

	@Before
	public void setUp() {

        organisation = newOrganisation().build();

        leadApplicantRole = newRole(LEADAPPLICANT).build();
        projectManagerRole = newRole(PROJECT_MANAGER).build();
        partnerRole = newRole(PARTNER).build();

    	user = newUser().
    			withid(userId).
    			build();

    	leadApplicantProcessRole = newProcessRole().
    			withOrganisation(organisation).
    			withRole(leadApplicantRole).
    			withUser(user).
    			build();

        leadPartnerProjectUser = newProjectUser().
                withOrganisation(organisation).
                withRole(partnerRole).
                withUser(user).
                build();

    	application = newApplication().
				withId(applicationId).
	            withProcessRoles(leadApplicantProcessRole).
                withName("My Application").
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                build();

    	project = newProject().
                withId(projectId).
                withApplication(application).
                withProjectUsers(singletonList(leadPartnerProjectUser)).
                build();

        monitoringOfficerResource = newMonitoringOfficerResource()
                .withProject(1L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
	}
	
    @Test
    public void testCreateProjectFromApplication() {

        Role partnerRole = newRole().withType(PARTNER).build();

        ProjectResource newProjectResource = newProjectResource().build();

        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);

        Project savedProject = newProject().build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);

        Project newProjectExpectations = createProjectExpectationsFromOriginalApplication(application);
        when(projectRepositoryMock.save(newProjectExpectations)).thenReturn(savedProject);

        when(projectMapperMock.mapToResource(savedProject)).thenReturn(newProjectResource);

        ServiceResult<ProjectResource> project = service.createProjectFromApplication(applicationId);
        assertTrue(project.isSuccess());
        assertEquals(newProjectResource, project.getSuccessObject());
    }
    
    @Test
    public void testInvalidProjectManagerProvided() {

        ServiceResult<Void> result = service.setProjectManager(projectId, otherUserId);
        assertFalse(result.isSuccess());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER));
    }

    @Test
    public void testSetProjectManagerWhenProjectDetailsAlreadySubmitted() {

        Project existingProject = newProject().withSubmittedDate(LocalDateTime.now()).build();

        assertTrue(existingProject.getProjectUsers().isEmpty());

        when(projectRepositoryMock.findOne(projectId)).thenReturn(existingProject);

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED));

        assertTrue(existingProject.getProjectUsers().isEmpty());
    }
    
    @Test
    public void testValidProjectManagerProvided() {

        when(roleRepositoryMock.findOneByName(PROJECT_MANAGER.getName())).thenReturn(projectManagerRole);

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isSuccess());

        ProjectUser expectedProjectManager = newProjectUser().
                withId().
                withProject(project).
                withOrganisation(organisation).
                withRole(projectManagerRole).
                withUser(user).
                build();

        assertEquals(expectedProjectManager, project.getProjectUsers().get(project.getProjectUsers().size() - 1));
    }

    @Test
    public void testValidProjectManagerProvidedWithExistingProjectManager() {

        User differentUser = newUser().build();
        Organisation differentOrganisation = newOrganisation().build();

        @SuppressWarnings("unused")
        ProjectUser existingProjenullctManager = newProjectUser().
                withId(456L).
                withProject(project).
                withRole(projectManagerRole).
                withOrganisation(differentOrganisation).
                withUser(differentUser).
                build();

        when(roleRepositoryMock.findOneByName(PROJECT_MANAGER.getName())).thenReturn(projectManagerRole);

        ServiceResult<Void> result = service.setProjectManager(projectId, userId);
        assertTrue(result.isSuccess());

        ProjectUser expectedProjectManager = newProjectUser().
                withId(456L).
                withProject(project).
                withOrganisation(organisation).
                withRole(projectManagerRole).
                withUser(user).
                build();

        assertEquals(expectedProjectManager, project.getProjectUsers().get(project.getProjectUsers().size() - 1));
    }

    @Test
    public void testUpdateProjectStartDate() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isSuccess());

        verify(projectRepositoryMock).findOne(123L);
        assertEquals(validDate, existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateButProjectDoesntExist() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        when(projectRepositoryMock.findOne(123L)).thenReturn(null);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(notFoundError(Project.class, 123L)));
    }

    @Test
    public void testUpdateProjectStartDateButStartDateDoesntBeginOnFirstDayOfMonth() {

        LocalDate now = LocalDate.now();
        LocalDate dateNotOnFirstDayOfMonth = LocalDate.of(now.getYear(), now.getMonthValue(), 2).plusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, dateNotOnFirstDayOfMonth);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));

        verify(projectRepositoryMock, never()).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateButStartDateNotInFuture() {

        LocalDate now = LocalDate.now();
        LocalDate pastDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).minusMonths(1);

        Project existingProject = newProject().build();
        assertNull(existingProject.getTargetStartDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, pastDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));

        verify(projectRepositoryMock, never()).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateProjectStartDateWhenProjectDetailsAlreadySubmitted() {

        LocalDate now = LocalDate.now();
        LocalDate validDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        Project existingProject = newProject().withSubmittedDate(LocalDateTime.now()).build();
        assertNull(existingProject.getTargetStartDate());
        assertNotNull(existingProject.getSubmittedDate());

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);

        ServiceResult<Void> updateResult = service.updateProjectStartDate(123L, validDate);
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED));

        verify(projectRepositoryMock).findOne(123L);
        assertNull(existingProject.getTargetStartDate());
    }

    @Test
    public void testUpdateFinanceContact() {

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withid(7L).build();

        Role partnerRole = newRole().withType(PARTNER).build();
        newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(partnerRole).build();

        Role financeContactRole = newRole().withType(FINANCE_CONTACT).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(roleRepositoryMock.findOneByName(FINANCE_CONTACT.getName())).thenReturn(financeContactRole);

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, 7L);

        assertTrue(updateResult.isSuccess());

        List<ProjectUser> foundFinanceContacts = simpleFilter(project.getProjectUsers(), projectUser ->
                projectUser.getOrganisation().equals(organisation) &&
                        projectUser.getUser().equals(user) &&
                        projectUser.getProject().equals(project) &&
                        projectUser.getRole().equals(financeContactRole));

        assertEquals(1, foundFinanceContacts.size());
    }

    @Test
    public void testUpdateFinanceContactWithExistingFinanceContactChosenForSameOrganisation() {

        Role partnerRole = newRole().withType(PARTNER).build();
        Role financeContactRole = newRole().withType(FINANCE_CONTACT).build();

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();

        User newFinanceContactUser = newUser().withid(7L).build();
        newProjectUser().withOrganisation(organisation).withUser(newFinanceContactUser).withProject(project).withRole(partnerRole).build();

        User existingFinanceContactUser = newUser().withid(9999L).build();
        newProjectUser().withOrganisation(organisation).withUser(existingFinanceContactUser).withProject(project).withRole(partnerRole).build();
        newProjectUser().withOrganisation(organisation).withUser(existingFinanceContactUser).withProject(project).withRole(financeContactRole).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(roleRepositoryMock.findOneByName(FINANCE_CONTACT.getName())).thenReturn(financeContactRole);

        List<ProjectUser> existingFinanceContactForOrganisation = simpleFilter(project.getProjectUsers(), projectUser ->
                projectUser.getOrganisation().equals(organisation) &&
                        projectUser.getProject().equals(project) &&
                        projectUser.getRole().equals(financeContactRole));

        assertEquals(1, existingFinanceContactForOrganisation.size());

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, 7L);

        assertTrue(updateResult.isSuccess());

        List<ProjectUser> foundFinanceContacts = simpleFilter(project.getProjectUsers(), projectUser ->
                projectUser.getOrganisation().equals(organisation) &&
                        projectUser.getUser().equals(newFinanceContactUser) &&
                        projectUser.getProject().equals(project) &&
                        projectUser.getRole().equals(financeContactRole));

        assertEquals(1, foundFinanceContacts.size());
    }

    @Test
    public void testUpdateFinanceContactButUserIsNotExistingPartner() {

        Role projectManagerRole = newRole().withType(PROJECT_MANAGER).build();

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withid(7L).build();
        newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(projectManagerRole).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, 7L);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION));

        verify(processRoleRepositoryMock, never()).save(isA(ProcessRole.class));
    }

    @Test
    public void testUpdateFinanceContactWhenNotPresentOnTheProject() {

        long userIdForUserNotOnProject = 6L;

        Role partnerRole = newRole().withType(PARTNER).build();

        Project existingProject = newProject().withId(123L).build();
        Project anotherProject = newProject().withId(9999L).build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(existingProject);
        
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withid(7L).build();
        newProjectUser().withOrganisation(organisation).withUser(user).withProject(anotherProject).withRole(partnerRole).build();

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, userIdForUserNotOnProject);
        
        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION));
    }

    @Test
    public void testUpdateFinanceContactWhenProjectDetailsAlreadySubmitted() {

        Project project = newProject().withId(123L).withSubmittedDate(LocalDateTime.now()).build();

        assertTrue(project.getProjectUsers().isEmpty());

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);

        ServiceResult<Void> updateResult = service.updateFinanceContact(123L, 5L, 7L);

        assertTrue(updateResult.isFailure());
        assertTrue(updateResult.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED));

        verify(projectRepositoryMock).findOne(123L);
        assertTrue(project.getProjectUsers().isEmpty());
    }

    @Test
    public void testFindByUserIdReturnsOnlyDistinctProjects(){

        Project project = newProject().withId(123L).build();
        Organisation organisation = newOrganisation().withId(5L).build();
        User user = newUser().withid(7L).build();

        Role partnerRole = newRole().withType(PARTNER).build();
        Role financeContactRole = newRole().withType(FINANCE_CONTACT).build();

        ProjectUser projectUserWithPartnerRole = newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(partnerRole).build();
        ProjectUser projectUserWithFinanceRole = newProjectUser().withOrganisation(organisation).withUser(user).withProject(project).withRole(financeContactRole).build();

        List<ProjectUser> projectUserRecords = asList(projectUserWithPartnerRole, projectUserWithFinanceRole);

        ProjectResource projectResource = newProjectResource().withId(project.getId()).build();

        when(projectUserRepositoryMock.findByUserId(user.getId())).thenReturn(projectUserRecords);

        when(projectMapperMock.mapToResource(project)).thenReturn(projectResource);

        ServiceResult<List<ProjectResource>> result = service.findByUserId(user.getId());

        assertTrue(result.isSuccess());

        assertEquals(result.getSuccessObject().size(), 1L);
    }

    @Test
    public void testUpdateProjectAddressToBeRegisteredAddress(){

        Project project = newProject().withId(1L).build();
        Organisation leadOrganisation = newOrganisation().withId(1L).build();
        AddressResource existingRegisteredAddressResource = newAddressResource().build();
        Address registeredAddress = newAddress().build();

        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(addressRepositoryMock.exists(existingRegisteredAddressResource.getId())).thenReturn(true);
        when(addressRepositoryMock.findOne(existingRegisteredAddressResource.getId())).thenReturn(registeredAddress);

        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), REGISTERED, existingRegisteredAddressResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateProjectAddressToBeOperatingAddress(){
        Project project = newProject().withId(1L).build();
        Organisation leadOrganisation = newOrganisation().withId(1L).build();
        AddressResource existingOperatingAddressResource = newAddressResource().build();
        Address operatingAddress = newAddress().build();

        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(addressRepositoryMock.exists(existingOperatingAddressResource.getId())).thenReturn(true);
        when(addressRepositoryMock.findOne(existingOperatingAddressResource.getId())).thenReturn(operatingAddress);

        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), OPERATING, existingOperatingAddressResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateProjectAddressToNewProjectAddress(){
        Project project = newProject().withId(1L).build();
        Organisation leadOrganisation = newOrganisation().withId(1L).build();
        AddressResource newAddressResource = newAddressResource().build();
        Address newAddress = newAddress().build();
        AddressType projectAddressType = newAddressType().withId((long)PROJECT.getOrdinal()).withName(PROJECT.name()).build();
        OrganisationAddress organisationAddress = newOrganisationAddress().withOrganisation(leadOrganisation).withAddress(newAddress).withAddressType(projectAddressType).build();

        when(projectRepositoryMock.findOne(project.getId())).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation);
        when(addressRepositoryMock.exists(newAddressResource.getId())).thenReturn(false);
        when(addressMapperMock.mapToDomain(newAddressResource)).thenReturn(newAddress);
        when(addressTypeRepositoryMock.findOne((long)PROJECT.getOrdinal())).thenReturn(projectAddressType);
        when(organisationAddressRepositoryMock.findByOrganisationIdAndAddressType(leadOrganisation.getId(), projectAddressType)).thenReturn(emptyList());
        when(organisationAddressRepositoryMock.save(organisationAddress)).thenReturn(organisationAddress);

        ServiceResult<Void> result = service.updateProjectAddress(leadOrganisation.getId(), project.getId(), PROJECT, newAddressResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveProjectSubmitDateTimeIsSuccessfulWhenAllProjectDetailsHaveBeenProvided(){
        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();
        Organisation organisation3 = newOrganisation().build();

        Role projectManagerRole = newRole().withType(PROJECT_MANAGER).build();

        ProjectUser projectManagerProjectUser = newProjectUser().withRole(projectManagerRole).build();
        Address address = newAddress().build();
        Project project = newProject().withId(1L).withAddress(address).withProjectUsers(singletonList(projectManagerProjectUser)).withTargetStartDate(LocalDate.now()).build();

        Role financeContactRole = newRole().withType(FINANCE_CONTACT).build();
        Role partnerRole = newRole().withType(PARTNER).build();

        List<ProjectUser> projectUserObjs;

        ProjectUser projectUser1WithPartnerRole = newProjectUser().withProject(project).withOrganisation(organisation1).withRole(partnerRole).build();
        ProjectUser projectUser1WithFinanceRole = newProjectUser().withProject(project).withOrganisation(organisation1).withRole(financeContactRole).build();
        ProjectUser projectUser2WithPartnerRole = newProjectUser().withProject(project).withOrganisation(organisation2).withRole(partnerRole).build();
        ProjectUser projectUser2WithFinanceRole = newProjectUser().withProject(project).withOrganisation(organisation2).withRole(financeContactRole).build();
        ProjectUser projectUser3WithPartnerRole = newProjectUser().withProject(project).withOrganisation(organisation3).withRole(partnerRole).build();
        ProjectUser projectUser3WithFinanceRole = newProjectUser().withProject(project).withOrganisation(organisation3).withRole(financeContactRole).build();

        ProjectUserResource projectUser1WithPartnerRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation1.getId()).withRole(partnerRole.getId()).withRoleName(PARTNER.getName()).build();
        ProjectUserResource projectUser1WithFinanceRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation1.getId()).withRole(financeContactRole.getId()).withRoleName(FINANCE_CONTACT.getName()).build();
        ProjectUserResource projectUser2WithPartnerRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation2.getId()).withRole(partnerRole.getId()).withRoleName(PARTNER.getName()).build();
        ProjectUserResource projectUser2WithFinanceRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation2.getId()).withRole(financeContactRole.getId()).withRoleName(FINANCE_CONTACT.getName()).build();
        ProjectUserResource projectUser3WithPartnerRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation3.getId()).withRole(partnerRole.getId()).withRoleName(PARTNER.getName()).build();
        ProjectUserResource projectUser3WithFinanceRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation3.getId()).withRole(partnerRole.getId()).withRoleName(FINANCE_CONTACT.getName()).build();
        ProjectUserResource projectManagerProjectUserResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation3.getId()).withRole(projectManagerRole.getId()).withRoleName(PROJECT_MANAGER.getName()).build();

        projectUserObjs = asList(projectManagerProjectUser, projectUser1WithPartnerRole, projectUser1WithFinanceRole, projectUser2WithPartnerRole, projectUser2WithFinanceRole, projectUser3WithPartnerRole, projectUser3WithFinanceRole);

        when(projectRepositoryMock.findOne(1L)).thenReturn(project);
        when(projectUserRepositoryMock.findByProjectId(1L)).thenReturn(projectUserObjs);
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(organisationRepositoryMock.findOne(organisation2.getId())).thenReturn(organisation2);
        when(organisationRepositoryMock.findOne(organisation3.getId())).thenReturn(organisation3);

        when(projectUserMapperMock.mapToResource(projectUser1WithFinanceRole)).thenReturn(projectUser1WithFinanceRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser1WithPartnerRole)).thenReturn(projectUser1WithPartnerRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser2WithPartnerRole)).thenReturn(projectUser2WithPartnerRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser2WithFinanceRole)).thenReturn(projectUser2WithFinanceRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser3WithPartnerRole)).thenReturn(projectUser3WithPartnerRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser3WithFinanceRole)).thenReturn(projectUser3WithFinanceRoleResource);
        when(projectUserMapperMock.mapToResource(projectManagerProjectUser)).thenReturn(projectManagerProjectUserResource);

        ServiceResult result = service.saveProjectSubmitDateTime(1L, LocalDateTime.now());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveProjectSubmitDateTimeIsUnSuccessfulWhenAFinanceContactIsMissing(){
        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();
        Organisation organisation3 = newOrganisation().build();

        Role projectManagerRole = newRole().withType(PROJECT_MANAGER).build();

        ProjectUser projectManagerProjectUser = newProjectUser().withRole(projectManagerRole).build();
        Address address = newAddress().build();
        Project project = newProject().withId(1L).withAddress(address).withProjectUsers(singletonList(projectManagerProjectUser)).withTargetStartDate(LocalDate.now()).build();

        Role financeContactRole = newRole().withType(FINANCE_CONTACT).build();
        Role partnerRole = newRole().withType(PARTNER).build();

        List<ProjectUser> projectUserObjs;

        ProjectUser projectUser1WithPartnerRole = newProjectUser().withProject(project).withOrganisation(organisation1).withRole(partnerRole).build();
        ProjectUser projectUser1WithFinanceRole = newProjectUser().withProject(project).withOrganisation(organisation1).withRole(financeContactRole).build();
        ProjectUser projectUser2WithPartnerRole = newProjectUser().withProject(project).withOrganisation(organisation2).withRole(partnerRole).build();
        ProjectUser projectUser2WithFinanceRole = newProjectUser().withProject(project).withOrganisation(organisation2).withRole(financeContactRole).build();
        ProjectUser projectUserWithPartnerRole = newProjectUser().withProject(project).withOrganisation(organisation3).withRole(partnerRole).build();

        ProjectUserResource projectUser1WithPartnerRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation1.getId()).withRole(partnerRole.getId()).withRoleName(PARTNER.getName()).build();
        ProjectUserResource projectUser1WithFinanceRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation1.getId()).withRole(financeContactRole.getId()).withRoleName(FINANCE_CONTACT.getName()).build();
        ProjectUserResource projectUser2WithPartnerRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation2.getId()).withRole(partnerRole.getId()).withRoleName(PARTNER.getName()).build();
        ProjectUserResource projectUser2WithFinanceRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation2.getId()).withRole(financeContactRole.getId()).withRoleName(FINANCE_CONTACT.getName()).build();
        ProjectUserResource projectUserWithPartnerRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation3.getId()).withRole(partnerRole.getId()).withRoleName(PARTNER.getName()).build();

        projectUserObjs = asList(projectUser1WithPartnerRole, projectUser1WithFinanceRole, projectUser2WithPartnerRole, projectUser2WithFinanceRole, projectUserWithPartnerRole);

        when(projectRepositoryMock.findOne(1L)).thenReturn(project);
        when(projectUserRepositoryMock.findByProjectId(1L)).thenReturn(projectUserObjs);
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(organisationRepositoryMock.findOne(organisation2.getId())).thenReturn(organisation2);
        when(organisationRepositoryMock.findOne(organisation3.getId())).thenReturn(organisation3);

        when(projectUserMapperMock.mapToResource(projectUser1WithFinanceRole)).thenReturn(projectUser1WithFinanceRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser1WithPartnerRole)).thenReturn(projectUser1WithPartnerRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser2WithPartnerRole)).thenReturn(projectUser2WithPartnerRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser2WithFinanceRole)).thenReturn(projectUser2WithFinanceRoleResource);
        when(projectUserMapperMock.mapToResource(projectUserWithPartnerRole)).thenReturn(projectUserWithPartnerRoleResource);

        ServiceResult<Void> result = service.saveProjectSubmitDateTime(1L, LocalDateTime.now());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_SUBMITTED_IF_INCOMPLETE));
    }

    @Test
    public void testCannotSaveProjectSubmitDateTimeWhenNotAllProjectDetailsHaveBeenProvided() {
        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();
        Organisation organisation3 = newOrganisation().build();

        Role projectManagerRole = newRole().withType(PROJECT_MANAGER).build();

        ProjectUser projectManagerProjectUser = newProjectUser().withRole(projectManagerRole).build();
        Address address = newAddress().build();
        Project project = newProject().withId(1L).withAddress(address).withProjectUsers(singletonList(projectManagerProjectUser)).build();


        Role financeContactRole = newRole().withType(FINANCE_CONTACT).build();
        Role partnerRole = newRole().withType(PARTNER).build();

        List<ProjectUser> projectUserObjs;

        ProjectUser projectUser1WithPartnerRole = newProjectUser().withProject(project).withOrganisation(organisation1).withRole(partnerRole).build();
        ProjectUser projectUser1WithFinanceRole = newProjectUser().withProject(project).withOrganisation(organisation1).withRole(financeContactRole).build();
        ProjectUser projectUser2WithPartnerRole = newProjectUser().withProject(project).withOrganisation(organisation2).withRole(partnerRole).build();
        ProjectUser projectUser2WithFinanceRole = newProjectUser().withProject(project).withOrganisation(organisation2).withRole(financeContactRole).build();
        ProjectUser projectUserWithPartnerRole = newProjectUser().withProject(project).withOrganisation(organisation3).withRole(partnerRole).build();

        ProjectUserResource projectUser1WithPartnerRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation1.getId()).withRole(partnerRole.getId()).withRoleName(PARTNER.getName()).build();
        ProjectUserResource projectUser1WithFinanceRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation1.getId()).withRole(financeContactRole.getId()).withRoleName(FINANCE_CONTACT.getName()).build();
        ProjectUserResource projectUser2WithPartnerRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation2.getId()).withRole(partnerRole.getId()).withRoleName(PARTNER.getName()).build();
        ProjectUserResource projectUser2WithFinanceRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation2.getId()).withRole(financeContactRole.getId()).withRoleName(FINANCE_CONTACT.getName()).build();
        ProjectUserResource projectUserWithPartnerRoleResource = newProjectUserResource().withProject(project.getId()).withOrganisation(organisation3.getId()).withRole(partnerRole.getId()).withRoleName(PARTNER.getName()).build();

        projectUserObjs = asList(projectUser1WithPartnerRole, projectUser1WithFinanceRole, projectUser2WithPartnerRole, projectUser2WithFinanceRole, projectUserWithPartnerRole);

        when(projectRepositoryMock.findOne(1L)).thenReturn(project);
        when(projectUserRepositoryMock.findByProjectId(1L)).thenReturn(projectUserObjs);
        when(organisationRepositoryMock.findOne(organisation1.getId())).thenReturn(organisation1);
        when(organisationRepositoryMock.findOne(organisation2.getId())).thenReturn(organisation2);
        when(organisationRepositoryMock.findOne(organisation3.getId())).thenReturn(organisation3);

        when(projectUserMapperMock.mapToResource(projectUser1WithFinanceRole)).thenReturn(projectUser1WithFinanceRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser1WithPartnerRole)).thenReturn(projectUser1WithPartnerRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser2WithPartnerRole)).thenReturn(projectUser2WithPartnerRoleResource);
        when(projectUserMapperMock.mapToResource(projectUser2WithFinanceRole)).thenReturn(projectUser2WithFinanceRoleResource);
        when(projectUserMapperMock.mapToResource(projectUserWithPartnerRole)).thenReturn(projectUserWithPartnerRoleResource);

        ServiceResult<Void> result = service.saveProjectSubmitDateTime(1L, LocalDateTime.now());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_SUBMITTED_IF_INCOMPLETE));
    }

    @Test
    public void testSaveMOWithDiffProjectIdInURLAndMOResource() {

        Long projectid = 1L;

        MonitoringOfficerResource monitoringOfficerResource = newMonitoringOfficerResource()
                .withProject(3L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();

        ServiceResult<Void> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE));
    }

    @Test
    public void testSaveMOWhenProjectDetailsNotYetSubmitted() {

        Long projectid = 1L;

        Project projectInDB = newProject().withId(1L).build();

        when(projectRepositoryMock.findOne(projectid)).thenReturn(projectInDB);

        ServiceResult<Void> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        assertTrue(result.getFailure().is(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED));
    }

    @Test
    public void testSaveMOWhenMOExistsForAProject() {

        Long projectid = 1L;

        // Set this to different values, so that we can assert that it gets updated
        MonitoringOfficer monitoringOfficerInDB = MonitoringOfficerBuilder.newMonitoringOfficer()
                .withFirstName("def")
                .withLastName("klm")
                .withEmail("def.klm@gmail.com")
                .withPhoneNumber("079237439")
                .build();


        Project projectInDB = newProject().withId(1L).withSubmittedDate(LocalDateTime.now()).build();

        when(projectRepositoryMock.findOne(projectid)).thenReturn(projectInDB);

        when(monitoringOfficerRepository.findOneByProjectId(monitoringOfficerResource.getProject())).thenReturn(monitoringOfficerInDB);

        ServiceResult<Void> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        // Assert that the MO in DB is updated with the correct values from MO Resource
        Assert.assertEquals("First name of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getFirstName(), monitoringOfficerResource.getFirstName());
        Assert.assertEquals("Last name of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getLastName(), monitoringOfficerResource.getLastName());
        Assert.assertEquals("Email of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getEmail(), monitoringOfficerResource.getEmail());
        Assert.assertEquals("Phone number of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getPhoneNumber(), monitoringOfficerResource.getPhoneNumber());

        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveMOWhenMODoesNotExistForAProject() {

        Long projectid = 1L;

        Project projectInDB = newProject().withId(1L).withSubmittedDate(LocalDateTime.now()).build();

        when(projectRepositoryMock.findOne(projectid)).thenReturn(projectInDB);

        when(monitoringOfficerRepository.findOneByProjectId(monitoringOfficerResource.getProject())).thenReturn(null);

        ServiceResult<Void> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetMonitoringOfficerWhenMODoesNotExistInDB() {

        Long projectid = 1L;

        ServiceResult<MonitoringOfficerResource> result = service.getMonitoringOfficer(projectid);

        String errorKey = result.getFailure().getErrors().get(0).getErrorKey();
        Assert.assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.name(), errorKey);
    }

    @Test
    public void testGetMonitoringOfficerWhenMOExistsInDB() {

        Long projectid = 1L;

        MonitoringOfficer monitoringOfficerInDB = MonitoringOfficerBuilder.newMonitoringOfficer()
                .withFirstName("def")
                .withLastName("klm")
                .withEmail("def.klm@gmail.com")
                .withPhoneNumber("079237439")
                .build();

        when(monitoringOfficerRepository.findOneByProjectId(projectid)).thenReturn(monitoringOfficerInDB);

        ServiceResult<MonitoringOfficerResource> result = service.getMonitoringOfficer(projectid);

        assertTrue(result.isSuccess());

    }

    private Project createProjectExpectationsFromOriginalApplication(Application application) {

        assertFalse(application.getProcessRoles().isEmpty());

        return createLambdaMatcher(project -> {
            assertEquals(application.getName(), project.getName());
            assertEquals(application.getDurationInMonths(), project.getDurationInMonths());
            assertEquals(application.getStartDate(), project.getTargetStartDate());
            assertFalse(project.getProjectUsers().isEmpty());
            assertNull(project.getAddress());

            List<ProcessRole> collaborativeRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);

            assertEquals(collaborativeRoles.size(), project.getProjectUsers().size());

            collaborativeRoles.forEach(processRole -> {

                List<ProjectUser> matchingProjectUser = simpleFilter(project.getProjectUsers(), projectUser ->
                        projectUser.getOrganisation().equals(processRole.getOrganisation()) &&
                           projectUser.getUser().equals(processRole.getUser()));

                assertEquals(1, matchingProjectUser.size());
                assertEquals(PARTNER.getName(), matchingProjectUser.get(0).getRole().getName());
                assertEquals(project, matchingProjectUser.get(0).getProject());
            });
        });
    }

    private ProcessRole createProcessRoleExpectations(ProcessRole expectedProcessRole) {

        return createLambdaMatcher(processRole -> {
            assertEquals(expectedProcessRole.getApplication().getId(), processRole.getApplication().getId());
            assertEquals(expectedProcessRole.getOrganisation().getId(), processRole.getOrganisation().getId());
            assertEquals(expectedProcessRole.getRole().getId(), processRole.getRole().getId());
            assertEquals(expectedProcessRole.getUser().getId(), processRole.getUser().getId());
        });
    }

    @Override
    protected ProjectService supplyServiceUnderTest() {
        return new ProjectServiceImpl();
    }
}
