package org.innovateuk.ifs.application.forms.academiccosts.populator;

import org.hamcrest.Matcher;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.commons.rest.RestFailure;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AcademicCostFormPopulatorTest extends BaseServiceUnitTest<AcademicCostFormPopulator> {

    private static final long APPLICATION_ID = 1L;
    private static final long ORGANISATION_ID = 2L;
    private static final long FILE_ENTRY_ID = 3L;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private FileEntryRestService fileEntryRestService;

    @Mock
    private DefaultFinanceRowRestService defaultFinanceRowRestService;

    @Override
    protected AcademicCostFormPopulator supplyServiceUnderTest() {
        return new AcademicCostFormPopulator();
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

        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("tsb_reference")));
        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("incurred_staff")));
        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("incurred_travel_subsistence")));
        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("incurred_other_costs")));
        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("allocated_investigators")));
        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("allocated_estates_costs")));
        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("allocated_other_costs")));
        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("indirect_costs")));
        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("exceptions_staff")));
        verify(defaultFinanceRowRestService).addWithResponse(eq(finance.getId()), argThat(hasName("exceptions_other_costs")));

        assertNull(form.getFilename());
    }

    private Matcher<AcademicCost> hasName(String name) {
        return lambdaMatches(cost -> cost.getName().equals(name));
    }
}
