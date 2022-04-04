package org.innovateuk.ifs.project.eligibility.saver;


import org.innovateuk.ifs.application.forms.hecpcosts.form.HorizonEuropeGuaranteeCostsForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
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
        saveOverhead(form, projectFinance);
        saveMaterial(form, projectFinance);
        saveCapital(form, projectFinance);
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

    private void saveOverhead(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        OverheadCostCategory category = (OverheadCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS);
        Overhead cost = category.getCosts().stream().findAny().map(Overhead.class::cast).get();

        if (nullOrZero(form.getOverhead())) {
            cost.setRateType(OverheadRateType.NONE);
            cost.setRate(0);
        } else {
            cost.setRateType(OverheadRateType.HORIZON_EUROPE_GUARANTEE_TOTAL);
            cost.setRate(form.getOverhead().intValue());
        }
        financeRowRestService.update(cost);
    }


    private void saveMaterial(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        DefaultCostCategory category = (DefaultCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.MATERIALS);
        Optional<Materials> cost = category.getCosts().stream().findAny().map(Materials.class::cast);

        if (nullOrZero(form.getMaterial())) {
            cost.map(Materials::getId).ifPresent(financeRowRestService::delete);
        } else {
            Materials materials = cost.orElseGet(() -> newCost(new Materials(projectFinance.getId())));
            materials.setCost(new BigDecimal(form.getMaterial()));
            materials.setQuantity(1);
            materials.setItem("Total material costs");
            financeRowRestService.update(materials);
        }
    }

    private void saveCapital(HorizonEuropeGuaranteeCostsForm form, ProjectFinanceResource projectFinance) {
        DefaultCostCategory category = (DefaultCostCategory) projectFinance.getFinanceOrganisationDetails().get(FinanceRowType.CAPITAL_USAGE);
        Optional<CapitalUsage> cost = category.getCosts().stream().findAny().map(CapitalUsage.class::cast);

        if (nullOrZero(form.getMaterial())) {
            cost.map(CapitalUsage::getId).ifPresent(financeRowRestService::delete);
        } else {
            CapitalUsage capitalUsage = cost.orElseGet(() -> newCost(new CapitalUsage(projectFinance.getId())));
            capitalUsage.setUtilisation(100);
            //Add one and leave residual value as 1.
            capitalUsage.setNpv(new BigDecimal(form.getCapital()).add(BigDecimal.ONE));
            capitalUsage.setResidualValue(BigDecimal.ONE);
            capitalUsage.setDeprecation(1);
            capitalUsage.setExisting("New");
            capitalUsage.setDescription("Total capital usage costs");
            financeRowRestService.update(capitalUsage);
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
