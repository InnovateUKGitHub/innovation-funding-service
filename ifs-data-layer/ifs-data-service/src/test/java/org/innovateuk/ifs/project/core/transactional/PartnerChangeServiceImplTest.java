package org.innovateuk.ifs.project.core.transactional;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.Collections;
import java.util.List;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.documents.repository.ProjectDocumentRepository;
import org.innovateuk.ifs.project.finance.resource.Viability;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
public class PartnerChangeServiceImplTest extends BaseServiceUnitTest<PartnerChangeService> {

    @Mock
    private ProjectDocumentRepository projectDocumentRepository;

    @Mock
    private FinanceCheckService financeCheckService;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private Organisation organisation;

    private ProjectDocument projectDocument = mock(ProjectDocument.class);

    @Override
    protected PartnerChangeService supplyServiceUnderTest() {
        return new PartnerChangeServiceImpl();
    }

    @Before
    public void setup() {
        when(organisation.getId()).thenReturn(1L);
        ProjectFinance projectFinance = new ProjectFinance();
        projectFinance.setOrganisation(organisation);
        List<ProjectFinance> projectFinances = Collections.singletonList(projectFinance);
        when(projectFinanceRepository.findByProjectId(1L)).thenReturn(projectFinances);
    }

    private void stubResetProjectFinance() {
        when(projectFinanceRepository.findByProjectId(1L)).thenReturn(Collections.emptyList());
    }

    @Test
    public void updateProjectAfterChangingPartners_ViabilityAndEligibilityAreReset() {
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(Collections.emptyList());

        when(financeCheckService.resetViability(any(), any(), any())).thenReturn(ServiceResult.serviceSuccess());
        when(financeCheckService.resetEligibility(any(), any(), any())).thenReturn(ServiceResult.serviceSuccess());

        boolean result = service.updateProjectAfterChangingPartners(1L).isSuccess();

        verify(financeCheckService, times(1)).resetViability(new ProjectOrganisationCompositeId(1L, 1L), Viability.REVIEW, ViabilityRagStatus.UNSET);
        verify(financeCheckService, times(1)).resetEligibility(any(), any(), any());
        assertTrue(result);
    }

    @Test
    public void updateProjectAfterChangingPartners_submittedDocumentIsRejected() {
        stubResetProjectFinance();
        List<ProjectDocument> documents = Collections.singletonList(projectDocument);
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(documents);
        when(projectDocument.getStatus()).thenReturn(DocumentStatus.APPROVED);
        when(projectDocumentRepository.saveAll(any())).thenReturn(documents);

        boolean result = service.updateProjectAfterChangingPartners(1).isSuccess();

        verify(projectDocument).setStatus(DocumentStatus.REJECTED);
        verify(projectDocumentRepository, times(1)).saveAll(documents);
        verify(projectDocumentRepository, times(1)).saveAll(any());
        assertTrue(result);
    }

    @Test
    public void updateProjectAfterChangingPartners_thereAreNoSubmittedDocuments() {
        stubResetProjectFinance();
        List<ProjectDocument> documents = Collections.emptyList();
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(documents);

        boolean result = service.updateProjectAfterChangingPartners(1).isSuccess();

        verify(projectDocumentRepository, times(0)).saveAll(any());
        assertTrue(result);
    }

    @Test
    public void updateProjectAfterChangingPartners_thereAreOnlyRejectedDocuments() {
        stubResetProjectFinance();
        List<ProjectDocument> documents = Collections.singletonList(projectDocument);
        when(projectDocumentRepository.findAllByProjectId(1L)).thenReturn(documents);
        when(projectDocument.getStatus()).thenReturn(DocumentStatus.REJECTED);

        boolean result = service.updateProjectAfterChangingPartners(1).isSuccess();

        verify(projectDocumentRepository, times(0)).saveAll(any());
        assertTrue(result);
    }
}