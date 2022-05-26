package org.innovateuk.ifs.project.eligibility.saver;


import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.HecpIndirectCostsCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Component
public class FinanceChecksEligibilityHecpCostsSaver {

    private final ProjectFinanceRestService projectFinanceRestService;
    private final ProjectFinanceRowRestService financeRowRestService;

    public FinanceChecksEligibilityHecpCostsSaver(ProjectFinanceRestService projectFinanceRestService, ProjectFinanceRowRestService financeRowRestService) {
        this.projectFinanceRestService = projectFinanceRestService;
        this.financeRowRestService = financeRowRestService;
    }

    public ServiceResult<Void> save(HorizonEuropeGuaranteeCostsForm form, long projectId, long organisationId) {
        ProjectFinanceResource projectFinance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();

        saveLabour(form, projectFinance);
        saveHecpIndirectCosts(form, projectFinance);
        saveEquipment(form, projectFinance);
        saveOtherGoods(form, projectFinance);
        saveSubcontracting(form, projectFinance);
        saveTravel(form, projectFinance);
        saveOther(form, projectFinance);

        return ServiceResult.serviceSuccess();
    }

    private void saveLabour(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        LabourCostCategory category = (LabourCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR);
        Optional<LabourCost> cost = category.getCosts().stream().findAny().map(LabourCost.class::cast);

        LabourCost workingDays = category.getWorkingDaysPerYearCostItem();
        workingDays.setLabourDays(1);
        financeRowRestService.update(workingDays);

        if (nullOrZero(form.getLabour())) {
            cost.map(LabourCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            LabourCost labour = cost.orElseGet(() -> newCost(new LabourCost(projectFinance.getId())));
            labour.setLabourDays(1);
            labour.setGrossEmployeeCost(new BigDecimal(form.getLabour()));
            labour.setRole("Total Labour costs");
            financeRowRestService.update(labour);
        }
    }

    private void saveHecpIndirectCosts(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        HecpIndirectCostsCostCategory category = (HecpIndirectCostsCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.HECP_INDIRECT_COSTS);
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


    private void saveEquipment(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        DefaultCostCategory category = (DefaultCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.EQUIPMENT);
        Optional<Equipment> cost = category.getCosts().stream().findAny().map(Equipment.class::cast);

        if (nullOrZero(form.getEquipment())) {
            cost.map(Equipment::getId).ifPresent(financeRowRestService::delete);
        } else {
            Equipment equipment = cost.orElseGet(() -> newCost(new Equipment(projectFinance.getId())));
            equipment.setCost(new BigDecimal(form.getEquipment()));
            equipment.setQuantity(1);
            equipment.setItem("Total equipment costs");
            financeRowRestService.update(equipment);
        }
    }

    private void saveOtherGoods(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        DefaultCostCategory category = (DefaultCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_GOODS);
        Optional<OtherGoods> cost = category.getCosts().stream().findAny().map(OtherGoods.class::cast);

        if (nullOrZero(form.getOtherGoods())) {
            cost.map(OtherGoods::getId).ifPresent(financeRowRestService::delete);
        } else {
            OtherGoods otherGoods = cost.orElseGet(() -> newCost(new OtherGoods(projectFinance.getId())));
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

    private void saveSubcontracting(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        DefaultCostCategory category = (DefaultCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS);
        Optional<SubContractingCost> cost = category.getCosts().stream().findAny().map(SubContractingCost.class::cast);

        if (nullOrZero(form.getSubcontracting())) {
            cost.map(SubContractingCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            SubContractingCost subContractingCost = cost.orElseGet(() -> newCost(new SubContractingCost(projectFinance.getId())));
            subContractingCost.setCost(new BigDecimal(form.getSubcontracting()));
            subContractingCost.setRole("Not specified");
            subContractingCost.setCountry("Not specified");
            subContractingCost.setName("Total subcontracting costs");
            financeRowRestService.update(subContractingCost);
        }
    }

    private void saveTravel(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        DefaultCostCategory category = (DefaultCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL);
        Optional<TravelCost> cost = category.getCosts().stream().findAny().map(TravelCost.class::cast);

        if (nullOrZero(form.getTravel())) {
            cost.map(TravelCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            TravelCost travelCost = cost.orElseGet(() -> newCost(new TravelCost(projectFinance.getId())));
            travelCost.setCost(new BigDecimal(form.getTravel()));
            travelCost.setQuantity(1);
            travelCost.setItem("Total travel costs");
            financeRowRestService.update(travelCost);
        }
    }

    private void saveOther(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        DefaultCostCategory category = (DefaultCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS);
        Optional<OtherCost> cost = category.getCosts().stream().findAny().map(OtherCost.class::cast);

        if (nullOrZero(form.getOther())) {
            cost.map(OtherCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            OtherCost other = cost.orElseGet(() -> newCost(new OtherCost(projectFinance.getId())));
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
