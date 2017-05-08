package org.innovateuk.ifs.project.finance;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectFinanceServiceImplTest {
    @InjectMocks
    private ProjectFinanceServiceImpl service;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Test
    public void testGetProjectFinances() {

        List<ProjectFinanceResource> resources = newProjectFinanceResource().build(2);
        when(projectFinanceRestService.getProjectFinances(123L)).thenReturn(restSuccess(resources));

        List<ProjectFinanceResource> financeTotals = service.getProjectFinances(123L);
        assertEquals(resources, financeTotals);
    }

    @Test
    public void testGetViability() {

        ViabilityResource viability = new ViabilityResource(Viability.APPROVED, ViabilityRagStatus.GREEN);

        when(projectFinanceRestService.getViability(123L, 456L)).thenReturn(restSuccess(viability));

        ViabilityResource result = service.getViability(123L, 456L);
        assertEquals(viability, result);
    }

    @Test
    public void testSaveViability() {

        when(projectFinanceRestService.saveViability(123L, 456L, Viability.APPROVED, ViabilityRagStatus.GREEN)).thenReturn(restSuccess());

        service.saveViability(123L, 456L, Viability.APPROVED, ViabilityRagStatus.GREEN);

        verify(projectFinanceRestService).saveViability(123L, 456L, Viability.APPROVED, ViabilityRagStatus.GREEN);
    }

    @Test
    public void testGetEligibility() {

        EligibilityResource eligibility = new EligibilityResource(Eligibility.APPROVED, EligibilityRagStatus.GREEN);

        when(projectFinanceRestService.getEligibility(123L, 456L)).thenReturn(restSuccess(eligibility));

        EligibilityResource result = service.getEligibility(123L, 456L);
        assertEquals(eligibility, result);
    }

    @Test
    public void testSaveEligibility() {

        when(projectFinanceRestService.saveEligibility(123L, 456L, Eligibility.APPROVED, EligibilityRagStatus.GREEN)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.saveEligibility(123L, 456L, Eligibility.APPROVED, EligibilityRagStatus.GREEN);

        assertTrue(result.isSuccess());

        verify(projectFinanceRestService).saveEligibility(123L, 456L, Eligibility.APPROVED, EligibilityRagStatus.GREEN);
    }
}
