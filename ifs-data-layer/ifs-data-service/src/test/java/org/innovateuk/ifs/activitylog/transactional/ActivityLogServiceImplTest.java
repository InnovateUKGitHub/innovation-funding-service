package org.innovateuk.ifs.activitylog.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.domain.ActivityType;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentBuilder.newCompetitionDocument;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}
