package org.innovateuk.ifs.application.forms.sections.horizoneuropeguaranteecosts.saver;

import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.application.forms.sections.hecpcosts.saver.HorizonEuropeGuaranteeCostsSaver;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.CapitalUsageBuilder.newCapitalUsage;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.HecpIndirectCostsCategoryBuilder.newHecpIndirectCostsCostCategory;
import static org.innovateuk.ifs.finance.builder.LabourCostBuilder.newLabourCost;
import static org.innovateuk.ifs.finance.builder.LabourCostCategoryBuilder.newLabourCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.finance.builder.OtherCostBuilder.newOtherCost;
import static org.innovateuk.ifs.finance.builder.OverheadBuilder.newOverhead;
import static org.innovateuk.ifs.finance.builder.SubcontractingCostBuilder.newSubContractingCost;
import static org.innovateuk.ifs.finance.builder.TravelCostBuilder.newTravelCost;
import static org.innovateuk.ifs.finance.resource.category.LabourCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HorizonEuropeGuaranteeCostsSaverTest {

    private static final long APPLICATION_ID = 1L;
    private static final long ORGANISATION_ID = 2L;

    @InjectMocks
    private HorizonEuropeGuaranteeCostsSaver saver;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private ApplicationFinanceRowRestService financeRowRestService;

    @Test
    public void save() {
        LabourCost workingDays = newLabourCost()
                .withGrossEmployeeCost(BigDecimal.ZERO)
                .withDescription(WORKING_DAYS_PER_YEAR)
                .withLabourDays(1)
                .build();
        Overhead overhead = newOverhead()
                .withRateType(OverheadRateType.NONE)
                .build();
        ApplicationFinanceResource finance = newApplicationFinanceResource().withFinanceOrganisationDetails(asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(singletonList(workingDays)).build(),
                FinanceRowType.HECP_INDIRECT_COSTS, newHecpIndirectCostsCostCategory().withCosts(singletonList(overhead)).build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(emptyList()).build(),
                FinanceRowType.CAPITAL_USAGE, newDefaultCostCategory().withCosts(emptyList()).build(),
                FinanceRowType.SUBCONTRACTING_COSTS, newDefaultCostCategory().withCosts(emptyList()).build(),
                FinanceRowType.TRAVEL, newDefaultCostCategory().withCosts(emptyList()).build(),
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(emptyList()).build()
        )).build();

        LabourCost newLabourCost = newLabourCost().build();
        Materials newMaterial = newMaterials().build();
        CapitalUsage newCapital = newCapitalUsage().build();
        SubContractingCost newSubcontracting = newSubContractingCost().build();
        TravelCost newTravel = newTravelCost().build();
        OtherCost newOther = newOtherCost().build();

        when(financeRowRestService.create(any(LabourCost.class))).thenReturn(restSuccess(newLabourCost));
        when(financeRowRestService.create(any(Materials.class))).thenReturn(restSuccess(newMaterial));
        when(financeRowRestService.create(any(CapitalUsage.class))).thenReturn(restSuccess(newCapital));
        when(financeRowRestService.create(any(SubContractingCost.class))).thenReturn(restSuccess(newSubcontracting));
        when(financeRowRestService.create(any(TravelCost.class))).thenReturn(restSuccess(newTravel));
        when(financeRowRestService.create(any(OtherCost.class))).thenReturn(restSuccess(newOther));

        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        HorizonEuropeGuaranteeCostsForm form = new HorizonEuropeGuaranteeCostsForm();
        form.setLabour(BigInteger.valueOf(1L));
        form.setHecpIndirectCosts(BigInteger.valueOf(2L));
        form.setEquipment(BigInteger.valueOf(3L));
        form.setOtherGoods(BigInteger.valueOf(4L));
        form.setSubcontracting(BigInteger.valueOf(5L));
        form.setTravel(BigInteger.valueOf(6L));
        form.setOther(BigInteger.valueOf(7L));

        saver.save(form, APPLICATION_ID, ORGANISATION_ID);

        verify(financeRowRestService).update(workingDays);
        verify(financeRowRestService).update(overhead);

        verify(financeRowRestService).create(any(LabourCost.class));
        verify(financeRowRestService).create(any(Materials.class));
        verify(financeRowRestService).create(any(CapitalUsage.class));
        verify(financeRowRestService).create(any(SubContractingCost.class));
        verify(financeRowRestService).create(any(TravelCost.class));
        verify(financeRowRestService).create(any(OtherCost.class));

        verify(financeRowRestService).update(newLabourCost);
        verify(financeRowRestService).update(newMaterial);
        verify(financeRowRestService).update(newCapital);
        verify(financeRowRestService).update(newSubcontracting);
        verify(financeRowRestService).update(newTravel);
        verify(financeRowRestService).update(newOther);
        verifyNoMoreInteractions(financeRowRestService);

        assertEquals(newLabourCost.getTotal(workingDays.getLabourDays()).toBigInteger().longValue(), 1L);
        assertEquals(overhead.getRateType(), OverheadRateType.HORIZON_EUROPE_GUARANTEE_TOTAL);
        assertEquals(overhead.getRate(), (Integer) 2);
        assertEquals(newMaterial.getTotal().toBigInteger().longValue(), 3L);
        assertEquals(newCapital.getTotal().toBigInteger().longValue(), 4L);
        assertEquals(newSubcontracting.getTotal().toBigInteger().longValue(), 5L);
        assertEquals(newTravel.getTotal().toBigInteger().longValue(), 6L);
        assertEquals(newOther.getTotal().toBigInteger().longValue(), 7L);

        assertEquals(workingDays.getLabourDays(), (Integer) 1);
    }

    @Test
    public void save_zeros() {
        LabourCost workingDays = newLabourCost()
                .withGrossEmployeeCost(BigDecimal.ZERO)
                .withDescription(WORKING_DAYS_PER_YEAR)
                .withLabourDays(1)
                .build();
        Overhead overhead = newOverhead()
                .withRateType(OverheadRateType.HORIZON_2020_TOTAL)
                .build();
        ApplicationFinanceResource finance = newApplicationFinanceResource().withFinanceOrganisationDetails(asMap(
                FinanceRowType.LABOUR, newLabourCostCategory().withCosts(asList(workingDays, newLabourCost().build())).build(),
                FinanceRowType.HECP_INDIRECT_COSTS, newHecpIndirectCostsCostCategory().withCosts(singletonList(overhead)).build(),
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(newMaterials().build(1)).build(),
                FinanceRowType.CAPITAL_USAGE, newDefaultCostCategory().withCosts(newCapitalUsage().build(1)).build(),
                FinanceRowType.SUBCONTRACTING_COSTS, newDefaultCostCategory().withCosts(newSubContractingCost().build(1)).build(),
                FinanceRowType.TRAVEL, newDefaultCostCategory().withCosts(newTravelCost().build(1)).build(),
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(newOtherCost().build(1)).build()
        )).build();

        when(applicationFinanceRestService.getFinanceDetails(APPLICATION_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        HorizonEuropeGuaranteeCostsForm form = new HorizonEuropeGuaranteeCostsForm();
        form.setLabour(BigInteger.valueOf(0L));
        form.setHecpIndirectCosts(BigInteger.valueOf(0L));
        form.setEquipment(BigInteger.valueOf(0L));
        form.setOtherGoods(BigInteger.valueOf(0L));
        form.setSubcontracting(BigInteger.valueOf(0L));
        form.setTravel(BigInteger.valueOf(0L));
        form.setOther(BigInteger.valueOf(0L));

        saver.save(form, APPLICATION_ID, ORGANISATION_ID);

        verify(financeRowRestService).update(workingDays);
        verify(financeRowRestService).update(overhead);

        verify(financeRowRestService, times(6)).delete(anyLong());
        verifyNoMoreInteractions(financeRowRestService);
        assertEquals(workingDays.getLabourDays(), (Integer) 1);
    }
}
