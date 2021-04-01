package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.*;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.AssociateSalaryCostBuilder.newAssociateSalaryCost;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AbstractYourProjectCostsSaverTest {
    private static ApplicationFinanceResource APPLICATION_FINANCE_RESOURCE = newApplicationFinanceResource().withIndustrialCosts().build();
    private static ApplicationFinanceResource APPLICATION_FINANCE_RESOURCE_WITH_EMPTY_INDIRECT_COST = newApplicationFinanceResource().withEmptyIndirectCosts().build();

    @Mock
    private FinanceRowRestService financeRowRestService;

    @Mock
    private AsyncFuturesGenerator futuresGeneratorMock;

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGeneratorMock);
    }

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

    @InjectMocks
    private AbstractYourProjectCostsSaver targetWithEmptyIndirectCost = new AbstractYourProjectCostsSaver() {
        @Override
        protected BaseFinanceResource getFinanceResource(long targetId, long organisationId) {
            return APPLICATION_FINANCE_RESOURCE_WITH_EMPTY_INDIRECT_COST;
        }

        @Override
        protected FinanceRowRestService getFinanceRowService() {
            return financeRowRestService;
        }
    };

    private ArgumentCaptor<IndirectCost> indirectCostArgumentCaptor = ArgumentCaptor.forClass(IndirectCost.class);

    @Test
    public void save() {
        BigDecimal grantClaimPercentage = BigDecimal.valueOf(50);
        BigInteger associateOneCost = BigInteger.valueOf(100);
        BigInteger associateTwoCost = BigInteger.valueOf(200);
        BigInteger academicAndSecretarialSupportCost = BigInteger.valueOf(300);
        BigInteger totalGrantAssociateSalaryCost = associateOneCost
                .add(associateTwoCost)
                .multiply(grantClaimPercentage.toBigIntegerExact())
                .divide(BigInteger.valueOf(100));
        BigInteger totalGrantAcademicAndSecretarialSupportCost = academicAndSecretarialSupportCost
                .multiply(grantClaimPercentage.toBigIntegerExact())
                .divide(BigInteger.valueOf(100));
        BigInteger expected = totalGrantAssociateSalaryCost
                .add(totalGrantAcademicAndSecretarialSupportCost)
                .multiply(BigInteger.valueOf(46))
                .divide(BigInteger.valueOf(100));

        YourProjectCostsForm form = new YourProjectCostsForm();
        form.setFecModelEnabled(false);
        form.setGrantClaimPercentage(grantClaimPercentage);

        LabourForm labourForm = new LabourForm();
        labourForm.setWorkingDaysPerYear(365);
        LabourRowForm labourRow = new LabourRowForm();
        labourRow.setGross(new BigDecimal(123));
        labourForm.setRows(asMap(UNSAVED_ROW_PREFIX, labourRow));
        form.setLabour(labourForm);

        OverheadForm overheadForm = new OverheadForm();
        overheadForm.setRateType(OverheadRateType.TOTAL);
        overheadForm.setTotalSpreadsheet(100);
        form.setOverhead(overheadForm);

        MaterialRowForm materialRow = new MaterialRowForm();
        materialRow.setCost(new BigDecimal(123));
        form.setMaterialRows(asMap(UNSAVED_ROW_PREFIX, materialRow));

        CapitalUsageRowForm capitalUsageRow = new CapitalUsageRowForm();
        capitalUsageRow.setNetValue(new BigDecimal(123));
        form.setCapitalUsageRows(asMap(UNSAVED_ROW_PREFIX, capitalUsageRow));

        SubcontractingRowForm subcontractingRow = new SubcontractingRowForm();
        subcontractingRow.setCost(new BigDecimal(123));
        form.setSubcontractingRows(asMap(UNSAVED_ROW_PREFIX, subcontractingRow));

        TravelRowForm travelRow = new TravelRowForm();
        travelRow.setEachCost(new BigDecimal(123));
        form.setTravelRows(asMap(UNSAVED_ROW_PREFIX, travelRow));

        OtherCostRowForm otherRow = new OtherCostRowForm();
        otherRow.setEstimate(new BigDecimal(123));
        form.setOtherRows(asMap(UNSAVED_ROW_PREFIX, otherRow));

        VatForm vat = new VatForm();
        vat.setRegistered(false);
        form.setVatForm(vat);

        form.getAdditionalCompanyCostForm().setAssociateSalary(new AdditionalCostAndDescription());
        form.getAdditionalCompanyCostForm().setCapitalEquipment(new AdditionalCostAndDescription());
        form.getAdditionalCompanyCostForm().setConsumables(new AdditionalCostAndDescription());
        form.getAdditionalCompanyCostForm().setManagementSupervision(new AdditionalCostAndDescription());
        form.getAdditionalCompanyCostForm().setOtherCosts(new AdditionalCostAndDescription());
        form.getAdditionalCompanyCostForm().setOtherStaff(new AdditionalCostAndDescription());

        setupDataForIndirectCost(associateOneCost, associateTwoCost, academicAndSecretarialSupportCost, form);

        FinanceRowItem mockResponse = mock(FinanceRowItem.class);
        when(financeRowRestService.update(any())).thenReturn(restSuccess(new ValidationMessages()));
        when(financeRowRestService.create(any())).thenReturn(restSuccess(mockResponse));

        OrganisationResource organisationResource = newOrganisationResource().withId(2L).build();

        ServiceResult<Void> result = target.save(form, 1L, organisationResource, new ValidationMessages());

        assertTrue(result.isSuccess());

        LabourCost workingDaysCost = ((LabourCostCategory) APPLICATION_FINANCE_RESOURCE.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR)).getWorkingDaysPerYearCostItem();
        assertEquals(workingDaysCost.getLabourDays(), (Integer) 365);
        verify(financeRowRestService).update(workingDaysCost);

        Overhead overhead = (Overhead) APPLICATION_FINANCE_RESOURCE.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS).getCosts().get(0);
        assertEquals(overhead.getRateType(), OverheadRateType.TOTAL);
        assertEquals(overhead.getRate(), (Integer) 100);
        verify(financeRowRestService).update(overhead);

        verify(financeRowRestService).create(isA(LabourCost.class));
        verify(financeRowRestService).create(isA(Materials.class));
        verify(financeRowRestService).create(isA(CapitalUsage.class));
        verify(financeRowRestService).create(isA(SubContractingCost.class));
        verify(financeRowRestService).create(isA(TravelCost.class));
        verify(financeRowRestService).create(isA(OtherCost.class));
        verify(financeRowRestService).update(isA(Vat.class));

        verify(financeRowRestService).update(isA(IndirectCost.class));
        IndirectCost indirectCost = (IndirectCost) APPLICATION_FINANCE_RESOURCE.getFinanceOrganisationDetails().get(FinanceRowType.INDIRECT_COSTS).getCosts().get(0);
        assertEquals(indirectCost.getCostType(), FinanceRowType.INDIRECT_COSTS);
        assertEquals(expected, indirectCost.getCost());
        assertEquals(expected, indirectCost.getTotal().toBigInteger());

        verify(financeRowRestService, times(6)).update(isA(AdditionalCompanyCost.class));
        verify(financeRowRestService, times(2)).update(isA(AssociateSalaryCost.class));
        verify(financeRowRestService, times(1)).update(isA(IndirectCost.class));
        verify(financeRowRestService, times(6)).update(mockResponse);

        verifyNoMoreInteractions(financeRowRestService);
    }

    private void setupDataForIndirectCost(BigInteger associateOneCost, BigInteger associateTwoCost, BigInteger academicAndSecretarialSupportCost, YourProjectCostsForm form) {
        AssociateSalaryCost associateOne = newAssociateSalaryCost()
                .withCost(associateOneCost)
                .withDuration(1)
                .build();
        AssociateSalaryCostRowForm associateOneForm = new AssociateSalaryCostRowForm(associateOne);
        associateOneForm.setTotal(BigDecimal.valueOf(associateOneCost.intValue()));

        AssociateSalaryCost associateTwo = newAssociateSalaryCost()
                .withCost(associateTwoCost)
                .withDuration(1)
                .build();
        AssociateSalaryCostRowForm associateTwoForm = new AssociateSalaryCostRowForm(associateTwo);
        associateTwoForm.setTotal(BigDecimal.valueOf(associateTwoCost.intValue()));

        Map<String, AssociateSalaryCostRowForm> associateSalaryCostRows = asMap("associate_salary_costs-1", associateOneForm,
                "associate_salary_costs-2", associateTwoForm);

        form.setAssociateSalaryCostRows(associateSalaryCostRows);
        AcademicAndSecretarialSupportCostRowForm academicAndSecretarialSupportForm = new AcademicAndSecretarialSupportCostRowForm();
        academicAndSecretarialSupportForm.setCost(academicAndSecretarialSupportCost);
        form.setAcademicAndSecretarialSupportForm(academicAndSecretarialSupportForm);
    }

    @Test
    public void saveIndirectCostCreatesFinanceRow() {
        BigDecimal grantClaimPercentage = BigDecimal.valueOf(50);
        BigInteger associateOneCost = BigInteger.valueOf(100);
        BigInteger associateTwoCost = BigInteger.valueOf(200);
        BigInteger academicAndSecretarialSupportCost = BigInteger.valueOf(300);

        YourProjectCostsForm form = new YourProjectCostsForm();
        form.setGrantClaimPercentage(grantClaimPercentage);

        setupDataForIndirectCost(associateOneCost, associateTwoCost, academicAndSecretarialSupportCost, form);

        IndirectCost indirectCost = new IndirectCost(1L);
        when(financeRowRestService.create(any())).thenReturn(restSuccess(indirectCost));
        when(financeRowRestService.update(any())).thenReturn(restSuccess(new ValidationMessages()));

        OrganisationResource organisationResource = newOrganisationResource().withId(2L).build();

        BigDecimal totalGrantAssociateSalaryCost = form.getTotalAssociateSalaryCosts()
                .multiply(grantClaimPercentage)
                .divide(new BigDecimal(100));
        BigDecimal totalGrantAcademicAndSecretarialSupportCost = form.getTotalAcademicAndSecretarialSupportCosts()
                .multiply(grantClaimPercentage)
                .divide(new BigDecimal(100));
        BigDecimal expected = totalGrantAssociateSalaryCost
                .add(totalGrantAcademicAndSecretarialSupportCost)
                .multiply(BigDecimal.valueOf(46))
                .divide(new BigDecimal(100));

        ServiceResult<Void> result = targetWithEmptyIndirectCost.save(form, 1L, organisationResource, new ValidationMessages());

        assertTrue(result.isSuccess());

        verify(financeRowRestService, times(1)).create(isA(IndirectCost.class));

        verify(financeRowRestService, times(1)).update(indirectCostArgumentCaptor.capture());
        IndirectCost indirectCostToSave = indirectCostArgumentCaptor.getValue();
        assertNotNull(indirectCostToSave);
        assertEquals(FinanceRowType.INDIRECT_COSTS, indirectCostToSave.getCostType());
        assertEquals(expected.toBigIntegerExact(), indirectCostToSave.getCost());
        assertEquals(expected, indirectCostToSave.getTotal());

        verifyNoMoreInteractions(financeRowRestService);
    }
}
