package org.innovateuk.ifs.application.forms.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.yourprojectcosts.form.*;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.builder.BaseFinanceResourceBuilder;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm.EMPTY_ROW_ID;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractYourProjectCostsSaverTest {
    private static ApplicationFinanceResource APPLICATION_FINANCE_RESOURCE = newApplicationFinanceResource().withFinanceOrganisationDetails(BaseFinanceResourceBuilder.INDUSTRIAL_FINANCES).build();
    @Mock
    private FinanceRowRestService financeRowRestService;

    @InjectMocks
    private AbstractYourProjectCostsSaver target = new AbstractYourProjectCostsSaver() {
        @Override
        protected BaseFinanceResource getFinanceResource(long targetId, long organisationId) {
            return APPLICATION_FINANCE_RESOURCE;
        }

        @Override
        protected FinanceRowRestService getFinanceRowService() {
            return financeRowRestService;
        }
    };

    @Test
    public void save() {
        YourProjectCostsForm form = new YourProjectCostsForm();

        LabourForm labourForm = new LabourForm();
        labourForm.setWorkingDaysPerYear(365);
        LabourRowForm labourRow =  new LabourRowForm();
        labourRow.setGross(new BigDecimal(123));
        labourForm.setRows(asMap(EMPTY_ROW_ID, labourRow));
        form.setLabour(labourForm);

        OverheadForm overheadForm = new OverheadForm();
        overheadForm.setRateType(OverheadRateType.TOTAL);
        overheadForm.setTotalSpreadsheet(100);
        form.setOverhead(overheadForm);

        MaterialRowForm materialRow = new MaterialRowForm();
        materialRow.setCost(new BigDecimal(123));
        form.setMaterialRows(asMap(EMPTY_ROW_ID, materialRow));

        CapitalUsageRowForm capitalUsageRow = new CapitalUsageRowForm();
        capitalUsageRow.setNetValue(new BigDecimal(123));
        form.setCapitalUsageRows(asMap(EMPTY_ROW_ID, capitalUsageRow));

        SubcontractingRowForm subcontractingRow = new SubcontractingRowForm();
        subcontractingRow.setCost(new BigDecimal(123));
        form.setSubcontractingRows(asMap(EMPTY_ROW_ID, subcontractingRow));

        TravelRowForm travelRow = new TravelRowForm();
        travelRow.setEachCost(new BigDecimal(123));
        form.setTravelRows(asMap(EMPTY_ROW_ID, travelRow));

        OtherCostRowForm otherRow = new OtherCostRowForm();
        otherRow.setEstimate(new BigDecimal(123));
        form.setOtherRows(asMap(EMPTY_ROW_ID, otherRow));

        FinanceRowItem mockResponse = mock(FinanceRowItem.class);
        when(financeRowRestService.update(any())).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.addWithResponse(eq(APPLICATION_FINANCE_RESOURCE.getId()), any())).thenReturn(restSuccess(mockResponse));

        ServiceResult<Void> result = target.save(form, 1L, 2L);

        assertTrue(result.isSuccess());

        LabourCost workingDaysCost = ((LabourCostCategory) APPLICATION_FINANCE_RESOURCE.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR)).getWorkingDaysPerYearCostItem();
        assertEquals(workingDaysCost.getLabourDays(), (Integer) 365);
        verify(financeRowRestService).update(workingDaysCost);

        Overhead overhead = (Overhead) APPLICATION_FINANCE_RESOURCE.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS).getCosts().get(0);
        assertEquals(overhead.getRateType(), OverheadRateType.TOTAL);
        assertEquals(overhead.getRate(), (Integer) 100);
        verify(financeRowRestService).update(overhead);

        verify(financeRowRestService).addWithResponse(eq(APPLICATION_FINANCE_RESOURCE.getId()), isA(LabourCost.class));
        verify(financeRowRestService).addWithResponse(eq(APPLICATION_FINANCE_RESOURCE.getId()), isA(Materials.class));
        verify(financeRowRestService).addWithResponse(eq(APPLICATION_FINANCE_RESOURCE.getId()), isA(CapitalUsage.class));
        verify(financeRowRestService).addWithResponse(eq(APPLICATION_FINANCE_RESOURCE.getId()), isA(SubContractingCost.class));
        verify(financeRowRestService).addWithResponse(eq(APPLICATION_FINANCE_RESOURCE.getId()), isA(TravelCost.class));
        verify(financeRowRestService).addWithResponse(eq(APPLICATION_FINANCE_RESOURCE.getId()), isA(OtherCost.class));
        verify(financeRowRestService, times(6)).update(mockResponse);

        verifyNoMoreInteractions(financeRowRestService);
    }


}
