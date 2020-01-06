package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
public class ProjectPartnerChangeServiceImplTest extends BaseServiceUnitTest<ProjectPartnerChangeService> {

    @Mock
    private ProjectDocumentRepository projectDocumentRepository;

    @Mock
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private PartnerOrganisationRepository partnerOrganisationRepository;

    @Mock
    private Organisation organisation;

    @Mock
    private PartnerOrganisation partnerOrganisation;

    @Mock
    private ProjectDocument projectDocument;

    @Mock
    private UserResource userResource;

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @Mock
    private ProjectFinance projectFinance;

    @Override
    protected ProjectPartnerChangeService supplyServiceUnderTest() {
        return new ProjectPartnerChangeServiceImpl();
    }

    @Before
    public void setup() {
        when(organisation.getId()).thenReturn(1L);
        when(projectFinance.getOrganisation()).thenReturn(organisation);
        List<ProjectFinance> projectFinances = Collections.singletonList(projectFinance);
        when(projectFinanceRepository.findByProjectId(2L)).thenReturn(projectFinances);

        when(userResource.getId()).thenReturn(3L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        setLoggedInUser(userResource);
    }

    @Test
    public void updateProjectWhenPartnersChange_EligibilityIsReset() {
        when(projectDocumentRepository.findAllByProjectId(2L)).thenReturn(Collections.emptyList());
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(2L, 1L)).thenReturn(partnerOrganisation);

        service.updateProjectWhenPartnersChange(2L);

        verify(eligibilityWorkflowHandler, times(1)).eligibilityReset(partnerOrganisation, user);
    }

    @Test
    public void updateProjectWhenPartnersChange_submittedDocumentIsRejected() {
        List<ProjectDocument> documents = Collections.singletonList(projectDocument);
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(documents);

        service.updateProjectWhenPartnersChange(1L);

        verify(projectDocument).setStatus(DocumentStatus.REJECTED_DUE_TO_TEAM_CHANGE);
    }

    @Test
    public void updateProjectWhenPartnersChange_thereAreNoSubmittedDocuments() {
        List<ProjectDocument> documents = Collections.emptyList();
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(documents);

        service.updateProjectWhenPartnersChange(1L);

        verify(projectDocumentRepository, times(0)).saveAll(any());
    }

    @Test
    public void updateProjectWhenPartnersChange_thereAreOnlyRejectedDocuments() {
        List<ProjectDocument> documents = Collections.singletonList(projectDocument);
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(documents);

        service.updateProjectWhenPartnersChange(1L);

        verify(projectDocumentRepository, times(0)).saveAll(any());
    }
}