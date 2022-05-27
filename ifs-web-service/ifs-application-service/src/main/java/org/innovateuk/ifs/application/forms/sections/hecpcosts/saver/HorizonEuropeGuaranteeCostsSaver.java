package org.innovateuk.ifs.application.forms.sections.hecpcosts.saver;

import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.HecpIndirectCostsCostCategory;
import org.innovateuk.ifs.finance.resource.category.PersonnelCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Component
public class HorizonEuropeGuaranteeCostsSaver {

    private final ApplicationFinanceRestService applicationFinanceRestService;
    private final ApplicationFinanceRowRestService financeRowRestService;

    public HorizonEuropeGuaranteeCostsSaver(ApplicationFinanceRestService applicationFinanceRestService, ApplicationFinanceRowRestService financeRowRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.financeRowRestService = financeRowRestService;
    }

    public ServiceResult<Void> save(HorizonEuropeGuaranteeCostsForm form, long applicationId, long organisationId) {
        ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

        savePersonnel(form, applicationFinance);
        saveHecpIndirectCosts(form, applicationFinance);
        saveEquipment(form, applicationFinance);
        saveOtherGoods(form, applicationFinance);
        saveSubcontracting(form, applicationFinance);
        saveTravel(form, applicationFinance);
        saveOther(form, applicationFinance);

        return ServiceResult.serviceSuccess();
    }

    private void savePersonnel(HorizonEuropeGuaranteeCostsForm form, ApplicationFinanceResource applicationFinance) {
        PersonnelCostCategory category = (PersonnelCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.PERSONNEL);
        Optional<PersonnelCost> cost = category.getCosts().stream().findAny().map(PersonnelCost.class::cast);

        PersonnelCost workingDays = category.getWorkingDaysPerYearCostItem();
        workingDays.setLabourDays(1);
        financeRowRestService.update(workingDays);

        if (nullOrZero(form.getPersonnel())) {
            cost.map(PersonnelCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            PersonnelCost personnel = cost.orElseGet(() -> newCost(new PersonnelCost(applicationFinance.getId())));
            personnel.setLabourDays(1);
            personnel.setGrossEmployeeCost(new BigDecimal(form.getPersonnel()));
            personnel.setRole("Total Personnel costs");
            financeRowRestService.update(personnel);
        }
    }

    private void saveHecpIndirectCosts(HorizonEuropeGuaranteeCostsForm form, ApplicationFinanceResource applicationFinance) {
        HecpIndirectCostsCostCategory category = (HecpIndirectCostsCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.HECP_INDIRECT_COSTS);
        HecpIndirectCosts cost = category.getCosts().stream().findAny().map(HecpIndirectCosts.class::cast).get();

        if (nullOrZero(form.getHecpIndirectCosts())) {
            cost.setRateType(OverheadRateType.NONE);
            cost.setRate(0);
        } else {
            cost.setRateType(OverheadRateType.HORIZON_EUROPE_GUARANTEE_TOTAL);
            cost.setRate(form.getHecpIndirectCosts().intValue());
        }
        financeRowRestService.update(cost);
    }


    private void saveEquipment(HorizonEuropeGuaranteeCostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.EQUIPMENT);
        Optional<Equipment> cost = category.getCosts().stream().findAny().map(Equipment.class::cast);

        if (nullOrZero(form.getEquipment())) {
            cost.map(Equipment::getId).ifPresent(financeRowRestService::delete);
        } else {
            Equipment equipment = cost.orElseGet(() -> newCost(new Equipment(applicationFinance.getId())));
            equipment.setCost(new BigDecimal(form.getEquipment()));
            equipment.setQuantity(1);
            equipment.setItem("Total equipment costs");
            financeRowRestService.update(equipment);
        }
    }

    private void saveOtherGoods(HorizonEuropeGuaranteeCostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_GOODS);
        Optional<OtherGoods> cost = category.getCosts().stream().findAny().map(OtherGoods.class::cast);

        if (nullOrZero(form.getOtherGoods())) {
            cost.map(OtherGoods::getId).ifPresent(financeRowRestService::delete);
        } else {
            OtherGoods otherGoods = cost.orElseGet(() -> newCost(new OtherGoods(applicationFinance.getId())));
            otherGoods.setUtilisation(100);
            //Add one and leave residual value as 1.
            otherGoods.setNpv(new BigDecimal(form.getOtherGoods()).add(BigDecimal.ONE));
            otherGoods.setResidualValue(BigDecimal.ONE);
            otherGoods.setDeprecation(1);
            otherGoods.setExisting("New");
            otherGoods.setDescription("Total other goods costs");
            financeRowRestService.update(otherGoods);
        }
    }

    private void saveSubcontracting(HorizonEuropeGuaranteeCostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS);
        Optional<SubContractingCost> cost = category.getCosts().stream().findAny().map(SubContractingCost.class::cast);

        if (nullOrZero(form.getSubcontracting())) {
            cost.map(SubContractingCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            SubContractingCost subContractingCost = cost.orElseGet(() -> newCost(new SubContractingCost(applicationFinance.getId())));
            subContractingCost.setCost(new BigDecimal(form.getSubcontracting()));
            subContractingCost.setRole("Not specified");
            subContractingCost.setCountry("Not specified");
            subContractingCost.setName("Total subcontracting costs");
            financeRowRestService.update(subContractingCost);
        }
    }

    private void saveTravel(HorizonEuropeGuaranteeCostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL);
        Optional<TravelCost> cost = category.getCosts().stream().findAny().map(TravelCost.class::cast);

        if (nullOrZero(form.getTravel())) {
            cost.map(TravelCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            TravelCost travelCost = cost.orElseGet(() -> newCost(new TravelCost(applicationFinance.getId())));
            travelCost.setCost(new BigDecimal(form.getTravel()));
            travelCost.setQuantity(1);
            travelCost.setItem("Total travel costs");
            financeRowRestService.update(travelCost);
        }
    }

    private void saveOther(HorizonEuropeGuaranteeCostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS);
        Optional<OtherCost> cost = category.getCosts().stream().findAny().map(OtherCost.class::cast);

        if (nullOrZero(form.getOther())) {
            cost.map(OtherCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            OtherCost other = cost.orElseGet(() -> newCost(new OtherCost(applicationFinance.getId())));
            other.setCost(new BigDecimal(form.getOther()));
            other.setDescription("Total other costs");
            financeRowRestService.update(other);
        }
    }

    @SuppressWarnings("unchecked")
    private <C extends FinanceRowItem> C newCost(C cost) {
        return (C) financeRowRestService.create(cost).getSuccess();
    }

    private boolean nullOrZero(BigInteger value) {
        return value == null || BigInteger.ZERO.equals(value);
    }
}