package org.innovateuk.ifs.project.eligibility.populator;

import org.hamcrest.Matcher;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.commons.rest.RestFailure;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class ProjectAcademicCostFormPopulatorTest extends BaseServiceUnitTest<ProjectAcademicCostFormPopulator> {

    private static final long PROJECT_ID = 1L;
    private static final long APPLICATION_ID = 4L;
    private static final long ORGANISATION_ID = 2L;
    private static final long FILE_ENTRY_ID = 3L;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private FileEntryRestService fileEntryRestService;

    @Mock
    private ProjectFinanceRowRestService defaultFinanceRowRestService;

    @Mock
    private ProjectRestService projectRestService;
    @Override
    protected ProjectAcademicCostFormPopulator supplyServiceUnderTest() {
        return new ProjectAcademicCostFormPopulator();
    }

    @Test
    public void populate() {
        AcademicCostForm form = new AcademicCostForm();

        ProjectResource project = newProjectResource().withId(PROJECT_ID)
                .withApplication(APPLICATION_ID)
                .build();

        when(projectRestService.getProjectById(PROJECT_ID)).thenReturn(restSuccess(project));

        when(projectFinanceRestService.getProjectFinance(PROJECT_ID, ORGANISATION_ID)).thenReturn(restSuccess(newProjectFinanceResource()
                .withProject(PROJECT_ID)
                .withOrganisation(ORGANISATION_ID)
                .withAcademicCosts()
                .build()));

        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(newApplicationFinanceResource()
                .withAcademicCosts()
                .withFinanceFileEntry(FILE_ENTRY_ID)
                .build()));

        when(fileEntryRestService.findOne(FILE_ENTRY_ID)).thenReturn(restSuccess(newFileEntryResource().withName("Filename").build()));

        service.populate(form, PROJECT_ID, ORGANISATION_ID);

        assertEquals(form.getTsbReference(), "TSBReference");
        assertEquals(form.getIncurredStaff(), new BigDecimal("100"));
        assertEquals(form.getIncurredOtherCosts(), new BigDecimal("100"));
        assertEquals(form.getIncurredTravel(), new BigDecimal("100"));
        assertEquals(form.getAllocatedEstateCosts(), new BigDecimal("100"));
        assertEquals(form.getAllocatedInvestigators(), new BigDecimal("200"));
        assertEquals(form.getAllocatedOtherCosts(), new BigDecimal("200"));
        assertEquals(form.getIndirectCosts(), new BigDecimal("100"));
        assertEquals(form.getExceptionsOtherCosts(), new BigDecimal("300"));
        assertEquals(form.getExceptionsStaff(), new BigDecimal("300"));
        assertEquals(form.getFilename(), "Filename");
    }

    @Test
    public void populate_emptyFinances_noApplicationCosts() {
        AcademicCostForm form = new AcademicCostForm();
        ProjectFinanceResource finance = newProjectFinanceResource()
                .withProject(PROJECT_ID)
                .withOrganisation(ORGANISATION_ID)
                .withFinanceOrganisationDetails(emptyMap()).build();
        ProjectResource project = newProjectResource().withId(PROJECT_ID)
                .withApplication(APPLICATION_ID)
                .build();
        when(projectRestService.getProjectById(PROJECT_ID)).thenReturn(restSuccess(project));
        when(projectFinanceRestService.getProjectFinance(PROJECT_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));
        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restFailure(new RestFailure(emptyList(), HttpStatus.NOT_FOUND)));

        service.populate(form, PROJECT_ID, ORGANISATION_ID);

        verify(defaultFinanceRowRestService).create(argThat(hasName("tsb_reference")));
        verify(defaultFinanceRowRestService).create(argThat(hasName("incurred_staff")));
        verify(defaultFinanceRowRestService).create(argThat(hasName("incurred_travel_subsistence")));
        verify(defaultFinanceRowRestService).create(argThat(hasName("incurred_other_costs")));
        verify(defaultFinanceRowRestService).create(argThat(hasName("allocated_investigators")));
        verify(defaultFinanceRowRestService).create(argThat(hasName("allocated_estates_costs")));
        verify(defaultFinanceRowRestService).create(argThat(hasName("allocated_other_costs")));
        verify(defaultFinanceRowRestService).create(argThat(hasName("indirect_costs")));
        verify(defaultFinanceRowRestService).create(argThat(hasName("exceptions_staff")));
        verify(defaultFinanceRowRestService).create(argThat(hasName("exceptions_other_costs")));

        assertNull(form.getFilename());
    }

    private Matcher<AcademicCost> hasName(String name) {
        return lambdaMatches(cost -> cost.getName().equals(name));
    }
}
