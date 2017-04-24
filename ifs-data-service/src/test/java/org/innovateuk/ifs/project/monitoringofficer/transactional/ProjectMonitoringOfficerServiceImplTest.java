package org.innovateuk.ifs.project.monitoringofficer.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.builder.MonitoringOfficerBuilder;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectMonitoringOfficerServiceImplTest extends BaseServiceUnitTest<ProjectMonitoringOfficerService> {

    private MonitoringOfficerResource monitoringOfficerResource;
    private static final String webBaseUrl = "https://ifs-local-dev/dashboard";

    @Before
    public void setUp() {
        monitoringOfficerResource = newMonitoringOfficerResource()
                .withProject(1L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();
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

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        assertTrue(result.getFailure().is(PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE));
    }

    @Test
    public void testSaveMOWhenProjectDetailsNotYetSubmitted() {

        Long projectid = 1L;

        Project projectInDB = newProject().withId(1L).build();

        when(projectRepositoryMock.findOne(projectid)).thenReturn(projectInDB);

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

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


        Project projectInDB = newProject().withId(1L).build();

        when(projectRepositoryMock.findOne(projectid)).thenReturn(projectInDB);
        when(monitoringOfficerRepositoryMock.findOneByProjectId(monitoringOfficerResource.getProject())).thenReturn(monitoringOfficerInDB);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(projectInDB)).thenReturn(true);

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        // Assert that the MO in DB is updated with the correct values from MO Resource
        Assert.assertEquals("First name of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getFirstName(), monitoringOfficerResource.getFirstName());
        Assert.assertEquals("Last name of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getLastName(), monitoringOfficerResource.getLastName());
        Assert.assertEquals("Email of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getEmail(), monitoringOfficerResource.getEmail());
        Assert.assertEquals("Phone number of MO in DB should be updated with the value from MO Resource", monitoringOfficerInDB.getPhoneNumber(), monitoringOfficerResource.getPhoneNumber());

        Optional<SaveMonitoringOfficerResult> successResult = result.getOptionalSuccessObject();
        assertTrue(successResult.isPresent());
        assertTrue(successResult.get().isMonitoringOfficerSaved());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveMOWhenMODetailsRemainsTheSame() {

        Long projectId = 1L;

        // The details for the MO is set to the same as in resource
        MonitoringOfficer monitoringOfficerInDB = MonitoringOfficerBuilder.newMonitoringOfficer()
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();


        Project projectInDB = newProject().withId(1L).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(projectInDB);
        when(monitoringOfficerRepositoryMock.findOneByProjectId(monitoringOfficerResource.getProject())).thenReturn(monitoringOfficerInDB);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(projectInDB)).thenReturn(true);

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectId, monitoringOfficerResource);

        Optional<SaveMonitoringOfficerResult> successResult = result.getOptionalSuccessObject();
        assertTrue(successResult.isPresent());
        assertFalse(successResult.get().isMonitoringOfficerSaved());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testSaveMOWhenMODoesNotExistForAProject() {

        Long projectid = 1L;

        Project projectInDB = newProject().withId(1L).build();

        when(projectRepositoryMock.findOne(projectid)).thenReturn(projectInDB);
        when(monitoringOfficerRepositoryMock.findOneByProjectId(monitoringOfficerResource.getProject())).thenReturn(null);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(projectInDB)).thenReturn(true);

        ServiceResult<SaveMonitoringOfficerResult> result = service.saveMonitoringOfficer(projectid, monitoringOfficerResource);

        Optional<SaveMonitoringOfficerResult> successResult = result.getOptionalSuccessObject();
        assertTrue(successResult.isPresent());
        assertTrue(successResult.get().isMonitoringOfficerSaved());
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

        when(monitoringOfficerRepositoryMock.findOneByProjectId(projectid)).thenReturn(monitoringOfficerInDB);

        ServiceResult<MonitoringOfficerResource> result = service.getMonitoringOfficer(projectid);

        assertTrue(result.isSuccess());

    }

    @Override
    protected ProjectMonitoringOfficerService supplyServiceUnderTest() {

        ProjectMonitoringOfficerService projectMonitoringOfficerService =  new ProjectMonitoringOfficerServiceImpl();
        ReflectionTestUtils.setField(projectMonitoringOfficerService, "webBaseUrl", webBaseUrl);
        return projectMonitoringOfficerService;
    }
}
