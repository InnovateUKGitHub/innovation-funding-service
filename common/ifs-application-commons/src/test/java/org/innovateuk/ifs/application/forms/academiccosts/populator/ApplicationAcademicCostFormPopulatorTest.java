package org.innovateuk.ifs.application.forms.academiccosts.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.commons.rest.RestFailure;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationAcademicCostFormPopulatorTest extends BaseServiceUnitTest<ApplicationAcademicCostFormPopulator> {

    private static final long APPLICATION_ID = 1L;
    private static final long ORGANISATION_ID = 2L;
    private static final long FILE_ENTRY_ID = 3L;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private FileEntryRestService fileEntryRestService;

    @Mock
    private ApplicationFinanceRowRestService defaultFinanceRowRestService;

    @Override
    protected ApplicationAcademicCostFormPopulator supplyServiceUnderTest() {
        return new ApplicationAcademicCostFormPopulator();
    }

    @Test
    public void populate() {
        AcademicCostForm form = new AcademicCostForm();

        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(newApplicationFinanceResource()
                .withAcademicCosts()
                .withFinanceFileEntry(FILE_ENTRY_ID)
                .build()));

        when(fileEntryRestService.findOne(FILE_ENTRY_ID)).thenReturn(restSuccess(newFileEntryResource().withName("Filename").build()));

        service.populate(form, APPLICATION_ID, ORGANISATION_ID);

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
    public void populate_emptyFinances() {
        AcademicCostForm form = new AcademicCostForm();
        ApplicationFinanceResource finance = newApplicationFinanceResource()
                .withFinanceOrganisationDetails(emptyMap()).build();

        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        when(fileEntryRestService.findOne(FILE_ENTRY_ID)).thenReturn(restFailure(new RestFailure(emptyList(), HttpStatus.NOT_FOUND)));

        service.populate(form, APPLICATION_ID, ORGANISATION_ID);

        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("tsb_reference")));
        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("incurred_staff")));
        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("incurred_travel_subsistence")));
        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("incurred_other_costs")));
        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("allocated_investigators")));
        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("allocated_estates_costs")));
        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("allocated_other_costs")));
        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("indirect_costs")));
        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("exceptions_staff")));
        verify(defaultFinanceRowRestService).create(argThat(i -> i.getName().equals("exceptions_other_costs")));

        assertNull(form.getFilename());
    }
}
