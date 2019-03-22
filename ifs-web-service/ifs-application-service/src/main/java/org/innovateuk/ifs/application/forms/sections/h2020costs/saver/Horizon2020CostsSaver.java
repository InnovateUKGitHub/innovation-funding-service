package org.innovateuk.ifs.application.forms.sections.h2020costs.saver;

import org.innovateuk.ifs.application.forms.sections.h2020costs.form.Horizon2020CostsForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.DefaultCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Component
public class Horizon2020CostsSaver {

    private final ApplicationFinanceRestService applicationFinanceRestService;
    private final DefaultFinanceRowRestService financeRowRestService;

    public Horizon2020CostsSaver(ApplicationFinanceRestService applicationFinanceRestService, DefaultFinanceRowRestService financeRowRestService) {
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.financeRowRestService = financeRowRestService;
    }

    public ServiceResult<Void> save(Horizon2020CostsForm form, long applicationId, long organisationId) {
        ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();

        saveLabour(form, applicationFinance);
        saveOverhead(form, applicationFinance);
        saveMaterial(form, applicationFinance);
        saveCapital(form, applicationFinance);
        saveSubcontracting(form, applicationFinance);
        saveTravel(form, applicationFinance);
        saveOther(form, applicationFinance);

        return ServiceResult.serviceSuccess();
    }

    private void saveLabour(Horizon2020CostsForm form, ApplicationFinanceResource applicationFinance) {
        LabourCostCategory category = (LabourCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR);
        Optional<LabourCost> cost = category.getCosts().stream().findAny().map(LabourCost.class::cast);

        LabourCost workingDays = category.getWorkingDaysPerYearCostItem();
        workingDays.setLabourDays(1);
        financeRowRestService.update(workingDays);

        if (nullOrZero(form.getLabour())) {
            cost.map(LabourCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            LabourCost labour = cost.orElseGet(() -> newCost(applicationFinance, new LabourCost()));
            labour.setLabourDays(1);
            labour.setGrossEmployeeCost(new BigDecimal(form.getLabour()));
            labour.setRole("Total Labour costs");
            financeRowRestService.update(labour);
        }
    }

    private void saveOverhead(Horizon2020CostsForm form, ApplicationFinanceResource applicationFinance) {
        OverheadCostCategory category = (OverheadCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS);
        Overhead cost = category.getCosts().stream().findAny().map(Overhead.class::cast).get();

        if (nullOrZero(form.getOverhead())) {
            cost.setRateType(OverheadRateType.NONE);
            cost.setRate(0);
        } else {
            cost.setRateType(OverheadRateType.TOTAL);
            cost.setRate(form.getOverhead().intValue());
        }
        financeRowRestService.update(cost);
    }


    private void saveMaterial(Horizon2020CostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.MATERIALS);
        Optional<Materials> cost = category.getCosts().stream().findAny().map(Materials.class::cast);

        if (nullOrZero(form.getMaterial())) {
            cost.map(Materials::getId).ifPresent(financeRowRestService::delete);
        } else {
            Materials materials = cost.orElseGet(() -> newCost(applicationFinance, new Materials()));
            materials.setCost(new BigDecimal(form.getMaterial()));
            materials.setQuantity(1);
            materials.setItem("Total material costs");
            financeRowRestService.update(materials);
        }
    }

    private void saveCapital(Horizon2020CostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.CAPITAL_USAGE);
        Optional<CapitalUsage> cost = category.getCosts().stream().findAny().map(CapitalUsage.class::cast);

        if (nullOrZero(form.getMaterial())) {
            cost.map(CapitalUsage::getId).ifPresent(financeRowRestService::delete);
        } else {
            CapitalUsage capitalUsage = cost.orElseGet(() -> newCost(applicationFinance, new CapitalUsage()));
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

    private void saveSubcontracting(Horizon2020CostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.SUBCONTRACTING_COSTS);
        Optional<SubContractingCost> cost = category.getCosts().stream().findAny().map(SubContractingCost.class::cast);

        if (nullOrZero(form.getSubcontracting())) {
            cost.map(SubContractingCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            SubContractingCost subContractingCost = cost.orElseGet(() -> newCost(applicationFinance, new SubContractingCost()));
            subContractingCost.setCost(new BigDecimal(form.getSubcontracting()));
            subContractingCost.setRole("Not specified");
            subContractingCost.setCountry("Not specified");
            subContractingCost.setName("Total subcontracting costs");
            financeRowRestService.update(subContractingCost);
        }
    }

    private void saveTravel(Horizon2020CostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.TRAVEL);
        Optional<TravelCost> cost = category.getCosts().stream().findAny().map(TravelCost.class::cast);

        if (nullOrZero(form.getTravel())) {
            cost.map(TravelCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            TravelCost travelCost = cost.orElseGet(() -> newCost(applicationFinance, new TravelCost()));
            travelCost.setCost(new BigDecimal(form.getTravel()));
            travelCost.setQuantity(1);
            travelCost.setItem("Total travel costs");
            financeRowRestService.update(travelCost);
        }
    }

    private void saveOther(Horizon2020CostsForm form, ApplicationFinanceResource applicationFinance) {
        DefaultCostCategory category = (DefaultCostCategory) applicationFinance.getFinanceOrganisationDetails().get(FinanceRowType.OTHER_COSTS);
        Optional<OtherCost> cost = category.getCosts().stream().findAny().map(OtherCost.class::cast);

        if (nullOrZero(form.getOther())) {
            cost.map(OtherCost::getId).ifPresent(financeRowRestService::delete);
        } else {
            OtherCost other = cost.orElseGet(() -> newCost(applicationFinance, new OtherCost()));
            other.setCost(new BigDecimal(form.getOther()));
            other.setDescription("Total other costs");
            financeRowRestService.update(other);
        }
    }

    private <C extends FinanceRowItem> C newCost(ApplicationFinanceResource applicationFinance, C cost) {
        return (C) financeRowRestService.addWithResponse(applicationFinance.getId(), cost).getSuccess();
    }

    private boolean nullOrZero(BigInteger value) {
        return value == null || BigInteger.ZERO.equals(value);
    }
}
