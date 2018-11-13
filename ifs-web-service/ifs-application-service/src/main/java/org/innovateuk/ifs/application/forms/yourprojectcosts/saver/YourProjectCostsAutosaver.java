package org.innovateuk.ifs.application.forms.yourprojectcosts.saver;

import org.innovateuk.ifs.application.forms.yourfunding.form.YourFundingForm;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

@Component
public class YourProjectCostsAutosaver {

    private final static Logger LOG = LoggerFactory.getLogger(YourProjectCostsAutosaver.class);

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private DefaultFinanceRowRestService financeRowRestService;

    public Optional<Long> autoSave(String field, String value, long applicationId, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisation.getId()).getSuccess();

        try {
            if (field.equals("workingDaysPerYear")) {
                finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();
                LabourCostCategory category = (LabourCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR);
                LabourCost workingDaysCost = category.getWorkingDaysPerYearCostItem();
                workingDaysCost.setLabourDays(Integer.parseInt(value));
                financeRowRestService.update(workingDaysCost).getSuccess();
            } else if (field.startsWith("labourCosts")) {
                return autosaveLabourCost(field, value, finance);
            } else if (field.startsWith("overhead")) {
                return autosaveOverheadCost(field, value, finance, applicationId, organisation.getId());
            } else if (field.startsWith("materialRows")) {
                return autosaveMaterialCost(field, value, finance);
            } else if (field.startsWith("capitalUsageRows")) {
                return autosaveCapitalUsageCost(field, value, finance);
            } else if (field.startsWith("subcontractingRows")) {
                return autosaveSubcontractingCost(field, value, finance);
            } else if (field.startsWith("travelRows")) {
                return autosaveTravelCost(field, value, finance);
            } else if (field.startsWith("otherRows")) {
                return autosaveOtherCost(field, value, finance);
            } else {
                throw new IFSRuntimeException(String.format("Auto save field not handled %s", field), Collections.emptyList());
            }
        } catch (Exception e) {
            LOG.debug("Error auto saving", e);
            LOG.info(String.format("Unable to auto save field (%s) value (%s)", field, value));
        }
        return Optional.empty();
    }

    private Optional<Long> autosaveLabourCost(String field, String value, ApplicationFinanceResource finance) throws InstantiationException, IllegalAccessException {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        LabourCost cost = getCost(id, finance, LabourCost.class);

        switch (rowField) {
            case "role":
                cost.setRole(value);
                break;
            case "gross":
                cost.setGrossEmployeeCost(new BigDecimal(value));
                break;
            case "fundingAmount":
                cost.setLabourDays(Integer.parseInt(value));
                break;
            default:
                throw new IFSRuntimeException(String.format("Auto save labour field not handled %s", rowField), Collections.emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveOverheadCost(String field, String value, ApplicationFinanceResource finance, long applicationId, Long organisationId) {
        if (field.equals("overhead.totalSpreadsheet")) {
            finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
            OverheadCostCategory category = (OverheadCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS);
            Overhead overheadCost = (Overhead) category.getCosts().get(0);
            overheadCost.setRate(Integer.valueOf(value));
            financeRowRestService.update(overheadCost);
        }
        return Optional.empty();
    }

    private Optional<Long> autosaveMaterialCost(String field, String value, ApplicationFinanceResource finance) throws InstantiationException, IllegalAccessException {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        Materials cost = getCost(id, finance, Materials.class);
        switch (rowField) {
            case "item":
                cost.setItem(value);
                break;
            case "quantity":
                cost.setQuantity(Integer.parseInt(value));
                break;
            case "cost":
                cost.setCost(new BigDecimal(value));
                break;
            default:
                throw new IFSRuntimeException(String.format("Auto save material field not handled %s", rowField), Collections.emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveCapitalUsageCost(String field, String value, ApplicationFinanceResource finance) throws InstantiationException, IllegalAccessException {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        CapitalUsage cost = getCost(id, finance, CapitalUsage.class);
        switch (rowField) {
            case "item":
                cost.setDescription(value);
                break;
            case "newItem":
                cost.setExisting(Boolean.parseBoolean(value) ? "New" : "Existing");
                break;
            case "deprecation":
                cost.setDeprecation(Integer.valueOf(value));
                break;
            case "netValue":
                cost.setNpv(new BigDecimal(value));
                break;
            case "residualValue":
                cost.setResidualValue(new BigDecimal(value));
                break;
            case "utilisation":
                cost.setUtilisation(Integer.valueOf(value));
                break;
            default:
                throw new IFSRuntimeException(String.format("Auto save capital usage field not handled %s", rowField), Collections.emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveSubcontractingCost(String field, String value, ApplicationFinanceResource finance) throws InstantiationException, IllegalAccessException {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        SubContractingCost cost = getCost(id, finance, SubContractingCost.class);
        switch (rowField) {
            case "name":
                cost.setName(value);
                break;
            case "country":
                cost.setCountry(value);
                break;
            case "role":
                cost.setRole(value);
                break;
            case "cost":
                cost.setCost(new BigDecimal(value));
                break;
            default:
                throw new IFSRuntimeException(String.format("Auto save sub-contracting field not handled %s", rowField), Collections.emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveTravelCost(String field, String value, ApplicationFinanceResource finance) throws InstantiationException, IllegalAccessException {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        TravelCost cost = getCost(id, finance, TravelCost.class);
        switch (rowField) {
            case "item":
                cost.setItem(value);
                break;
            case "times":
                cost.setQuantity(Integer.valueOf(value));
                break;
            case "eachCost":
                cost.setCost(new BigDecimal(value));
                break;
            default:
                throw new IFSRuntimeException(String.format("Auto save travel field not handled %s", rowField), Collections.emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveOtherCost(String field, String value, ApplicationFinanceResource finance) throws InstantiationException, IllegalAccessException {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        OtherCost cost = getCost(id, finance, OtherCost.class);
        switch (rowField) {
            case "description":
                cost.setDescription(value);
                break;
            case "estimate":
                cost.setCost(new BigDecimal(value));
                break;
            default:
                throw new IFSRuntimeException(String.format("Auto save other cost field not handled %s", rowField), Collections.emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private <R extends FinanceRowItem> R getCost(String id, ApplicationFinanceResource finance, Class<R> clazz) throws IllegalAccessException, InstantiationException {
        if (id.equals(YourFundingForm.EMPTY_ROW_ID)) {
            return (R) financeRowRestService.addWithResponse(finance.getId(), clazz.newInstance()).getSuccess();
        } else {
            return (R) financeRowRestService.getCost(Long.valueOf(id)).getSuccess();
        }
    }

    private String idFromRowPath(String field) {
        return field.substring(field.indexOf('[') + 1, field.indexOf(']'));
    }

    private String fieldFromRowPath(String field) {
        return field.substring(field.indexOf("].") + 2);
    }
}
