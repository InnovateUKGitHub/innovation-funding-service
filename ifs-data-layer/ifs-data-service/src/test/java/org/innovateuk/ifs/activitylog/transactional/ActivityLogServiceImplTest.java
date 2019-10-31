package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.threads.domain.Query;
import org.innovateuk.ifs.threads.repository.QueryRepository;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentBuilder.newCompetitionDocument;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class ActivityLogServiceImplTest {

    private static final ActivityType TEST_ACTIVITY_TYPE = ActivityType.APPLICATION_SUBMITTED;

    @InjectMocks
    private ActivityLogServiceImpl activityLogService;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private QueryRepository queryRepository;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private CompetitionDocumentConfigRepository competitionDocumentConfigRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OrganisationRepository organisationRepository;

    @Test
    public void recordActivityByApplicationId() {
        Application application = newApplication().build();
        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));

        activityLogService.recordActivityByApplicationId(application.getId(), TEST_ACTIVITY_TYPE);

        verify(activityLogRepository).save(new ActivityLog(application, TEST_ACTIVITY_TYPE));
    }

    @Test
    public void recordActivityByProjectId() {
        Application application = newApplication().build();
        Project project = newProject().withApplication(application).build();
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        activityLogService.recordActivityByProjectId(project.getId(), TEST_ACTIVITY_TYPE);

        verify(activityLogRepository).save(new ActivityLog(application, TEST_ACTIVITY_TYPE));
    }

    @Test
    public void recordActivityByProjectIdAndOrganisationId() {
        Application application = newApplication().build();
        Project project = newProject().withApplication(application).build();
        Organisation organisation = newOrganisation().build();
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(organisationRepository.findById(organisation.getId())).thenReturn(Optional.of(organisation));

        activityLogService.recordActivityByProjectIdAndOrganisationId(project.getId(), organisation.getId(), TEST_ACTIVITY_TYPE);

        verify(activityLogRepository).save(new ActivityLog(application, TEST_ACTIVITY_TYPE, organisation));
    }

    @Test
    public void recordDocumentActivityByProjectId() {
        Application application = newApplication().build();
        Project project = newProject().withApplication(application).build();
        CompetitionDocument documentConfig = newCompetitionDocument().build();
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(competitionDocumentConfigRepository.findById(documentConfig.getId())).thenReturn(Optional.of(documentConfig));

        activityLogService.recordDocumentActivityByProjectId(project.getId(), TEST_ACTIVITY_TYPE, documentConfig.getId());

        verify(activityLogRepository).save(new ActivityLog(application, TEST_ACTIVITY_TYPE, documentConfig));
    }

    @Test
    public void recordQueryActivityByProjectFinanceId() {
        Application application = newApplication().build();
        Project project = newProject().withApplication(application).build();
        Organisation organisation = newOrganisation().build();
        ProjectFinance projectFinance = newProjectFinance()
                .withProject(project)
                .withOrganisation(organisation)
                .build();
        Query query = new Query(1L, null, null, null, null, null, null);
        when(projectFinanceRepository.findById(projectFinance.getId())).thenReturn(Optional.of(projectFinance));
        when(queryRepository.findById(query.id())).thenReturn(Optional.of(query));

        activityLogService.recordQueryActivityByProjectFinanceId(projectFinance.getId(),TEST_ACTIVITY_TYPE, query.id());

        verify(activityLogRepository).save(new ActivityLog(application, TEST_ACTIVITY_TYPE, query, organisation));
    }

    @Test
    public void findByApplicationId() {
        long applicationId = 1L;
        Application application = newApplication().build();
        Organisation organisation = newOrganisation()
                .withName("My organisation")
                .build();
        Query query = new Query(1L, null, null, null, FinanceChecksSectionType.VIABILITY, null, null);
        CompetitionDocument competitionDocument = newCompetitionDocument()
                .withTitle("My document")
                .build();
        User createdBy = newUser()
                .withFirstName("Bob")
                .withLastName("Name")
                .withRoles(singleton(PROJECT_FINANCE))
                .build();
        ZonedDateTime createdOn = now();

        ActivityLog activityLog = new ActivityLog(application, TEST_ACTIVITY_TYPE, query, organisation);
        setField(activityLog, "createdOn", createdOn);
        setField(activityLog, "createdBy", createdBy);
        setField(activityLog, "competitionDocument", competitionDocument);

        when(activityLogRepository.findByApplicationIdOrderByCreatedOnDesc(applicationId)).thenReturn(singletonList(activityLog));

        ServiceResult<List<ActivityLogResource>> result = activityLogService.findByApplicationId(applicationId);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getSuccess().size());

        ActivityLogResource activityLogResource = result.getSuccess().get(0);

        assertEquals(TEST_ACTIVITY_TYPE, activityLogResource.getActivityType());
        assertEquals(createdBy.getId(), activityLogResource.getAuthoredBy());
        assertEquals("Bob Name", activityLogResource.getAuthoredByName());
        assertEquals(singleton(PROJECT_FINANCE), activityLogResource.getAuthoredByRoles());
        assertEquals(createdOn, activityLogResource.getCreatedOn());
        assertEquals(competitionDocument.getId(), activityLogResource.getDocumentConfig());
        assertEquals("My document", activityLogResource.getDocumentConfigName());
        assertEquals(organisation.getId(), activityLogResource.getOrganisation());
        assertEquals("My organisation", activityLogResource.getOrganisationName());
        assertEquals(query.id(), activityLogResource.getQuery());
        assertEquals(FinanceChecksSectionType.VIABILITY, activityLogResource.getQueryType());



    }

}
