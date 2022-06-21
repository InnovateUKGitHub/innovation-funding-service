package org.innovateuk.ifs.project.eligibility.saver;

import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
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
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.EquipmentCostBuilder.newEquipment;
import static org.innovateuk.ifs.finance.builder.HecpIndirectCostsBuilder.newHecpIndirectCosts;
import static org.innovateuk.ifs.finance.builder.HecpIndirectCostsCategoryBuilder.newHecpIndirectCostsCostCategory;
import static org.innovateuk.ifs.finance.builder.OtherCostBuilder.newOtherCost;
import static org.innovateuk.ifs.finance.builder.OtherGoodsBuilder.newOtherGoods;
import static org.innovateuk.ifs.finance.builder.PersonnelCostBuilder.newPersonnelCost;
import static org.innovateuk.ifs.finance.builder.PersonnelCostCategoryBuilder.newPersonnelCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.finance.builder.SubcontractingCostBuilder.newSubContractingCost;
import static org.innovateuk.ifs.finance.builder.TravelCostBuilder.newTravelCost;
import static org.innovateuk.ifs.finance.resource.category.PersonnelCostCategory.WORKING_DAYS_PER_YEAR;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FinanceChecksEligibilityHecpCostsSaverTest {

    private static final long PROJECT_ID = 1L;
    private static final long ORGANISATION_ID = 2L;

    @InjectMocks
    private FinanceChecksEligibilityHecpCostsSaver saver;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private ProjectFinanceRowRestService financeRowRestService;

    @Test
    public void save() {
        PersonnelCost workingDays = newPersonnelCost()
                .withGrossEmployeeCost(BigDecimal.ZERO)
                .withDescription(WORKING_DAYS_PER_YEAR)
                .withLabourDays(1)
                .build();

        HecpIndirectCosts hecpIndirectCosts = newHecpIndirectCosts()
                .withRateType(OverheadRateType.HORIZON_EUROPE_GUARANTEE_TOTAL)
                .build();

        ProjectFinanceResource finance = newProjectFinanceResource().withFinanceOrganisationDetails(asMap(
                FinanceRowType.PERSONNEL, newPersonnelCostCategory().withCosts(singletonList(workingDays)).build(),
                FinanceRowType.HECP_INDIRECT_COSTS, newHecpIndirectCostsCostCategory().withCosts(singletonList(hecpIndirectCosts)).build(),
                FinanceRowType.EQUIPMENT, newDefaultCostCategory().withCosts(emptyList()).build(),
                FinanceRowType.OTHER_GOODS, newDefaultCostCategory().withCosts(emptyList()).build(),
                FinanceRowType.SUBCONTRACTING_COSTS, newDefaultCostCategory().withCosts(emptyList()).build(),
                FinanceRowType.TRAVEL, newDefaultCostCategory().withCosts(emptyList()).build(),
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(emptyList()).build()
        )).build();

        PersonnelCost newPersonnelCost = newPersonnelCost().build();
        Equipment newEquipment = newEquipment().build();
        OtherGoods newOtherGoods = newOtherGoods().build();
        SubContractingCost newSubcontracting = newSubContractingCost().build();
        TravelCost newTravel = newTravelCost().build();
        OtherCost newOther = newOtherCost().build();

        when(financeRowRestService.create(any(PersonnelCost.class))).thenReturn(restSuccess(newPersonnelCost));
        when(financeRowRestService.create(any(Equipment.class))).thenReturn(restSuccess(newEquipment));
        when(financeRowRestService.create(any(OtherGoods.class))).thenReturn(restSuccess(newOtherGoods));
        when(financeRowRestService.create(any(SubContractingCost.class))).thenReturn(restSuccess(newSubcontracting));
        when(financeRowRestService.create(any(TravelCost.class))).thenReturn(restSuccess(newTravel));
        when(financeRowRestService.create(any(OtherCost.class))).thenReturn(restSuccess(newOther));

        when(projectFinanceRestService.getProjectFinance(PROJECT_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        HorizonEuropeGuaranteeCostsForm form = new HorizonEuropeGuaranteeCostsForm();
        form.setPersonnel(BigInteger.valueOf(1L));
        form.setHecpIndirectCosts(BigInteger.valueOf(2L));
        form.setEquipment(BigInteger.valueOf(3L));
        form.setOtherGoods(BigInteger.valueOf(4L));
        form.setSubcontracting(BigInteger.valueOf(5L));
        form.setTravel(BigInteger.valueOf(6L));
        form.setOther(BigInteger.valueOf(7L));

        saver.save(form, PROJECT_ID, ORGANISATION_ID);

        verify(financeRowRestService).update(workingDays);
        verify(financeRowRestService).update(hecpIndirectCosts);

        verify(financeRowRestService).create(any(PersonnelCost.class));
        verify(financeRowRestService).create(any(Equipment.class));
        verify(financeRowRestService).create(any(OtherGoods.class));
        verify(financeRowRestService).create(any(SubContractingCost.class));
        verify(financeRowRestService).create(any(TravelCost.class));
        verify(financeRowRestService).create(any(OtherCost.class));

        verify(financeRowRestService).update(newPersonnelCost);
        verify(financeRowRestService).update(newEquipment);
        verify(financeRowRestService).update(newOtherGoods);
        verify(financeRowRestService).update(newSubcontracting);
        verify(financeRowRestService).update(newTravel);
        verify(financeRowRestService).update(newOther);
        verifyNoMoreInteractions(financeRowRestService);

        assertEquals(1L, newPersonnelCost.getTotal(workingDays.getLabourDays()).toBigInteger().longValue());
        assertEquals(OverheadRateType.HORIZON_EUROPE_GUARANTEE_TOTAL, hecpIndirectCosts.getRateType());
        assertEquals(hecpIndirectCosts.getRate(), (Integer) 2);
        assertEquals(3L, newEquipment.getTotal().toBigInteger().longValue());
        assertEquals(4L, newOtherGoods.getTotal().toBigInteger().longValue());
        assertEquals(5L, newSubcontracting.getTotal().toBigInteger().longValue());
        assertEquals(6L, newTravel.getTotal().toBigInteger().longValue());
        assertEquals(7L, newOther.getTotal().toBigInteger().longValue());

        assertEquals(workingDays.getLabourDays(), (Integer) 1);
    }

    @Test
    public void save_zeros() {
        PersonnelCost workingDays = newPersonnelCost()
                .withGrossEmployeeCost(BigDecimal.ZERO)
                .withDescription(WORKING_DAYS_PER_YEAR)
                .withLabourDays(1)
                .build();

        HecpIndirectCosts hecpIndirectCosts = newHecpIndirectCosts()
                .withRateType(OverheadRateType.HORIZON_EUROPE_GUARANTEE_TOTAL)
                .build();

        ProjectFinanceResource finance = newProjectFinanceResource().withFinanceOrganisationDetails(asMap(
                FinanceRowType.PERSONNEL, newPersonnelCostCategory().withCosts(asList(workingDays, newPersonnelCost().build())).build(),
                FinanceRowType.HECP_INDIRECT_COSTS, newHecpIndirectCostsCostCategory().withCosts(singletonList(hecpIndirectCosts)).build(),
                FinanceRowType.EQUIPMENT, newDefaultCostCategory().withCosts(newEquipment().build(1)).build(),
                FinanceRowType.OTHER_GOODS, newDefaultCostCategory().withCosts(newOtherGoods().build(1)).build(),
                FinanceRowType.SUBCONTRACTING_COSTS, newDefaultCostCategory().withCosts(newSubContractingCost().build(1)).build(),
                FinanceRowType.TRAVEL, newDefaultCostCategory().withCosts(newTravelCost().build(1)).build(),
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().withCosts(newOtherCost().build(1)).build()
        )).build();

        when(projectFinanceRestService.getProjectFinance(PROJECT_ID, ORGANISATION_ID)).thenReturn(restSuccess(finance));

        HorizonEuropeGuaranteeCostsForm form = new HorizonEuropeGuaranteeCostsForm();
        form.setPersonnel(BigInteger.valueOf(0L));
        form.setHecpIndirectCosts(BigInteger.valueOf(0L));
        form.setEquipment(BigInteger.valueOf(0L));
        form.setOtherGoods(BigInteger.valueOf(0L));
        form.setSubcontracting(BigInteger.valueOf(0L));
        form.setTravel(BigInteger.valueOf(0L));
        form.setOther(BigInteger.valueOf(0L));

        saver.save(form, PROJECT_ID, ORGANISATION_ID);

        verify(financeRowRestService).update(workingDays);
        verify(financeRowRestService).update(hecpIndirectCosts);

        verify(financeRowRestService, times(6)).delete(anyLong());
        verifyNoMoreInteractions(financeRowRestService);
        assertEquals(workingDays.getLabourDays(), (Integer) 1);
    }
}
