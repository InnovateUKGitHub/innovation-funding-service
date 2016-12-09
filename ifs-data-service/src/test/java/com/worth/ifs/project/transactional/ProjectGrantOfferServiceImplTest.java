package com.worth.ifs.project.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.builder.PartnerOrganisationBuilder;
import com.worth.ifs.project.domain.PartnerOrganisation;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserRoleType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Supplier;

import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Collections.singletonList;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class ProjectGrantOfferServiceImplTest extends BaseServiceUnitTest<ProjectGrantOfferService> {

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;

    private Application application;
    private Organisation organisation;
    private Role leadApplicantRole;
    private Role projectManagerRole;
    private User user;
    private ProcessRole leadApplicantProcessRole;
    private ProjectUser leadPartnerProjectUser;
    private FileEntryResource fileEntryResource;
    private Project project;

    @Before
    public void setUp() {

        organisation = newOrganisation().build();

        Competition competition = newCompetition().build();

        Address address = newAddress().withAddressLine1("test1")
                .withAddressLine2("test2")
                .withPostcode("PST")
                .withTown("town").build();

        leadApplicantRole = newRole(UserRoleType.LEADAPPLICANT).build();
        projectManagerRole = newRole(UserRoleType.PROJECT_MANAGER).build();

        user = newUser().
                withId(userId).
                build();

        leadApplicantProcessRole = newProcessRole().
                withOrganisation(organisation).
                withRole(leadApplicantRole).
                withUser(user).
                build();

        leadPartnerProjectUser = newProjectUser().
                withOrganisation(organisation).
                withRole(PROJECT_PARTNER).
                withUser(user).
                build();

        application = newApplication().
                withId(applicationId).
                withCompetition(competition).
                withProcessRoles(leadApplicantProcessRole).
                withName("My Application").
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                build();

        project = newProject().
                withId(projectId).
                withAddress(address).
                withApplication(application).
                withProjectUsers(singletonList(leadPartnerProjectUser)).
                build();
        fileEntryResource = newFileEntryResource().
                withFilesizeBytes(1024).
                withMediaType("application/pdf").
                withName("grant_offer_letter").
                build();


        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
    }

    @Test
    public void testCreateSignedGrantOfferLetterFileEntry() {
        assertCreateFile(
                project::getSignedGrantOfferLetter,
                (fileToCreate, inputStreamSupplier) ->
                        service.createSignedGrantOfferLetterFileEntry(123L, fileToCreate, inputStreamSupplier));
    }

    @Test
    public void testCreateGrantOfferLetterFileEntry() {
        assertCreateFile(
                project::getGrantOfferLetter,
                (fileToCreate, inputStreamSupplier) ->
                        service.createGrantOfferLetterFileEntry(123L, fileToCreate, inputStreamSupplier));
    }


    @Test
    public void testCreateAdditionalContractFileEntry() {
        assertCreateFile(
                project::getAdditionalContractFile,
                (fileToCreate, inputStreamSupplier) ->
                        service.createAdditionalContractFileEntry(123L, fileToCreate, inputStreamSupplier));
    }


    @Test
    public void testGetAdditionalContractFileEntryDetails() {
        assertGetFileDetails(
                project::setAdditionalContractFile,
                () -> service.getAdditionalContractFileEntryDetails(123L));
    }

    @Test
    public void testGetGrantOfferLetterFileEntryDetails() {
        assertGetFileDetails(
                project::setGrantOfferLetter,
                () -> service.getGrantOfferLetterFileEntryDetails(123L));
    }

    @Test
    public void testGetSignedGrantOfferLetterFileEntryDetails() {
        assertGetFileDetails(
                project::setSignedGrantOfferLetter,
                () -> service.getSignedGrantOfferLetterFileEntryDetails(123L));
    }

    @Test
    public void testGetAdditionalContractFileContents() {
        assertGetFileContents(
                project::setAdditionalContractFile,
                () -> service.getAdditionalContractFileAndContents(123L));
    }

    @Test
    public void testGetGrantOfferLetterFileContents() {
        assertGetFileContents(
                project::setGrantOfferLetter,
                () -> service.getGrantOfferLetterFileAndContents(123L));
    }

    @Test
    public void testGetSignedGrantOfferLetterFileContents() {
        assertGetFileContents(
                project::setSignedGrantOfferLetter,
                () -> service.getSignedGrantOfferLetterFileAndContents(123L));
    }

    @Test
    public void testUpdateSignedGrantOfferLetterFileEntry() {
        assertUpdateFile(
                project::getSignedGrantOfferLetter,
                (fileToUpdate, inputStreamSupplier) ->
                        service.updateSignedGrantOfferLetterFile(123L, fileToUpdate, inputStreamSupplier));
    }

    @Test
    public void testSubmitGrantOfferLetterFailure() {

        ServiceResult<Void> result = service.submitGrantOfferLetter(projectId);

        Assert.assertFalse(result.isSuccess());
        Assert.assertThat(project.getOfferSubmittedDate(), nullValue());
    }

    @Test
    public void testSubmitGrantOfferLetterSuccess() {
        project.setSignedGrantOfferLetter(mock(FileEntry.class));
        ServiceResult<Void> result = service.submitGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
        Assert.assertThat(project.getOfferSubmittedDate(), notNullValue());
    }

    @Test
    public void testGenerateGrantOfferLetter() {
        assertGenerateFile(
                fileEntryResource ->
                        service.generateGrantOfferLetter(123L, fileEntryResource));
    }

    @Test
    public void testRemoveGrantOfferLetterFileEntry() {

        FileEntry existingGOLFile = newFileEntry().build();
        project.setGrantOfferLetter(existingGOLFile);

        when(fileServiceMock.deleteFile(existingGOLFile.getId())).thenReturn(serviceSuccess(existingGOLFile));

        ServiceResult<Void> result = service.removeGrantOfferLetterFileEntry(123L);

        assertTrue(result.isSuccess());
        assertNull(project.getGrantOfferLetter());
        verify(fileServiceMock).deleteFile(existingGOLFile.getId());
    }

    @Test
    public void testGenerateGrantOfferLetterIfReadySuccess() {
        FileEntryResource fileEntryResource = newFileEntryResource().
                withFilesizeBytes(1024).
                withMediaType("application/pdf").
                withName("grant_offer_letter").
                build();

        FileEntry createdFile = newFileEntry().build();
        Pair<File, FileEntry> fileEntryPair = Pair.of(new File("blah"), createdFile);

        StringBuilder stringBuilder = new StringBuilder();
        String htmlFile = stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<html dir=\"ltr\" lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"UTF-8\"></meta>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<p>\n")
                .append("${LeadContact}<br/>\n")
                .append("</p>\n")
                .append("</body>\n")
                .append("</html>\n").toString();

        Competition comp = newCompetition().withName("Test Comp").build();
        Organisation o = newOrganisation().withName("Org1").build();
        Role leadAppRole = newRole(UserRoleType.LEADAPPLICANT).build();
        User u = newUser().withFirstName("ab").withLastName("cd").build();
        ProcessRole leadAppProcessRole = newProcessRole().withOrganisation(o).withUser(u).withRole(leadAppRole).build();
        Application app = newApplication().withCompetition(comp).withProcessRoles(leadAppProcessRole).withId(3L).build();
        ProjectUser pm = newProjectUser().withRole(PROJECT_MANAGER).withOrganisation(o).build();
        PartnerOrganisation po = PartnerOrganisationBuilder.newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(true).build();
        Project project = newProject().withOtherDocumentsApproved(Boolean.TRUE).withApplication(app).withPartnerOrganisations(asList(po)).withProjectUsers(asList(pm)).withDuration(10L).build();

        when(projectFinanceServiceMock.getSpendProfileStatusByProjectId(123L)).thenReturn(serviceSuccess(ApprovalType.APPROVED));
        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(rendererMock.renderTemplate(any(String.class), any(Map.class))).thenReturn(ServiceResult.serviceSuccess(htmlFile));
        when(fileServiceMock.createFile(any(FileEntryResource.class), any(Supplier.class))).thenReturn(ServiceResult.serviceSuccess(fileEntryPair));
        when(fileEntryMapperMock.mapToResource(createdFile)).thenReturn(fileEntryResource);

        ServiceResult<Void> result = service.generateGrantOfferLetterIfReady(123L);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGenerateGrantOfferLetterFailureSpendProfilesNotApproved() {
        FileEntryResource fileEntryResource = newFileEntryResource().
                withFilesizeBytes(1024).
                withMediaType("application/pdf").
                withName("grant_offer_letter").
                build();

        FileEntry createdFile = newFileEntry().build();
        Pair<File, FileEntry> fileEntryPair = Pair.of(new File("blah"), createdFile);

        StringBuilder stringBuilder = new StringBuilder();
        String htmlFile = stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<html dir=\"ltr\" lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"UTF-8\"></meta>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<p>\n")
                .append("${LeadContact}<br/>\n")
                .append("</p>\n")
                .append("</body>\n")
                .append("</html>\n").toString();

        Competition comp = newCompetition().withName("Test Comp").build();
        Organisation o = newOrganisation().withName("Org1").build();
        Role leadAppRole = newRole(UserRoleType.LEADAPPLICANT).build();
        User u = newUser().withFirstName("ab").withLastName("cd").build();
        ProcessRole leadAppProcessRole = newProcessRole().withOrganisation(o).withUser(u).withRole(leadAppRole).build();
        Application app = newApplication().withCompetition(comp).withProcessRoles(leadAppProcessRole).withId(3L).build();
        ProjectUser pm = newProjectUser().withRole(PROJECT_MANAGER).withOrganisation(o).build();
        PartnerOrganisation po = PartnerOrganisationBuilder.newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(true).build();
        Project project = newProject().withOtherDocumentsApproved(Boolean.TRUE).withApplication(app).withPartnerOrganisations(asList(po)).withProjectUsers(asList(pm)).withDuration(10L).build();

        when(projectFinanceServiceMock.getSpendProfileStatusByProjectId(123L)).thenReturn(serviceSuccess(ApprovalType.REJECTED));
        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(rendererMock.renderTemplate(any(String.class), any(Map.class))).thenReturn(ServiceResult.serviceSuccess(htmlFile));
        when(fileServiceMock.createFile(any(FileEntryResource.class), any(Supplier.class))).thenReturn(ServiceResult.serviceSuccess(fileEntryPair));
        when(fileEntryMapperMock.mapToResource(createdFile)).thenReturn(fileEntryResource);

        ServiceResult<Void> result = service.generateGrantOfferLetterIfReady(123L);
        assertTrue(result.isSuccess());
    }


    @Test
    public void testGenerateGrantOfferLetterOtherDocsNotApproved() {
        FileEntryResource fileEntryResource = newFileEntryResource().
                withFilesizeBytes(1024).
                withMediaType("application/pdf").
                withName("grant_offer_letter").
                build();

        FileEntry createdFile = newFileEntry().build();
        Pair<File, FileEntry> fileEntryPair = Pair.of(new File("blah"), createdFile);

        StringBuilder stringBuilder = new StringBuilder();
        String htmlFile = stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<html dir=\"ltr\" lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"UTF-8\"></meta>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<p>\n")
                .append("${LeadContact}<br/>\n")
                .append("</p>\n")
                .append("</body>\n")
                .append("</html>\n").toString();

        Competition comp = newCompetition().withName("Test Comp").build();
        Organisation o = newOrganisation().withName("Org1").build();
        Role leadAppRole = newRole(UserRoleType.LEADAPPLICANT).build();
        User u = newUser().withFirstName("ab").withLastName("cd").build();
        ProcessRole leadAppProcessRole = newProcessRole().withOrganisation(o).withUser(u).withRole(leadAppRole).build();
        Application app = newApplication().withCompetition(comp).withProcessRoles(leadAppProcessRole).withId(3L).build();
        ProjectUser pm = newProjectUser().withRole(PROJECT_MANAGER).withOrganisation(o).build();
        PartnerOrganisation po = PartnerOrganisationBuilder.newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(true).build();
        Project project = newProject().withOtherDocumentsApproved(Boolean.FALSE).withApplication(app).withPartnerOrganisations(asList(po)).withProjectUsers(asList(pm)).withDuration(10L).build();

        when(projectFinanceServiceMock.getSpendProfileStatusByProjectId(123L)).thenReturn(serviceSuccess(ApprovalType.APPROVED));
        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(rendererMock.renderTemplate(any(String.class), any(Map.class))).thenReturn(ServiceResult.serviceSuccess(htmlFile));
        when(fileServiceMock.createFile(any(FileEntryResource.class), any(Supplier.class))).thenReturn(ServiceResult.serviceSuccess(fileEntryPair));
        when(fileEntryMapperMock.mapToResource(createdFile)).thenReturn(fileEntryResource);

        ServiceResult<Void> result = service.generateGrantOfferLetterIfReady(123L);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGenerateGrantOfferLetterNoProject() {
        FileEntryResource fileEntryResource = newFileEntryResource().
                withFilesizeBytes(1024).
                withMediaType("application/pdf").
                withName("grant_offer_letter").
                build();

        FileEntry createdFile = newFileEntry().build();
        Pair<File, FileEntry> fileEntryPair = Pair.of(new File("blah"), createdFile);

        StringBuilder stringBuilder = new StringBuilder();
        String htmlFile = stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<html dir=\"ltr\" lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"UTF-8\"></meta>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<p>\n")
                .append("${LeadContact}<br/>\n")
                .append("</p>\n")
                .append("</body>\n")
                .append("</html>\n").toString();

        Competition comp = newCompetition().withName("Test Comp").build();
        Organisation o = newOrganisation().withName("Org1").build();
        Role leadAppRole = newRole(UserRoleType.LEADAPPLICANT).build();
        User u = newUser().withFirstName("ab").withLastName("cd").build();
        ProcessRole leadAppProcessRole = newProcessRole().withOrganisation(o).withUser(u).withRole(leadAppRole).build();
        Application app = newApplication().withCompetition(comp).withProcessRoles(leadAppProcessRole).withId(3L).build();
        ProjectUser pm = newProjectUser().withRole(PROJECT_MANAGER).withOrganisation(o).build();
        PartnerOrganisation po = PartnerOrganisationBuilder.newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(true).build();
        Project project = newProject().withOtherDocumentsApproved(Boolean.FALSE).withApplication(app).withPartnerOrganisations(asList(po)).withProjectUsers(asList(pm)).withDuration(10L).build();

        when(projectFinanceServiceMock.getSpendProfileStatusByProjectId(123L)).thenReturn(serviceSuccess(ApprovalType.APPROVED));
        when(projectRepositoryMock.findOne(123L)).thenReturn(null);
        when(rendererMock.renderTemplate(any(String.class), any(Map.class))).thenReturn(ServiceResult.serviceSuccess(htmlFile));
        when(fileServiceMock.createFile(any(FileEntryResource.class), any(Supplier.class))).thenReturn(ServiceResult.serviceSuccess(fileEntryPair));
        when(fileEntryMapperMock.mapToResource(createdFile)).thenReturn(fileEntryResource);

        ServiceResult<Void> result = service.generateGrantOfferLetterIfReady(123L);
        assertTrue(!result.isSuccess());
        assertEquals(result.getFailure().getErrors().get(0).getErrorKey(), CommonFailureKeys.GENERAL_NOT_FOUND.toString());
    }
        @Override
    protected ProjectGrantOfferService supplyServiceUnderTest() {
        return new ProjectGrantOfferServiceImpl();
    }
}