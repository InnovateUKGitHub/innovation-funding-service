package org.innovateuk.ifs.project.core.transactional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
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
        when(projectFinanceRepository.findByProjectId(1L)).thenReturn(projectFinances);

        when(userResource.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        setLoggedInUser(userResource);
    }

    private void setupForRejectProjectDocuments() {
        when(projectFinanceRepository.findByProjectId(1L)).thenReturn(Collections.emptyList());
    }

    private void setUpForResetProjectFinance() {
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(Collections.emptyList());
        when(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(1L, 1L)).thenReturn(partnerOrganisation);
    }

    @Test
    public void updateProjectWhenPartnersChange_EligibilityResetRequired() {
        setUpForResetProjectFinance();
        when(projectFinance.getEligibilityStatus()).thenReturn(EligibilityRagStatus.AMBER);

        boolean result = service.updateProjectWhenPartnersChange(1L).isSuccess();

        verify(eligibilityWorkflowHandler, times(1)).eligibilityReset(partnerOrganisation, user);
        verify(projectFinanceRepository, times(1)).save(projectFinance);
        assertTrue(result);
    }

    @Test
    public void updateProjectWhenPartnersChange_EligibilityResetNotRequired() {
        setUpForResetProjectFinance();
        when(projectFinance.getEligibilityStatus()).thenReturn(EligibilityRagStatus.UNSET);

        boolean result = service.updateProjectWhenPartnersChange(1L).isSuccess();

        verify(eligibilityWorkflowHandler, times(1)).eligibilityReset(partnerOrganisation, user);
        verify(projectFinanceRepository, never()).save(projectFinance);
        assertTrue(result);
    }

    @Test
    public void updateProjectWhenPartnersChange_submittedDocumentIsRejected() {
        setupForRejectProjectDocuments();
        List<ProjectDocument> documents = Collections.singletonList(projectDocument);
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(documents);
        when(projectDocument.getStatus()).thenReturn(DocumentStatus.APPROVED);

        boolean result = service.updateProjectWhenPartnersChange(1).isSuccess();

        verify(projectDocument).setStatus(DocumentStatus.REJECTED);
        assertTrue(result);
    }

    @Test
    public void updateProjectWhenPartnersChange_thereAreNoSubmittedDocuments() {
        setupForRejectProjectDocuments();
        List<ProjectDocument> documents = Collections.emptyList();
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(documents);

        boolean result = service.updateProjectWhenPartnersChange(1).isSuccess();

        verify(projectDocumentRepository, times(0)).saveAll(any());
        assertTrue(result);
    }

    @Test
    public void updateProjectWhenPartnersChange_thereAreOnlyRejectedDocuments() {
        setupForRejectProjectDocuments();
        List<ProjectDocument> documents = Collections.singletonList(projectDocument);
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(documents);
        when(projectDocument.getStatus()).thenReturn(DocumentStatus.REJECTED);

        boolean result = service.updateProjectWhenPartnersChange(1).isSuccess();

        verify(projectDocumentRepository, times(0)).saveAll(any());
        assertTrue(result);
    }
}